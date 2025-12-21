package com.iw3.tpfinal.grupoTeyo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.iw3.tpfinal.grupoTeyo.model.Alarma;
import com.iw3.tpfinal.grupoTeyo.model.Camion;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.BusinessException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.FoundException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.NotFoundException;
import com.iw3.tpfinal.grupoTeyo.model.business.interfaces.IAlarmaBusiness;
import com.iw3.tpfinal.grupoTeyo.util.IStandartResponseBusiness;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(Constants.URL_ALARMAS)
@Tag(name = "Alarma", description = "API de gestión de alarmas")
@SecurityRequirement(name = "bearerAuth")
public class AlarmaRestController {

	  @Autowired
	    private IAlarmaBusiness alarmaBusiness;
	    @Autowired
	    private IStandartResponseBusiness response;
	    
	    @Operation(summary = "Listar alarmas", description = "Devuelve la lista completa de alarmas.")
	    @ApiResponses({
	        @ApiResponse(responseCode = "200", description = "Lista devuelta OK"),
	        @ApiResponse(responseCode = "500", description = "Error interno")
	    })
	    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
	    public ResponseEntity<?> list(){
	        try {
	            return new ResponseEntity<>(alarmaBusiness.list(), HttpStatus.OK);
	        }catch(BusinessException e){
	            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
	                    HttpStatus.INTERNAL_SERVER_ERROR);
	        }
	    }
	    
	    @Operation(summary = "Obtener alarma por id", description = "Carga una alarma por su id numérico.")
	    @ApiResponses({
	        @ApiResponse(responseCode = "200", description = "Alarma encontrada"),
	        @ApiResponse(responseCode = "404", description = "No encontrada"),
	        @ApiResponse(responseCode = "500", description = "Error interno")
	    })
	    @GetMapping(value = "/{id}")
	    public ResponseEntity<?> loadAlarma(
	        @Parameter(in = ParameterIn.PATH, name = "id", schema = @Schema(type = "integer"), required = true, description = "Identificador de la alarma.") // { changed code }
	        @PathVariable long id){
	        try{
	            return new ResponseEntity<>(alarmaBusiness.load(id), HttpStatus.OK);
	        }catch (BusinessException e){
	            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
	                    HttpStatus.INTERNAL_SERVER_ERROR);
	        } catch (NotFoundException e) {
	            return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
	        }
	    }
	    
	    @Operation(summary = "Listar alarmas por numero de orden", description = "Devuelve las alarmas asociadas a una orden.")
	    @ApiResponses({
	        @ApiResponse(responseCode = "200", description = "Detalles devueltos OK"),
	        @ApiResponse(responseCode = "404", description = "Orden no encontrada"),
	        @ApiResponse(responseCode = "500", description = "Error interno")
	    })
	    @GetMapping(value = "/by-orden/{ordenId}", produces = MediaType.APPLICATION_JSON_VALUE)
	    public ResponseEntity<?> list(
	        @Parameter(in = ParameterIn.PATH, name = "ordenId", schema = @Schema(type = "integer"), required = true, description = "Identificador de la orden.")
	        @PathVariable long ordenId){
	        try {
	            return new ResponseEntity<>(alarmaBusiness.listByOrden(ordenId), HttpStatus.OK);
	        } catch(BusinessException e){ //Devuelve un standart response
	            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()), 
	                    HttpStatus.INTERNAL_SERVER_ERROR);
	        } catch(NotFoundException e){
	            return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
	        }
	    }
	    
	    
	    @Operation(summary = "Actualizar estado de una Alarma",description = "Cambia el estado de una alarma existente. ")
	    	@ApiResponses({
	    	    @ApiResponse(responseCode = "200",description = "Estado actualizado correctamente"),
	    	    @ApiResponse(responseCode = "400",description = "Estado inválido"),
	    	    @ApiResponse(responseCode = "404",description = "Alarma no encontrada"),
	    	    @ApiResponse(responseCode = "500",description = "Error interno")
	    	})
	    @PutMapping("/{id}/estado")
	    public ResponseEntity<?> updateEstado(
	            @PathVariable long id,
	            @RequestParam Alarma.Estado estado) {

	        try {
	            alarmaBusiness.updateEstado(id, estado);
	            return ResponseEntity.ok().build();
	        } catch (NotFoundException e) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	        } catch (BusinessException e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	        }
	    }
}
