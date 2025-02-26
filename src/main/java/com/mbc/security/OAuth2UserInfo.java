package com.mbc.security;

public interface OAuth2UserInfo {

    String getProvider();

    String getProviderId();

    String getEmail();

    String getName();
}
