/**
 * 用户接口定义
 * 描述系统用户的基本信息结构
 */
export interface User {
  id: string; // 用户唯一标识符
  username: string; // 用户名，用于登录和显示
  nickname: string; // 用户昵称，用于登录和显示
  email: string; // 用户邮箱地址
  avatar?: string; // 用户头像URL（可选）
}

/**
 * 登录请求接口
 * 用于用户登录时提交的凭证信息
 */
export interface LoginRequest {
  username: string; // 登录用户名
  password: string; // 登录密码
}

/**
 * 注册请求接口
 * 用于用户注册时提交的信息
 */
export interface RegisterRequest {
  username: string; // 注册用户名
  nickname: string; // 注册用户昵称
  email: string; // 注册邮箱
  password: string; // 注册密码
  confirmPassword: string; // 确认密码
}

/**
 * 认证响应接口
 * 登录或注册成功后服务器返回的数据结构
 */
export interface AuthResponse {
  token: string; // JWT认证令牌，用于后续请求的身份验证
  user: User; // 用户信息对象
}

/**
 * 认证上下文接口
 * 定义React认证上下文提供的功能和方法
 */
export interface AuthContextType {
  user: User | null; // 当前登录用户信息，未登录时为null
  token: string | null; // 当前认证令牌，未登录时为null
  login: (credentials: LoginRequest) => Promise<void>; // 登录方法，接收登录凭证
  register: (userData: RegisterRequest) => Promise<void>; // 注册方法，接收注册信息
  logout: () => void; // 退出登录方法，清除认证状态
  isLoading: boolean; // 加载状态标识，表示认证操作是否正在进行中
}
