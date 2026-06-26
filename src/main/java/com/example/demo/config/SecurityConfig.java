package com.example.demo.config;

import com.example.demo.security.JwtAuthenticationFilter;
import com.example.demo.security.PepperingPasswordEncoder;
import com.example.demo.security.RestAccessDeniedHandler;
import com.example.demo.security.RestAuthenticationEntryPoint;
import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableConfigurationProperties(SecurityProperties.class)
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder(SecurityProperties securityProperties) {
        return new PepperingPasswordEncoder(securityProperties.getPasswordPepper());
    }

    @Bean
    public UserDetailsService userDetailsService(SecurityProperties securityProperties) {
        UserDetails user = buildUser(securityProperties.getUsers().getUser());
        UserDetails reader = buildUser(securityProperties.getUsers().getReader());
        return new InMemoryUserDetailsManager(user, reader);
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter,
            AuthenticationProvider authenticationProvider,
            RestAuthenticationEntryPoint authenticationEntryPoint,
            RestAccessDeniedHandler accessDeniedHandler) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .authenticationProvider(authenticationProvider)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/v1/auth/login").permitAll()
                        .requestMatchers("/external/**").permitAll()
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**",
                                "/v3/api-docs/**").permitAll()
                        .requestMatchers("/actuator/health", "/actuator/metrics/**").permitAll()
                        .requestMatchers("/api/v1/profile").hasRole("USER")
                        .requestMatchers("/api/v1/docs").hasAuthority("READ_PRIVILEGE")
                        .requestMatchers("/api/v1/tasks/**").authenticated()
                        .anyRequest().permitAll())
                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    private UserDetails buildUser(SecurityProperties.StaticUser configuredUser) {
        List<String> authorities = new ArrayList<>();
        configuredUser.getRoles().forEach(role -> authorities.add("ROLE_" + role));
        authorities.addAll(configuredUser.getAuthorities());

        return User.withUsername(configuredUser.getUsername())
                .password(configuredUser.getPasswordHash())
                .authorities(authorities.toArray(String[]::new))
                .build();
    }
}
