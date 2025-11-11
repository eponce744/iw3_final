package com.iw3.tpfinal.grupoTeyo.integration.tms.model.business.implementations;

import com.iw3.tpfinal.grupoTeyo.integration.tms.model.business.interfaces.IOrdenCli2Business;
import com.iw3.tpfinal.grupoTeyo.model.Orden;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.BusinessException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.InvalidityException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.NotFoundException;
import com.iw3.tpfinal.grupoTeyo.model.business.implementations.DetalleBusiness;
import com.iw3.tpfinal.grupoTeyo.model.business.interfaces.IOrdenBusiness;
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

    /*
     * Este metodo recibe una patente de Camion y su Tara, corrobora que esté 
     * ligado a una orden con estado PENDIENTE_PESAJE_INICIAL y luego retorna la Orden pero con cambios:
     * Password = cargada
     * Pesaje inicial = cargado
     * Fecha Pesaje inicial = cargado
     * Estado = cambiado a  PESAJE_INICIAL_REGISTRADO
     * */
    @Override
    public Orden registrarPesajeInicial(String patente, Double pesajeInicial) throws BusinessException, NotFoundException, InvalidityException {
        Optional<Orden> ordenEncontrada;

        try {
            ordenEncontrada = ordenRepository.findByCamion_PatenteAndEstado(patente, Orden.Estado.PENDIENTE_PESAJE_INICIAL);
        } catch (Exception e) {
            //log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (ordenEncontrada.isEmpty()) {
            throw NotFoundException.builder().message("No se encuentra orden para cargar en camion con patente " + patente).build();
        }

        int password;
        do {
            password = Integer.parseInt(ActivacionPassword.generarActivacionPassword());
        } while (ordenRepository.findByActivacionPassword(password).isPresent());


        ordenEncontrada.get().setActivacionPassword(password);
        ordenEncontrada.get().setInicialPesaje(pesajeInicial);
        ordenEncontrada.get().setFechaPesajeInicial(new Date(System.currentTimeMillis()));
        ordenEncontrada.get().setEstado(Orden.Estado.PESAJE_INICIAL_REGISTRADO);
        ordenBusiness.update(ordenEncontrada.get());

        return ordenEncontrada.get();
    }

    @Override
    public Orden registrarPesajeFinal(String patente, Double pesajeFinal) throws BusinessException, NotFoundException {
        Optional<Orden> ordenActiva;

        try {
            ordenActiva = ordenRepository.findByCamion_PatenteAndEstado(patente, Orden.Estado.CERRADA_PARA_CARGA);
        } catch (Exception e) {
            throw BusinessException.builder().ex(e).build();
        }
        if (ordenActiva.isEmpty()) {
            throw NotFoundException.builder().message("La Orden no tiene el estado correcto o No se encuentra una Orden para el Camion con patente " + patente).build();
        }
        
        Orden orden = ordenActiva.get();
        orden.setFinalPesaje(pesajeFinal);
        orden.setFechaPesajeFinal(new Date(System.currentTimeMillis()));
        orden.setEstado(Orden.Estado.FINALIZADA);

        ordenBusiness.update(orden);

        return orden;
    }
}