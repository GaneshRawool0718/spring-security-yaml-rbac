import { useState } from "react";
import Layout from "../layout/Layout";
import { useAuthContext } from "../../auth/RoleContext";
import { callApi } from "../../services/apiClient";

export default function AdminDashboard() {
  const { profile, roles } = useAuthContext();
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const handleAdminApi = async () => {
    setLoading(true);
    setError(null);
    setData(null);

    try {
      const response = await callApi("/api/admin");
      setData(response);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Layout>
      <div className="dashboard">
        <h1>Admin Dashboard</h1>

        <div className="welcome-section">
          <p>
            Welcome, <strong>{profile?.firstName} {profile?.lastName}</strong>!
          </p>
          <p className="email">{profile?.email}</p>
          <div className="role-badge">
            {roles.map((group) => (
              <span key={group} className="badge">
                {group}
              </span>
            ))}
          </div>
        </div>

        <div className="dashboard-section">
          <h2>Administrative Operations</h2>

          <div className="api-controls">
            <button
              className="btn btn-primary"
              onClick={handleAdminApi}
              disabled={loading}
            >
              {loading ? "Loading..." : "Call Admin API"}
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
          <h2>Admin Tools</h2>
          <ul className="resource-list">
            <li>System monitoring</li>
          </ul>
        </div>
      </div>
    </Layout>
  );
}
