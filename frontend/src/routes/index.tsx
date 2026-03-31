import { Navigate, Route, Routes } from "react-router-dom";
import { LoginPage } from "../features/auth/pages/LoginPage";
import { RegisterPage } from "../features/auth/pages/RegisterPage";
import { MovieSearchPage } from "../features/movies/pages/MovieSearchPage";
import { MovieDetailsPage } from "../features/movies/pages/MovieDetailsPage";
import { MyLogPage } from "../features/dashboard/pages/MyLogPage";
import { ProtectedRoute } from "../components/layout/ProtectedRoute";

export function AppRoutes() {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/movies" replace />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />
      <Route path="/movies" element={<MovieSearchPage />} />
      <Route path="/movies/:id" element={<MovieDetailsPage />} />
      <Route path="/me/log" element={<ProtectedRoute><MyLogPage /></ProtectedRoute>} />
    </Routes>
  );
}
