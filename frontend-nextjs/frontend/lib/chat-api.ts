// lib/chat-api.ts
import type { HistoryConversation } from "@/types/chat";

const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_BASE_URL || "http://13.51.85.43:8080";

// const API_BASE_URL =
//   process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";

class ChatAPI {
  private getAuthHeaders(token?: string) {
    const headers: Record<string, string> = {
      "Content-Type": "application/json",
    };

    if (token) {
      headers.Authorization = `Bearer ${token}`;
    }

    return headers;
  }

  /* ---------- 获取用户对话历史 ---------- */
  async getUserConversations(
    userId: string,
    token: string
  ): Promise<HistoryConversation[]> {
    try {
      console.log("Fetching user conversations for userId:", userId);
      console.log("API_BASE_URL@chat-api:", API_BASE_URL);

      const response = await fetch(
        `${API_BASE_URL}/api/qa/history/user/${userId}`,
        {
          method: "GET",
          headers: this.getAuthHeaders(token),
          credentials: "include",
        }
      );

      console.log("Response status:", response.status);

      if (!response.ok) {
        const errorText = await response.text();
        console.error("Get conversations error response:", errorText);

        if (response.status === 404) {
          // 用户没有对话历史，返回空数组
          return [];
        }

        try {
          const errorJson = JSON.parse(errorText);
          throw new Error(
            errorJson.message ||
              errorJson.error ||
              `获取对话历史失败 (${response.status})`
          );
        } catch {
          throw new Error(errorText || `获取对话历史失败 (${response.status})`);
        }
      }

      const data: HistoryConversation[] = await response.json();
      console.log("Conversations response:", data);

      return data || [];
    } catch (error) {
      console.error("Get conversations request failed:", error);
      throw error;
    }
  }

  /* ---------- 删除对话 ---------- */
  async deleteConversation(
    conversationId: string,
    token: string
  ): Promise<void> {
    console.log("API_BASE_URL@chat-api:", API_BASE_URL);
    
    try {
      const response = await fetch(
        `${API_BASE_URL}/api/qa/history/${conversationId}`,
        {
          method: "DELETE",
          headers: this.getAuthHeaders(token),
          credentials: "include",
        }
      );

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(`删除对话失败: ${errorText}`);
      }
    } catch (error) {
      console.error("Delete conversation failed:", error);
      throw error;
    }
  }
}

export const chatAPI = new ChatAPI();
