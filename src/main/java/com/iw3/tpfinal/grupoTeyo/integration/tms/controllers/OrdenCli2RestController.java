package com.iw3.tpfinal.grupoTeyo.integration.tms.controllers;

import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.iw3.tpfinal.grupoTeyo.controllers.Constants;
import com.iw3.tpfinal.grupoTeyo.integration.tms.model.OrdenTmsSlimV1JsonSerializer;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.BusinessException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.InvalidityException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.NotFoundException;
import com.iw3.tpfinal.grupoTeyo.model.business.interfaces.IDetalleBusiness;
import com.iw3.tpfinal.grupoTeyo.integration.tms.model.business.interfaces.IOrdenCli2Business;
import com.iw3.tpfinal.grupoTeyo.model.Orden;
import com.iw3.tpfinal.grupoTeyo.util.IStandartResponseBusiness;
import com.iw3.tpfinal.grupoTeyo.util.JsonUtiles;

import lombok.SneakyThrows;

import java.util.Map;
import java.util.LinkedHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping(Constants.URL_INTEGRATION_TMS + "/ordenes")
@Tag(name = "Integración TMS - Balanza", description = "Endpoints que utiliza el sistema de balanza/TMS para el flujo de pesaje y cierre de órdenes. "
		+ "Flujo soportado:\n"
		+ "1) pesaje-inicial: registra la tara (peso vacío) asociada a una patente; la orden debe estar en estado "
		+ "PENDIENTE_PESAJE_INICIAL. Devuelve la contraseña de activación (5 dígitos) y el Id-Orden en header.\n"
		+ "2) finalizar: registra el pesaje final y devuelve la orden con la conciliación/estado actualizado.\n\n"
		+ "Notas: las referencias a entidades externas en los payloads (cuando aplicable) se resuelven por código externo (string) en la capa de negocio.")
public class OrdenCli2RestController {

	@Autowired
	IOrdenCli2Business ordenCli2Business;

	@Autowired
	private IStandartResponseBusiness response;

	/*
	 * Enviamos la patente de un camion y su peso vacio (Tara), dicho camion tiene
	 * que estar ligado a una orden con estado PENDIENTE_PESAJE_INICIAL para que
	 * funcione. El devuelve un 200, el Id-Orden en el header y en el cuerpo la
	 * Password.
	 */
	@SneakyThrows
	@Operation(summary = "Registrar pesaje inicial (tara)", description = "Registra el pesaje inicial (tara) del camión identificado por su patente. "
			+ "Requisitos: existe una orden asociada a la patente en estado 'Pendiente de pesaje inicial'. "
			+ "Al registrarse la tara se genera y asocia a la orden una contraseña de activación (entero de 5 dígitos).\n\n"
			+ "Request Headers:\n" + " - Patente: patente del camión (string)\n"
			+ " - Peso-Inicial: peso registrado en kg (double)\n\n" + "Response:\n"
			+ " - 200 OK: JSON con la password, el codigo externo de la orden y header 'Id-Orden' con el id interno de la orden.\n"
			+ " - 400/404/500: según error (payload inválido, orden no encontrada, error interno).")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Tara registrada, devuelve password y header Id-Orden"),
			@ApiResponse(responseCode = "400", description = "Solicitud inválida"),
			@ApiResponse(responseCode = "404", description = "Orden o camión no encontrado"),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor") })
	@PostMapping(value = "/pesaje-inicial", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> registrarPesajeInicial(
			@Parameter(in = ParameterIn.HEADER, name = "Patente", description = "Patente del camión (string)", required = true, schema = @Schema(type = "string")) @RequestHeader("Patente") String patente,
			@Parameter(in = ParameterIn.HEADER, name = "Peso-Inicial", description = "Peso vacío (tara) en kilogramos (double)", required = true, schema = @Schema(type = "number", format = "double")) @RequestHeader("Peso-Inicial") Double pesoInicial) {
		try {
			Orden orden = ordenCli2Business.registrarPesajeInicial(patente, pesoInicial);
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.set("Id-Orden", String.valueOf(orden.getId()));

			// Armamos un cuerpo JSON estable, incluyendo CodSap cuando la orden es SAP
			Map<String, Object> body = new LinkedHashMap<>();
			body.put("Password", orden.getActivacionPassword());
			if (orden instanceof com.iw3.tpfinal.grupoTeyo.integration.sap.model.OrdenSap sap) {
				body.put("CodSap", sap.getCodSap());
			}

			return ResponseEntity.ok().headers(responseHeaders).body(body);
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

	@Autowired
	private IDetalleBusiness detalleBusiness;

	@SneakyThrows
	@Operation(summary = "Registrar pesaje final y finalizar orden", description = "Registra el pesaje final del camión identificado por la patente y finaliza la orden asociada.\n\n"
			+ "Request Headers:\n" + " - Patente: patente del camión (string)\n"
			+ " - Peso-Final: peso final en kg (double)\n\n" + "Response:\n"
			+ " - 200 OK: devuelve la orden resultante (formato JSON reducido TMS) con estado actualizado y datos de conciliación calculados.\n"
			+ " - 400/404/500: según error (por ejemplo: orden no encontrada, orden en estado incorrecto, error interno).")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Pesaje final registrado y orden finalizada"),
			@ApiResponse(responseCode = "400", description = "Solicitud inválida"),
			@ApiResponse(responseCode = "404", description = "Orden o camión no encontrado"),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor") })
	@PostMapping(value = "/finalizar", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> finalizarOrden(
			@Parameter(in = ParameterIn.HEADER, name = "Patente", description = "Patente del camión (string)", required = true, schema = @Schema(type = "string")) @RequestHeader("Patente") String patente,
			@Parameter(in = ParameterIn.HEADER, name = "Peso-Final", description = "Peso final en kilogramos (double)", required = true, schema = @Schema(type = "number", format = "double")) @RequestHeader("Peso-Final") Double pesoFinal) {
		try {
			Orden orden = ordenCli2Business.registrarPesajeFinal(patente, pesoFinal);
			StdSerializer<Orden> ser = new OrdenTmsSlimV1JsonSerializer(Orden.class, false, detalleBusiness);
			String result = JsonUtiles.getObjectMapper(Orden.class, ser, null).writeValueAsString(orden);
			return new ResponseEntity<>(result, HttpStatus.OK);
		} catch (NotFoundException e) {
			return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
		} catch (BusinessException e) {
			return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} 
	}

	/*
	 * @SneakyThrows
	 * 
	 * @PostMapping(value = "/finalizar", produces =
	 * MediaType.APPLICATION_JSON_VALUE) public ResponseEntity finalizarOrden(
	 * 
	 * @RequestHeader("Patente") String patente,
	 * 
	 * @RequestHeader("Peso-Final") Double pesoFinal) { Orden orden =
	 * ordenCli2Business.registrarPesajeFinal(patente,pesoFinal);
	 * StdSerializer<Orden> ser = new OrdenCli3SlimV1JsonSerializer(Orden.class,
	 * false); String result = JsonUtiles.getObjectMapper(Orden.class, ser,
	 * null).writeValueAsString(orden); return new ResponseEntity<>(result,
	 * HttpStatus.OK); }
	 */
}