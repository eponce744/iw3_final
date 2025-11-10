package com.iw3.tpfinal.grupoTeyo.integration.sap.model;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;

import org.apache.coyote.BadRequestException;

import com.iw3.tpfinal.grupoTeyo.model.Cliente;
import com.iw3.tpfinal.grupoTeyo.model.Camion;
import com.iw3.tpfinal.grupoTeyo.model.Chofer;
import com.iw3.tpfinal.grupoTeyo.model.Producto;
import com.iw3.tpfinal.grupoTeyo.model.Orden;
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
import com.iw3.tpfinal.grupoTeyo.integration.sap.model.ClienteSap;
import com.iw3.tpfinal.grupoTeyo.integration.sap.model.CamionSap;
import com.iw3.tpfinal.grupoTeyo.integration.sap.model.ChoferSap;
import com.iw3.tpfinal.grupoTeyo.integration.sap.model.ProductoSap;

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
		//Valores iniciales de la orden
		r.setUltimaMasaAcumulada(0.0);
		r.setUltimaDensidad(0.0);
		r.setUltimaTemperatura(0.0);
		r.setUtimoCaudal(0.0);
		
		JsonNode node = jp.getCodec().readTree(jp); //Instancia de un objeto que representa el JSON recibido
		
		//***Recepcion y guardado del valor recibido que se encuentre bajo el nombre de alguno de los tipos que se pasa en el get (getString, getDouble, etc)
		String numero = JsonUtiles.getString(node, "order_code,code,number_order,order_number".split(","),
				System.currentTimeMillis() + "");
		if(numero == null || numero.isEmpty()) {
			throw new IOException("Numero de Orden no puede ser nulo o vacio");
		}
		
	
		double preset = JsonUtiles.getDouble(node, "preset,load,to_load".split(","), 0);
		if(preset < 0) {
			throw new IOException("Preset no puede ser negativo");
		}

		Date fechaPrevistaCarga= JsonUtiles.getDate(node, "fechaPrevistaCarga,fecha_prevista_carga,scheduled_time,scheduled_at,turno".split(","), String.valueOf(new Date()));
		if(fechaPrevistaCarga == null) {
			throw new BadRequestException("Fecha prevista de carga falta o es invalida");
		}


		//Llamado a los Getter de la clase OrdenSap, pasando los valores recolectados del JSON
		//Recordar que OrdenSap hereda los atributos de la clase Orden y solo agrega codSap como nuevo atributo
		r.setCodSap(numero);
		r.setPreset(preset);
		r.setFechaPrevistaCarga(fechaPrevistaCarga);
		
		//Seteamos el estado como Pendiente de pesaje final
		r.setEstado(Orden.Estado.PENDIENTE_PESAJE_INICIAL);
		
		//***Recepcion y guardado de las relaciones con otras entidades (Cliente, Camion, Chofer, Producto)
		//Cliente (usar existente si id>0; crear si no)
		JsonNode clienteNode = JsonUtiles.getNode(node, "cliente,client,customer".split(","), null);
		if (clienteNode == null) throw new BadRequestException("Nodo Cliente no puede ser nulo");
		Long clienteId = readId(clienteNode, "id,client_id,customer_id".split(","));
		if (clienteId != null && clienteId > 0) {
			try {
				Cliente existing = clienteBusiness.load(clienteId);
				r.setCliente(existing);
			} catch (NotFoundException e) {
				throw new BadRequestException("Cliente id=" + clienteId + " no existe");
			} catch (BusinessException e) {
				throw new IOException("Error al cargar Cliente id=" + clienteId + ": " + e.getMessage(), e);
			}
		} else {
			String razonSocial = JsonUtiles.getString(clienteNode, "razonSocial,business_name,name".split(","), "");
			if (razonSocial == null || razonSocial.isEmpty()) throw new BadRequestException("Razon Social de Cliente no puede ser nulo o vacio");
			String codClienteSap = JsonUtiles.getString(clienteNode, "codigo_cliente,client_code,customer_code".split(","), "");
			if (codClienteSap == null || codClienteSap.isEmpty()) throw new BadRequestException("Codigo de Cliente no puede ser nulo o vacio");
			String contacto = JsonUtiles.getString(clienteNode, "contacto,contact".split(","), "");
			ClienteSap nuevo = new ClienteSap();
			nuevo.setRazonSocial(razonSocial);
			nuevo.setCodClienteSap(codClienteSap);
			nuevo.setContacto(contacto);
			try {
				r.setCliente(clienteBusiness.add(nuevo));
			} catch (com.iw3.tpfinal.grupoTeyo.model.business.exceptions.FoundException fe) {
				// si ya existía por razonSocial, lo cargo y lo uso
				try { r.setCliente(clienteBusiness.load(razonSocial)); }
				catch (Exception ex2) { throw new IOException("Conflicto creando Cliente: " + ex2.getMessage(), ex2); }
			} catch (BusinessException be) {
				throw new IOException("Error creando Cliente: " + be.getMessage(), be);
			}
		}

		//Camion (usar existente si id>0; crear si no)
		JsonNode camionNode = JsonUtiles.getNode(node, "camion,truck".split(","), null);
		if (camionNode == null) throw new BadRequestException("Nodo Camion no puede ser nulo");
		Long camionId = readId(camionNode, "id,truck_id".split(","));
		if (camionId != null && camionId > 0) {
			try {
				Camion existing = camionBusiness.load(camionId);
				r.setCamion(existing);
			} catch (NotFoundException e) {
				throw new BadRequestException("Camion id=" + camionId + " no existe");
			} catch (BusinessException e) {
				throw new IOException("Error al cargar Camion id=" + camionId + ": " + e.getMessage(), e);
			}
		} else {
			String patente = JsonUtiles.getString(camionNode, "patente,license_plate".split(","), "");
			if (patente == null || patente.isEmpty()) throw new BadRequestException("Patente de Camion no puede ser nulo o vacio");
			String codCamionSap = JsonUtiles.getString(camionNode, "codigo_camion,truck_code".split(","), "");
			if (codCamionSap == null || codCamionSap.isEmpty()) throw new BadRequestException("Codigo de Camion no puede ser nulo o vacio");
			String descripcion = JsonUtiles.getString(camionNode, "descripcion,description".split(","), null);
			int[] cisternado = JsonUtiles.getInt(camionNode, "cisternado,cisterns".split(","), 0);
			CamionSap nuevo = new CamionSap();
			nuevo.setPatente(patente);
			nuevo.setCodCamionSap(codCamionSap);
			nuevo.setDescripcion(descripcion);
			nuevo.setCisternado(cisternado);
			try {
				r.setCamion(camionBusiness.add(nuevo));
			} catch (com.iw3.tpfinal.grupoTeyo.model.business.exceptions.FoundException fe) {
				try { r.setCamion(camionBusiness.load(patente)); }
				catch (Exception ex2) { throw new IOException("Conflicto creando Camion: " + ex2.getMessage(), ex2); }
			} catch (BusinessException be) {
				throw new IOException("Error creando Camion: " + be.getMessage(), be);
			}
		}

		//Chofer (usar existente si id>0; crear si no)
		JsonNode choferNode = JsonUtiles.getNode(node, "chofer,driver".split(","), null);
		if (choferNode == null) throw new BadRequestException("Nodo Chofer no puede ser nulo");
		Long choferId = readId(choferNode, "id,driver_id".split(","));
		if (choferId != null && choferId > 0) {
			try {
				Chofer existing = choferBusiness.load(choferId);
				r.setChofer(existing);
			} catch (NotFoundException e) {
				throw new BadRequestException("Chofer id=" + choferId + " no existe");
			} catch (BusinessException e) {
				throw new IOException("Error al cargar Chofer id=" + choferId + ": " + e.getMessage(), e);
			}
		} else {
			String documento = JsonUtiles.getString(choferNode, "documento,document_id".split(","), "");
			if (documento == null || documento.isEmpty()) throw new BadRequestException("Documento de Chofer no puede ser nulo o vacio");
			String codChoferSap = JsonUtiles.getString(choferNode, "codigo_chofer,driver_code".split(","), "");
			if (codChoferSap == null || codChoferSap.isEmpty()) throw new BadRequestException("Codigo de Chofer no puede ser nulo o vacio");
			String nombre = JsonUtiles.getString(choferNode, "nombre,name".split(","), "");
			String apellido = JsonUtiles.getString(choferNode, "apellido,surname".split(","), "");
			ChoferSap nuevo = new ChoferSap();
			nuevo.setDocumento(documento);
			nuevo.setCodChoferSap(codChoferSap);
			nuevo.setNombre(nombre);
			nuevo.setApellido(apellido);
			try {
				r.setChofer(choferBusiness.add(nuevo));
			} catch (com.iw3.tpfinal.grupoTeyo.model.business.exceptions.FoundException fe) {
				try { r.setChofer(choferBusiness.load(documento)); }
				catch (Exception ex2) { throw new IOException("Conflicto creando Chofer: " + ex2.getMessage(), ex2); }
			} catch (BusinessException be) {
				throw new IOException("Error creando Chofer: " + be.getMessage(), be);
			}
		}

		//Producto (usar existente si id>0; crear si no)
		JsonNode productoNode = JsonUtiles.getNode(node, "producto,product".split(","), null);
		if (productoNode == null) throw new BadRequestException("Nodo Producto no puede ser nulo");
		Long productoId = readId(productoNode, "id,product_id".split(","));
		if (productoId != null && productoId > 0) {
			try {
				Producto existing = productoBusiness.load(productoId);
				r.setProducto(existing);
			} catch (NotFoundException e) {
				throw new BadRequestException("Producto id=" + productoId + " no existe");
			} catch (BusinessException e) {
				throw new IOException("Error al cargar Producto id=" + productoId + ": " + e.getMessage(), e);
			}
		} else {
			String nombreProducto = JsonUtiles.getString(productoNode, "nombre,name".split(","), "");
			if (nombreProducto == null || nombreProducto.isEmpty()) throw new BadRequestException("Nombre de Producto no puede ser nulo o vacio");
			String codProductoSap = JsonUtiles.getString(productoNode, "codigo_producto,product_code".split(","), "");
			if (codProductoSap == null || codProductoSap.isEmpty()) throw new BadRequestException("Codigo de Producto no puede ser nulo o vacio");
			String descripcionProducto = JsonUtiles.getString(productoNode, "descripcion,description".split(","), null);
			ProductoSap nuevo = new ProductoSap();
			nuevo.setNombre(nombreProducto);
			nuevo.setCodProductoSap(codProductoSap);
			nuevo.setDescripcion(descripcionProducto);
			try {
				r.setProducto(productoBusiness.add(nuevo));
			} catch (com.iw3.tpfinal.grupoTeyo.model.business.exceptions.FoundException fe) {
				try { r.setProducto(productoBusiness.load(nombreProducto)); }
				catch (Exception ex2) { throw new IOException("Conflicto creando Producto: " + ex2.getMessage(), ex2); }
			} catch (BusinessException be) {
				throw new IOException("Error creando Producto: " + be.getMessage(), be);
			}
		}

		return r;

	}

	private Long readId(JsonNode parent, String[] aliases) {
		if (parent == null || aliases == null) return null;
		for (String k : aliases) {
			JsonNode n = parent.get(k);
			if (n != null && n.isNumber()) {
				return n.asLong();
			}
		}
		return null;
	}

}
