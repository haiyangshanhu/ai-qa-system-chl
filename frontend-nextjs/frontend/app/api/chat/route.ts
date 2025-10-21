import { google } from "@ai-sdk/google"
import { convertToModelMessages, streamText, type UIMessage } from "ai"

export const maxDuration = 30

process.env.sss="ssss"

export async function POST(req: Request) {
  try {
    const { messages }: { messages: UIMessage[] } = await req.json()

    if (!messages || !Array.isArray(messages) || messages.length === 0) {
      return new Response("Messages are required", { status: 400 })
    }

    const prompt = convertToModelMessages(messages)

    const result = streamText({
      model: google("gemini-1.5-pro"),
      messages: prompt,
      abortSignal: req.signal,
      // maxTokens: 2000,
      temperature: 0.7,
    })

    return result.toUIMessageStreamResponse({
      onFinish: async ({ isAborted }) => {
        if (isAborted) {
          console.log("Chat request aborted")
        }
      },
    })
  } catch (error) {
    console.error("Chat API error:", error)
    return new Response("Internal server error", { status: 500 })
  }
}
