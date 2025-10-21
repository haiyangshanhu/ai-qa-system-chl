package com.ai.qa.user.infrastructure.config;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ai.qa.user.common.JwtUtil;

/**
 * JWT认证过滤器
 * 继承OncePerRequestFilter确保每次请求只过滤一次
 * 用于处理JWT令牌的认证流程
 *
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // JWT工具类，用于生成和解析JWT令牌
    @Autowired
    private JwtUtil jwtUtil;

    // 用户详情服务，用于根据用户名加载用户信息
    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * 核心过滤方法，处理每个HTTP请求
     * 
     * @param request     HTTP请求对象
     * @param response    HTTP响应对象
     * @param filterChain 过滤器链，用于继续处理请求
     * @throws ServletException 可能抛出的Servlet异常
     * @throws IOException      可能抛出的IO异常
     */
    @SuppressWarnings("null")
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        // String path = request.getRequestURI();
        //
        // System.out.println("JwtFilter: path=" + path + " method=" +
        // request.getMethod());

        // 允许匿名访问的接口直接放行
        // if ("/api/user/login".equals(path) ||
        // "/api/user/register".equals(path) ||
        // request.getMethod().equalsIgnoreCase("OPTIONS")) {
        // filterChain.doFilter(request, response);
        // return;
        // }

        String path = request.getServletPath();
        System.out.println("JwtFilter: servletPath=" + path + " method=" + request.getMethod());

        // 放行 login 和 register，无论前面加不加前缀
        if (path.endsWith("/login") ||
                path.endsWith("/register") ||
                request.getMethod().equalsIgnoreCase("OPTIONS")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            try {
                username = jwtUtil.getUsernameFromToken(token);
            } catch (Exception e) {
                logger.error("Invalid JWT token: {}", e);
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtUtil.validateToken(token)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}