package com.iw3.tpfinal.grupoTeyo.integration.cli3.model.business.implementations;

import com.iw3.tpfinal.grupoTeyo.integration.cli3.model.business.interfaces.IOrdenCli3Business;
import com.iw3.tpfinal.grupoTeyo.integration.sap.model.OrdenSap;
import com.iw3.tpfinal.grupoTeyo.integration.sap.model.persistence.OrdenSapRepository;
import com.iw3.tpfinal.grupoTeyo.model.Detalle;
import com.iw3.tpfinal.grupoTeyo.model.Orden;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.*;
import com.iw3.tpfinal.grupoTeyo.model.business.implementations.OrdenBusiness;
import com.iw3.tpfinal.grupoTeyo.model.business.interfaces.IOrdenBusiness;
import com.iw3.tpfinal.grupoTeyo.model.persistence.OrdenRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class OrdenCli3Business implements IOrdenCli3Business {

	@Autowired
    private OrdenSapRepository ordenSapDAO;

    @Autowired
    private OrdenBusiness ordenBusiness;

    @Override
    public Orden validacionPassword(String codOrdenSap, int password) throws NotFoundException, BusinessException, InvalidityException {
        Optional<OrdenSap> ordenSap;

        //Buscamos la orden a partir de su codigo externo de Orden y comparamos la password 
        //con la que nos envia el sistema de control y tambien verificamos que la orden 
        //esté en estado 2 (con el pesaje inicial registrado)
        try {
        	ordenSap = ordenSapDAO.findOneByCodSap(codOrdenSap);
        } catch (BusinessException e) {
            log.error(e.getMessage(), e);
            throw new BusinessException("Error al recuperar orden", e);
        } catch (NotFoundException e) {
            log.error(e.getMessage(), e);
            throw new NotFoundException("No se encontro la orden con codigo" + codOrdenSap, e);
        }
        
        if (ordenSap.get().getEstado() != Orden.Estado.PESAJE_INICIAL_REGISTRADO) {
            throw new InvalidityException("Estado de orden no válido");

        Integer contraseniaOrdenSap = ordenSap.get().getActivarPassword();
        if (contraseniaOrdenSap == password) {
        	return ordenBusiness.load(ordenSap.get().getId());
        }
      }
        return null;
    }

    @Override
    public Orden recepcionDetalles(Detalle detalle) throws NotFoundException, BusinessException, UnProcessableException, InvalidityException {
        Orden ordenEncontrada = ordenBusiness.load(detalle.getOrden().getId());

        // Validaciones del estado de la Orden
        if (ordenEncontrada.getEstado() != Orden.Estado.PESAJE_INICIAL_REGISTRADO) {
            throw new InvalidityException("Estado de orden no válido");
        }
        if (detalle.getCaudal() < 0) {
            throw new UnProcessableException("Caudal no válido");
        }
        if (detalle.getMasaAcumulada() < ordenEncontrada.getUltimaMasaAcumulada()) {
            throw new UnProcessableException("Masa acumulada no válida");
        }

        // Actualizacion de cabecera de la Orden
        ordenEncontrada.setUltimaMasaAcumulada(detalle.getMasaAcumulada());
        ordenEncontrada.setUltimaDensidad(detalle.getDensidad());
        ordenEncontrada.setUltimaTemperatura(detalle.getTemperatura());
        ordenEncontrada.setUtimoCaudal(detalle.getCaudal());
        ordenDAO.save(ordenEncontrada);

        return ordenEncontrada;
    }

    @Override
    public Orden cierreOrden(Long ordenId) throws BusinessException, NotFoundException, InvalidityException {
        Optional<Orden> orden;

        try {
            orden = ordenDAO.findByIdAndEstado(ordenId, Orden.Estado.PESAJE_INICIAL_REGISTRADO);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BusinessException("Error al recuperar orden", e);
        }
        if (orden.isEmpty()) {
            throw new NotFoundException("Orden no econtrada");
        }
        //Chequeamos que la orden este en estado "Pesaje inicial registrado"
        chequeoEstadoOrden(orden.get());
        //Cambiamos el estado de la orden a "Cerrada"
        orden.get().setEstado(Orden.Estado.CERRADA_PARA_CARGA);
        //Desactivamos el ingreso de Password para la carga de la orden
        orden.get().setActivarPassword(null);
        return ordenDAO.save(orden.get());
    }
    
    private void chequeoEstadoOrden(Orden orden) throws InvalidityException {
        if (orden.getEstado() != Orden.Estado.PESAJE_INICIAL_REGISTRADO) {
            throw new InvalidityException("Estado de orden no válido");
        }
    }
	
}
