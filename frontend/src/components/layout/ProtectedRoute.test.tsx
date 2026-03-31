import { screen } from "@testing-library/react";
import { useAuth } from "../../features/auth/context/AuthContext";
import { ProtectedRoute } from "./ProtectedRoute";
import { renderWithRouter } from "../../test/utils";

vi.mock("../../features/auth/context/AuthContext", () => ({
  useAuth: vi.fn()
}));

const mockUseAuth = vi.mocked(useAuth);

beforeEach(() => {
  vi.clearAllMocks();
});

describe("ProtectedRoute", () => {
  it("renders children when authenticated", () => {
    mockUseAuth.mockReturnValue({
      isAuthenticated: true,
      isLoading: false,
      user: null,
      accessToken: "token",
      loginUser: vi.fn(),
      registerUser: vi.fn(),
      logout: vi.fn()
    });

    renderWithRouter(
      <ProtectedRoute>
        <p>Protected Content</p>
      </ProtectedRoute>
    );

    expect(screen.getByText("Protected Content")).toBeInTheDocument();
  });

  it("does not render children when unauthenticated", () => {
    mockUseAuth.mockReturnValue({
      isAuthenticated: false,
      isLoading: false,
      user: null,
      accessToken: null,
      loginUser: vi.fn(),
      registerUser: vi.fn(),
      logout: vi.fn()
    });

    renderWithRouter(
      <ProtectedRoute>
        <p>Protected Content</p>
      </ProtectedRoute>,
      { initialEntries: ["/me/log"] }
    );

    expect(screen.queryByText("Protected Content")).not.toBeInTheDocument();
  });

  it("shows a loading indicator while session is being checked", () => {
    mockUseAuth.mockReturnValue({
      isAuthenticated: false,
      isLoading: true,
      user: null,
      accessToken: null,
      loginUser: vi.fn(),
      registerUser: vi.fn(),
      logout: vi.fn()
    });

    renderWithRouter(
      <ProtectedRoute>
        <p>Protected Content</p>
      </ProtectedRoute>
    );

    expect(screen.getByText(/checking/i)).toBeInTheDocument();
    expect(screen.queryByText("Protected Content")).not.toBeInTheDocument();
  });
});
