package com.iw3.tpfinal.grupoTeyo.controllers;

import com.iw3.tpfinal.grupoTeyo.model.Producto;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.BusinessException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.FoundException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.NotFoundException;
import com.iw3.tpfinal.grupoTeyo.model.business.interfaces.IProductoBusiness;
import com.iw3.tpfinal.grupoTeyo.util.IStandartResponseBusiness;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(Constants.URL_PRODUCTOS)
@Tag(name = "Producto", description = "API de gestión de productos")
@SecurityRequirement(name = "bearerAuth")
public class ProductoRestController {

    @Autowired
    private IProductoBusiness productoBusiness;
    @Autowired
    private IStandartResponseBusiness response;

    @Operation(summary = "Listar productos", description = "Devuelve la lista completa de productos.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista devuelta OK"),
        @ApiResponse(responseCode = "500", description = "Error interno")
    })
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> list(){
        try {
            return new ResponseEntity<>(productoBusiness.list(), HttpStatus.OK);
        }catch(BusinessException e){
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Obtener producto por id", description = "Devuelve un producto por su identificador numérico.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto encontrado"),
        @ApiResponse(responseCode = "404", description = "No encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno")
    })
    @GetMapping(value = "/{id}")
    public ResponseEntity<?> loadProducto(
        @Parameter(in = ParameterIn.PATH, name = "id", schema = @Schema(type = "integer"), required = true, description = "Identificador del producto.")
        @PathVariable long id){
        try{
            return new ResponseEntity<>(productoBusiness.load(id), HttpStatus.OK);
        }catch (BusinessException e){
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Obtener producto por nombre", description = "Devuelve un producto por su nombre.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto encontrado"),
        @ApiResponse(responseCode = "404", description = "No encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno")
    })
    @GetMapping(value = "/by-nombre/{producto}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> loadProductoByNombre(
        @Parameter(in = ParameterIn.PATH, name = "producto", schema = @Schema(type = "string"), required = true, description = "Nombre del producto.")
        @PathVariable String producto){
        try{
            return new ResponseEntity<>(productoBusiness.load(producto), HttpStatus.OK);
        }catch (BusinessException e){
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Crear producto", description = "Crea un nuevo producto. Devuelve Location con la URL del recurso.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Producto creado"),
        @ApiResponse(responseCode = "302", description = "Producto ya existente (Found)"),
        @ApiResponse(responseCode = "500", description = "Error interno")
    })
    @PostMapping(value = "")
    public ResponseEntity<?> addProducto(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Objeto Producto", required = true)
        @RequestBody Producto producto){
        try{
            Producto resp = productoBusiness.add(producto);
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("location", Constants.URL_PRODUCTOS + "/" + resp.getId());
            return new ResponseEntity<>(responseHeaders, HttpStatus.CREATED);
        }catch (BusinessException e){
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (FoundException e) {
            return new ResponseEntity<>(response.build(HttpStatus.FOUND, e, e.getMessage()),
                    HttpStatus.FOUND);
        }
    }

    @Operation(summary = "Actualizar producto", description = "Actualiza los datos de un producto existente.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Actualizado OK"),
        @ApiResponse(responseCode = "404", description = "No encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflicto / Found"),
        @ApiResponse(responseCode = "500", description = "Error interno")
    })
    @PutMapping(value = "")
    public ResponseEntity<?> updateProducto(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Objeto Producto actualizado", required = true)
        @RequestBody Producto producto){
        try{
            productoBusiness.update(producto);
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

    @Operation(summary = "Eliminar producto", description = "Elimina un producto por su id.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Eliminado"),
        @ApiResponse(responseCode = "404", description = "No encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno")
    })
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteProducto(
        @Parameter(in = ParameterIn.PATH, name = "id", schema = @Schema(type = "integer"), required = true, description = "Identificador del producto.")
        @PathVariable long id){
        try{
            productoBusiness.delete(id);
            return new ResponseEntity<String>(HttpStatus.OK);
        }catch (BusinessException e){
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }catch (NotFoundException e) {
            return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

}

    /* 
    @DeleteMapping(value = "/by-name/{producto}")
    public ResponseEntity<?> deleteProducto(@PathVariable String producto){
        try{
            //ATENCION!! revisar la firma delete, porque quiere parsearse a un long
            return new ResponseEntity<>(productoBusiness.delete(producto), HttpStatus.OK);

        }catch (BusinessException e){
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }catch (NotFoundException e) {
            return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }
    */
