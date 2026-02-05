import { useOktaAuth } from "@okta/okta-react";

export default function Login() {
  const { oktaAuth, authState } = useOktaAuth();

  if (!authState) return null;

  const handleLogin = () => {
    oktaAuth.signInWithRedirect();
  };

  return (
    <div className="login-container">
      <div className="login-card">
        <div className="login-header">
          <h1>Portal</h1>
          <p>Welcome to RBAC Portal</p>
        </div>

        <p className="login-description">
          Login or sign up using your Okta account
        </p>

        <button className="login-button" onClick={handleLogin}>
          Login with Okta
        </button>

        <p className="login-footer">
          Secure authentication powered by Okta
        </p>
      </div>
    </div>
  );
}
