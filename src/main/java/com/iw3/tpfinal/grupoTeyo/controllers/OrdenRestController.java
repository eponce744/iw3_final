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

import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.iw3.tpfinal.grupoTeyo.model.Orden;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.BusinessException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.FoundException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.InvalidityException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.NotFoundException;
import com.iw3.tpfinal.grupoTeyo.model.business.interfaces.IOrdenBusiness;
import com.iw3.tpfinal.grupoTeyo.util.IStandartResponseBusiness;
import com.iw3.tpfinal.grupoTeyo.util.JsonUtiles;
import com.iw3.tpfinal.grupoTeyo.integration.tms.model.OrdenTmsSlimV1JsonSerializer;
import com.iw3.tpfinal.grupoTeyo.model.business.interfaces.IDetalleBusiness;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.SneakyThrows;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping(Constants.URL_ORDENES)
@Tag(name = "Orden", description = "API de gestión de órdenes y ciclo de carga (puntos 1–5).")
@SecurityRequirement(name = "bearerAuth")
public class OrdenRestController {

    //Creo una instancia de la interface de Orden Business
    @Autowired
    private IOrdenBusiness ordenBusiness;
    @Autowired
    private IStandartResponseBusiness response;

    @Operation(summary = "Listar órdenes", description = "Devuelve la lista completa de órdenes.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista devuelta OK"),
        @ApiResponse(responseCode = "500", description = "Error interno")
    })
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> list(){
        try {
            return new ResponseEntity<>(ordenBusiness.list(), HttpStatus.OK);
        }catch(BusinessException e){ //Devuelve un standart response
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()), 
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Obtener orden por id", description = "Devuelve una orden por su identificador numérico.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Orden encontrada"),
        @ApiResponse(responseCode = "404", description = "No encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno")
    })
    @GetMapping(value = "/{id}")
    public ResponseEntity<?> loadOrden(
        @Parameter(in = ParameterIn.PATH, name = "id", schema = @Schema(type = "integer"), required = true, description = "Identificador de la orden.")
        @PathVariable long id){
        try{
            return new ResponseEntity<>(ordenBusiness.load(id), HttpStatus.OK);
        }catch (BusinessException e){
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Agregar nueva orden", description = "Crea una nueva orden (puede usarse para sincronización desde sistemas externos). Devuelve Location con la URL del recurso.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Orden creada"),
        @ApiResponse(responseCode = "302", description = "Orden ya existente (Found)"),
        @ApiResponse(responseCode = "500", description = "Error interno")
    })
    @PostMapping(value = "")
    public ResponseEntity<?> addOrden(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Objeto Orden", required = true)
        @RequestBody Orden orden){
        try{
            Orden resp = ordenBusiness.add(orden);
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("location", Constants.URL_ORDENES + "/" + resp.getId());
            return new ResponseEntity<>(responseHeaders, HttpStatus.CREATED);
        }catch (BusinessException e){
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()), 
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (FoundException e) {
            return new ResponseEntity<>(response.build(HttpStatus.FOUND, e, e.getMessage()), 
                    HttpStatus.FOUND);
        }
    }

    @Operation(summary = "Actualizar orden", description = "Actualiza los datos de una orden existente.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Actualizado OK"),
        @ApiResponse(responseCode = "404", description = "No encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno")
    })
    @PutMapping(value = "")
    public ResponseEntity<?> updateOrden(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Objeto Orden actualizado", required = true)
        @RequestBody Orden orden){
        try{
            ordenBusiness.update(orden);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (BusinessException e){
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }catch (NotFoundException e) {
            return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Eliminar orden", description = "Elimina una orden por su id.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Eliminado"),
        @ApiResponse(responseCode = "404", description = "No encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno")
    })
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteOrden(
        @Parameter(in = ParameterIn.PATH, name = "id", schema = @Schema(type = "integer"), required = true, description = "Identificador de la orden.")
        @PathVariable long id){
        try{
            ordenBusiness.delete(id);
            return new ResponseEntity<String>(HttpStatus.OK);
        }catch (BusinessException e){
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }catch (NotFoundException e) {
            return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @Autowired
    private IDetalleBusiness detalleBusiness;

    @SneakyThrows
    @Operation(
        summary = "Conciliación de orden",
        description = "Genera y devuelve la conciliación para una orden identificada por su id interno. " +
                      "La conciliación sólo puede solicitarse para órdenes en estado 4 (Finalizada). " +
                      "Devuelve: pesaje inicial (tara), pesaje final, producto cargado (último valor de masa acumulada), " +
                      "neto por balanza (pesaje final - pesaje inicial), diferencia entre balanza y caudalímetro, " +
                      "promedio de temperatura, promedio de densidad y promedio de caudal según datos almacenados. " +
                      "Si la orden no está en estado 4 se devolverá 400 o 404 según corresponda."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Conciliación generada correctamente"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida (por ejemplo: orden en estado no finalizada)"),
        @ApiResponse(responseCode = "404", description = "Orden no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno")
    })
    @GetMapping(value = "/conciliacion/{idOrden}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> conciliacion(
            @PathVariable long idOrden) {
        try{
            Orden orden = ordenBusiness.conciliacion(idOrden);
            StdSerializer<Orden> ser = new OrdenTmsSlimV1JsonSerializer(Orden.class, false, detalleBusiness);
            String result = JsonUtiles.getObjectMapper(Orden.class, ser, null).writeValueAsString(orden);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (BusinessException e) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (InvalidityException e) {
            return new ResponseEntity<>(response.build(HttpStatus.BAD_REQUEST, e, e.getMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }
    
}