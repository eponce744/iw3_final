package com.iw3.tpfinal.integration.sap.model.controllers;

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
import com.iw3.tpfinal.grupoTeyo.controllers.Constants;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.BusinessException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.FoundException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.NotFoundException;
import com.iw3.tpfinal.grupoTeyo.util.IStandartResponseBusiness;
import com.iw3.tpfinal.integration.sap.model.OrdenSap;
import com.iw3.tpfinal.integration.sap.model.business.IOrdenSapBusiness;

@RestController
@RequestMapping(Constants.URL_INTEGRATION_SAP + "/ordensap")
public class OrdenSapRestController {

    @Autowired
    private IOrdenSapBusiness ordenSapBusiness;

    @Autowired
    private IStandartResponseBusiness response;

    //Listar todas las ordenes
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> list(){
        try {
            return new ResponseEntity<>(ordenSapBusiness.list(), HttpStatus.OK);
        }catch(BusinessException e){ //Devuelve un standart response
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()), 
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //Traer una orden por su ID
    @GetMapping(value = "/{codSap}")
    public ResponseEntity<?> loadByCode(@PathVariable String codSap){
        try{
            return new ResponseEntity<>(ordenSapBusiness.load(codSap), HttpStatus.OK);
        }catch (BusinessException e){
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    //Agregar una nueva orden
    @PostMapping(value = "")
    public ResponseEntity<?> addOrdenSap(@RequestBody OrdenSap orden){
        try{
            OrdenSap response = ordenSapBusiness.add(orden);
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("location", Constants.URL_INTEGRATION_SAP + "/" + response.getId());
            return new ResponseEntity<>(responseHeaders, HttpStatus.CREATED);
        }catch (BusinessException e){
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()), 
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (FoundException e) {
            return new ResponseEntity<>(response.build(HttpStatus.FOUND, e, e.getMessage()), 
                    HttpStatus.FOUND);
        }
    }

	@PostMapping(value = "/b2b")
	public ResponseEntity<?> addExternal(HttpEntity<String> httpEntity) {
		try {
			OrdenSap response = ordenSapBusiness.addExternal(httpEntity.getBody());
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.set("location", Constants.URL_INTEGRATION_SAP + "/ordenes/" + response.getId());
			return new ResponseEntity<>(responseHeaders, HttpStatus.CREATED);
		} catch (BusinessException e) {
			return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (FoundException e) {
			return new ResponseEntity<>(response.build(HttpStatus.FOUND, e, e.getMessage()), HttpStatus.FOUND);
		}
	}

}
