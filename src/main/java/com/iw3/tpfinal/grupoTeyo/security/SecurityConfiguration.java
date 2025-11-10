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
/*public class SecurityConfiguration { //Es para eliminar la "autenticación" por defecto que trae spring.

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
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // permitir rutas de swagger y docs
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                // permitir temporalmente listar ordenes para pruebas
                .requestMatchers("/api/v1/ordenes", "/api/v1/ordenes/**").permitAll()
                // mantener el resto protegido
                .anyRequest().authenticated()
            )
            .csrf().disable();
        return http.build();
    }
}

