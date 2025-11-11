package com.iw3.tpfinal.grupoTeyo.model.business.interfaces;

import java.util.List;

import com.iw3.tpfinal.grupoTeyo.model.Orden;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.BadRequestException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.BusinessException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.FoundException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.InvalidityException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.NotFoundException;

public interface IOrdenBusiness {

    // Método para listar todas las órdenes
    public List<Orden> list() throws BusinessException;

    // Método para cargar(o traer) una orden por su ID
    public Orden load(long id) throws NotFoundException, BusinessException;

    // Método para agregar una nueva orden
    public Orden add(Orden orden) throws FoundException, BusinessException;

    // Método para modificar una orden existente
    public Orden update(Orden orden) throws NotFoundException, BusinessException;

    // Método para eliminar una orden por su ID
    public void delete(long id) throws NotFoundException, BusinessException;

    public void delete(Orden orden) throws NotFoundException, BusinessException;

    public Orden conciliacion(Long idOrden) throws NotFoundException, BusinessException, InvalidityException, BadRequestException;
}
