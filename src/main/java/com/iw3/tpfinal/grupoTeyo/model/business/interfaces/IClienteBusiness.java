package com.iw3.tpfinal.grupoTeyo.model.business.interfaces;

import java.util.List;

import com.iw3.tpfinal.grupoTeyo.model.Cliente;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.BusinessException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.FoundException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.NotFoundException;

public interface IClienteBusiness {

    // Método para listar todos los clientes
    public List<Cliente> list() throws BusinessException;

    // Método para cargar(o traer) un cliente por su ID
    public Cliente load(long id) throws NotFoundException, BusinessException;

    // Método para cargar(o traer) un cliente por su razón social
    public Cliente load(String razonSocial) throws NotFoundException, BusinessException;

    // Método para agregar un nuevo cliente
    public Cliente add(Cliente cliente) throws FoundException, BusinessException;

    // Método para modificar un cliente existente
    public Cliente update(Cliente cliente) throws FoundException,NotFoundException, BusinessException;

    // Método para eliminar un cliente por su ID
    public void delete(long id) throws NotFoundException, BusinessException;

    // Elimina un cliente dado el objeto Cliente
    public void delete(Cliente cliente) throws NotFoundException, BusinessException;
}
