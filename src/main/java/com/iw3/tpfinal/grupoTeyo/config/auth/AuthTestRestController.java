package com.iw3.tpfinal.grupoTeyo.config.auth;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import com.iw3.tpfinal.grupoTeyo.controllers.Constants; // { changed code }

/**
 * Controlador de pruebas de autorización (documentación + endpoints de ejemplo).
 * - Adaptado para no depender de servicios externos.
 * - Para probar autorización en Swagger: activar un perfil con seguridad o usar token y Authorize.
 */
@RestController
@RequestMapping(Constants.URL_AUTHORIZATION)
@Tag(name = "AuthTest", description = "Endpoints de prueba de autorización")
@SecurityRequirement(name = "bearerAuth")
public class AuthTestRestController {

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<String> onlyAdmin() {
        return new ResponseEntity<>("Servicio admin", HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/user")
    public ResponseEntity<String> onlyUser() {
        return new ResponseEntity<>("Servicio user", HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @GetMapping("/user-or-admin")
    public ResponseEntity<String> rolUserOArdmin() {
        return new ResponseEntity<>("Servicio user or admin", HttpStatus.OK);
    }

    // Se compara con los datos de entrada
    @PreAuthorize("#username == authentication.name")
    @GetMapping("/my-rols")
    public ResponseEntity<String> myRols(@RequestParam String username) {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        String roles = a.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));
        return new ResponseEntity<>(roles, HttpStatus.OK);
    }

    @GetMapping("/variable")
    public ResponseEntity<String> variable(HttpServletRequest request) {
        if (request.isUserInRole("ROLE_ADMIN")) {
            return new ResponseEntity<>("Tenés rol admin", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("No tenés rol admin", HttpStatus.OK);
        }
    }

    // Se compara con los datos de respuesta
    // Devuelve un objeto con 'username' para que la expresión PostAuthorize funcione
    public static record UserInfo(String username, List<String> roles) {}

    @PostAuthorize("returnObject.username == #username")
    @GetMapping("/full-data")
    public UserInfo fullData(@RequestParam String username) {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        List<String> roles = a.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        return new UserInfo(a.getName(), roles);
    }

    // Ejemplo de PostFilter: la lista devuelta no contendrá el usuario logueado
    @PostFilter("filterObject != authentication.name")
    @GetMapping("/self-filter")
    public List<String> selfFilter() {
        // Lista de ejemplo; reemplazar por llamadas a servicios reales si los tenés
        return List.of("admin", "user", "demo", "sofia");
    }

}