package com.iw3.tpfinal.grupoTeyo.model.business.interfaces;

import java.util.List;

import com.iw3.tpfinal.grupoTeyo.model.Camion;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.BusinessException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.FoundException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.NotFoundException;

public interface ICamionBusiness {

    // Método para listar todos los camiones
    public List<Camion> list() throws BusinessException;

    // Método para cargar(o traer) un camión por su ID
    public Camion load(long id) throws NotFoundException, BusinessException;

    // Método para agregar un nuevo camión
    public Camion add(Camion camion) throws FoundException, BusinessException;

    // Método para modificar un camión existente
    public Camion update(Camion camion) throws NotFoundException, BusinessException;

    // Método para eliminar un camión por su ID
    public void delete(long id) throws NotFoundException, BusinessException;
}
