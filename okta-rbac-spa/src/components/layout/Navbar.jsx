import { useOktaAuth } from "@okta/okta-react";
import { useAuthContext } from "../../auth/RoleContext";

export default function Navbar() {
  const { oktaAuth } = useOktaAuth();
  const { profile, roles } = useAuthContext();

  const isAdmin = roles?.includes("ROLE_ADMIN");

  const handleLogout = async () => {
    await oktaAuth.signOut({
      revokeAccessToken: true,
      revokeRefreshToken: true,
    });
  };

  return (
    <header className="navbar">
      <h2>{isAdmin ? "Admin Portal" : "User Portal"}</h2>

      <div className="navbar-profile">
        <div className="profile-info">
          <div className="profile-name">
            {profile?.firstName} {profile?.lastName}
          </div>
          <div className="profile-email">
            {profile?.email}
          </div>
        </div>

        <button className="logout-button" onClick={handleLogout}>
          Logout
        </button>
      </div>
    </header>
  );
}
