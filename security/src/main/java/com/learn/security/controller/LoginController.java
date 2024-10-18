package com.learn.security.controller;

import com.learn.security.dto.AuthResponseDto;
import com.learn.security.JwtGenerator;
import com.learn.security.dto.LoginDto;
import com.learn.security.entity.User;
import com.learn.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RequestMapping("/api/auth")
@RestController
public class LoginController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtGenerator jwtGenerator;

    @Autowired
    public LoginController(UserService userService, AuthenticationManager authenticationManager, JwtGenerator jwtGenerator) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtGenerator = jwtGenerator;
    }

    @PostMapping("/signup")
    public ResponseEntity<User> signup(@RequestBody LoginDto loginDto) {
        Optional<User> joinedUser = userService.join(loginDto);
        return new ResponseEntity<>(joinedUser.get(), HttpStatus.CREATED);
    }

    // 테스트를 위해 만든 임시 로그인
    @PostMapping("/signin")
    @ResponseBody
    public AuthResponseDto signIn(@RequestBody User user) {
        /*
        인증 절차를 거친 후에 토큰을 생성하여 반환한다.
        토큰 생성을 하려면 사용자가 보낸 username과 password가 유효한지 검증(authenticate)해야하기 때문이다.
         */
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        user.getPassword()
                ));
        // 단순 검증만 하면 모를까 왜 Authentication을 설정하는지 모르겠다.
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtGenerator.generateToken(authentication);
        return new AuthResponseDto(token);
    }
}
