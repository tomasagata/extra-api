package org.mojodojocasahouse.extra.configuration;

import lombok.RequiredArgsConstructor;
import org.mojodojocasahouse.extra.security.DelegatingBasicAuthenticationEntryPoint;
import org.mojodojocasahouse.extra.security.ExtraUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final ExtraUserDetailsService userDetailsService;

    private final DelegatingBasicAuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/getMyExpenses").authenticated()
                        .requestMatchers("/addExpense").authenticated()
                        .requestMatchers("/protected").authenticated()
                        .requestMatchers("/fullyProtected").fullyAuthenticated()
                        .requestMatchers("/logout").authenticated()
                        .requestMatchers("/login").fullyAuthenticated()
                        .requestMatchers("/auth/password/change").fullyAuthenticated()
                        .requestMatchers("/register*").permitAll()
                )
                .httpBasic(httpBasic -> httpBasic
                        .realmName("extra")
                        .authenticationEntryPoint(authenticationEntryPoint)
                )
                .sessionManagement(session -> session
                        .maximumSessions(2)
                )
                .logout(logout -> logout
                        .deleteCookies("JSESSIONID")
                )
                .rememberMe(Customizer.withDefaults())
                .exceptionHandling(exc -> exc
                        .authenticationEntryPoint(authenticationEntryPoint)
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


}
