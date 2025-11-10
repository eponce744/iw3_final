package com.iw3.tpfinal.grupoTeyo.integration.cli3.model.business.implementations;

import com.iw3.tpfinal.grupoTeyo.controllers.Constants;
import com.iw3.tpfinal.grupoTeyo.integration.cli3.model.business.interfaces.IOrdenCli3Business;
import com.iw3.tpfinal.grupoTeyo.integration.sap.model.OrdenSap;
import com.iw3.tpfinal.grupoTeyo.integration.sap.model.persistence.OrdenSapRepository;
import com.iw3.tpfinal.grupoTeyo.model.Detalle;
import com.iw3.tpfinal.grupoTeyo.model.Orden;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.*;
import com.iw3.tpfinal.grupoTeyo.model.business.interfaces.IDetalleBusiness;
import com.iw3.tpfinal.grupoTeyo.model.business.interfaces.IOrdenBusiness;
import com.iw3.tpfinal.grupoTeyo.model.persistence.OrdenRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@Slf4j
public class OrdenCli3Business implements IOrdenCli3Business {

	@Autowired
    private OrdenSapRepository ordenSapDAO;
	
	@Autowired
	private OrdenRepository ordenDAO;

    @Autowired
    private IOrdenBusiness ordenBusiness;
    
    @Autowired
    private IDetalleBusiness detalleBusiness;

    /*
     * Validamos la orden, enviando el codigoExterno y la password. 
     * El método devuelve una orden
     * */
    @Override
    public Orden validacionPassword(String codOrdenSap, int password)
            throws NotFoundException, BusinessException, InvalidityException {

        Optional<OrdenSap> ordenSap = ordenSapDAO.findOneByCodSap(codOrdenSap);
        if (ordenSap.isEmpty()) {
            throw new NotFoundException("No se encontro la orden con codigo " + codOrdenSap);
        }

        Orden orden = ordenBusiness.load(ordenSap.get().getId());

        if (orden.getEstado() != Orden.Estado.PESAJE_INICIAL_REGISTRADO) {
            throw new InvalidityException("Estado de orden no válido");
        }

        Integer contraseniaOrdenSap = orden.getActivacionPassword();
        if (contraseniaOrdenSap != null && contraseniaOrdenSap == password) {
            return orden; 
        }
        return null;
    }

    @Override
    public Orden recepcionDetalles(Detalle detalle) throws NotFoundException, BusinessException, UnProcessableException, InvalidityException {
        //Traemos la orden asociada al Detalle, para asegurarnos de que ese Detalle esta asociado a una Orden
    	Orden ordenEncontrada = ordenBusiness.load(detalle.getOrden().getId());

        // Validaciones del Detalle
        if (ordenEncontrada.getEstado() != Orden.Estado.PESAJE_INICIAL_REGISTRADO) {
            throw new InvalidityException("Estado de orden no válido");
        }
        else if (detalle.getCaudal() < 0) {
            throw new UnProcessableException("Caudal no válido");
        }
        else if (detalle.getMasaAcumulada() < ordenEncontrada.getUltimaMasaAcumulada()) {
            throw new UnProcessableException("Masa acumulada no válida");
        }

        Date currentTime = new Date(System.currentTimeMillis());
        
        // Actualizacion de la Orden
        ordenEncontrada.setUltimaMasaAcumulada(detalle.getMasaAcumulada());
        ordenEncontrada.setUltimaDensidad(detalle.getDensidad());
        ordenEncontrada.setUltimaTemperatura(detalle.getTemperatura());
        ordenEncontrada.setUtimoCaudal(detalle.getCaudal());
        ordenEncontrada.setFechaFinCarga(currentTime);
        //ordenDAO.save(ordenEncontrada);
        ordenBusiness.update(ordenEncontrada);
        
        detalle.setFechaUltimoDato(currentTime);
        try{
        	detalleBusiness.add(detalle);
        }catch (BusinessException e){
        } catch (FoundException e) {
        }
        


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
        //Cambiamos el estado de la orden a "Cerrada"
        orden.get().setEstado(Orden.Estado.CERRADA_PARA_CARGA);
        //Desactivamos el ingreso de Password para la carga de la orden
        orden.get().setActivacionPassword(null);
        return ordenDAO.save(orden.get());
        }
	
}
