package com.iw3.tpfinal.grupoTeyo.auth;

import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class BadPasswordException extends Exception {

	private static final long serialVersionUID = 1L;

	@Builder
	public BadPasswordException(String message, Throwable ex) {
		super(message, ex);
	}
	@Builder
	public BadPasswordException(String message) {
		super(message);
	}
	@Builder
	public BadPasswordException(Throwable ex) {
		super(ex.getMessage(), ex);
	}
}

