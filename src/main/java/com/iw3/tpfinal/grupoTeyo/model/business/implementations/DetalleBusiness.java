package com.iw3.tpfinal.grupoTeyo.model.business.implementations;
import java.util.List;
import java.util.Optional;

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

    @Override // Método para listar todos los detalles de una orden específica
    public List<Detalle> listByOrden(long ordenId) throws NotFoundException, BusinessException{ 
        
        Optional<List<Detalle>> detallesEncontrados; // Lista para almacenar los detalles encontrados

        try {
            detallesEncontrados = detalleDAO.findByOrdenId(ordenId); // findByOrdenId() viene de la interfaz DetalleRepository
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).message(e.getMessage()).build();
            // Reenvío la excepción como BusinessException usando el patrón Builder
        }
        if (detallesEncontrados.isEmpty()) { // Si no se encontraron detalles
            throw NotFoundException.builder().message("No se encontraron detalles para la orden con ID: " + ordenId).build();
        }
        return detallesEncontrados.get();
    }

    @Override
    public Detalle load(long id) throws NotFoundException, BusinessException {
        Optional<Detalle> detalleEncontrado;
        try {
            detalleEncontrado = detalleDAO.findById(id); // findById() ya viene implementado en JpaRepository
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).message(e.getMessage()).build();
            // Reenvío la excepción como BusinessException usando el patrón Builder
        }
        if (detalleEncontrado.isEmpty()) {
            throw NotFoundException.builder().message("No se encontró el detalle con ID: " + id).build();
        }
        return detalleEncontrado.get();
    }

    @Override
    public Detalle add(Detalle detalle) throws FoundException, BusinessException {
        try {
            load(detalle.getId());
            throw FoundException.builder().message("El detalle con ID: " + detalle.getId() + " ya existe").build();
        } catch (NotFoundException e) {
        }
        try {
            return detalleDAO.save(detalle); // save() ya viene implementado en JpaRepository
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).message(e.getMessage()).build();
            // Reenvío la excepción como BusinessException usando el patrón Builder
        }
    }


}
