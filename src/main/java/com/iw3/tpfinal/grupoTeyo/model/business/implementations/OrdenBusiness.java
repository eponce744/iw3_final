package com.iw3.tpfinal.grupoTeyo.model.business.implementations;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iw3.tpfinal.grupoTeyo.model.Orden;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.BusinessException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.FoundException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.NotFoundException;
import com.iw3.tpfinal.grupoTeyo.model.business.interfaces.IOrdenBusiness;
import com.iw3.tpfinal.grupoTeyo.model.persistence.OrdenRepository;

import lombok.extern.slf4j.Slf4j;

@Service // Indica que esta clase es un servicio de negocio (y se va a instanciar cada vez que quiera un IOrdenBusiness)
@Slf4j // Para usar el logger (log.info, log.error, etc)
public class OrdenBusiness implements IOrdenBusiness{

    // Básicamente traigo el DAO (Repository) para poder usarlo en los métodos de la interfaz
    @Autowired // Para que Spring lo inyecte automáticamente
    private OrdenRepository ordenDAO;

    @Override
    public List<Orden> list() throws BusinessException {

        try {
            return ordenDAO.findAll(); // findAll() ya viene implementado en JpaRepository
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).message(e.getMessage()).build();
            // Reenvío la excepción como BusinessException usando el patrón Builder
        }
        
    }

    @Override
    public Orden load(long id) throws NotFoundException, BusinessException {
        Optional<Orden> ordenEncontrada;
        try {
            ordenEncontrada = ordenDAO.findById(id); // findById() ya viene implementado en JpaRepository
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).message(e.getMessage()).build();
            // Reenvío la excepción como BusinessException usando el patrón Builder
        }
        if (ordenEncontrada.isEmpty()) {
            throw NotFoundException.builder().message("No se encontró la orden con ID: " + id).build();
        }
        return ordenEncontrada.get();
    }

    @Override
    public Orden add(Orden orden) throws FoundException, BusinessException {
        try {
            load(orden.getId());
            throw FoundException.builder().message("La orden con ID: " + orden.getId() + " ya existe").build();
        } catch (NotFoundException e) {
        }
        try {
            return ordenDAO.save(orden); // save() ya viene implementado en JpaRepository
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).message(e.getMessage()).build();
            // Reenvío la excepción como BusinessException usando el patrón Builder
        }
    }

    @Override
    public Orden update(Orden orden) throws NotFoundException, BusinessException {
        load(orden.getId()); // Verifico que la orden exista, si no lanza NotFoundException
        try {
            return ordenDAO.save(orden); // save() ya viene implementado en JpaRepository
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).message(e.getMessage()).build();
            // Reenvío la excepción como BusinessException usando el patrón Builder
        }
    }

    @Override
    public void delete(long id) throws NotFoundException, BusinessException {
        load(id);
        try {
            ordenDAO.deleteById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).message(e.getMessage()).build();
            // Reenvío la excepción como BusinessException usando el patrón Builder
        }
    }

    @Override
    public void delete(Orden orden) throws NotFoundException, BusinessException {
        delete(orden.getId());   
    }

}
