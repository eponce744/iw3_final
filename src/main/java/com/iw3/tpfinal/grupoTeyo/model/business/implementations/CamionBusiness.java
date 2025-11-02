package com.iw3.tpfinal.grupoTeyo.model.business.implementations;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iw3.tpfinal.grupoTeyo.model.Camion;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.BusinessException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.FoundException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.NotFoundException;
import com.iw3.tpfinal.grupoTeyo.model.business.interfaces.ICamionBusiness;
import com.iw3.tpfinal.grupoTeyo.model.persistence.CamionRepository;

import lombok.extern.slf4j.Slf4j;

@Service // Indica que esta clase es un servicio de negocio (y se va a instanciar cada vez que quiera un ICamionBusiness)
@Slf4j // Para usar el logger (log.info, log.error, etc)
public class CamionBusiness implements ICamionBusiness{

    // Básicamente traigo el DAO (Repository) para poder usarlo en los métodos de la interfaz
    @Autowired // Para que Spring lo inyecte automáticamente
    private CamionRepository camionDAO;

    @Override
    public List<Camion> list() throws BusinessException {
        
        try {
            return camionDAO.findAll(); // findAll() ya viene implementado en JpaRepository
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).message(e.getMessage()).build();
            // Reenvío la excepción como BusinessException usando el patrón Builder
        }
        
    }

    @Override
    public Camion load(long id) throws NotFoundException, BusinessException {
        Optional<Camion> camionEncontrado;
        try {
            camionEncontrado = camionDAO.findById(id); // findById() ya viene implementado en JpaRepository
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).message(e.getMessage()).build();
            // Reenvío la excepción como BusinessException usando el patrón Builder
        }
        if (camionEncontrado.isEmpty()) {
            throw NotFoundException.builder().message("No se encontró el camión con ID: " + id).build();
        }
        return camionEncontrado.get();
    }

    @Override
    public Camion load(String patente) throws NotFoundException, BusinessException{
        Optional<Camion> camionEncontrado;
        try {
            camionEncontrado = camionDAO.findByPatente(patente); // findById() ya viene implementado en JpaRepository
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).message(e.getMessage()).build();
            // Reenvío la excepción como BusinessException usando el patrón Builder
        }
        if (camionEncontrado.isEmpty()) {
            throw NotFoundException.builder().message("No se encontró el camión con patente: " + patente).build();
        }
        return camionEncontrado.get();
    }

    @Override
    public Camion add(Camion camion) throws FoundException, BusinessException {
        try {
            load(camion.getId());
            throw FoundException.builder().message("El camión con ID: " + camion.getId() + " ya existe").build();
        } catch (NotFoundException e) {
        }
        try {
            load(camion.getPatente());
            throw FoundException.builder().message("El camión con patente: " + camion.getPatente() + " ya existe").build();
        } catch (NotFoundException e) {
        }
        try {
            return camionDAO.save(camion); // save() ya viene implementado en JpaRepository
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).message(e.getMessage()).build();
            // Reenvío la excepción como BusinessException usando el patrón Builder
        }
    }

    @Override
    public Camion update(Camion camion) throws FoundException, NotFoundException, BusinessException {
        load(camion.getId()); // Verifico que el camión exista, si no lanza NotFoundException
        Optional<Camion> camionEncontrado=null;
        try {
            camionEncontrado = camionDAO.findByPatenteAndIdNot(camion.getPatente(), camion.getId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).message(e.getMessage()).build();
        }

        if (camionEncontrado.isPresent()) {
            throw FoundException.builder().message("El camión con patente: " + camion.getPatente() + " ya existe").build();
        }

        try {
            return camionDAO.save(camion); // save() ya viene implementado en JpaRepository
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
            camionDAO.deleteById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).message(e.getMessage()).build();
            // Reenvío la excepción como BusinessException usando el patrón Builder
        }
    }

    @Override
    public void delete(Camion camion) throws NotFoundException, BusinessException {
        delete(camion.getId());
    }

}
