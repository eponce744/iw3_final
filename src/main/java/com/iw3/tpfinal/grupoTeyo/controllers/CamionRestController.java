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

import com.iw3.tpfinal.grupoTeyo.model.Camion;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.BusinessException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.FoundException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.NotFoundException;
import com.iw3.tpfinal.grupoTeyo.model.business.interfaces.ICamionBusiness;
import com.iw3.tpfinal.grupoTeyo.util.IStandartResponseBusiness;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn; 
import io.swagger.v3.oas.annotations.media.Schema;       
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping(Constants.URL_CAMIONES)
@Tag(name = "Camion", description = "API de gestión de camiones")
@SecurityRequirement(name = "bearerAuth")
public class CamionRestController {

    @Autowired
    private ICamionBusiness camionBusiness;
    @Autowired
    private IStandartResponseBusiness response;

    @Operation(summary = "Listar camiones", description = "Devuelve la lista completa de camiones.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista devuelta OK"),
        @ApiResponse(responseCode = "500", description = "Error interno")
    })
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> list(){
        try {
            return new ResponseEntity<>(camionBusiness.list(), HttpStatus.OK);
        }catch(BusinessException e){
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Obtener camión por id", description = "Carga un camión por su id numérico.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Camión encontrado"),
        @ApiResponse(responseCode = "404", description = "No encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno")
    })
    @GetMapping(value = "/{id}")
    public ResponseEntity<?> loadCamion(
        @Parameter(in = ParameterIn.PATH, name = "id", schema = @Schema(type = "integer"), required = true, description = "Identificador del camión.") // { changed code }
        @PathVariable long id){
        try{
            return new ResponseEntity<>(camionBusiness.load(id), HttpStatus.OK);
        }catch (BusinessException e){
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Buscar camión por patente", description = "Devuelve el camión asociado a la patente indicada.")
    @GetMapping(value = "/by-patente/{patente}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> loadCamionByPatente(@Parameter(description = "Patente del camión") @PathVariable String patente){
        try{
            return new ResponseEntity<>(camionBusiness.load(patente), HttpStatus.OK);
        }catch (BusinessException e){
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Alta de camión", description = "Crea un nuevo camión. Devuelve header Location con la URL del recurso.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Camión creado"),
        @ApiResponse(responseCode = "302", description = "Camión ya existente (Found)"),
        @ApiResponse(responseCode = "500", description = "Error interno")
    })
    @PostMapping(value = "")
    public ResponseEntity<?> addCamion(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Objeto Camion", required = true) @RequestBody Camion camion){
        try{
            Camion response = camionBusiness.add(camion);
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("location", Constants.URL_CAMIONES + "/" + response.getId());
            return new ResponseEntity<>(responseHeaders, HttpStatus.CREATED);
        }catch (BusinessException e){
            return new ResponseEntity<>(this.response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (FoundException e) {
            return new ResponseEntity<>(this.response.build(HttpStatus.FOUND, e, e.getMessage()),
                    HttpStatus.FOUND);
        }
    }

    @Operation(summary = "Actualizar camión", description = "Actualiza los datos de un camión existente.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Actualizado OK"),
        @ApiResponse(responseCode = "404", description = "No encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflicto / Found"),
        @ApiResponse(responseCode = "500", description = "Error interno")
    })
    @PutMapping(value = "")
    public ResponseEntity<?> updateCamion(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Objeto Camion actualizado", required = true) @RequestBody Camion camion){
        try{
            camionBusiness.update(camion);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (FoundException e) {
            return new ResponseEntity<>(response.build(HttpStatus.FOUND, e, e.getMessage()),
                    HttpStatus.FOUND);
        }catch (BusinessException e){
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }catch (NotFoundException e) {
            return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Eliminar camión", description = "Borra un camión por id.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Eliminado"),
        @ApiResponse(responseCode = "404", description = "No encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno")
    })
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteCamion(
        @Parameter(in = ParameterIn.PATH, name = "id", schema = @Schema(type = "integer"), required = true, description = "Identificador del camión.") // { changed code }
        @PathVariable long id){
        try{
            camionBusiness.delete(id);
            return new ResponseEntity<String>(HttpStatus.OK);
        }catch (BusinessException e){
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }catch (NotFoundException e) {
            return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

}