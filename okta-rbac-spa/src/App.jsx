import { Routes, Route } from "react-router-dom";
import { LoginCallback } from "@okta/okta-react";

import Login from "./components/Login";
import UserDashboard from "./components/dashboard/UserDashboard";
import AdminDashboard from "./components/dashboard/AdminDashboard";
import UserProfile from "./components/profile/UserProfile";
import Unauthorized from "./components/Unauthorized";
import AuthGate from "./auth/AuthGate";
import ProtectedRoute from "./auth/ProtectedRoute";
import ErrorBoundary from "./components/ErrorBoundary";
import "./App.css";

export default function App() {
  return (
    <ErrorBoundary>
      <Routes>
        <Route path="/login/callback" element={<LoginCallback />} />
        <Route path="/login" element={<Login />} />

        <Route
          path="/"
          element={
            <AuthGate>
              <UserDashboard />
            </AuthGate>
          }
        />

        <Route
          path="/admin"
          element={
            <AuthGate>
              <ProtectedRoute allowedRoles={["ROLE_ADMIN"]}>
                <AdminDashboard />
              </ProtectedRoute>
            </AuthGate>
          }
        />

        <Route
          path="/profile"
          element={
            <AuthGate>
              <UserProfile />
            </AuthGate>
          }
        />

        <Route path="/unauthorized" element={<Unauthorized />} />
      </Routes>
    </ErrorBoundary>
  );
}
