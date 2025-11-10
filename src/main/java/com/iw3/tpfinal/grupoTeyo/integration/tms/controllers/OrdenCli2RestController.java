package com.iw3.tpfinal.grupoTeyo.integration.tms.controllers;

import com.iw3.tpfinal.grupoTeyo.controllers.Constants;
import com.iw3.tpfinal.grupoTeyo.integration.tms.model.business.interfaces.IOrdenCli2Business;
import com.iw3.tpfinal.grupoTeyo.model.Orden;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constants.URL_INTEGRATION_TMS+"/ordenes")
public class OrdenCli2RestController {

    @Autowired
    IOrdenCli2Business ordenCli2Business;

    /*
     * Enviamos la patente de un camion y su peso vacio (Tara), 
     * dicho camion tiene que estar ligado a una orden con estado PENDIENTE_PESAJE_INICIAL para que funcione.
     * El devuelve un 200, el Id-Orden en el header y en el cuerpo la Password.
     * */
    @SneakyThrows
    @PostMapping(value = "/pesaje-inicial", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity registrarPesajeInicial(
            @RequestHeader("Patente") String patente,
            @RequestHeader("Peso-Inicial") Double pesoInicial) {
        Orden orden = ordenCli2Business.registrarPesajeInicial(patente, pesoInicial);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Id-Orden", String.valueOf(orden.getId()));
        return new ResponseEntity<>(orden.getActivacionPassword().toString(), responseHeaders, HttpStatus.OK);
    }

}
