package com.iw3.tpfinal.grupoTeyo.model.business.exceptions;

import lombok.Builder;
import java.io.Serial;

public class UnProcessableException extends Exception {

    @Serial
    private static final long serialVersionUID = 1L;

    @Builder
    public UnProcessableException(String message, Throwable ex) {
        super(message, ex);
    }
    @Builder
    public UnProcessableException(String message) {
        super(message);
    }
    @Builder
    public UnProcessableException(Throwable ex) {
        super(ex.getMessage(), ex);
    }
}
