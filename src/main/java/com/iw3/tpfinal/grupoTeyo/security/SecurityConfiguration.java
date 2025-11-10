package com.iw3.tpfinal.grupoTeyo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
/*
public class SecurityConfiguration { //Es para eliminar la "autenticación" por defecto que trae spring.

    @Bean	
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // CORS: https://developer.mozilla.org/es/docs/Web/HTTP/CORS 
        // CSRF: https://developer.mozilla.org/es/docs/Glossary/CSRF
        http.cors(CorsConfigurer::disable);
        http.csrf(AbstractHttpConfigurer::disable);
        http.authorizeHttpRequests(auth -> auth
                 // Permitir acceso a la documentación OpenAPI / Swagger y recursos estáticos
                .requestMatchers(
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/swagger-ui/index.html",
                    "/webjars/**",
                    "/v3/api-docs.yaml"
                ).permitAll()
                // TODO: ajustar permisos para endpoints públicos si corresponde
                .anyRequest().authenticated()
        );
        return http.build();
    }

}*/

//PARA VER SIN NINGUNA AUTENTICACIÓN (DESARROLLO LOCAL)
//@Configuration
//PARA VER SIN NINGUNA AUTENTICACIÓN (DESARROLLO LOCAL)
public class SecurityConfiguration {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF and CORS for local development convenience
            .csrf(AbstractHttpConfigurer::disable)
            .cors(AbstractHttpConfigurer::disable)
            // Permit everything in development
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

        // Optionally, allow frames (e.g., H2 console) by disabling frame options
        // http.headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }
} 

