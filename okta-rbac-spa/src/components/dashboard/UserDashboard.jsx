import { useState } from "react";
import Layout from "../layout/Layout";
import { useAuthContext } from "../../auth/RoleContext";
import { callApi } from "../../services/apiClient";
import { useOktaAuth } from "@okta/okta-react";

export default function UserDashboard() {
  const { profile, roles } = useAuthContext();
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [userInfo, setUserInfo] = useState(null);
  const { oktaAuth } = useOktaAuth();
  const [error, setError] = useState(null);

  const handleUserApi = async () => {
    setLoading(true);
    setError(null);
    setData(null);

    try {
      const response = await callApi("/api/user");
      setData(response);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
    
  };

  const handleUserInfo = async () => {
    setLoading(true);
    setError(null);

    try {
      const info = await oktaAuth.getUser();
      setUserInfo(info);
    } catch (err) {
      setError("Failed to load userinfo");
    } finally {
      setLoading(false);
    }
  };

  return (
    <Layout>
      <div className="dashboard">
        <h1>User Dashboard</h1>

        <div className="welcome-section">
          <p>
            Welcome, <strong>{profile?.firstName} {profile?.lastName}</strong>!
          </p>
          <div className="role-badge">
            {roles.map((group) => (
              <span key={group} className="badge">
                {group}
              </span>
            ))}
          </div>
        </div>

        <div className="dashboard-section">
          <h2>API Operations</h2>

          <div className="api-controls">
            <button
              className="btn btn-primary"
              onClick={handleUserApi}
              disabled={loading}
            >
              {loading ? "Loading..." : "Call User API"}
            </button>
          </div>

          {error && <div className="error-message">{error}</div>}

          {data && (
            <div className="api-response">
              <h3>Response</h3>
              <pre>{data.message}</pre>
            </div>
          )}
        </div>

        <div className="dashboard-section">
          <h2>Available Resources</h2>
          <ul className="resource-list">
            <li>View your profile</li>
          </ul>
        </div>

        {/* UserInfo Card */}
        <div className="dashboard-section">
          <h2>UserInfo (OIDC)</h2>

          <button className="btn"
          onClick={handleUserInfo} disabled={loading}>
            {loading ? "Loading..." : "Load UserInfo"}
          </button>

          {/* {error && <div className="error-message">{error}</div>} */}

          {userInfo && (
            <div className="userinfo-card">
              <pre>{JSON.stringify(userInfo, null, 2)}</pre>
            </div>
          )}
        </div>
      </div>
    </Layout>
  );
}
