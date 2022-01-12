package com.example.social.controller;

import com.example.social.service.LoginService;
import com.example.social.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/login")
@Slf4j
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping
    public ResponseEntity<Object> login(
            @RequestParam("username") String username,
            @RequestParam("password") String password) {

        log.info("username={}, password={}", username, password);
        String apiKey = loginService.login(username, password);
        log.info("apiKey={}", apiKey);

        if (apiKey == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        return ResponseEntity.ok(apiKey);
    }
}
