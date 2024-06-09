package com.example.sbbackend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {
    private static final String[] WHITE_LIST_URL =
            {"/api/v1/auth/login",
                    "/api/v1/auth/register/",
                    "/api/v1/auth/confirm",
                    "/api/v1/auth/test",
                    "/api/v1/users/forgotpassword",
                    "/api/v1/users/resetpassword",

            };
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final LogoutHandler logoutHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(req ->
                        req
                                .requestMatchers(WHITE_LIST_URL)
                                .permitAll()
                                .requestMatchers(HttpMethod.OPTIONS, "**").permitAll()
                                .requestMatchers( "/ws/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/v1/posts/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/v1/filters").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/v1/messages/").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/v1/mycvs").permitAll()
                                .requestMatchers(HttpMethod.GET, "/images/profiles/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/images/posts/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/images/mycvs/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/myfiles/**").permitAll()
                                .requestMatchers(HttpMethod.OPTIONS, "/myfiles/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/v1/posts/**").authenticated()
                                .requestMatchers(HttpMethod.PUT, "/api/v1/posts/").authenticated()
                                .requestMatchers(HttpMethod.PATCH, "/api/v1/posts/").authenticated()
                                .anyRequest()
                                .authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout ->
                        logout.logoutUrl("/api/v1/auth/logout")
                                .addLogoutHandler(logoutHandler)
                                .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext())
                );


        return http.build();

    }


}
