package com.iw3.tpfinal.grupoTeyo.model.business.implementations;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iw3.tpfinal.grupoTeyo.model.Chofer;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.BusinessException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.FoundException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.NotFoundException;
import com.iw3.tpfinal.grupoTeyo.model.business.interfaces.IChoferBusiness;
import com.iw3.tpfinal.grupoTeyo.model.persistence.ChoferRepository;

import lombok.extern.slf4j.Slf4j;

@Service // Indica que esta clase es un servicio de negocio (y se va a instanciar cada vez que quiera un IChoferBusiness)
@Slf4j // Para usar el logger (log.info, log.error, etc)
public class ChoferBusiness implements IChoferBusiness{

    // Básicamente traigo el DAO (Repository) para poder usarlo en los métodos de la interfaz
    @Autowired // Para que Spring lo inyecte automáticamente
    private ChoferRepository choferDAO;

    @Override
    public List<Chofer> list() throws BusinessException {
        
        try {
            return choferDAO.findAll(); // findAll() ya viene implementado en JpaRepository
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).message(e.getMessage()).build();
            // Reenvío la excepción como BusinessException usando el patrón Builder
        }
        
    }

    @Override
    public Chofer load(long id) throws NotFoundException, BusinessException {
        Optional<Chofer> choferEncontrado;
        try {
            choferEncontrado = choferDAO.findById(id); // findById() ya viene implementado en JpaRepository
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).message(e.getMessage()).build();
            // Reenvío la excepción como BusinessException usando el patrón Builder
        }
        if (choferEncontrado.isEmpty()) {
            throw NotFoundException.builder().message("No se encontró el chofer con ID: " + id).build();
        }
        return choferEncontrado.get();
    }

    public Chofer load(String documento) throws NotFoundException, BusinessException {
        Optional<Chofer> choferEncontrado;
        try {
            choferEncontrado = choferDAO.findByDocumento(documento); // findById() ya viene implementado en JpaRepository
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).message(e.getMessage()).build();
            // Reenvío la excepción como BusinessException usando el patrón Builder
        }
        if (choferEncontrado.isEmpty()) {
            throw NotFoundException.builder().message("No se encontró el chofer con documento: " + documento).build();
        }
        return choferEncontrado.get();
    }

    @Override
    public Chofer add(Chofer chofer) throws FoundException, BusinessException {
        try {
            load(chofer.getId());
            throw FoundException.builder().message("El chofer con ID: " + chofer.getId() + " ya existe").build();
        } catch (NotFoundException e) {
        }
        try {
            load(chofer.getDocumento());
            throw FoundException.builder().message("El chofer con documento: " + chofer.getDocumento() + " ya existe").build();
        } catch (NotFoundException e) {
        }
        try {
            return choferDAO.save(chofer); // save() ya viene implementado en JpaRepository
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).message(e.getMessage()).build();
            // Reenvío la excepción como BusinessException usando el patrón Builder
        }
    }

    @Override
    public Chofer update(Chofer chofer) throws FoundException,NotFoundException, BusinessException {
        load(chofer.getId()); // Verifico que el chofer exista, si no lanza NotFoundException
        Optional<Chofer> choferEncontrado=null;
        try {
            choferEncontrado = choferDAO.findByDocumentoAndIdNot(chofer.getDocumento(), chofer.getId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).message(e.getMessage()).build();
        }

        if (choferEncontrado.isPresent()) {
            throw FoundException.builder().message("El chofer con documento: " + chofer.getDocumento() + " ya existe").build();
        }

        try {
            return choferDAO.save(chofer); // save() ya viene implementado en JpaRepository
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
            choferDAO.deleteById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).message(e.getMessage()).build();
            // Reenvío la excepción como BusinessException usando el patrón Builder
        }
    }

    @Override
    public void delete(Chofer chofer) throws NotFoundException, BusinessException {
        delete(chofer.getId());
    }

}
