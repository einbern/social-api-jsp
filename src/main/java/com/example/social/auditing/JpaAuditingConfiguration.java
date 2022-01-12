package com.example.social.auditing;

import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaAuditingConfiguration {
 
    @Bean
    public AuditorAware<String> auditorProvider() {

        return () -> {
            var username = (String) RequestContextHolder.currentRequestAttributes()
            .getAttribute("username", RequestAttributes.SCOPE_REQUEST);

            return Optional.ofNullable(username);
        };
    }
}
