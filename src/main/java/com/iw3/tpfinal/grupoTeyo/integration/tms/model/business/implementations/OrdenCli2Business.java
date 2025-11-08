/*package com.iw3.tpfinal.grupoTeyo.integration.tms.model.business.implementations;

import com.iw3.tpfinal.grupoTeyo.integration.tms.model.business.interfaces.IOrdenCli2Business;
import com.iw3.tpfinal.grupoTeyo.model.Orden;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.BusinessException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.FoundException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.NotFoundException;
import com.iw3.tpfinal.grupoTeyo.model.business.interfaces.IOrdenBusiness;
import com.iw3.tpfinal.grupoTeyo.model.persistence.DetalleRepository;
import com.iw3.tpfinal.grupoTeyo.model.persistence.OrdenRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class OrdenCli2Business implements IOrdenCli2Business {

    @Autowired
    private OrdenRepository ordenRepository;

    @Autowired
    private IOrdenBusiness ordenBusiness;

    @Autowired
    private DetalleRepository detalleRepository;

    @Override
    public Orden registrarPesajeInicial(String numeroOrden, double pesoInicial) throws BusinessException, NotFoundException, FoundException {

        return ordenBusiness.registrarPesajeInicialCli2(numeroOrden, pesoInicial);
    }
}*/
