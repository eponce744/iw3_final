package com.iw3.tpfinal.grupoTeyo.model.business.exceptions;

import lombok.Builder;

import java.io.Serial;

public class InvalidityException extends Exception {

	@Serial
    private static final long serialVersionUID = 1L;

    @Builder
    public InvalidityException(String message, Throwable ex) {
        super(message, ex);
    }

    @Builder
    public InvalidityException(String message) {
        super(message);
    }

    @Builder
    public InvalidityException(Throwable ex) {
        super(ex.getMessage(), ex);
    }
}