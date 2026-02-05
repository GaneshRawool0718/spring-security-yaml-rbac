import { createContext, useContext, useEffect, useState } from "react";
import { useOktaAuth } from "@okta/okta-react";
import { callApi } from "../services/apiClient";

const RoleContext = createContext(null);

export function RoleProvider({ children }) {
  const { authState } = useOktaAuth();
  const [profile, setProfile] = useState(null);
  const [roles, setRoles] = useState([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    if (!authState?.isAuthenticated) {
      setProfile(null);
      setRoles([]);
      setIsLoading(false);
      return;
    }

    const loadProfile = async () => {
      try {
        const data = await callApi("/api/user/me");

        setProfile({
          id: data.id,
          email: data.email,
          firstName: data.firstName,
          lastName: data.lastName,
          nickName: data.nickName,
        });

        setRoles(data.roles ?? []);
      } catch (error) {
        console.error("Failed to load user profile in RoleContext:", error);
        setProfile(null);
        setRoles([]);
      } finally {
        setIsLoading(false);
      }
    };

    loadProfile();
  }, [authState]);

  return (
    <RoleContext.Provider value={{ profile, roles, isLoading }}>
      {children}
    </RoleContext.Provider>
  );
}

export function useAuthContext() {
  const ctx = useContext(RoleContext);
  if (!ctx) {
    throw new Error("useAuthContext must be used inside RoleProvider");
  }
  return ctx;
}
