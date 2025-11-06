package com.iw3.tpfinal.integration.sap.model;

import java.io.IOException;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.BusinessException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.NotFoundException;
import com.iw3.tpfinal.grupoTeyo.model.business.interfaces.ICamionBusiness;
import com.iw3.tpfinal.grupoTeyo.model.business.interfaces.IChoferBusiness;
import com.iw3.tpfinal.grupoTeyo.model.business.interfaces.IClienteBusiness;
import com.iw3.tpfinal.grupoTeyo.model.business.interfaces.IProductoBusiness;
import com.iw3.tpfinal.grupoTeyo.util.JsonUtiles;

public class OrdenSapJsonDeserializer extends StdDeserializer<OrdenSap> {


	protected OrdenSapJsonDeserializer(Class<?> vc) {
		super(vc);
	}
	
	private IClienteBusiness clienteBusiness;
	
	//Constructor que recibe el tipo de clase y la instancia de la interfaz usada en la deserialización.
	//Deserealizacion: JSON a clase Java
	public OrdenSapJsonDeserializer(Class<?> vc, IClienteBusiness clienteBusiness) {
		super(vc);
		this.clienteBusiness = clienteBusiness;
	}
	
	
	private ICamionBusiness camionBusiness;
	
	
	public OrdenSapJsonDeserializer(Class<?> vc, ICamionBusiness camionBusiness) {
		super(vc);
		this.camionBusiness = camionBusiness;
	}
	
	
	private IChoferBusiness choferBusiness;
	
	public OrdenSapJsonDeserializer(Class<?> vc, IChoferBusiness choferBusiness) {
		super(vc);
		this.choferBusiness = choferBusiness;
	}
	
	
	private IProductoBusiness productoBusiness;
	
	public OrdenSapJsonDeserializer(Class<?> vc, IProductoBusiness productoBusiness) {
		super(vc);
		this.productoBusiness = productoBusiness;
	}
	
	//El resultado de esto es la instanciacion de una clase Java a partir del Json recibido
	@Override
	public OrdenSap deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JacksonException {
		OrdenSap r = new OrdenSap();
		JsonNode node = jp.getCodec().readTree(jp); //Instancia de un objeto que representa el JSON recibido

		String numero = JsonUtiles.getString(node, "order_code,code,number_order,order_number".split(","),
				System.currentTimeMillis() + "");
		String fechaCarga = JsonUtiles.getString(node,
				"upload_date,date_upload".split(","), null);
		double preset = JsonUtiles.getDouble(node, "preset,load,to_load".split(","), 0);
		r.setCodSap(numero);
		r.setFechaPrevistaCarga(fechaCarga); //Ver como pasar de String a Date
		r.setPreset(preset);
		
		//Esto construye un nuevo objeto Camion a partir de la recepcion de su patente, 
		//si no viene una patente se carga el valor Null
		//igual para el resto de las clases relacionadas con la clase Orden
		String patenteCamion = JsonUtiles.getString(node, "patente,camion_id,identificator_truck,truck_patent".split(","), null);
		if (patenteCamion != null) {
			try {
				r.setCamion(camionBusiness.load(patenteCamion));
			} catch (NotFoundException | BusinessException e) {
			}
		}
		String documentoChofer = JsonUtiles.getString(node, "identity,identity_document".split(","), null);
		if (documentoChofer != null) {
			try {
				r.setChofer(choferBusiness.load(documentoChofer));
			} catch (NotFoundException | BusinessException e) {
			}
		}
		String razonSocial = JsonUtiles.getString(node, "company_name,name_company".split(","), null);
		if (razonSocial != null) {
			try {
				r.setCliente(clienteBusiness.load(razonSocial));
			} catch (NotFoundException | BusinessException e) {
			}
		}
		String nombreProducto = JsonUtiles.getString(node, "name_product,product_name".split(","), null);
		if (nombreProducto != null) {
			try {
				r.setProducto(productoBusiness.load(nombreProducto));
			} catch (NotFoundException | BusinessException e) {
			}
		}
		return r;
	}

}
