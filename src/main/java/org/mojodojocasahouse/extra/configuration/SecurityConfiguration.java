package org.mojodojocasahouse.extra.configuration;

import lombok.RequiredArgsConstructor;
import org.mojodojocasahouse.extra.security.DelegatingBasicAuthenticationEntryPoint;
import org.mojodojocasahouse.extra.security.ExtraLogoutSuccessHandler;
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
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {


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
                        .requestMatchers("/getMyExpensesByCategory").authenticated()
                        .requestMatchers("/getAllCategories").authenticated()
                        .requestMatchers("/addExpense").authenticated()
                        .requestMatchers("/protected").authenticated()
                        .requestMatchers("/fullyProtected").fullyAuthenticated()
                        .requestMatchers("/logout").authenticated()
                        .requestMatchers("/login").fullyAuthenticated()
                        .requestMatchers("/auth/password/change").fullyAuthenticated()
                        .requestMatchers("/auth/forgotten").permitAll()
                        .requestMatchers("/auth/forgotten/reset").permitAll()
                        .requestMatchers("/register*").permitAll()
                        .requestMatchers("/editExpense/{id}").authenticated()
                        .requestMatchers("/expenses/{id}").authenticated()
                        .requestMatchers("/getSumOfExpenses").authenticated()
                        .requestMatchers("/getMyExpensesFrom").authenticated()
                        .requestMatchers("/addBudget").authenticated()
                        .requestMatchers("/allBudgets").authenticated()
                        .requestMatchers("/deleteBudget/{id}").authenticated()
                        .requestMatchers("/editBudget/{id}").authenticated()
                        .requestMatchers("/budget/{id}").authenticated()
                        .requestMatchers("/getAllCategoriesWithIcons").authenticated()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/getActiveBudgets").authenticated()
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
                        .invalidateHttpSession(true)
                        .logoutSuccessHandler(customLogoutSuccessHandler())
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

    @Bean
    public LogoutSuccessHandler customLogoutSuccessHandler() {
        return new ExtraLogoutSuccessHandler();
    }
}
