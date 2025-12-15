package com.iw3.tpfinal.grupoTeyo.controllers;

import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.BusinessException;
import com.iw3.tpfinal.grupoTeyo.util.EmailBusiness;
import com.iw3.tpfinal.grupoTeyo.util.IStandartResponseBusiness;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mail")
@SecurityRequirement(name = "bearerAuth")
public class MailRestController extends BaseRestController {

    @Autowired
    private IStandartResponseBusiness response;

    @Autowired
    private EmailBusiness emailBusiness;



    // BORRAR DESPUES DE PRUEBAS

    @PostMapping("/test")
    public ResponseEntity<?> test(@RequestParam String to, @RequestParam String subject){
        try {
            emailBusiness.sendSimpleMessage(to, subject, String.format("Este es un correo de prueba enviado a %s con asunto %s", to, subject));
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (BusinessException e) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
