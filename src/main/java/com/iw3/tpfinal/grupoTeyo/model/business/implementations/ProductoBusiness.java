package com.iw3.tpfinal.grupoTeyo.model.business.implementations;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.iw3.tpfinal.grupoTeyo.model.Producto;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.BusinessException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.FoundException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.NotFoundException;
import com.iw3.tpfinal.grupoTeyo.model.business.interfaces.IProductoBusiness;
import com.iw3.tpfinal.grupoTeyo.model.persistence.ProductoRepository;

import lombok.extern.slf4j.Slf4j;

@Service // Indica que esta clase es un servicio de negocio (y se va a instanciar cada vez que quiera un IProductoBusiness)
@Slf4j // Para usar el logger (log.info, log.error, etc)
public class ProductoBusiness implements IProductoBusiness{

    // Básicamente traigo el DAO (Repository) para poder usarlo en los métodos de la interfaz
    @Autowired // Para que Spring lo inyecte automáticamente
    private ProductoRepository productoDAO;

    @Override
    public List<Producto> list() throws BusinessException {
        
        try {
            return productoDAO.findAll(); // findAll() ya viene implementado en JpaRepository
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).message(e.getMessage()).build();
            // Reenvío la excepción como BusinessException usando el patrón Builder
        }
        
    }

    @Override
    public Page<Producto> list(Pageable pageable) throws BusinessException {
        try {
            return productoDAO.findAll(pageable);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).message(e.getMessage()).build();
        }
    }

    @Override
    public Producto load(long id) throws NotFoundException, BusinessException {
        Optional<Producto> productoEncontrado;
        try {
            productoEncontrado = productoDAO.findById(id); // findById() ya viene implementado en JpaRepository
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).message(e.getMessage()).build();
            // Reenvío la excepción como BusinessException usando el patrón Builder
        }
        if (productoEncontrado.isEmpty()) {
            throw NotFoundException.builder().message("No se encontró el producto con ID: " + id).build();
        }
        return productoEncontrado.get();
    }

    @Override
    public Producto load(String producto) throws NotFoundException, BusinessException {
        Optional<Producto> productoEncontrado;
        try {
            productoEncontrado = productoDAO.findByNombre(producto); // findById() ya viene implementado en JpaRepository
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).message(e.getMessage()).build();
            // Reenvío la excepción como BusinessException usando el patrón Builder
        }
        if (productoEncontrado.isEmpty()) {
            throw NotFoundException.builder().message("No se encontró el producto con nombre: " + producto).build();
        }
        return productoEncontrado.get();
    }

    @Override
    public Producto add(Producto producto) throws FoundException, BusinessException {
        try {
            load(producto.getId());
            throw FoundException.builder().message("El producto con ID: " + producto.getId() + " ya existe").build();
        } catch (NotFoundException e) {
        }
        try {
            load(producto.getNombre());
            throw FoundException.builder().message("El producto con nombre: " + producto.getNombre() + " ya existe").build();
        } catch (NotFoundException e) {
        }
        try {
            return productoDAO.save(producto); // save() ya viene implementado en JpaRepository
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).message(e.getMessage()).build();
            // Reenvío la excepción como BusinessException usando el patrón Builder
        }
    }

    @Override
    public Producto update(Producto producto) throws  FoundException, NotFoundException, BusinessException {
        load(producto.getId()); // Verifico que el producto exista, si no lanza NotFoundException
        Optional<Producto> productoEncontrado=null;
        try {
            productoEncontrado = productoDAO.findByNombreAndIdNot(producto.getNombre(), producto.getId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).message(e.getMessage()).build();
        }

        if (productoEncontrado.isPresent()) {
            throw FoundException.builder().message("El producto con nombre: " + producto.getNombre() + " ya existe").build();
        }

        try {
            return productoDAO.save(producto); // save() ya viene implementado en JpaRepository
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
            productoDAO.deleteById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).message(e.getMessage()).build();
            // Reenvío la excepción como BusinessException usando el patrón Builder
        }
    }

    @Override
    public void delete(Producto producto) throws NotFoundException, BusinessException {
        delete(producto.getId());
    }
}
