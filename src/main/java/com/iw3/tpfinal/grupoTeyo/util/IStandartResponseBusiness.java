package com.iw3.tpfinal.grupoTeyo.util;

import org.springframework.http.HttpStatus;

public interface IStandartResponseBusiness {

    public StandartResponse build(HttpStatus httpStatus, Throwable ex, String message);

}
