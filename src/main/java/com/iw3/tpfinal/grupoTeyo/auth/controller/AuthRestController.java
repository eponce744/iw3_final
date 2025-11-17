// ...existing code...
package com.iw3.tpfinal.grupoTeyo.auth.controller;

import java.util.ArrayList;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import com.iw3.tpfinal.grupoTeyo.auth.User;
import com.iw3.tpfinal.grupoTeyo.auth.custom.CustomAuthenticationManager;
import com.iw3.tpfinal.grupoTeyo.auth.filters.AuthConstants;
import com.iw3.tpfinal.grupoTeyo.controllers.BaseRestController;
import com.iw3.tpfinal.grupoTeyo.controllers.Constants;
import com.iw3.tpfinal.grupoTeyo.util.IStandartResponseBusiness;
//import ar.edu.iw3.auth.event.UserEvent;
import jakarta.servlet.http.HttpServletRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@Tag(name = "Auth", description = "Endpoints de autenticación / generación de JWT")
public class AuthRestController extends BaseRestController {
    @Autowired
    private AuthenticationManager authManager;
    @Autowired
    private IStandartResponseBusiness response;
    
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;


    @Operation(summary = "Login - obtener token JWT",
            description = "Autentica usuario y contraseña. Devuelve un JWT (texto) que debe usarse en Authorization: Bearer <token> para las operaciones protegidas.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Token JWT (texto plano)"),
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas"),
        @ApiResponse(responseCode = "500", description = "Error interno")
    })
    @PostMapping(value = Constants.URL_LOGIN, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<?> loginExternalOnlyToken(
            @Parameter(in = ParameterIn.QUERY, name = "username", required = true, description = "Nombre de usuario") @RequestParam String username,
            @Parameter(in = ParameterIn.QUERY, name = "password", required = true, description = "Contraseña") @RequestParam String password,
            HttpServletRequest request) {
        Authentication auth = null;
        try {
            auth = authManager.authenticate(((CustomAuthenticationManager) authManager).authWrap(username, password));
        } catch (AuthenticationServiceException e0) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e0, e0.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(response.build(HttpStatus.UNAUTHORIZED, e, e.getMessage()),
                    HttpStatus.UNAUTHORIZED);
        }

        User user = (User) auth.getPrincipal();
        String token = JWT.create().withSubject(user.getUsername())
                .withClaim("internalId", user.getIdUser())
                .withClaim("roles", new ArrayList<String>(user.getAuthoritiesStr()))
                .withClaim("email", user.getEmail())
                .withClaim("version", "1.0.0")
                .withExpiresAt(new Date(System.currentTimeMillis() + AuthConstants.EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(AuthConstants.SECRET.getBytes()));

        //applicationEventPublisher.publishEvent(new UserEvent(user, request, UserEvent.TypeEvent.LOGIN));

        return new ResponseEntity<String>(token, HttpStatus.OK);
    }
    @Autowired
    private PasswordEncoder pEncoder;

    @Operation(summary = "Encode password (demo)", description = "Endpoint de utilidad para codificar una contraseña con el PasswordEncoder configurado (solo demo).")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Password codificada"),
        @ApiResponse(responseCode = "500", description = "Error interno")
    })
    @GetMapping(value = "/demo/encodepass", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<?> encodepass(
            @Parameter(in = ParameterIn.QUERY, name = "password", required = true, description = "Password a codificar") @RequestParam String password) {
        try {
            return new ResponseEntity<String>(pEncoder.encode(password), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
// ...existing code...