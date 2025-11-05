package com.iw3.tpfinal.integration.sap.model;

import java.io.IOException;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.BusinessException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.NotFoundException;
import com.iw3.tpfinal.grupoTeyo.util.JsonUtiles;

import com.iw3.tpfinal.grupoTeyo.model.business.ICategoryBusiness;

public class OrdenSapJsonDeserializer extends StdDeserializer<OrdenSap> {


	protected OrdenSapJsonDeserializer(Class<?> vc) {
		super(vc);
	}

	/*
	 * Se recibira, por ejemplo
	 * { 
	 * 	"order_code" : 12as52as1c,
	 * 	"identificator_truck" : "EE542SS52" 
	 * 	"identity_document" : "ARG45002542"
	 *  "company_name":"fuel company"
	 *  "code_product":"petrolARG254"
	 *  "upload_date":"2012-04-23T18:25:43.511Z"
	 *  "preset": "20.000"
	 *  }
	 * */
	
/*	FALTA HACER LOS BUSINESS DE Cliente, Chofer, Camion y Producto POR ESO TIRA ERRORES ESTA CLASE
 *  SACADO DE LOS VIDEOS DEL 2025-09-30
 * 
*	private IClienteBusiness clienteBusiness;
*	
*	public OrdenSapJsonDeserializer(Class<?> vc, IClienteBusiness clienteBusiness) {
*		super(vc);
*		this.clienteBusiness = clienteBusiness;
*	}
*	
*	
*	private ICamionBusiness camionBusiness;
*	
*	public OrdenSapJsonDeserializer(Class<?> vc, ICamionBusiness camionBusiness) {
*		super(vc);
*		this.camionBusiness = camionBusiness;
*	}
*	
*	
*	private IChoferBusiness choferBusiness;
*	
*	public OrdenSapJsonDeserializer(Class<?> vc, IChoferBusiness categoryBusiness) {
*		super(vc);
*		this.choferBusiness = choferBusiness;
*	}
*	
*	
*	private IProductoBusiness productoBusiness;
*	
*	public OrdenSapJsonDeserializer(Class<?> vc, ICategoryBusiness productoBusiness) {
*		super(vc);
*		this.productoBusiness = productoBusiness;
*	}
*/	
	
	@Override
	public OrdenSap deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JacksonException {
		OrdenSap r = new OrdenSap();
		JsonNode node = jp.getCodec().readTree(jp);

		String numero = JsonUtiles.getString(node, "order_code,code,number_order,order_number".split(","),
				System.currentTimeMillis() + "");
		String fechaCarga = JsonUtiles.getString(node,
				"upload_date,date_upload".split(","), null);
		double preset = JsonUtiles.getDouble(node, "preset,load,to_load".split(","), 0);
		boolean stock = JsonUtiles.getBoolean(node, "stock,in_stock".split(","), false);
		r.setCodSap1(numero);
		r.setp(patenteCamion);
		r.setProduct(productDesc);
		r.setPrecio(price);
		r.setStock(stock);
		
		String patenteCamion = JsonUtiles.getString(node, "patente,camion_id,identificator_truck,truck_patent".split(","), null);
		if (patenteCamion != null) {
			try {
				r.setCategory(camionBusiness.load(patenteCamion));
			} catch (NotFoundException | BusinessException e) {
			}
		}
		String documentoChofer = JsonUtiles.getString(node, "identity,identity_document".split(","), null);
		if (documentoChofer != null) {
			try {
				r.setCategory(choferBusiness.load(documentoChofer));
			} catch (NotFoundException | BusinessException e) {
			}
		}
		String razonSocial = JsonUtiles.getString(node, "company_name,name_company".split(","), null);
		if (razonSocial != null) {
			try {
				r.setCategory(clienteBusiness.load(razonSocial));
			} catch (NotFoundException | BusinessException e) {
			}
		}
		String codigoProducto = JsonUtiles.getString(node, "code_product,product_code".split(","), null);
		if (codigoProducto != null) {
			try {
				r.setCategory(productoBusiness.load(codigoProducto));
			} catch (NotFoundException | BusinessException e) {
			}
		}
		return r;
	}

}
