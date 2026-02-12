package dev.jade.todolist.security;

import dev.jade.todolist.user.entity.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CustomUserDetails implements UserDetails {

    private final Long userId;
    private final String email;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    @Builder.Default
    private final boolean enabled = true;

    @Builder.Default
    private final boolean accountNonExpired = true;

    @Builder.Default
    private final boolean accountNonLocked = true;

    @Builder.Default
    private final boolean credentialsNonExpired = true;

    /**
     * Domain → Security transformation
     * This is the ONLY supported creation path.
     */
    public static CustomUserDetails from(User user) {
        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_USER")
        );

        return CustomUserDetails.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .password(user.getPassword())
                .authorities(authorities)
                .build();
    }

    @Override
    public String getUsername() {
        return email; // Email is the username
    }
}
