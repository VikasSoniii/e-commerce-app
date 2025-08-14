package org.sds.sonizone.payment.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)  //API Security- USE FOR METHOD LEVEL SECURITY - Like Has Role, Has Authority
public class WebSecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .authorizeHttpRequests(auth -> auth
                        //starts: Added for h2-console access in Dev Region Only
                        .requestMatchers("/h2-console/**").permitAll() // allow H2 console
                        //ends: Added for h2-console access in Dev Region Only
                        .anyRequest().authenticated()  // all endpoints need authenticated requests
                )
                //starts: Added for h2-console access in Dev Region Only
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/h2-console/**") // disable CSRF for H2 console
                )
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin()) // allow frames for H2 console
                )
                //ends: Added for h2-console access in Dev Region Only
                .oauth2ResourceServer(resourceServer ->
                        resourceServer.jwt(Customizer.withDefaults()));  // validate Bearer JWT tokens
        return httpSecurity.build();
    }
}