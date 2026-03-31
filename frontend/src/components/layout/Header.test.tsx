import { screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { useAuth } from "../../features/auth/context/AuthContext";
import { Header } from "./Header";
import { renderWithRouter } from "../../test/utils";

vi.mock("../../features/auth/context/AuthContext", () => ({
  useAuth: vi.fn()
}));

const mockUseAuth = vi.mocked(useAuth);

beforeEach(() => {
  vi.clearAllMocks();
});

describe("Header", () => {
  it("shows Login and Register links when signed out", () => {
    mockUseAuth.mockReturnValue({
      isAuthenticated: false,
      isLoading: false,
      user: null,
      accessToken: null,
      loginUser: vi.fn(),
      registerUser: vi.fn(),
      logout: vi.fn()
    });

    renderWithRouter(<Header />);

    expect(screen.getByRole("link", { name: /login/i })).toBeInTheDocument();
    expect(screen.getByRole("link", { name: /register/i })).toBeInTheDocument();
    expect(screen.queryByRole("button", { name: /logout/i })).not.toBeInTheDocument();
  });

  it("shows My Log link and Logout button when signed in", () => {
    mockUseAuth.mockReturnValue({
      isAuthenticated: true,
      isLoading: false,
      user: { id: 1, email: "test@example.com", username: "testuser" },
      accessToken: "token",
      loginUser: vi.fn(),
      registerUser: vi.fn(),
      logout: vi.fn()
    });

    renderWithRouter(<Header />);

    expect(screen.getByRole("link", { name: /my log/i })).toBeInTheDocument();
    expect(screen.getByRole("button", { name: /logout/i })).toBeInTheDocument();
    expect(screen.queryByRole("link", { name: /login/i })).not.toBeInTheDocument();
  });

  it("displays the username when signed in", () => {
    mockUseAuth.mockReturnValue({
      isAuthenticated: true,
      isLoading: false,
      user: { id: 1, email: "test@example.com", username: "alice" },
      accessToken: "token",
      loginUser: vi.fn(),
      registerUser: vi.fn(),
      logout: vi.fn()
    });

    renderWithRouter(<Header />);

    expect(screen.getByText("alice")).toBeInTheDocument();
  });

  it("calls logout when the Logout button is clicked", async () => {
    const user = userEvent.setup();
    const logout = vi.fn();

    mockUseAuth.mockReturnValue({
      isAuthenticated: true,
      isLoading: false,
      user: { id: 1, email: "test@example.com", username: "testuser" },
      accessToken: "token",
      loginUser: vi.fn(),
      registerUser: vi.fn(),
      logout
    });

    renderWithRouter(<Header />);

    await user.click(screen.getByRole("button", { name: /logout/i }));

    expect(logout).toHaveBeenCalledOnce();
  });
});
