package com.example.social.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.social.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ApiKeyFilter extends OncePerRequestFilter {

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    UserService userService;

    // @Override
    // protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
    //     var uri = request.getRequestURI();
    //     log.info("requestURI={}", uri);

    //     if (uri.equals("/login") || uri.startsWith("/login/"))
    //         return true;
    //     if (uri.equals("/rest") || uri.startsWith("/rest/"))
    //         return true;
    //     if (uri.equals("/demo") || uri.startsWith("/demo/"))
    //         return true;

    //     return false;
    // }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authorization = request.getHeader("Authorization");
        log.info("Authorization={}", authorization);

        if (authorization != null && authorization.startsWith("Bearer ")) {
            var apiKey = authorization.substring(7);
            log.info("apiKey={}", apiKey);

            // check api key
            var valueObject = redisTemplate
                    .opsForValue()
                    .get(apiKey);

            if (valueObject == null) {
                response.sendError(401);
                return;
            }

            var userId = Long.valueOf(valueObject.toString());
            log.info("userId={}", userId);

            var user = userService.getUser(userId);
            if (user == null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            request.setAttribute("userId", userId);
            request.setAttribute("username", user.getUsername());
        }

        filterChain.doFilter(request, response);

        // if (authorization != null && authorization.startsWith("Bearer ")) {
        // String apiKey = authorization.substring(7);
        // if (!"1234".equals(apiKey)) {
        // // api key is invalid
        // response.sendError(401);
        // return;
        // }

        // //api key passed

        // } else {
        // // no api key
        // response.sendError(401);
        // return;
        // }

        // // filter chain
        // filterChain.doFilter(request, response);

    }

}
