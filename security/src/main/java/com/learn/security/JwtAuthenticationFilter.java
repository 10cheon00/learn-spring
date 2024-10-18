package com.learn.security;

import com.learn.security.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtGenerator jwtGenerator;

    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        // 토큰이 요청에 담겨있고 유효한지 검사
        String token = getJwtFromRequest(request);
        if (StringUtils.hasText(token) && jwtGenerator.validateToken(token)) {
            /*
            jwt 필터를 통해 SecurityContext의 authentication을 설정
            이걸 하는 이유는 다음 필터들이 활용할 수 있기 때문일까?
            로그인 요청이 아닌 상황에서는 아이디랑 비밀번호가 없으므로 어느 부분에서 Authentication을 요구하는 필터가 실행되기 때문에 에러를 낸다.
            그래서 어쩔 수 없이 미리 UserDetails를 갖고 온 후 직접 Context에 저장하는 처리를 해야한다.
             */
            String username = jwtGenerator.getUsernameFromJWT(token);
            UserDetails userDetails = userService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        // 사용자가 request의 헤더에 넣은 토큰 갖고 오기
        String bearerToken = request.getHeader("Authorization");
        // 'bearer '를 제거하고 암호화된 문자열만 갖고 오기
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("bearer ")) {
            return bearerToken.substring(7, bearerToken.length());
        }
        // 예외사항은 null반환해서 StringUtils.hasText()로 검사
        return null;
    }
}
