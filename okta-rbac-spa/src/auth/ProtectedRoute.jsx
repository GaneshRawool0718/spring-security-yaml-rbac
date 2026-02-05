import { Navigate } from "react-router-dom";
import { useAuthContext } from "./RoleContext";

export default function ProtectedRoute({ allowedRoles, children }) {
  const { roles, isLoading } = useAuthContext();

  if (isLoading) {
    return <div style={{ textAlign: "center", marginTop: 40 }}>Loading...</div>;
  }

  const hasAccess =
    Array.isArray(roles) &&
    allowedRoles.every(role => roles.includes(role));

  return hasAccess ? children : <Navigate to="/unauthorized" replace />;
}
