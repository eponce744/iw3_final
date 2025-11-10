// ...existing code...
package com.iw3.tpfinal.grupoTeyo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.iw3.tpfinal.grupoTeyo.model.Detalle;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.BusinessException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.FoundException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.NotFoundException;
import com.iw3.tpfinal.grupoTeyo.model.business.interfaces.IDetalleBusiness;
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
@RequestMapping(Constants.URL_DETALLES)
@Tag(name = "Detalle", description = "API de gestión de datos de detalle de carga")
@SecurityRequirement(name = "bearerAuth")
public class DetalleRestController {

        //Creo una instancia de la interface de Detalle Business
    @Autowired
    private IDetalleBusiness detalleBusiness;
    @Autowired
    private IStandartResponseBusiness response;

    @Operation(summary = "Listar detalles por orden", description = "Devuelve los detalles asociados a una orden (últimos registros según política de muestreo).")
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
            return new ResponseEntity<>(detalleBusiness.listByOrden(ordenId), HttpStatus.OK);
        } catch(BusinessException e){ //Devuelve un standart response
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()), 
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch(NotFoundException e){
            return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Obtener detalle por id", description = "Devuelve un registro de detalle por su identificador.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Detalle encontrado"),
        @ApiResponse(responseCode = "404", description = "Detalle no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno")
    })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> loadDetalle(
        @Parameter(in = ParameterIn.PATH, name = "id", schema = @Schema(type = "integer"), required = true, description = "Identificador del detalle.")
        @PathVariable long id){
        try{
            return new ResponseEntity<>(detalleBusiness.load(id), HttpStatus.OK);
        }catch (BusinessException e){
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Agregar detalle de carga", description = "Recibe un registro de detalle (masa acumulada, densidad, temperatura, caudal). Se aplican reglas de filtrado y muestreo.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Detalle creado"),
        @ApiResponse(responseCode = "302", description = "Detalle duplicado (Found)"),
        @ApiResponse(responseCode = "500", description = "Error interno")
    })
    @PostMapping(value = "")
    public ResponseEntity<?> addDetalle(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Objeto Detalle con masaAcumulada, densidad, temperatura, caudal y referencia a orden", required = true)
        @RequestBody Detalle detalle){
        try{
            Detalle response = detalleBusiness.add(detalle);
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("location", Constants.URL_DETALLES + "/" + response.getId());
            return new ResponseEntity<>(responseHeaders, HttpStatus.CREATED);
        }catch (BusinessException e){
            return new ResponseEntity<>(this.response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()), 
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (FoundException e) {
            return new ResponseEntity<>(this.response.build(HttpStatus.FOUND, e, e.getMessage()), 
                    HttpStatus.FOUND);
        }
    }


}
