package com.iw3.tpfinal.grupoTeyo.util;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StandartResponse { //Sirve para estandarizar las respuestas de la API con message y devInfo
	private String message;

	@JsonIgnore
	private Throwable ex;

	@JsonIgnore
	private HttpStatus httpStatus;

	public int getCode() {
		return httpStatus.value();
	}

	@JsonIgnore
	private boolean devInfoEnabled;

	public String getDevInfo() {
		if (devInfoEnabled) { //Activo o desactivo la info para desarrolladores (esta en application.properties)
			if (ex != null) {
				return ExceptionUtils.getStackTrace(ex);
			} else {
				return "No stack trace";
			}
		} else {
			return null;
		}
	}

	public String getMessage() {
		if (message != null)
			return message;
		if (ex != null)
			return ex.getMessage();
		return null;
	}

}
