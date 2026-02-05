import { NavLink } from "react-router-dom";
import { useAuthContext } from "../../auth/RoleContext";

export default function Sidebar() {
  const { roles } = useAuthContext();
  const isAdmin = roles?.includes("ROLE_ADMIN");

  return (
    <aside className="sidebar">
      <div className="sidebar-logo">
        <h3>RBAC Portal</h3>
      </div>

      <nav className="sidebar-nav">
        <div className="nav-section">
          <h4 className="nav-label">Main</h4>

          <NavLink to="/" className="nav-link">
            Dashboard
          </NavLink>

          <NavLink to="/profile" className="nav-link">
            My Profile
          </NavLink>

          {/* <NavLink to="/userinfo" className="nav-link">
            User Info
          </NavLink> */}
        </div>

        {isAdmin && (
          <div className="nav-section">
            <h4 className="nav-label">Administration</h4>

            <NavLink to="/admin" className="nav-link">
              Admin Dashboard
            </NavLink>
          </div>
        )}
      </nav>
    </aside>
  );
}
