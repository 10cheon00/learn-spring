package com.learn.security;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/test")
@RestController
public class TestController {
    @GetMapping("")
    @ResponseBody
    public String test() {
        return "You requested with Jwt token.";
    }
}
