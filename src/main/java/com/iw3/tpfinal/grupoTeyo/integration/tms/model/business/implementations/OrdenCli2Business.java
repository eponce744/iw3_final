/*package com.iw3.tpfinal.grupoTeyo.integration.tms.model.business.implementations;

import com.iw3.tpfinal.grupoTeyo.integration.tms.model.business.interfaces.IOrdenCli2Business;
import com.iw3.tpfinal.grupoTeyo.model.Orden;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.BusinessException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.FoundException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.NotFoundException;
import com.iw3.tpfinal.grupoTeyo.model.business.interfaces.IOrdenBusiness;
import com.iw3.tpfinal.grupoTeyo.model.persistence.DetalleRepository;
import com.iw3.tpfinal.grupoTeyo.model.persistence.OrdenRepository;
import com.iw3.tpfinal.grupoTeyo.util.ActivacionPassword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class OrdenCli2Business implements IOrdenCli2Business {

    @Autowired
    private OrdenRepository ordenRepository;

    @Autowired
    private IOrdenBusiness ordenBusiness;

    @Autowired
    private DetalleRepository detalleRepository;

    @Override
    public Orden registrarPesajeInicial(String patente, double pesoInicial) throws BusinessException, NotFoundException, FoundException {
        Optional<Orden> orden = Optional.empty();

        int password;
        do{
            password = Integer.parseInt(ActivacionPassword.generarActivacionPassword());
        }while (ordenRepository.findByActivarPassword(password).isPresent());

        orden.get().setActivarPassword(password);
        orden.get().setInicialPesaje(pesoInicial);
        orden.get().setFechaInicioCarga(new Date(System.currentTimeMillis()));
        orden.get().setEstado(Orden.Estado.PESAJE_INICIAL_REGISTRADO);
        ordenBusiness.update(orden.get());
        
        return orden.get();

    }

    public Orden registrarPesajeFinal(String patente, double pesoFinal) throws BusinessException, NotFoundException, FoundException{
        Optional<Orden> ordenActiva = Optional.empty();

        Orden orden = ordenActiva.get();
        orden.setFinalPesaje(pesoFinal);
        orden.setFechaPesajeFinal(new Date(System.currentTimeMillis()));
        orden.setEstado(Orden.Estado.PESAJE_FINAL_REGISTRADO);

        double pesajeInicial = orden.getInicialPesaje();
    }
}*/