import { screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { useAuth } from "../context/AuthContext";
import { RegisterPage } from "./RegisterPage";
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

describe("RegisterPage", () => {
  it("renders email, username, password fields and a create account button", () => {
    mockUseAuth.mockReturnValue({ ...signedOutState });

    renderWithRouter(<RegisterPage />);

    expect(screen.getByLabelText(/email/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/username/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/password/i)).toBeInTheDocument();
    expect(screen.getByRole("button", { name: /create account/i })).toBeInTheDocument();
  });

  it("calls registerUser with the submitted form values", async () => {
    const user = userEvent.setup();
    const registerUser = vi.fn().mockResolvedValue({
      accessToken: "tok",
      tokenType: "Bearer",
      user: { id: 2, email: "new@example.com", username: "newuser" }
    });

    mockUseAuth.mockReturnValue({ ...signedOutState, registerUser });

    renderWithRouter(<RegisterPage />);

    await user.type(screen.getByLabelText(/email/i), "new@example.com");
    await user.type(screen.getByLabelText(/username/i), "newuser");
    await user.type(screen.getByLabelText(/password/i), "securepass1");
    await user.click(screen.getByRole("button", { name: /create account/i }));

    await waitFor(() => {
      expect(registerUser).toHaveBeenCalledWith({
        email: "new@example.com",
        username: "newuser",
        password: "securepass1"
      });
    });
  });

  it("displays an error message when registration fails", async () => {
    const user = userEvent.setup();
    const registerUser = vi.fn().mockRejectedValue(new Error("Email already in use"));

    mockUseAuth.mockReturnValue({ ...signedOutState, registerUser });

    renderWithRouter(<RegisterPage />);

    await user.type(screen.getByLabelText(/email/i), "taken@example.com");
    await user.type(screen.getByLabelText(/username/i), "someone");
    await user.type(screen.getByLabelText(/password/i), "password123");
    await user.click(screen.getByRole("button", { name: /create account/i }));

    await waitFor(() => {
      expect(screen.getByText("Email already in use")).toBeInTheDocument();
    });
  });

  it("redirects away from the register form when already authenticated", () => {
    mockUseAuth.mockReturnValue({
      isAuthenticated: true,
      isLoading: false,
      user: { id: 1, email: "test@example.com", username: "testuser" },
      accessToken: "tok",
      loginUser: vi.fn(),
      registerUser: vi.fn(),
      logout: vi.fn()
    });

    renderWithRouter(<RegisterPage />, { initialEntries: ["/register"] });

    expect(screen.queryByLabelText(/email/i)).not.toBeInTheDocument();
  });
});
