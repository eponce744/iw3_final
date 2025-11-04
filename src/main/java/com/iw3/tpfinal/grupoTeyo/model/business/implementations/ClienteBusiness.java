package com.iw3.tpfinal.grupoTeyo.model.business.implementations;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iw3.tpfinal.grupoTeyo.model.Cliente;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.BusinessException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.FoundException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.NotFoundException;
import com.iw3.tpfinal.grupoTeyo.model.business.interfaces.IClienteBusiness;
import com.iw3.tpfinal.grupoTeyo.model.persistence.ClienteRepository;

import lombok.extern.slf4j.Slf4j;

@Service // Indica que esta clase es un servicio de negocio (y se va a instanciar cada vez que quiera un IClienteBusiness)
@Slf4j // Para usar el logger (log.info, log.error, etc)
public class ClienteBusiness implements IClienteBusiness{

    // Básicamente traigo el DAO (Repository) para poder usarlo en los métodos de la interfaz
    @Autowired // Para que Spring lo inyecte automáticamente
    private ClienteRepository clienteDAO;

    @Override
    public List<Cliente> list() throws BusinessException {

        try {
            return clienteDAO.findAll(); // findAll() ya viene implementado en JpaRepository
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).message(e.getMessage()).build();
            // Reenvío la excepción como BusinessException usando el patrón Builder
        }
        
    }

    @Override
    public Cliente load(long id) throws NotFoundException, BusinessException {
        Optional<Cliente> clienteEncontrado;
        try {
            clienteEncontrado = clienteDAO.findById(id); // findById() ya viene implementado en JpaRepository
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).message(e.getMessage()).build();
            // Reenvío la excepción como BusinessException usando el patrón Builder
        }
        if (clienteEncontrado.isEmpty()) {
            throw NotFoundException.builder().message("No se encontró el cliente con ID: " + id).build();
        }
        return clienteEncontrado.get();
    }

    @Override
    public Cliente load(String razonSocial) throws NotFoundException, BusinessException {
        Optional<Cliente> clienteEncontrado;
        try {
            clienteEncontrado = clienteDAO.findByRazonSocial(razonSocial); // findById() ya viene implementado en JpaRepository
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).message(e.getMessage()).build();
            // Reenvío la excepción como BusinessException usando el patrón Builder
        }
        if (clienteEncontrado.isEmpty()) {
            throw NotFoundException.builder().message("No se encontró el cliente con razón social: " + razonSocial).build();
        }
        return clienteEncontrado.get();
    }

    @Override
    public Cliente add(Cliente cliente) throws FoundException, BusinessException {
        try {
            load(cliente.getId());
            throw FoundException.builder().message("El cliente con ID: " + cliente.getId() + " ya existe").build();
        } catch (NotFoundException e) {
        }
        try {
            load(cliente.getRazonSocial());
            throw FoundException.builder().message("El cliente con razón social: " + cliente.getRazonSocial() + " ya existe").build();
        } catch (NotFoundException e) {
        }
        try {
            return clienteDAO.save(cliente); // save() ya viene implementado en JpaRepository
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).message(e.getMessage()).build();
            // Reenvío la excepción como BusinessException usando el patrón Builder
        }
    }

    @Override
    public Cliente update(Cliente cliente) throws FoundException,NotFoundException, BusinessException {
        load(cliente.getId()); // Verifico que el cliente exista, si no lanza NotFoundException
        Optional<Cliente> clienteEncontrado=null;
        try {
            clienteEncontrado = clienteDAO.findByRazonSocialAndIdNot(cliente.getRazonSocial(), cliente.getId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).message(e.getMessage()).build();
        }

        if (clienteEncontrado.isPresent()) {
            throw FoundException.builder().message("El cliente con razón social: " + cliente.getRazonSocial() + " ya existe").build();
        }

        try {
            return clienteDAO.save(cliente); // save() ya viene implementado en JpaRepository
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
            clienteDAO.deleteById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).message(e.getMessage()).build();
            // Reenvío la excepción como BusinessException usando el patrón Builder
        }
    }

    @Override
    public void delete(Cliente cliente) throws NotFoundException, BusinessException {
        delete(cliente.getId());
    }

}
