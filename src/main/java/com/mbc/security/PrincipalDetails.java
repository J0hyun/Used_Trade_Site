package com.mbc.security;

import com.mbc.entity.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class PrincipalDetails implements UserDetails, OAuth2User {

    private final Member member;
    private final Map<String, Object> attributes;
    private final boolean isOAuth2User;

    // Constructor for normal login
    public PrincipalDetails(Member member) {
        this.isOAuth2User = false;
        this.member = member;
        this.attributes = null; // No attributes for non-OAuth login
    }

    // Constructor for OAuth2 login
    public PrincipalDetails(Member member, Map<String, Object> attributes) {
        this.isOAuth2User = true;
        this.member = member;
        this.attributes = attributes;
    }

    public Member getMember() {
        return member;
    }

    public boolean isOAuth2User() {
        return isOAuth2User;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return member.getName();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 단일 권한만 부여하도록 수정
        // "ROLE_" 접두어를 붙여 권한을 생성
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + member.getRole().name()));
    }
    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
