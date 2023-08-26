
package com.qortex.inventory.common.config;

import com.qortex.inventory.common.filter.RequestValidationFilter;
import com.qortex.inventory.common.token.JWTTokenProps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
@SuppressWarnings("java:S4502")
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${iam.service.validation.url}")
    private String validationUrl;

    @Value("${portal.url}")
    private String portalUrl;

    @Autowired
    private JWTTokenProps jwtTokenProps;

    private static final String[] AUTH_WHITELIST = {

            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/webjars/**",
            "/swagger-ui/**",
            "/swagger-ui/index.html",
            "/api/actuator/health/**",
            "/swagger-ui/swagger-ui.css",
            "/swagger-ui/index.css",
            "/swagger-ui/swagger-initializer.js",
            "/swagger-ui/swagger-ui-standalone-preset.js",
            "/swagger-ui/swagger-initializer.js",
            "/swagger-ui/swagger-ui-bundle.js",
            "/openapi/swagger-config",
            "swagger-ui/favicon-32x32.png",
            "swagger-ui/favicon-16x16.png"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.cors().and()
                .addFilterBefore(new RequestValidationFilter(restTemplate, validationUrl, jwtTokenProps),
                        BasicAuthenticationFilter.class)
                .authorizeHttpRequests()
                .requestMatchers("/api/actuator/health/**").permitAll()
                .requestMatchers(AUTH_WHITELIST).permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(portalUrl.split(",")));
        configuration.setAllowedMethods(Arrays.asList("OPTIONS", "HEAD", "GET", "POST", "PUT", "DELETE", "PATCH"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(Arrays.asList("*"));
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
