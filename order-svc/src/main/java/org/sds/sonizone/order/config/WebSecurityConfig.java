package org.sds.sonizone.order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)  //API Security- USE FOR METHOD LEVEL SECURITY - Like Has Role, Has Authority
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated()  // all endpoints need authenticated requests
                )
                .oauth2ResourceServer(resourceServer ->
                        resourceServer.jwt(Customizer.withDefaults()));  // validate Bearer JWT tokens
        return httpSecurity.build();


        //BELOW CODE ADDED FOR SWAGGER UI - DEV Region
        /*httpSecurity
                .authorizeHttpRequests(auth -> auth
                        //starts: Added for swagger ui access in Dev Region Only
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll() // allow Swagger UI
                        //ends: Added for swagger ui access in Dev Region Only
                        .anyRequest().authenticated()  // all endpoints need authenticated requests
                )
                //starts: Added for Swagger UI access in Dev Region Only
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/swagger-ui/**", "/v3/api-docs/**") // disable CSRF for Swagger UI
                )
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin()) // allow frames for Swagger UI
                )
                //ends: Added for Swagger UI access in Dev Region Only
                .oauth2ResourceServer(resourceServer ->
                        resourceServer.jwt(Customizer.withDefaults()));  // validate Bearer JWT tokens
        return httpSecurity.build();*/
    }
}