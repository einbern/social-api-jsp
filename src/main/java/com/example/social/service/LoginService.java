package com.example.social.service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;

import javax.transaction.Transactional;
import javax.xml.bind.DatatypeConverter;

import com.example.social.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Transactional
@Slf4j
public class LoginService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    private static String generateKey(final int keyLen) {

        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[keyLen/8];
        random.nextBytes(bytes);
        return DatatypeConverter.printHexBinary(bytes).toLowerCase();
    }

    public String login(String username, String password) {
        var user = userRepository.findByUsername(username);
        if (user == null) return null;
        
        if (user.getPassword().equals(password)) {
            // return new API Key
            var apiKey = generateKey(128);

            // save to redis
            log.info("save apiKey={}, user={}, to redis", apiKey, username);

            redisTemplate.opsForValue().set(apiKey, String.valueOf(user.getId()), Duration.ofHours(1));

            return apiKey;
        } else {
            return null;
        }
    }
}
