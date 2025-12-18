package com.iw3.tpfinal.grupoTeyo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


import com.iw3.tpfinal.grupoTeyo.auth.model.business.interfaces.IUserAuthBusiness;
import com.iw3.tpfinal.grupoTeyo.auth.custom.CustomAuthenticationManager;
import com.iw3.tpfinal.grupoTeyo.auth.filters.JWTAuthorizationFilter;
import com.iw3.tpfinal.grupoTeyo.controllers.Constants;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration {
	
	@Bean
	PasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	// HABILITAR CORS
    @Bean
    WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                    .allowedOrigins("http://localhost:5173") // Puerto de Vite
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    .allowedHeaders("*")
                    .allowCredentials(true);
            }
        };
    }

	@Autowired
	private IUserAuthBusiness userBusiness;
	@Bean
	AuthenticationManager authenticationManager() {
		return new CustomAuthenticationManager(bCryptPasswordEncoder(), userBusiness);
	}

   @Bean
   SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		// CORS: https://developer.mozilla.org/es/docs/Web/HTTP/CORS
		// CSRF: https://developer.mozilla.org/es/docs/Glossary/CSRF
		
		http.csrf(AbstractHttpConfigurer::disable);
		http.cors(Customizer.withDefaults());

		http.authorizeHttpRequests(auth -> auth
				
				.requestMatchers(HttpMethod.POST, Constants.URL_LOGIN).permitAll()
				
				.requestMatchers("/v3/api-docs/**").permitAll()
				.requestMatchers("/swagger-ui.html").permitAll()
				.requestMatchers("/swagger-ui/**").permitAll()
				
				.requestMatchers("/ws/**").permitAll()
				.requestMatchers("/ui/**").permitAll()
				.requestMatchers("/ui/js/**").permitAll()
				.requestMatchers("/favicon.ico").permitAll()
				
				.requestMatchers("/demo/**").permitAll()
				
				.anyRequest().authenticated());
		
		//http.httpBasic(Customizer.withDefaults());
		
		http.addFilter(new JWTAuthorizationFilter(authenticationManager()));
		
		http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		return http.build();
   }
}
