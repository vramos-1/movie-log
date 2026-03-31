import { screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { useAuth } from "../context/AuthContext";
import { LoginPage } from "./LoginPage";
import { renderWithRouter } from "../../../test/utils";

vi.mock("../context/AuthContext", () => ({
  useAuth: vi.fn()
}));

const mockUseAuth = vi.mocked(useAuth);

const signedOutState = {
  isAuthenticated: false,
  isLoading: false,
  user: null,
  accessToken: null,
  loginUser: vi.fn(),
  registerUser: vi.fn(),
  logout: vi.fn()
} as const;

beforeEach(() => {
  vi.clearAllMocks();
});

describe("LoginPage", () => {
  it("renders email, password fields and a sign-in button", () => {
    mockUseAuth.mockReturnValue({ ...signedOutState });

    renderWithRouter(<LoginPage />);

    expect(screen.getByLabelText(/email/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/password/i)).toBeInTheDocument();
    expect(screen.getByRole("button", { name: /sign in/i })).toBeInTheDocument();
  });

  it("calls loginUser with the submitted email and password", async () => {
    const user = userEvent.setup();
    const loginUser = vi.fn().mockResolvedValue({
      accessToken: "tok",
      tokenType: "Bearer",
      user: { id: 1, email: "test@example.com", username: "testuser" }
    });

    mockUseAuth.mockReturnValue({ ...signedOutState, loginUser });

    renderWithRouter(<LoginPage />);

    await user.type(screen.getByLabelText(/email/i), "test@example.com");
    await user.type(screen.getByLabelText(/password/i), "password123");
    await user.click(screen.getByRole("button", { name: /sign in/i }));

    await waitFor(() => {
      expect(loginUser).toHaveBeenCalledWith({
        email: "test@example.com",
        password: "password123"
      });
    });
  });

  it("displays an error message when login fails", async () => {
    const user = userEvent.setup();
    const loginUser = vi.fn().mockRejectedValue(new Error("Invalid credentials"));

    mockUseAuth.mockReturnValue({ ...signedOutState, loginUser });

    renderWithRouter(<LoginPage />);

    await user.type(screen.getByLabelText(/email/i), "bad@example.com");
    await user.type(screen.getByLabelText(/password/i), "wrongpass");
    await user.click(screen.getByRole("button", { name: /sign in/i }));

    await waitFor(() => {
      expect(screen.getByText("Invalid credentials")).toBeInTheDocument();
    });
  });

  it("redirects away from the login form when already authenticated", () => {
    mockUseAuth.mockReturnValue({
      isAuthenticated: true,
      isLoading: false,
      user: { id: 1, email: "test@example.com", username: "testuser" },
      accessToken: "tok",
      loginUser: vi.fn(),
      registerUser: vi.fn(),
      logout: vi.fn()
    });

    renderWithRouter(<LoginPage />, { initialEntries: ["/login"] });

    expect(screen.queryByLabelText(/email/i)).not.toBeInTheDocument();
  });
});
