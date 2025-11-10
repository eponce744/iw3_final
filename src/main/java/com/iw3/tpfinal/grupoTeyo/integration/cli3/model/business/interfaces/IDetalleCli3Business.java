package com.iw3.tpfinal.grupoTeyo.integration.cli3.model.business.interfaces;

import com.iw3.tpfinal.grupoTeyo.model.Detalle;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.*;

public interface IDetalleCli3Business {

    void add(Detalle detalle) throws FoundException, BusinessException, NotFoundException;
}