import { useNavigate } from "react-router-dom";

export default function Unauthorized() {
  const navigate = useNavigate();

  return (
    <div className="unauthorized-container">
      <div className="unauthorized-card">
        <h1 className="unauthorized-code">403</h1>
        <h2>Access Denied</h2>
        <p>You do not have permission to access this page.</p>
        <p className="unauthorized-hint">
          Only administrators can access this resource.
        </p>
        <button className="unauthorized-button" onClick={() => navigate("/")}>
          Back to Dashboard
        </button>
      </div>
    </div>
  );
}
