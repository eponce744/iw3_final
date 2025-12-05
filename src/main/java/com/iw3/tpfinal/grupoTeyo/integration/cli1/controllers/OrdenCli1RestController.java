package com.iw3.tpfinal.grupoTeyo.integration.cli1.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iw3.tpfinal.grupoTeyo.controllers.BaseRestController;
import com.iw3.tpfinal.grupoTeyo.controllers.Constants;
import com.iw3.tpfinal.grupoTeyo.integration.cli1.model.OrdenCli1;
import com.iw3.tpfinal.grupoTeyo.integration.cli1.model.business.interfaces.IOrdenCli1Business;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.BusinessException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.FoundException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.NotFoundException;
import com.iw3.tpfinal.grupoTeyo.util.IStandartResponseBusiness;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping(Constants.URL_INTEGRATION_CLI1 + "/ordenes")
@Tag(
    name = "Integración SAP - Órdenes",
    description = "Endpoints para recibir y consultar órdenes provenientes de SAP. " +
                  "Importante: los sub-objetos relacionados (cliente, camion, chofer, productos, etc.) " +
                  "se reciben por su código externo (string) y no como objetos embebidos. " +
                  "La capa de negocio se encarga de resolver esos códigos y sincronizar entidades."
)
public class OrdenCli1RestController extends BaseRestController {

    @Autowired
    private IOrdenCli1Business ordenBusiness;

    @Autowired
    private IStandartResponseBusiness response;

    @Operation(summary = "Listar órdenes externas", description = "Devuelve todas las órdenes sincronizadas desde SAP.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de órdenes devuelta"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> list() {
        try {
            return new ResponseEntity<>(ordenBusiness.list(), HttpStatus.OK);
        } catch (BusinessException e) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Obtener orden por código externo (codSap)", description = "Recupera una orden usando su código externo provisto por SAP.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Orden encontrada"),
        @ApiResponse(responseCode = "404", description = "Orden no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping(value = "/{codSap}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> loadByCode(
            @Parameter(in = ParameterIn.PATH, name = "codSap", required = true, description = "Código externo de la orden (string).", schema = @Schema(type = "string"))
            @PathVariable("codSap") String codSap) {
        try {
            return new ResponseEntity<>(ordenBusiness.load(codSap), HttpStatus.OK);
        } catch (BusinessException e) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(
        summary = "Crear orden (payload mapeado)",
        description = "Crea una orden a partir de un objeto OrdenSap. Los campos que referencian otras entidades " +
                      "(por ejemplo clienteCodExterno, camionCodExterno, choferCodExterno, productos[].productoCodExterno) " +
                      "deben ser códigos externos (strings). La resolución de dichos códigos la realiza la capa de negocio.\n\n" +
                      "Ejemplo de JSON de entrada:\n" +
                      "{\n" +
                      "  \"codSap\": \"ORD-12345\",\n" +
                      "  \"clienteCodExterno\": \"CLI-0001\",\n" +
                      "  \"choferCodExterno\": \"CH-010\",\n" +
                      "  \"camionCodExterno\": \"CAM-22\",\n" +
                      "  \"productos\": [ { \"productoCodExterno\": \"PR-100\", \"cantidad\": 5 } ],\n" +
                      "  \"fechaEntrega\": \"2025-11-10T10:00:00\",\n" +
                      "  \"preset\": 2000\n" +
                      "}"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Orden creada. Header 'location' con URL del recurso"),
        @ApiResponse(responseCode = "302", description = "Orden ya existe (Found)"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping(value = "")
    public ResponseEntity<?> add(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Objeto OrdenSap (ver ejemplo en la descripción)", required = true)
            @RequestBody OrdenCli1 product) {
        try {
            OrdenCli1 response = ordenBusiness.add(product);
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("location", Constants.URL_INTEGRATION_CLI1 + "/ordenes/" + response.getCodSap());
            return new ResponseEntity<>(responseHeaders, HttpStatus.CREATED);
        } catch (BusinessException e) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (FoundException e) {
            return new ResponseEntity<>(response.build(HttpStatus.FOUND, e, e.getMessage()), HttpStatus.FOUND);
        }
    }

    @Operation(
        summary = "Crear orden (B2B/raw JSON)",
        description = "Recibe el payload raw (JSON) desde un sistema B2B. El cuerpo se procesa como texto y la capa de negocio " +
                      "realiza la deserialización y resolución de referencias por código externo (strings).\n\n" +
                      "IMPORTANTE: los sub-objetos relacionados se deben pasar por su código externo (string) en los campos " +
                      "correspondientes. Ejemplo de payload (cadena JSON):\n" +
                      "{ \"codSap\":\"ORD-12345\", \"clienteCodExterno\":\"CLI-0001\", \"camionCodExterno\":\"CAM-22\", \"productos\":[{ \"productoCodExterno\":\"PR-100\",\"cantidad\":5 }] }\n\n" +
                      "Respuestas esperadas: 201 CREATED (location), 302 FOUND (si ya existe), 500 INTERNAL_SERVER_ERROR."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Orden creada. Header 'location' con URL del recurso"),
        @ApiResponse(responseCode = "302", description = "Orden ya existe (Found)"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping(value = "/b2b")
    public ResponseEntity<?> addExternal(HttpEntity<String> httpEntity) {
        try {
            OrdenCli1 response = ordenBusiness.addExternal(httpEntity.getBody());
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("location", Constants.URL_INTEGRATION_CLI1 + "/ordenes/" + response.getCodSap());
            return new ResponseEntity<>(responseHeaders, HttpStatus.CREATED);
        } catch (BusinessException e) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (FoundException e) {
            return new ResponseEntity<>(response.build(HttpStatus.FOUND, e, e.getMessage()), HttpStatus.FOUND);
        }
    }

}