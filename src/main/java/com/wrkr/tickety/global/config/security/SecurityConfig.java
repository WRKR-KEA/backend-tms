package com.wrkr.tickety.global.config.security;

import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.global.config.security.filter.JwtAuthenticationFilter;
import com.wrkr.tickety.global.config.security.handler.CustomAccessDeniedHandler;
import com.wrkr.tickety.global.config.security.handler.CustomAuthenticationEntryPoint;
import com.wrkr.tickety.global.config.security.jwt.JwtUtils;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtils jwtUtils;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    private static final String[] STATIC_RESOURCES = {
        "/resource/**",
        "/css/**",
        "/js/**",
        "/img/**",
        "/lib/**"
    };

    private static final String[] SWAGGER_ENDPOINTS = {
        "/v3/api-docs/**",
        "/swagger-resources/**",
        "/swagger-ui.html/**",
        "/swagger-ui/**",
        "/api/tickety-tms/**",
        "/backend/api/tickety-tms/**"
    };

    private static final String[] PUBLIC_API_ENDPOINTS = {
        "/api/auth/login",
        "/api/members/password/reissue"
    };

    @Bean
    protected SecurityFilterChain config(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(corsConfigurer -> corsConfigurer
                .configurationSource(corsConfigurationSource())
            )
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .logout(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(STATIC_RESOURCES).permitAll()
                .requestMatchers(SWAGGER_ENDPOINTS).permitAll()
                .requestMatchers(PUBLIC_API_ENDPOINTS).permitAll()
                .requestMatchers("/api/admin/**").hasAuthority(Role.ADMIN.name())
                .requestMatchers("/api/manager/**").hasAnyAuthority(Role.MANAGER.name(), Role.ADMIN.name())
                .requestMatchers("/api/user/**").hasAnyAuthority(Role.USER.name(), Role.MANAGER.name(), Role.ADMIN.name())
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(customAuthenticationEntryPoint)
                .accessDeniedHandler(customAccessDeniedHandler)
            )
            .addFilterBefore(new JwtAuthenticationFilter(jwtUtils), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",
            "http://localhost:8080",
            "http://172.16.211.116:3000",
            "http://172.16.211.53:8080"
        ));

        configuration.setAllowedHeaders(Arrays.asList("*"));

        configuration.setAllowedMethods(Arrays.asList(
            "GET",
            "POST",
            "DELETE",
            "PATCH",
            "OPTIONS"
        ));

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
