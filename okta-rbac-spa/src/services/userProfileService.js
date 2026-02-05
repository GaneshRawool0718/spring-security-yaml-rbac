import { callApi } from "./apiClient";

export function getMyProfile() {
  return callApi("/api/user/me");
}

export function updateMyProfile(payload) {
  return callApi("/api/user/me", {
    method: "PUT",
    body: JSON.stringify(payload),
  });
}
