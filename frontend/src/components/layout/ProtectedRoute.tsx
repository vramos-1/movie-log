import { Navigate } from "react-router-dom";
import { PropsWithChildren } from "react";
import { useAuth } from "../../features/auth/context/AuthContext";

export function ProtectedRoute({ children }: PropsWithChildren) {
  const { isAuthenticated, isLoading } = useAuth();

  if (isLoading) {
    return <p>Checking your session...</p>;
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  return <>{children}</>;
}
