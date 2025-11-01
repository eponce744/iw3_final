package com.iw3.tpfinal.grupoTeyo.model.business.implementations;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iw3.tpfinal.grupoTeyo.model.Detalle;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.BusinessException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.FoundException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.NotFoundException;
import com.iw3.tpfinal.grupoTeyo.model.business.interfaces.IDetalleBusiness;
import com.iw3.tpfinal.grupoTeyo.model.persistence.DetalleRepository;

import lombok.extern.slf4j.Slf4j;

@Service // Indica que esta clase es un servicio de negocio (y se va a instanciar cada vez que quiera un IDetalleBusiness)
@Slf4j // Para usar el logger (log.info, log.error, etc)
public class DetalleBusiness implements IDetalleBusiness{

    // Básicamente traigo el DAO (Repository) para poder usarlo en los métodos de la interfaz
    @Autowired // Para que Spring lo inyecte automáticamente
    private DetalleRepository detalleDAO;

    @Override
    public List<Detalle> listByOrden(long ordenId) throws NotFoundException, BusinessException{
        
        List<Detalle> detallesEncontrados; // Lista para almacenar los detalles encontrados

        try {
            detallesEncontrados = detalleDAO.findByOrdenId(ordenId); // findByOrdenId() viene de la interfaz DetalleRepository
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).message(e.getMessage()).build();
            // Reenvío la excepción como BusinessException usando el patrón Builder
        }
        if (detallesEncontrados == null || detallesEncontrados.isEmpty()) { // Si no se encontraron detalles
            throw NotFoundException.builder().message("No se encontraron detalles para la orden con ID: " + ordenId).build();
        }
        return detallesEncontrados;
    
    }

    @Override
    public Detalle load(long id) throws NotFoundException, BusinessException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'load'");
    }

    @Override
    public Detalle add(Detalle detalle) throws FoundException, BusinessException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'add'");
    }


}
