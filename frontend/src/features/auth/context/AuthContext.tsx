import { createContext, PropsWithChildren, useContext, useEffect, useState } from "react";
import {
  AuthResponse,
  LoginRequest,
  RegisterRequest,
  UserProfile,
  login,
  me,
  register
} from "../../../api/authApi";
import { clearAccessToken, getAccessToken, persistAccessToken } from "../authSession";

type AuthContextValue = {
  user: UserProfile | null;
  accessToken: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  loginUser: (request: LoginRequest) => Promise<AuthResponse>;
  registerUser: (request: RegisterRequest) => Promise<AuthResponse>;
  logout: () => void;
};

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

export function AuthProvider({ children }: PropsWithChildren) {
  const [accessToken, setAccessToken] = useState<string | null>(() => getAccessToken());
  const [user, setUser] = useState<UserProfile | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const persistedToken = getAccessToken();

    if (!persistedToken) {
      setIsLoading(false);
      return;
    }

    const token = persistedToken;
    let isMounted = true;

    async function loadProfile() {
      try {
        const profile = await me(token);
        if (isMounted) {
          setAccessToken(token);
          setUser(profile);
        }
      } catch {
        if (isMounted) {
          clearAccessToken();
          setAccessToken(null);
          setUser(null);
        }
      } finally {
        if (isMounted) {
          setIsLoading(false);
        }
      }
    }

    loadProfile();

    return () => {
      isMounted = false;
    };
  }, []);

  function applyAuthResult(result: AuthResponse) {
    persistAccessToken(result.accessToken);
    setAccessToken(result.accessToken);
    setUser(result.user);
    setIsLoading(false);
  }

  async function loginUser(request: LoginRequest): Promise<AuthResponse> {
    const result = await login(request);
    applyAuthResult(result);
    return result;
  }

  async function registerUser(request: RegisterRequest): Promise<AuthResponse> {
    const result = await register(request);
    applyAuthResult(result);
    return result;
  }

  function logout() {
    clearAccessToken();
    setAccessToken(null);
    setUser(null);
    setIsLoading(false);
  }

  return (
    <AuthContext.Provider
      value={{
        user,
        accessToken,
        isAuthenticated: Boolean(accessToken),
        isLoading,
        loginUser,
        registerUser,
        logout
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth(): AuthContextValue {
  const context = useContext(AuthContext);

  if (!context) {
    throw new Error("useAuth must be used within an AuthProvider");
  }

  return context;
}