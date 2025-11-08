package com.iw3.tpfinal.integration.sap.model;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.BusinessException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.NotFoundException;
import com.iw3.tpfinal.grupoTeyo.model.business.interfaces.ICamionBusiness;
import com.iw3.tpfinal.grupoTeyo.model.business.interfaces.IChoferBusiness;
import com.iw3.tpfinal.grupoTeyo.model.business.interfaces.IClienteBusiness;
import com.iw3.tpfinal.grupoTeyo.model.business.interfaces.IProductoBusiness;
import com.iw3.tpfinal.grupoTeyo.util.JsonUtiles;

public class OrdenSapJsonDeserializer extends StdDeserializer<OrdenSap> {

	//----------------Constructores---------------
	//Reciben el tipo de clase y la instancia de la interfaz usada en la deserialización.
	//Deserealizacion: JSON a clase Java

	protected OrdenSapJsonDeserializer(Class<?> vc) {
		super(vc);
	}
	
	private IClienteBusiness clienteBusiness;
	private ICamionBusiness camionBusiness;
	private IChoferBusiness choferBusiness;
	private IProductoBusiness productoBusiness;
	
	public OrdenSapJsonDeserializer(
			Class<?> vc, 
			IClienteBusiness clienteBusiness, 
			ICamionBusiness camionBusiness, 
			IChoferBusiness choferBusiness, 
			IProductoBusiness productoBusiness) {
		super(vc);
		this.clienteBusiness = clienteBusiness;
		this.camionBusiness = camionBusiness;
		this.choferBusiness = choferBusiness;
		this.productoBusiness = productoBusiness;
	}
	
	
	//---------------- Metodo Deserealizador ---------------
	
	//El resultado de esto es la instanciacion de una clase Java a partir del Json recibido
	@Override
	public OrdenSap deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JacksonException {
		OrdenSap r = new OrdenSap();
		JsonNode node = jp.getCodec().readTree(jp); //Instancia de un objeto que representa el JSON recibido
		
		ObjectMapper mapper = (ObjectMapper) jp.getCodec(); // reutiliza el mismo ObjectMapper
		//En éste caso es equivalente mapper.readTree(jp); a jp.getCodec().readTree(jp)

		//***Recepcion y guardado del valor recibido que se encuentre bajo el nombre de alguno de los tipos que se pasa en el get (getString, getDouble, etc)
		String numero = JsonUtiles.getString(node, "order_code,code,number_order,order_number".split(","),
				System.currentTimeMillis() + "");
	
		double preset = JsonUtiles.getDouble(node, "preset,load,to_load".split(","), 0);
		
		//Llamado a los Getter de la clase OrdenSap, pasando los valores recolectados del JSON
		//Recordar que OrdenSap hereda los atributos de la clase Orden y solo agrega codSap como nuevo atributo
		r.setCodSap(numero);
		r.setPreset(preset);
		
		//Las fechas en el JSON deben ser exactamente del formato "2025-11-08T14:35:00"
		if (node.has("fechaPrevistaCarga")) {
			try {
				Date fecha = mapper.convertValue(node.get("fechaPrevistaCarga"), Date.class);
				r.setFechaPrevistaCarga(fecha);
			} catch (IllegalArgumentException e) {
				System.err.println("Error al convertir la fecha: " + e.getMessage());
			}
		}
		
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
