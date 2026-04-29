package in.scalive.Velora.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(auth -> auth

                // FRONTEND STATIC + SPA ASSETS
                .requestMatchers(
                        "/",
                        "/index.html",
                        "/favicon.ico",
                       "/favicon.svg",
                        "/fallback.png",
                        "/assets/**",
                        "/models/**"
                ).permitAll()

                // REACT ROUTES (SPA PATHS) - direct refresh safe
                .requestMatchers(
                        "/shop/**",
                        "/products/**",
                        "/cart/**",
                        "/checkout/**",
                        "/orders/**",
                        "/wishlist/**",
                        "/dashboard/**",
                        "/recommendations/**",
                        "/help/**",
                        "/login/**"
                       
                ).permitAll()

                // AUTH
                .requestMatchers("/auth/**").permitAll()

                // USER REGISTER
                .requestMatchers("/api/users/register-user").permitAll()
                .requestMatchers("/api/users/register-seller").permitAll()

                // PUBLIC PRODUCT APIs
                .requestMatchers("/api/products/**").permitAll()
                .requestMatchers("/api/ratings/**").permitAll()
                 
                // baaki secure
                .anyRequest().authenticated()
            )

            .formLogin(form -> form
            	    .loginPage("/login") // React route, no default Spring login page
            	    .loginProcessingUrl("/auth/login")
            	    .usernameParameter("email")
            	    .passwordParameter("password")
            	    .permitAll()
            	    .successHandler((request, response, authentication) -> {
            	        response.setStatus(200);
            	        response.setContentType("application/json");
            	        response.getWriter().write("{\"success\": true}");
            	    })
            	    .failureHandler((request, response, exception) -> {
            	        response.setStatus(401);
            	        response.setContentType("application/json");
            	        response.getWriter().write("{\"success\": false, \"message\": \"Invalid credentials\"}");
            	    })
            	)

            	.exceptionHandling(ex -> ex
            	    .authenticationEntryPoint((request, response, authException) -> {
            	        response.setStatus(401);
            	        response.setContentType("application/json");
            	        response.getWriter().write("{\"success\": false, \"message\": \"Unauthorized\"}");
            	    })
            	)


            .logout(logout -> logout
                .logoutUrl("/auth/logout")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(
            CustomUserDetailsService service,
            PasswordEncoder encoder) {

        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(service);
        provider.setPasswordEncoder(encoder);
        return provider;
    }
}
