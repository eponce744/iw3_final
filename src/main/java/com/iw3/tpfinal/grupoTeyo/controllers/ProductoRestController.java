package com.iw3.tpfinal.grupoTeyo.controllers;

import com.iw3.tpfinal.grupoTeyo.model.Producto;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.BusinessException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.FoundException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.NotFoundException;
import com.iw3.tpfinal.grupoTeyo.model.business.interfaces.IProductoBusiness;
import com.iw3.tpfinal.grupoTeyo.util.IStandartResponseBusiness;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(Constants.URL_PRODUCTOS)
public class ProductoRestController {

    //Creo una instancia de la interface de Producto Business
    @Autowired
    private IProductoBusiness productoBusiness;
    @Autowired
    private IStandartResponseBusiness response;

    //produces = Formato en el que va a renderizar va a ser JSON, es decir que es el contetype de respuesta
    //ResponseEntity = se hace cargo de todo el mensaje completo(cabecera,cookie, status code real, y body si se quiere)
    //? = Puede ir cualquier cosa
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> list(){
        try {
            return new ResponseEntity<>(productoBusiness.list(), HttpStatus.OK);
        }catch(BusinessException e){ //Devuelve un standart response
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()), 
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> loadProducto(@PathVariable long id){
        try{
            return new ResponseEntity<>(productoBusiness.load(id), HttpStatus.OK);
        }catch (BusinessException e){
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/by-nombre/{producto}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> loadProductoByNombre(@PathVariable String producto){
        try{
            return new ResponseEntity<>(productoBusiness.load(producto), HttpStatus.OK);
        }catch (BusinessException e){
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    
    @PostMapping(value = "")
    public ResponseEntity<?> addProducto(@RequestBody Producto producto){
        try{
            Producto response = productoBusiness.add(producto);
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("location", Constants.URL_PRODUCTOS + "/" + response.getId());
            return new ResponseEntity<>(responseHeaders, HttpStatus.CREATED);
        }catch (BusinessException e){
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()), 
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (FoundException e) {
            return new ResponseEntity<>(response.build(HttpStatus.FOUND, e, e.getMessage()), 
                    HttpStatus.FOUND);
        }
    }

    @PutMapping(value = "")
    public ResponseEntity<?> updateProducto(@RequestBody Producto producto){
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

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteProducto(@PathVariable long id){
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
}
