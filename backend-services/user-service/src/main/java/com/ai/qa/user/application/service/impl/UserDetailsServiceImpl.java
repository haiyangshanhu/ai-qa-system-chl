package com.ai.qa.user.application.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ai.qa.user.domain.entity.User;
import com.ai.qa.user.domain.repository.UserRepository;

/**
 * 用户详情服务实现类
 * 实现Spring Security的UserDetailsService接口，用于用户认证
 * 负责从数据库加载用户信息并转换为Spring Security可识别的UserDetails对象
 *
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * 根据用户名加载用户详情
     * Spring Security认证过程中的核心方法，用于用户身份验证
     *
     * @param username 用户名
     * @return UserDetails Spring Security的用户详情对象
     * @throws UsernameNotFoundException 当用户不存在时抛出此异常
     * @apiNote 此方法在用户登录时被Spring Security自动调用
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 从数据库查询用户
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // 转换为Spring Security的UserDetails（可自定义权限）
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles("USER") // 简单起见，默认赋予USER角色
                .build();
    }
}