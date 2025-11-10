package com.iw3.tpfinal.grupoTeyo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iw3.tpfinal.grupoTeyo.model.Cliente;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.BusinessException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.FoundException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.NotFoundException;
import com.iw3.tpfinal.grupoTeyo.model.business.interfaces.IClienteBusiness;
import com.iw3.tpfinal.grupoTeyo.util.IStandartResponseBusiness;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

/**
 * ClienteRestController con documentación OpenAPI (Swagger).
 */
@RestController
@RequestMapping(Constants.URL_CLIENTES)
@Tag(name = "Cliente", description = "API de gestión de clientes")
@SecurityRequirement(name = "bearerAuth")
public class ClienteRestController {

    @Autowired
    private IClienteBusiness clienteBusiness;
    @Autowired
    private IStandartResponseBusiness response;

    @Operation(summary = "Listar clientes", description = "Devuelve la lista completa de clientes.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista devuelta OK"),
        @ApiResponse(responseCode = "500", description = "Error interno")
    })
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> list(){
        try {
            return new ResponseEntity<>(clienteBusiness.list(), HttpStatus.OK);
        } catch(BusinessException e){
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Obtener cliente por id", description = "Devuelve un cliente por su identificador numérico.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
        @ApiResponse(responseCode = "404", description = "No encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno")
    })
    @GetMapping(value = "/{id}")
    public ResponseEntity<?> loadCliente(
        @Parameter(in = ParameterIn.PATH, name = "id", schema = @Schema(type = "integer"), required = true, description = "Identificador del cliente.")
        @PathVariable long id){
        try{
            return new ResponseEntity<>(clienteBusiness.load(id), HttpStatus.OK);
        } catch (BusinessException e){
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Obtener cliente por razón social", description = "Devuelve un cliente por su razón social.")
    @GetMapping(value = "/by-razon-social/{razonSocial}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> loadClienteByRazonSocial(
        @Parameter(in = ParameterIn.PATH, name = "razonSocial", schema = @Schema(type = "string"), required = true, description = "Razón social del cliente.")
        @PathVariable String razonSocial){
        try{
            return new ResponseEntity<>(clienteBusiness.load(razonSocial), HttpStatus.OK);
        } catch (BusinessException e){
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Crear cliente", description = "Crea un nuevo cliente. Devuelve Location con la URL del recurso.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Cliente creado"),
        @ApiResponse(responseCode = "302", description = "Cliente ya existente (Found)"),
        @ApiResponse(responseCode = "500", description = "Error interno")
    })
    @PostMapping(value = "")
    public ResponseEntity<?> addCliente(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Objeto Cliente", required = true)
        @RequestBody Cliente cliente){
        try{
            Cliente resp = clienteBusiness.add(cliente);
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("location", Constants.URL_CLIENTES + "/" + resp.getId());
            return new ResponseEntity<>(responseHeaders, HttpStatus.CREATED);
        } catch (BusinessException e){
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (FoundException e) {
            return new ResponseEntity<>(response.build(HttpStatus.FOUND, e, e.getMessage()),
                    HttpStatus.FOUND);
        }
    }

    @Operation(summary = "Actualizar cliente", description = "Actualiza los datos de un cliente existente.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Actualizado OK"),
        @ApiResponse(responseCode = "404", description = "No encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflicto / Found"),
        @ApiResponse(responseCode = "500", description = "Error interno")
    })
    @PutMapping(value = "")
    public ResponseEntity<?> updateCliente(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Objeto Cliente actualizado", required = true)
        @RequestBody Cliente cliente){
        try{
            clienteBusiness.update(cliente);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (FoundException e) {
            return new ResponseEntity<>(response.build(HttpStatus.FOUND, e, e.getMessage()), HttpStatus.FOUND);
        } catch (BusinessException e){
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Eliminar cliente", description = "Elimina un cliente por su id.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Eliminado"),
        @ApiResponse(responseCode = "404", description = "No encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno")
    })
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteCliente(
        @Parameter(in = ParameterIn.PATH, name = "id", schema = @Schema(type = "integer"), required = true, description = "Identificador del cliente.")
        @PathVariable long id){
        try{
            clienteBusiness.delete(id);
            return new ResponseEntity<String>(HttpStatus.OK);
        } catch (BusinessException e){
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

}

