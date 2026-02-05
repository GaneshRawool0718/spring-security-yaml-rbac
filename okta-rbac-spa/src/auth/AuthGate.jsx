import { useOktaAuth } from "@okta/okta-react";
import { Navigate } from "react-router-dom";

export default function AuthGate({ children }) {
  const { authState } = useOktaAuth();

  if (!authState) {
    return null;
  }

  if (!authState.isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  return children;
}
