package com.iw3.tpfinal.grupoTeyo.controllers;

import com.iw3.tpfinal.grupoTeyo.model.Producto;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.BusinessException;
import com.iw3.tpfinal.grupoTeyo.model.business.interfaces.IProductoBusiness;
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

    //produces = Formato en el que va a renderizar va a ser JSON, es decir que es el contetype de respuesta
    //ResponseEntity = se hace cargo de todo el mensaje completo(cabecera,cookie, status code real, y body si se quiere)
    //? = Puede ir cualquier cosa
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> list(){
        try {
            return new ResponseEntity<>(productoBusiness.list(), HttpStatus.OK);
        }catch(BusinessException e){
            //Hay que usar el StandartResponse y llamarlo como response!!
            // ATENTO!!! IMPLEMENTAR standartResponse, y llamar una instancia que se llame response
            //return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
            throw new UnsupportedOperationException("Unimplemented method 'loadProducto'");
        }
    }

    public ResponseEntity<?> loadProducto(){
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'loadProducto'");
    }

    @PostMapping(value = "")
    /*public ResponseEntity<?> addProducto(@RequestBody Producto producto){
        try{
            //Me parece que salta el el error porque falta implementar el add
            Producto response = productoBusiness.add(producto);
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("location", Constants.URL_PRODUCTOS + "/" + response.getId());
            return new ResponseEntity<>(responseHeaders, HttpStatus.CREATED);
        }catch (BusinessException e){
            //FALTA IMPLEMENTAR la instancia de standartResponse
            throw new UnsupportedOperationException("Unimplemented method 'loadProducto'");
        }
    }*/

    public ResponseEntity<?> updateProducto(){
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'loadProducto'");
    }

    public ResponseEntity<?> deleteProducto(){
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'loadProducto'");
    }
}
