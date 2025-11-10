package com.iw3.tpfinal.grupoTeyo.integration.tms.model.business.interfaces;

import com.iw3.tpfinal.grupoTeyo.model.Orden;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.BusinessException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.FoundException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.NotFoundException;

public interface IOrdenCli2Business {

    Orden registrarPesajeInicial(String numeroOrden, Double pesajeInicial) throws BusinessException, NotFoundException;

    Orden registrarPesajeFinal(String numeroOrden, Double pesajeFinal) throws BusinessException, NotFoundException;
}
