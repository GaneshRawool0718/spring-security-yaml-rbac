import React from "react";
import ReactDOM from "react-dom/client";
import { BrowserRouter } from "react-router-dom";
import { Security } from "@okta/okta-react";
import { toRelativeUrl } from "@okta/okta-auth-js";

import App from "./App";
import { oktaAuth } from "./auth/oktaConfig";
import { RoleProvider } from "./auth/RoleContext";

const restoreOriginalUri = async (_oktaAuth, originalUri) => {
  window.location.replace(
    toRelativeUrl(originalUri || "/", window.location.origin)
  );
};

ReactDOM.createRoot(document.getElementById("root")).render(
  <React.StrictMode>
    <BrowserRouter>
      <Security oktaAuth={oktaAuth} restoreOriginalUri={restoreOriginalUri}>
        <RoleProvider>
          <App />
        </RoleProvider>
      </Security>
    </BrowserRouter>
  </React.StrictMode>
);
