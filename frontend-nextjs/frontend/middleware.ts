import { NextResponse } from "next/server";
import type { NextRequest } from "next/server";

export function middleware(request: NextRequest) {
  // Get the pathname of the request (e.g. /, /auth, /api/chat)
  const { pathname } = request.nextUrl;

  // Check if this is an API route that needs authentication
  if (pathname.startsWith("/api/qa/ask")) {
    const token = request.headers.get("authorization")?.replace("Bearer ", "");

    //todo token 是否合法的校验
    if (!token) {
      return NextResponse.json(
        { error: "Authentication required" },
        { status: 401 }
      );
    }
  }

  return NextResponse.next();
}

export const config = {
  matcher: [
    /*
     * Match all request paths except for the ones starting with:
     * - _next/static (static files)
     * - _next/image (image optimization files)
     * - favicon.ico (favicon file)
     */
    "/((?!_next/static|_next/image|favicon.ico).*)",
  ],
};
