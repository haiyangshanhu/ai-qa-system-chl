import { MessageSquare } from "lucide-react";

interface ChatHeaderProps {
  title?: string;
}

export function ChatHeader({ title }: ChatHeaderProps) {
  return (
    <div className="border-b bg-background px-4 py-3">
      <div className="flex items-center gap-2">
        <MessageSquare className="w-5 h-5 text-primary" />
        <h1 className="font-semibold text-foreground">
          {title || "AI 聊天助手"}
        </h1>
      </div>
    </div>
  );
}
