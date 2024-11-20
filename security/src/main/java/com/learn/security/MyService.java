package com.learn.security;

import io.netty.handler.codec.http.HttpHeaderValues;
import lombok.Getter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

public class MyService extends DefaultOAuth2UserService {
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 사용자 계정 정보
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, String> attribute = (Map<String, String>) oAuth2User.getAttributes().get("response");
        OAuth2AccessToken oAuth2AccessToken = userRequest.getAccessToken();
        // 액세스 토큰
        String accessToken = oAuth2AccessToken.getTokenValue();

        return oAuth2User;
    }
}
