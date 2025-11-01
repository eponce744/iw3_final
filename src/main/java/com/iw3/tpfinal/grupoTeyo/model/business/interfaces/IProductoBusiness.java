package com.iw3.tpfinal.grupoTeyo.model.business.interfaces;

import java.util.List;

import com.iw3.tpfinal.grupoTeyo.model.Producto;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.BusinessException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.FoundException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.NotFoundException;

public interface IProductoBusiness {

    // Método para listar todos los productos
    public List<Producto> list() throws BusinessException;

    // Método para cargar(o traer) un producto por su ID
    public Producto load(long id) throws NotFoundException, BusinessException;

    // Método para agregar un nuevo producto
    public Producto add(Producto producto) throws FoundException, BusinessException;

    // Método para modificar un producto existente
    public Producto update(Producto producto) throws NotFoundException, BusinessException;

    // Método para eliminar un producto por su ID
    public void delete(long id) throws NotFoundException, BusinessException;
}
