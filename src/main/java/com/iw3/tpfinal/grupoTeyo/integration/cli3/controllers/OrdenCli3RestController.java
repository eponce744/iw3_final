package com.iw3.tpfinal.grupoTeyo.integration.cli3.controllers;

import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.iw3.tpfinal.grupoTeyo.controllers.BaseRestController;
import com.iw3.tpfinal.grupoTeyo.controllers.Constants;
import com.iw3.tpfinal.grupoTeyo.integration.cli3.model.OrdenCli3SlimV1JsonSerializer;
import com.iw3.tpfinal.grupoTeyo.integration.cli3.model.business.interfaces.IOrdenCli3Business;
import com.iw3.tpfinal.grupoTeyo.model.Detalle;
import com.iw3.tpfinal.grupoTeyo.model.Orden;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.BusinessException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.FoundException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.InvalidityException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.NotFoundException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.UnProcessableException;
import com.iw3.tpfinal.grupoTeyo.util.IStandartResponseBusiness;
import com.iw3.tpfinal.grupoTeyo.util.JsonUtiles;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.SneakyThrows;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping(Constants.URL_INTEGRATION_CLI3 + "/ordenes")
@Tag(name = "Integración CLI3 - Sistema de carga", description = "Endpoints usados por el sistema de control de carga (CLI3). Los identificadores externos deben enviarse como códigos (string).")
@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLI3')")
public class OrdenCli3RestController extends BaseRestController {

	@Autowired
	private IOrdenCli3Business ordenCli3Business;

	@Autowired
	private IStandartResponseBusiness response;
	
	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	@SneakyThrows
	@Operation(summary = "Validar contraseña de activación", description = "Valida la contraseña de activación (5 dígitos) para la orden identificada por su código externo (CodOrdenSap). Si es válida devuelve la orden con los datos necesarios para iniciar la carga (incluye preset cuando corresponda).")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Contraseña válida, devuelve orden (JSON)"),
			@ApiResponse(responseCode = "400", description = "Solicitud inválida (headers faltantes o inválidos)"),
			@ApiResponse(responseCode = "404", description = "Orden no encontrada o en estado incorrecto"),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor") })
	@PostMapping(value = "/validacion", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> validatePassword(
			@Parameter(in = ParameterIn.HEADER, name = "CodOrdenSap", description = "Código externo de la orden (string).", required = true, schema = @Schema(type = "string")) @RequestHeader("CodOrdenSap") String codOrdenSap,
			@Parameter(in = ParameterIn.HEADER, name = "Password", description = "Contraseña de activación (5 dígitos).", required = true, schema = @Schema(type = "integer", format = "int32")) @RequestHeader("Password") Integer password) {
		try {
			Orden orden = ordenCli3Business.validacionPassword(codOrdenSap, password);
			StdSerializer<Orden> ser = new OrdenCli3SlimV1JsonSerializer(Orden.class, false);
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

	@SneakyThrows
	@Operation(summary = "Recibir detalle de carga (tiempo real)", description = "Recibe un registro Detalle con masaAcumulada, densidad, temperatura y caudal. Las reglas de validación y muestreo se aplican en la capa de negocio; se descartan registros inválidos.")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Detalle recibido y procesado"),
			@ApiResponse(responseCode = "400", description = "Payload inválido"),
			@ApiResponse(responseCode = "404", description = "Orden no encontrada"),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor") })
	@PostMapping(value = "/detalle", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> receiveLoadData(
			@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Registro de detalle con masaAcumulada, densidad, temperatura y caudal.", required = true) @RequestBody Detalle detalle) {
		try {
			Orden orden = ordenCli3Business.recepcionDetalles(detalle);
			messagingTemplate.convertAndSend("/topic/mass", detalle);
			
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.set("Order-Id", String.valueOf(orden.getId()));
			return new ResponseEntity<>(responseHeaders, HttpStatus.OK);
		} catch (NotFoundException e) {
			return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
		} catch (UnProcessableException e) {
			return new ResponseEntity<>(response.build(HttpStatus.UNPROCESSABLE_ENTITY, e, e.getMessage()),
					HttpStatus.UNPROCESSABLE_ENTITY);
		} catch (BusinessException e) {
			return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (InvalidityException e) {
			return new ResponseEntity<>(response.build(HttpStatus.BAD_REQUEST, e, e.getMessage()),
					HttpStatus.BAD_REQUEST);
		}
	}

	@SneakyThrows
	@Operation(summary = "Cerrar orden (bloquear recepción de detalles)", description = "Cierra la orden indicada por su id interno (OrderId). Después del cierre no se aceptan nuevos registros de detalle; la orden queda lista para pesaje final.")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Orden cerrada correctamente"),
			@ApiResponse(responseCode = "400", description = "Solicitud inválida o estado no permitido"),
			@ApiResponse(responseCode = "404", description = "Orden no encontrada"),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor") })
	@PostMapping(value = "/cerrar", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> closeOrder(
			@Parameter(in = ParameterIn.HEADER, name = "OrderId", description = "Id interno de la orden (long).", required = true, schema = @Schema(type = "integer", format = "int64")) @RequestHeader("OrderId") Long orderId) {
		try {
			Orden orden = ordenCli3Business.cierreOrden(orderId);
			// Construimos un JSON mínimo con la patente del camión asociado
			String patente = orden.getCamion() != null ? orden.getCamion().getPatente() : null;
			String json = "{\"orderId\":" + orden.getId() + ",\"patente\":" + (patente == null ? "null" : ("\""+patente+"\"")) + "}";
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.set("Order-Id", String.valueOf(orden.getId()));
			return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
		} catch (NotFoundException e) {
			return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
		} catch (BusinessException e) {
			return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
