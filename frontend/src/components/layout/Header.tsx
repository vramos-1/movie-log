import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../../features/auth/context/AuthContext";

export function Header() {
  const navigate = useNavigate();
  const { isAuthenticated, logout, user } = useAuth();

  function handleLogout() {
    logout();
    navigate("/login");
  }

  return (
    <header className="header">
      <h1>Movie Log</h1>
      <nav>
        <Link to="/movies">Movies</Link>
        {isAuthenticated ? (
          <>
            <Link to="/me/log">My Log</Link>
            <span className="nav-user">{user?.username ?? user?.email}</span>
            <button type="button" className="nav-button" onClick={handleLogout}>
              Logout
            </button>
          </>
        ) : (
          <>
            <Link to="/login">Login</Link>
            <Link to="/register">Register</Link>
          </>
        )}
      </nav>
    </header>
  );
}
