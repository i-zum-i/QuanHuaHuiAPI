package com.rihua.api.service;

import com.rihua.api.domain.User;
import com.rihua.api.repository.UserRepository;
import com.rihua.api.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * カスタムUserDetailsService実装
 * 
 * <p>Spring Securityの認証で使用するユーザー詳細情報を提供します。</p>
 * 
 * @author Rihua Development Team
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Loading user by email: {}", email);
        
        User user = userRepository.findByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> {
                    log.warn("User not found with email: {}", email);
                    return new UsernameNotFoundException("ユーザーが見つかりません: " + email);
                });

        return createUserPrincipal(user);
    }

    /**
     * ユーザーIDでUserDetailsを取得
     * 
     * @param userId ユーザーID
     * @return UserDetails
     * @throws UsernameNotFoundException ユーザーが見つからない場合
     */
    @Transactional(readOnly = true)
    public UserDetails loadUserById(String userId) throws UsernameNotFoundException {
        log.debug("Loading user by ID: {}", userId);
        
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> {
                    log.warn("User not found with ID: {}", userId);
                    return new UsernameNotFoundException("ユーザーが見つかりません: " + userId);
                });

        return createUserPrincipal(user);
    }

    /**
     * UserエンティティからUserPrincipalを作成
     * 
     * @param user Userエンティティ
     * @return UserPrincipal
     */
    private UserPrincipal createUserPrincipal(User user) {
        // ユーザーの権限を取得
        List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toList());

        return UserPrincipal.builder()
                .id(user.getId().toString())
                .email(user.getEmail())
                .password(user.getPasswordHash())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .preferredLanguage(user.getPreferredLanguage())
                .enabled(user.getStatus().isEnabled())
                .accountNonExpired(true)
                .accountNonLocked(!user.getStatus().isLocked())
                .credentialsNonExpired(true)
                .authorities(authorities)
                .build();
    }
}