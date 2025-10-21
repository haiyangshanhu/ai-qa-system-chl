import { Button } from "@/components/ui/button";
import { useAuth } from "@/contexts/auth-context";
import { QARequest } from "@/types/qa";
import { Bot, Sparkles } from "lucide-react";
import { useCallback, useEffect, useRef, useState } from "react";
import { v4 as uuidv4 } from "uuid";
import { ChatHeader } from "./chat-header";
import { ChatInput } from "./chat-input";
import { MessageBubble } from "./message-bubble";

const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_BASE_URL || "http://16.170.233.101:8080";

// const API_BASE_URL =
//   process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";


interface ChatWindowProps {
  conversationId?: string;
  conversationTitle?: string;
  initialMessages?: Array<{
    id: string;
    role: "user" | "assistant";
    content: string;
    timestamp: Date;
  }>;
  onMessageAdded?: (message: {
    role: "user" | "assistant";
    content: string;
  }) => void;
  onFirstMessage?: (content: string) => void;
}

interface CustomMessage {
  id: string;
  role: "user" | "assistant";
  content: string;
}

export function ChatWindow({
  conversationId,
  conversationTitle,
  initialMessages = [],
  onMessageAdded,
  onFirstMessage,
}: ChatWindowProps) {
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const { token, user } = useAuth();
  const [isLoading, setIsLoading] = useState(false);

  // 生成会话ID的函数
  const generateSessionId = useCallback((): string => {
    if (typeof uuidv4 === "function") {
      return uuidv4();
    }
    return `session_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`;
  }, []);

  // 会话ID状态管理
  const [currentSessionId, setCurrentSessionId] = useState<string>(
    conversationId || ""
  );

  // 初始化或重置会话ID
  useEffect(() => {
    if (!conversationId && !currentSessionId) {
      const newSessionId = generateSessionId();
      setCurrentSessionId(newSessionId);
      console.log("生成新的会话ID:", newSessionId);
    } else if (conversationId && conversationId !== currentSessionId) {
      setCurrentSessionId(conversationId);
      console.log("更新会话ID:", conversationId);
    }
  }, [conversationId, currentSessionId, generateSessionId]);

  // 消息状态管理 - 关键修复：正确响应 initialMessages 变化
  const [messages, setMessages] = useState<CustomMessage[]>([]);

  // 当 initialMessages 或 conversationId 变化时，更新消息状态
  useEffect(() => {
    console.log("ChatWindow: initialMessages 发生变化", {
      conversationId,
      initialMessagesCount: initialMessages.length,
      initialMessages,
    });

    const convertedMessages = initialMessages.map((msg) => ({
      id: msg.id,
      role: msg.role as "user" | "assistant",
      content: msg.content,
    }));

    setMessages(convertedMessages);
    console.log("ChatWindow: 更新消息状态", convertedMessages);
  }, [initialMessages, conversationId]);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  const handleSendMessage = useCallback(
    async (content: string) => {
      if (!user) {
        console.error("用户未登录");
        const errorMessage: CustomMessage = {
          id: (Date.now() + 1).toString(),
          role: "assistant",
          content: "请先登录后再使用聊天功能。",
        };
        setMessages([...messages, errorMessage]);
        return;
      }

      // 确保会话ID存在
      const effectiveSessionId = currentSessionId || generateSessionId();
      if (!currentSessionId) {
        setCurrentSessionId(effectiveSessionId);
        console.log("发送消息前生成会话ID:", effectiveSessionId);
      }

      if (messages.length === 0 && onFirstMessage) {
        onFirstMessage(content);
      }

      const userMessage: CustomMessage = {
        id: Date.now().toString(),
        role: "user",
        content,
      };

      const newMessages = [...messages, userMessage];
      setMessages(newMessages);

      if (onMessageAdded) {
        onMessageAdded({ role: "user", content });
      }

      setIsLoading(true);

      try {
        const requestData: QARequest = {
          userId: parseInt(user.id),
          question: content,
          sessionId: effectiveSessionId,
        };

        console.log("发送QA请求，会话ID:", effectiveSessionId);
        console.log("QaRequestData:", requestData);
        console.log("API_BASE_URL@chat-window:", API_BASE_URL);

        const response = await fetch(`${API_BASE_URL}/api/qa/ask`, {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            ...(token && { Authorization: `Bearer ${token}` }),
          },
          body: JSON.stringify(requestData),
        });

        console.log("响应状态:", response.status, response.statusText);

        if (!response.ok) {
          const errorText = await response.text();
          console.error("请求失败详情:", {
            status: response.status,
            statusText: response.statusText,
            errorResponse: errorText,
          });
          throw new Error(
            `HTTP ${response.status}: ${response.statusText} - ${errorText}`
          );
        }

        const answer = await response.text();
        console.log("成功响应:", answer);

        const assistantMessage: CustomMessage = {
          id: (Date.now() + 1).toString(),
          role: "assistant",
          content: answer,
        };

        const updatedMessages = [...newMessages, assistantMessage];
        setMessages(updatedMessages);

        if (onMessageAdded) {
          onMessageAdded({ role: "assistant", content: answer });
        }
      } catch (error) {
        console.error("发送消息失败:", error);

        const errorMessage: CustomMessage = {
          id: (Date.now() + 1).toString(),
          role: "assistant",
          content: `抱歉，发送消息时出现错误：${
            error instanceof Error ? error.message : "未知错误"
          }`,
        };
        setMessages([...newMessages, errorMessage]);
      } finally {
        setIsLoading(false);
      }
    },
    [
      messages,
      currentSessionId,
      conversationId,
      token,
      user,
      onFirstMessage,
      onMessageAdded,
      generateSessionId,
    ]
  );

  if (!user) {
    return (
      <div className="flex-1 flex flex-col min-h-0">
        <ChatHeader />
        <div className="flex-1 flex items-center justify-center min-h-0">
          <div className="text-center max-w-md mx-auto px-4">
            <div className="w-16 h-16 bg-primary/10 rounded-full flex items-center justify-center mx-auto mb-4">
              <Bot className="w-8 h-8 text-primary" />
            </div>
            <h1 className="text-2xl font-semibold mb-2">请先登录</h1>
            <p className="text-muted-foreground mb-6">
              登录后即可使用AI聊天助手功能
            </p>
            <Button
              onClick={() => {
                window.location.href = "/login";
              }}
            >
              前往登录
            </Button>
          </div>
        </div>
      </div>
    );
  }

  if (!conversationId && messages.length === 0) {
    return (
      <div className="flex-1 flex flex-col min-h-0">
        <ChatHeader />

        {/* 欢迎界面 */}
        <div className="flex-1 overflow-y-auto min-h-0">
          <div className="flex items-center justify-center min-h-full py-8">
            <div className="text-center max-w-md mx-auto px-4">
              <div className="w-16 h-16 bg-primary/10 rounded-full flex items-center justify-center mx-auto mb-4">
                <Bot className="w-8 h-8 text-primary" />
              </div>
              <h1 className="text-2xl font-semibold mb-2 text-balance">
                AI 聊天助手
              </h1>
              <p className="text-muted-foreground mb-6 text-pretty">
                我是您的智能助手，通过DeepSeek API为您服务。
                可以帮助您解答问题、提供建议或进行有趣的对话。
              </p>

              <div className="space-y-2">
                <p className="text-sm font-medium text-foreground mb-3">
                  试试这些问题：
                </p>
                <div className="grid gap-2">
                  {[
                    "帮我写一个简单的 Python 函数",
                    "推荐一些学习编程的资源",
                    "用 Markdown 格式介绍一下 React",
                  ].map((prompt, index) => (
                    <button
                      key={index}
                      onClick={() => handleSendMessage(prompt)}
                      className="text-left p-3 rounded-lg border border-border hover:bg-accent hover:text-accent-foreground transition-colors text-sm"
                    >
                      <Sparkles className="w-4 h-4 inline mr-2 text-primary" />
                      {prompt}
                    </button>
                  ))}
                </div>
              </div>
            </div>
          </div>
        </div>

        <ChatInput
          onSendMessage={handleSendMessage}
          disabled={isLoading}
          placeholder="开始新的对话..."
        />
      </div>
    );
  }

  return (
    <div className="flex-1 flex flex-col min-h-0">
      {/* 固定头部 */}
      <ChatHeader title={conversationTitle} />

      {/* 可滚动的消息区域 */}
      <div className="flex-1 overflow-y-auto p-4 space-y-4 min-h-0">
        {messages.length === 0 ? (
          <div className="text-center py-8 text-muted-foreground">
            <p>暂无消息</p>
          </div>
        ) : (
          messages.map((message) => (
            <MessageBubble
              key={message.id}
              role={message.role}
              content={message.content}
              timestamp={new Date()}
            />
          ))
        )}

        {isLoading && (
          <div className="flex gap-3 mb-4">
            <div className="flex-shrink-0 w-8 h-8 bg-card rounded-full flex items-center justify-center">
              <Bot className="w-4 h-4 text-card-foreground" />
            </div>
            <div className="bg-card text-card-foreground rounded-lg px-4 py-2 mr-12">
              <div className="flex items-center gap-1">
                <div className="w-2 h-2 bg-current rounded-full animate-bounce [animation-delay:-0.3s]"></div>
                <div className="w-2 h-2 bg-current rounded-full animate-bounce [animation-delay:-0.15s]"></div>
                <div className="w-2 h-2 bg-current rounded-full animate-bounce"></div>
              </div>
            </div>
          </div>
        )}

        <div ref={messagesEndRef} />
      </div>

      {/* 固定输入框 */}
      <ChatInput onSendMessage={handleSendMessage} disabled={isLoading} />
    </div>
  );
}
