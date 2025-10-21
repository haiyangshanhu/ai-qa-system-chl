import type {
  AuthResponse,
  LoginRequest,
  RegisterRequest,
  User,
} from "../types/auth";

const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_BASE_URL || "http://16.170.233.101:8080";

// const API_BASE_URL =
//   process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";

class AuthAPI {
  private getAuthHeaders(token?: string) {
    const headers: Record<string, string> = {
      "Content-Type": "application/json",
    };

    if (token) {
      headers.Authorization = `Bearer ${token}`;
    }

    return headers;
  }

  /* ----------  Login  ---------- */
  async login(credentials: LoginRequest): Promise<AuthResponse> {
    try {
      console.log("API_BASE_URL@auth-api:", API_BASE_URL);
      console.log(
        "Sending login request to:",
        `${API_BASE_URL}/api/user/login`
      );
      console.log("Request payload:", credentials);

      const response = await fetch(`${API_BASE_URL}/api/user/login`, {
        method: "POST",
        headers: this.getAuthHeaders(),
        body: JSON.stringify(credentials),
        credentials: "include",
      });

      console.log("Response status:", response.status);

      if (!response.ok) {
        // ✅ 改为 const
        const errorText = await response.text();
        console.error("Login error response:", errorText);

        if (response.status === 500) {
          throw new Error(`服务器内部错误 (500): ${errorText}`);
        }

        try {
          const errorJson = JSON.parse(errorText);
          throw new Error(
            errorJson.message ||
              errorJson.error ||
              `登录失败 (${response.status})`
          );
        } catch {
          throw new Error(errorText || `登录失败 (${response.status})`);
        }
      }

      const loginResponse = await response.json();
      console.log("Login success response:", loginResponse);

      return {
        token: loginResponse.token,
        user: {
          id: loginResponse.userId.toString(),
          username: loginResponse.username,
          nickname: loginResponse.nickname,
          email: loginResponse.email,
        },
      };
    } catch (error) {
      console.error("Login request failed:", error);
      throw error;
    }
  }

  /* ----------  Register  ---------- */
  async register(userData: RegisterRequest): Promise<AuthResponse> {
    try {
      console.log("API_BASE_URL@auth-api:", API_BASE_URL);
      console.log(
        "Sending register request to:",
        `${API_BASE_URL}/api/user/register`
      );
      console.log("Request register:", userData);

      const response = await fetch(`${API_BASE_URL}/api/user/register`, {
        method: "POST",
        headers: this.getAuthHeaders(),
        body: JSON.stringify(userData),
        credentials: "include",
      });

      console.log("Response status:", response.status);

      if (!response.ok) {
        // ✅ 改为 const
        const errorText = await response.text();
        console.error("Register error response:", errorText);

        if (response.status === 500) {
          throw new Error(`服务器内部错误 (500): ${errorText}`);
        }

        try {
          const errorJson = JSON.parse(errorText);
          throw new Error(
            errorJson.message ||
              errorJson.error ||
              `注册失败 (${response.status})`
          );
        } catch {
          throw new Error(errorText || `注册失败 (${response.status})`);
        }
      }

      const registerResponse = await response.json();
      console.log("Register success response:", registerResponse);

      return {
        token: registerResponse.token,
        user: {
          id: registerResponse.userId.toString(),
          username: registerResponse.username,
          nickname: registerResponse.nickname,
          email: registerResponse.email,
        },
      };
    } catch (error) {
      console.error("Register request failed:", error);
      throw error;
    }
  }

  /* ----------  Get Current User  ---------- */
  async getCurrentUser(token: string): Promise<User> {
    console.log("API_BASE_URL@auth-api:", API_BASE_URL);

    const response = await fetch(`${API_BASE_URL}/api/auth/me`, {
      method: "GET",
      headers: this.getAuthHeaders(token),
    });

    if (!response.ok) {
      throw new Error("Failed to get user info");
    }

    return response.json();
  }
}

export const authAPI = new AuthAPI();
