import { cn } from "@/lib/utils"
import { Bot, User } from "lucide-react"
import { MarkdownContent } from "./markdown-content"

interface MessageBubbleProps {
  role: "user" | "assistant"
  content: string
  timestamp?: Date
}

export function MessageBubble({ role, content, timestamp }: MessageBubbleProps) {
  const isUser = role === "user"

  return (
    <div className={cn("flex gap-3 mb-4", isUser ? "justify-end" : "justify-start")}>
      {!isUser && (
        <div className="flex-shrink-0 w-8 h-8 bg-card rounded-full flex items-center justify-center">
          <Bot className="w-4 h-4 text-card-foreground" />
        </div>
      )}

      <div
        className={cn(
          "max-w-[80%] rounded-lg px-4 py-3 text-sm",
          isUser ? "bg-primary text-primary-foreground ml-12" : "bg-card text-card-foreground mr-12",
        )}
      >
        {isUser ? (
          <div className="whitespace-pre-wrap break-words leading-relaxed">{content}</div>
        ) : (
          <MarkdownContent content={content} />
        )}

        {timestamp && (
          <div className={cn("text-xs mt-2 opacity-70", isUser ? "text-primary-foreground" : "text-muted-foreground")}>
            {timestamp.toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" })}
          </div>
        )}
      </div>

      {isUser && (
        <div className="flex-shrink-0 w-8 h-8 bg-primary rounded-full flex items-center justify-center">
          <User className="w-4 h-4 text-primary-foreground" />
        </div>
      )}
    </div>
  )
}
