"use client";

import { useState, useCallback, useEffect } from "react";
import { Sidebar } from "@/components/sidebar";
import { ChatWindow } from "@/components/chat-window";
import { ProtectedRoute } from "@/components/auth/protected-route";
import type { Conversation, HistoryConversation } from "@/types/chat";
import { useAuth } from "@/contexts/auth-context";
import { chatAPI } from "@/lib/chat-api";
import { generateUniqueId } from "@/lib/utils";

// 主页组件，包含聊天界面和侧边栏
function HomePage() {
  const [conversations, setConversations] = useState<Conversation[]>([]);
  const [activeConversationId, setActiveConversationId] = useState<
    string | undefined
  >();
  const [isLoadingHistory, setIsLoadingHistory] = useState(false);
  const { user, token } = useAuth();

  // 添加调试日志
  useEffect(() => {
    console.log("当前活动对话ID:", activeConversationId);
    console.log(
      "当前活动对话:",
      conversations.find((conv) => conv.id === activeConversationId)
    );
    console.log("所有对话数量:", conversations.length);
  }, [activeConversationId, conversations]);

  /**
   * 将历史对话转换为当前对话格式
   */
  const convertHistoryToConversation = useCallback(
    (history: HistoryConversation): Conversation => {
      const conversationId = history.sessionId || history.id;

      return {
        id: conversationId,
        title: history.question
          ? history.question.substring(0, 30) +
            (history.question.length > 30 ? "..." : "")
          : "历史对话",
        messages: [
          {
            id: `${history.id}_user`,
            role: "user" as const,
            content: history.question || "",
            timestamp: new Date(history.createTime || Date.now()),
          },
          {
            id: `${history.id}_assistant`,
            role: "assistant" as const,
            content: history.answer || "",
            timestamp: new Date(history.createTime || Date.now()),
          },
        ],
        createdAt: new Date(history.createTime || Date.now()),
        updatedAt: new Date(history.createTime || Date.now()),
        isHistory: true, // 标记为历史对话
      };
    },
    []
  );

  /**
   * 加载用户历史对话
   */
  const loadUserHistory = useCallback(async () => {
    if (!user?.id || !token) return;

    try {
      setIsLoadingHistory(true);
      const historyConversations = await chatAPI.getUserConversations(
        user.id,
        token
      );

      console.log("Loaded history conversations:", historyConversations);

      if (!historyConversations || historyConversations.length === 0) {
        console.log("No history conversations found");
        return;
      }

      // 转换历史对话数据
      const convertedConversations: Conversation[] = historyConversations.map(
        convertHistoryToConversation
      );

      setConversations((prev) => {
        // 创建映射来去重和合并对话
        const conversationMap = new Map<string, Conversation>();

        // 先添加历史对话（服务器数据优先）
        convertedConversations.forEach((conv) => {
          conversationMap.set(conv.id, conv);
        });

        // 再添加现有对话，但不覆盖历史对话
        prev.forEach((conv) => {
          if (!conversationMap.has(conv.id)) {
            conversationMap.set(conv.id, conv);
          }
        });

        // 转换为数组并按更新时间排序
        const mergedConversations = Array.from(conversationMap.values()).sort(
          (a, b) => b.updatedAt.getTime() - a.updatedAt.getTime()
        );

        console.log("Merged conversations:", mergedConversations);
        return mergedConversations;
      });

      // 移除自动设置第一个历史对话为活动对话的逻辑
      // 这样登录后会保持空会话状态
      console.log("历史对话已加载，但未设置活动对话");
    } catch (error) {
      console.error("Failed to load conversation history:", error);
    } finally {
      setIsLoadingHistory(false);
    }
  }, [user?.id, token, convertHistoryToConversation]);

  /**
   * 处理加载历史对话（侧边栏回调）
   */
  const handleLoadConversations = useCallback(
    (loadedConversations: HistoryConversation[]) => {
      // 如果通过侧边栏加载了对话，转换为当前格式并合并
      if (loadedConversations && loadedConversations.length > 0) {
        const convertedConversations: Conversation[] = loadedConversations.map(
          convertHistoryToConversation
        );

        setConversations((prev) => {
          const conversationMap = new Map<string, Conversation>();

          // 先添加现有对话
          prev.forEach((conv) => conversationMap.set(conv.id, conv));

          // 添加新加载的对话，不覆盖现有对话
          convertedConversations.forEach((conv) => {
            if (!conversationMap.has(conv.id)) {
              conversationMap.set(conv.id, conv);
            }
          });

          return Array.from(conversationMap.values()).sort(
            (a, b) => b.updatedAt.getTime() - a.updatedAt.getTime()
          );
        });
      }
    },
    [convertHistoryToConversation]
  );

  /**
   * 根据第一条消息内容生成对话标题
   */
  const generateConversationTitle = (firstMessage: string): string => {
    const title =
      firstMessage.length > 30
        ? firstMessage.substring(0, 30) + "..."
        : firstMessage;
    return title || "新对话";
  };

  /**
   * 处理新建聊天
   */
  const handleNewChat = useCallback(() => {
    const newConversation: Conversation = {
      id: generateUniqueId("conv"),
      title: "新对话",
      messages: [],
      createdAt: new Date(),
      updatedAt: new Date(),
    };

    setConversations((prev) => {
      // 确保新对话的ID不重复
      const finalId = prev.some((conv) => conv.id === newConversation.id)
        ? generateUniqueId("conv")
        : newConversation.id;

      return [{ ...newConversation, id: finalId }, ...prev];
    });
    setActiveConversationId(newConversation.id);
  }, []);

  /**
   * 处理选择对话
   */
  const handleSelectConversation = useCallback((conversationId: string) => {
    console.log("选择对话:", conversationId);
    setActiveConversationId(conversationId);
  }, []);

  /**
   * 处理删除对话
   */
  const handleDeleteConversation = useCallback(
    (conversationId: string) => {
      setConversations((prev) =>
        prev.filter((conv) => conv.id !== conversationId)
      );
      if (activeConversationId === conversationId) {
        setActiveConversationId(undefined);
      }
    },
    [activeConversationId]
  );

  /**
   * 处理重命名对话
   */
  const handleRenameConversation = useCallback(
    (conversationId: string, newTitle: string) => {
      setConversations((prev) =>
        prev.map((conv) =>
          conv.id === conversationId
            ? { ...conv, title: newTitle, updatedAt: new Date() }
            : conv
        )
      );
    },
    []
  );

  /**
   * 处理新增消息
   */
  const handleMessageAdded = useCallback(
    (message: { role: "user" | "assistant"; content: string }) => {
      if (!activeConversationId) return;

      const newMessage = {
        id: generateUniqueId("msg"),
        role: message.role,
        content: message.content,
        timestamp: new Date(),
      };

      setConversations((prev) =>
        prev.map((conv) =>
          conv.id === activeConversationId
            ? {
                ...conv,
                messages: [...conv.messages, newMessage],
                updatedAt: new Date(),
              }
            : conv
        )
      );
    },
    [activeConversationId]
  );

  /**
   * 处理第一条消息
   */
  const handleFirstMessage = useCallback(
    (content: string) => {
      if (!activeConversationId) return;

      const title = generateConversationTitle(content);
      setConversations((prev) =>
        prev.map((conv) =>
          conv.id === activeConversationId
            ? {
                ...conv,
                title,
                updatedAt: new Date(),
              }
            : conv
        )
      );
    },
    [activeConversationId]
  );

  // 在组件挂载时加载历史对话
  useEffect(() => {
    loadUserHistory();
  }, [loadUserHistory]);

  // 根据活动对话ID查找当前活动对话
  const activeConversation = conversations.find(
    (conv) => conv.id === activeConversationId
  );

  return (
    <div className="flex h-screen bg-background overflow-hidden">
      {/* 侧边栏组件 */}
      <Sidebar
        conversations={conversations}
        activeConversationId={activeConversationId}
        onNewChat={handleNewChat}
        onSelectConversation={handleSelectConversation}
        onDeleteConversation={handleDeleteConversation}
        onRenameConversation={handleRenameConversation}
        onLoadConversations={handleLoadConversations}
        isLoadingHistory={isLoadingHistory}
      />

      {/* 主聊天区域 */}
      <div className="flex-1 flex flex-col min-h-0">
        <ChatWindow
          conversationId={activeConversationId}
          conversationTitle={activeConversation?.title}
          initialMessages={activeConversation?.messages || []}
          onMessageAdded={handleMessageAdded}
          onFirstMessage={handleFirstMessage}
        />
      </div>
    </div>
  );
}

// 受保护的主页组件（需要认证才能访问）
export default function ProtectedHomePage() {
  return (
    <ProtectedRoute>
      <HomePage />
    </ProtectedRoute>
  );
}
