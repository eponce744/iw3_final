package com.iw3.tpfinal.grupoTeyo.model.business.interfaces;

import java.util.List;

import com.iw3.tpfinal.grupoTeyo.model.Chofer;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.BusinessException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.FoundException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.NotFoundException;

public interface IChoferBusiness {

    // Método para listar todos los choferes
    public List<Chofer> list() throws BusinessException;

    // Método para cargar(o traer) un chofer por su ID
    public Chofer load(long id) throws NotFoundException, BusinessException;

    // Método para cargar(o traer) un chofer por su documento
    public Chofer load(String documento) throws NotFoundException, BusinessException;

    // Método para agregar un nuevo chofer
    public Chofer add(Chofer chofer) throws FoundException, BusinessException;

    // Método para modificar un chofer existente
    public Chofer update(Chofer chofer) throws FoundException,NotFoundException, BusinessException;

    // Método para eliminar un chofer por su ID
    public void delete(long id) throws NotFoundException, BusinessException;

    // Elimina un chofer dado el objeto Chofer
    public void delete(Chofer chofer) throws NotFoundException, BusinessException;
}
