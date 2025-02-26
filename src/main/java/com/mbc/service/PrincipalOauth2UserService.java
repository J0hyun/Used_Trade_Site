package com.mbc.service;

import com.mbc.constant.MemberStatus;
import com.mbc.constant.Role;
import com.mbc.entity.Member;
import com.mbc.repository.MemberRepository;
import com.mbc.security.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Log4j2
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        OAuth2UserInfo oAuth2UserInfo = null;
        if (userRequest.getClientRegistration().getRegistrationId().equals("google")) {
            System.out.println("구글 로그인 요청");
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
        } else if(userRequest.getClientRegistration().getRegistrationId().equals("naver")){
            System.out.println("네이버 로그인 요청");
            oAuth2UserInfo = new NaverUserInfo((Map) oAuth2User.getAttributes().get("response"));
        } else if (userRequest.getClientRegistration().getRegistrationId().equals("kakao")){
            System.out.println("카카오 로그인 요청");
            oAuth2UserInfo = new KakaoUserInfo(oAuth2User.getAttributes());
        } else {
            System.out.println("우리는 구글과 네이버와 카카오만 지원합니다.");
        }

        String provider = oAuth2UserInfo.getProvider(); // google
        String providerId = oAuth2UserInfo.getProviderId();
        String username = provider + "_" + providerId; // google_10021320120
        String password = passwordEncoder.encode("겟인데어");
        String email = oAuth2UserInfo.getEmail();

        Member userEntity = memberRepository.findByname(username);

        if (userEntity == null) {
            System.out.println("OAuth2 로그인이 최초입니다.");
            userEntity = Member.builder()
                    .name(username)
                    .password(password)
                    .email(email)
                    .role(Role.USER)
                    .status(MemberStatus.ACTIVE)
                    .provider(provider)
                    .providerId(providerId)
                    .build();
            memberRepository.save(userEntity);
        } else {
            System.out.println("OAuth2 로그인을 이미 한적이 있습니다. 당신은 자동회원가입이 되어 있습니다.");
        }

        // 회원 가입을 강제로 진행해볼 예정
        return new PrincipalDetails(userEntity, oAuth2User.getAttributes());
    }




}
