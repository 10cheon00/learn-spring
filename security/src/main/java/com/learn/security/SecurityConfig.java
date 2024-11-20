package com.learn.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@EnableMethodSecurity
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity httpSecurity,
            AuthenticationSuccessHandler authenticationSuccessHandler
    ) throws Exception {
        httpSecurity
                .authorizeHttpRequests(
                        request -> {
                            request.requestMatchers("/oauth2/**").permitAll()
                                    .requestMatchers("/login/oauth2/code/**").permitAll()
                                    .anyRequest().authenticated();
                        }
                )

                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .oauth2Login(oAuth2LoginConfigurer ->
                        oAuth2LoginConfigurer
                                // 여기서는 authorization_code가 리다이렉트를 통해 전달되므로 그것을 수신할 컨트롤러를 지정합니다
                                .authorizationEndpoint(authorize -> authorize
                                        .baseUri("/oauth2/authorize"))
                                // authorization_code를 통해 액세스 토큰을 획득하는 클라이언트를 지정합니다.
                                .tokenEndpoint(tokenEndpointConfig -> tokenEndpointConfig.accessTokenResponseClient(
                                        accessTokenResponseClient()
                                ))
                                // 액세스 토큰을 사용하여 사용자 정보를 요청하는 역할을 하는데, 서비스 클래스를 등록해서,
                                // 사용자 계정 정보를 DB에 저장하거나 업데이트하는 등의 작업을 할 수 있습니다.
                                .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
                                        .userService(myService()))
                                // 최종적으로 Oauth2과정이 모두 성공하면 이 핸들러가 호출됩니다.
                                .successHandler(authenticationSuccessHandler)
                );

        return httpSecurity.build();
    }

    private OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient() {
        return new DefaultAuthorizationCodeTokenResponseClient();
    }

    private MyService myService() {
        return new MyService();
    }
}