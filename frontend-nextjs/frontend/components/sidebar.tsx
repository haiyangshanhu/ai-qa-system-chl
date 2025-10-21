"use client";

import { useState } from "react";
import { Button } from "@/components/ui/button";
import { ScrollArea } from "@/components/ui/scroll-area";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import {
  Plus,
  MessageSquare,
  MoreHorizontal,
  Trash2,
  Edit3,
  User,
  LogOut,
} from "lucide-react";
import { cn } from "@/lib/utils";
import type { Conversation, HistoryConversation } from "@/types/chat";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
  DropdownMenuSeparator,
} from "@/components/ui/dropdown-menu";
import { useAuth } from "@/contexts/auth-context";
import { UserProfileModal } from "@/components/user-profile-modal";
import { chatAPI } from "@/lib/chat-api";

interface SidebarProps {
  conversations: Conversation[];
  activeConversationId?: string;
  onNewChat: () => void;
  onSelectConversation: (conversationId: string) => void;
  onDeleteConversation: (conversationId: string) => void;
  onRenameConversation: (conversationId: string, newTitle: string) => void;
  onLoadConversations: (conversations: HistoryConversation[]) => void;
  isLoadingHistory?: boolean;
}

export function Sidebar({
  conversations,
  activeConversationId,
  onNewChat,
  onSelectConversation,
  onDeleteConversation,
  onRenameConversation,
  onLoadConversations,
  isLoadingHistory = false,
}: SidebarProps) {
  const [editingId, setEditingId] = useState<string | null>(null);
  const [editingTitle, setEditingTitle] = useState("");
  const [showProfileModal, setShowProfileModal] = useState(false);
  const { user, token, logout } = useAuth();

  const handleStartEdit = (conversation: Conversation) => {
    setEditingId(conversation.id);
    setEditingTitle(conversation.title);
  };

  const handleSaveEdit = (conversationId: string) => {
    if (editingTitle.trim()) {
      onRenameConversation(conversationId, editingTitle.trim());
    }
    setEditingId(null);
    setEditingTitle("");
  };

  const handleCancelEdit = () => {
    setEditingId(null);
    setEditingTitle("");
  };

  const handleDeleteConversation = async (conversationId: string) => {
    if (!token) return;

    try {
      await chatAPI.deleteConversation(conversationId, token);
      onDeleteConversation(conversationId);
    } catch (error) {
      console.error("Failed to delete conversation:", error);
    }
  };

  // 调试：检查重复的对话 ID
  console.log("Sidebar conversations:", conversations);
  const duplicateIds = conversations
    .map((c) => c.id)
    .filter((id, index, arr) => arr.indexOf(id) !== index);
  if (duplicateIds.length > 0) {
    console.warn("Duplicate conversation IDs found:", duplicateIds);
  }

  return (
    <>
      <div className="w-80 bg-sidebar border-r border-sidebar-border flex flex-col h-screen">
        {/* Header - 固定高度 */}
        <div className="p-4 border-b border-sidebar-border flex-shrink-0">
          <div className="flex items-center gap-3 mb-4">
            <Avatar className="h-8 w-8">
              <AvatarImage
                src={user?.avatar || "/placeholder.svg"}
                alt={user?.username}
              />
              <AvatarFallback className="bg-sidebar-accent text-sidebar-accent-foreground">
                {user?.username?.charAt(0).toUpperCase() || "U"}
              </AvatarFallback>
            </Avatar>
            <div className="flex-1 min-w-0">
              <p className="text-sm font-medium text-sidebar-foreground truncate">
                {user?.username || "用户"}
              </p>
              <p className="text-xs text-sidebar-foreground/60 truncate">
                {user?.email || ""}
              </p>
            </div>
            <DropdownMenu>
              <DropdownMenuTrigger asChild>
                <Button
                  variant="ghost"
                  size="sm"
                  className="h-8 w-8 p-0 text-sidebar-foreground hover:bg-sidebar-accent"
                >
                  <MoreHorizontal className="w-4 h-4" />
                </Button>
              </DropdownMenuTrigger>
              <DropdownMenuContent align="end" className="w-48">
                <DropdownMenuItem onClick={() => setShowProfileModal(true)}>
                  <User className="w-4 h-4 mr-2" />
                  个人资料
                </DropdownMenuItem>
                <DropdownMenuSeparator />
                <DropdownMenuItem
                  onClick={logout}
                  className="text-destructive focus:text-destructive"
                >
                  <LogOut className="w-4 h-4 mr-2" />
                  退出登录
                </DropdownMenuItem>
              </DropdownMenuContent>
            </DropdownMenu>
          </div>

          <Button
            onClick={onNewChat}
            className="w-full bg-sidebar-accent hover:bg-sidebar-accent/80 text-sidebar-accent-foreground"
            size="lg"
            disabled={isLoadingHistory}
          >
            <Plus className="w-4 h-4 mr-2" />
            新建对话
          </Button>
        </div>

        {/* Conversations List - 可滚动区域 */}
        <ScrollArea className="flex-1 min-h-0">
          <div className="p-2">
            {isLoadingHistory ? (
              <div className="text-center py-8 text-sidebar-foreground/60">
                <p className="text-sm">加载对话历史中...</p>
              </div>
            ) : conversations.length === 0 ? (
              <div className="text-center py-8 text-sidebar-foreground/60">
                <MessageSquare className="w-8 h-8 mx-auto mb-2 opacity-50" />
                <p className="text-sm">暂无对话历史</p>
              </div>
            ) : (
              <div className="space-y-1">
                {conversations.map((conversation) => {
                  // 检查重复 ID
                  const isDuplicate =
                    conversations.filter((c) => c.id === conversation.id)
                      .length > 1;

                  return (
                    <div
                      key={conversation.id}
                      className={cn(
                        "group relative flex items-center gap-2 p-3 rounded-lg cursor-pointer transition-colors",
                        "hover:bg-sidebar-accent/50",
                        activeConversationId === conversation.id
                          ? "bg-sidebar-accent text-sidebar-accent-foreground"
                          : "text-sidebar-foreground",
                        isDuplicate ? "border-2 border-red-500" : ""
                      )}
                      onClick={() => onSelectConversation(conversation.id)}
                    >
                      <MessageSquare className="w-4 h-4 flex-shrink-0" />

                      {editingId === conversation.id ? (
                        <input
                          type="text"
                          value={editingTitle}
                          onChange={(e) => setEditingTitle(e.target.value)}
                          onBlur={() => handleSaveEdit(conversation.id)}
                          onKeyDown={(e) => {
                            if (e.key === "Enter") {
                              handleSaveEdit(conversation.id);
                            } else if (e.key === "Escape") {
                              handleCancelEdit();
                            }
                          }}
                          className="flex-1 bg-transparent border-none outline-none text-sm"
                          autoFocus
                          onClick={(e) => e.stopPropagation()}
                        />
                      ) : (
                        <span
                          className="flex-1 text-sm truncate"
                          title={conversation.title}
                        >
                          {conversation.title}
                          {isDuplicate && " (重复)"}
                        </span>
                      )}

                      {editingId !== conversation.id && (
                        <DropdownMenu>
                          <DropdownMenuTrigger asChild>
                            <Button
                              variant="ghost"
                              size="sm"
                              className="opacity-0 group-hover:opacity-100 h-6 w-6 p-0 text-sidebar-foreground hover:bg-sidebar-accent"
                              onClick={(e) => e.stopPropagation()}
                            >
                              <MoreHorizontal className="w-3 h-3" />
                            </Button>
                          </DropdownMenuTrigger>
                          <DropdownMenuContent align="end" className="w-32">
                            <DropdownMenuItem
                              onClick={() => handleStartEdit(conversation)}
                            >
                              <Edit3 className="w-3 h-3 mr-2" />
                              重命名
                            </DropdownMenuItem>
                            <DropdownMenuItem
                              onClick={() =>
                                handleDeleteConversation(conversation.id)
                              }
                              className="text-destructive focus:text-destructive"
                            >
                              <Trash2 className="w-3 h-3 mr-2" />
                              删除
                            </DropdownMenuItem>
                          </DropdownMenuContent>
                        </DropdownMenu>
                      )}
                    </div>
                  );
                })}
              </div>
            )}
          </div>
        </ScrollArea>

        {/* Footer - 固定高度 */}
        {/* 修改这里：移除 border-t，添加 border-b 来与右侧输入框对齐 */}
        <div className="p-4 border-b border-sidebar-border flex-shrink-0">
          <div className="text-xs text-sidebar-foreground/60 text-center">
            AI 聊天助手 v1.0
          </div>
        </div>
      </div>

      {/* User Profile Modal */}
      <UserProfileModal
        open={showProfileModal}
        onOpenChange={setShowProfileModal}
      />
    </>
  );
}
