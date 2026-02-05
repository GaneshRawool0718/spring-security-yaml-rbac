import { oktaAuth } from "../auth/oktaConfig";

const API_BASE_URL = import.meta.env.VITE_API_URL || "http://localhost:8080";

export async function callApi(path, options = {}) {
  const accessToken = await oktaAuth.getAccessToken();

  if (!accessToken) {
    throw new Error("Not authenticated");
  }

  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...options,
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${accessToken}`,
      ...options.headers,
    },
  });

  if (response.status === 401) {
    await oktaAuth.signOut();
    throw new Error("Session expired");
  }

  if (!response.ok) {
    // Try to parse error details from server response
    const errorBody = await response.json().catch(() => ({}));
    throw new Error(errorBody.message || `API error ${response.status}`);
  }

  return response.json();
}
