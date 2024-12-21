package com.shnordiq.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final SecurityProperties securityProperties;
    private final DataSource dataSource;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Removing csrf as we use JWT
     * Allowing unauthorized view of Swagger
     * Forming logging page by giving everyone permit
     * Forming logout when pushed or time out to redirect on login screen
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/v1/register").permitAll()
                        .requestMatchers("/registration.html", "/css/**", "/js/**").permitAll()
                        .anyRequest().authenticated())

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .formLogin(formLogin -> formLogin
                        .defaultSuccessUrl(securityProperties.getOauth2().getDefaultSuccessPage(), true)
                        .failureUrl("/v1/auth/login/form?error=true")
                        .permitAll())

                .logout(logout -> logout
                        .logoutSuccessUrl("/v1/home")
                        .permitAll())

                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl(securityProperties.getOauth2().getDefaultSuccessPage(), true)
                        .failureUrl("/v1/auth/login/oauth2?error=true"));

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /**
     * Getting users first, then their roles, then encoding password
     */
    @Bean
    public AuthenticationManager configureGlobal(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder managerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        managerBuilder
                .jdbcAuthentication()
                .dataSource(dataSource)
                .usersByUsernameQuery(
                        "SELECT email, password, enabled " +
                                "FROM users " +
                                "WHERE email = ?")
                .authoritiesByUsernameQuery(
                        "SELECT u.email, a.role " +
                                "FROM users u " +
                                "JOIN authorities a ON u.user_id = a.user_id " +
                                "WHERE u.email = ?")
                .passwordEncoder(passwordEncoder());

        return managerBuilder.build();
    }

    /**
     * Simple password Encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}