import { OktaAuth } from "@okta/okta-auth-js";

export const oktaAuth = new OktaAuth({
  issuer: import.meta.env.VITE_OKTA_ISSUER,
  clientId: import.meta.env.VITE_OKTA_CLIENT_ID,
  redirectUri: `${window.location.origin}/login/callback`,
  postLogoutRedirectUri: `${window.location.origin}/login`,
  scopes: ["openid", "profile", "email", "groups"],
  pkce: true,
  tokenManager: {
    storage: "localStorage"
  }
});
