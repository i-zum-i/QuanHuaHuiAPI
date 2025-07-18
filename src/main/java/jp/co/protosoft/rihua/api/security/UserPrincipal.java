package jp.co.protosoft.rihua.api.security;

import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * Spring Security用のユーザープリンシパルクラス
 * 
 * <p>認証されたユーザーの情報を保持し、Spring Securityの認証・認可機能で使用されます。</p>
 * 
 * @author Rihua Development Team
 * @since 1.0.0
 */
@Data
@Builder
public class UserPrincipal implements UserDetails {

    private String id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String preferredLanguage;
    private boolean enabled;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private Collection<? extends GrantedAuthority> authorities;

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * フルネームを取得
     * 
     * @return フルネーム
     */
    public String getFullName() {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        } else if (firstName != null) {
            return firstName;
        } else if (lastName != null) {
            return lastName;
        }
        return email;
    }

    /**
     * 特定の権限を持っているかチェック
     * 
     * @param authority 権限名
     * @return 権限を持っている場合true
     */
    public boolean hasAuthority(String authority) {
        return authorities.stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(authority));
    }

    /**
     * 管理者権限を持っているかチェック
     * 
     * @return 管理者権限を持っている場合true
     */
    public boolean isAdmin() {
        return hasAuthority("ROLE_ADMIN");
    }

    /**
     * ビジネスユーザー権限を持っているかチェック
     * 
     * @return ビジネスユーザー権限を持っている場合true
     */
    public boolean isBusiness() {
        return hasAuthority("ROLE_BUSINESS") || isAdmin();
    }

    /**
     * 一般ユーザー権限を持っているかチェック
     * 
     * @return 一般ユーザー権限を持っている場合true
     */
    public boolean isUser() {
        return hasAuthority("ROLE_USER") || isBusiness() || isAdmin();
    }
}