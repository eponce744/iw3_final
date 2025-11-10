package com.iw3.tpfinal.grupoTeyo.integration.cli3.model.business.interfaces;

import com.iw3.tpfinal.grupoTeyo.model.Detalle;
import com.iw3.tpfinal.grupoTeyo.model.Orden;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.*;

public interface IOrdenCli3Business {

    public Orden validacionPassword(String codOrdenSap, int password) throws NotFoundException, BusinessException, InvalidityException;

    public Orden recepcionDetalles(Detalle detalle) throws NotFoundException, BusinessException, FoundException, UnProcessableException, InvalidityException;

    public Orden cierreOrden(Long ordenId) throws BusinessException, NotFoundException, InvalidityException;
}