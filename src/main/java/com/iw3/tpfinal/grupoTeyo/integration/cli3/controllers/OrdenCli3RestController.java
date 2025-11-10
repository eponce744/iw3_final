package com.iw3.tpfinal.grupoTeyo.integration.cli3.controllers;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.iw3.tpfinal.grupoTeyo.controllers.BaseRestController;
import com.iw3.tpfinal.grupoTeyo.controllers.Constants;
import com.iw3.tpfinal.grupoTeyo.integration.cli3.model.OrdenCli3SlimV1JsonSerializer;
import com.iw3.tpfinal.grupoTeyo.integration.cli3.model.business.interfaces.IOrdenCli3Business;
import com.iw3.tpfinal.grupoTeyo.model.Detalle;
import com.iw3.tpfinal.grupoTeyo.model.Orden;
import com.iw3.tpfinal.grupoTeyo.util.JsonUtiles;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.SneakyThrows;

@RestController
@RequestMapping(Constants.URL_INTEGRATION_CLI3 + "/ordenes")
public class OrdenCli3RestController extends BaseRestController{

    @Autowired
    private IOrdenCli3Business ordenCli3Business;
    
    @SneakyThrows
    @PostMapping(value = "/validacion", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> validatePassword(
    		@RequestHeader("CodOrdenSap") String codOrdenSap, 
    		@RequestHeader("Password") Integer password) {
        Orden orden = ordenCli3Business.validacionPassword(codOrdenSap,password);
        StdSerializer<Orden> ser = new OrdenCli3SlimV1JsonSerializer(Orden.class, false);
        String result = JsonUtiles.getObjectMapper(Orden.class, ser, null).writeValueAsString(orden);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    
    @SneakyThrows
    @PostMapping("/detalle")
    public ResponseEntity<?> receiveLoadData(@RequestBody Detalle detalle) {
        Orden orden = ordenCli3Business.recepcionDetalles(detalle);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Order-Id", String.valueOf(orden.getId()));
        return new ResponseEntity<>(responseHeaders, HttpStatus.OK);
    }
    
    @SneakyThrows
    @PostMapping("/cerrar")
    public ResponseEntity<?> closeOrder(@RequestHeader("OrderId") Long orderId) {
        Orden orden = ordenCli3Business.cierreOrden(orderId);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Order-Id", String.valueOf(orden.getId()));
        return new ResponseEntity<>(responseHeaders, HttpStatus.OK);
    }
}
