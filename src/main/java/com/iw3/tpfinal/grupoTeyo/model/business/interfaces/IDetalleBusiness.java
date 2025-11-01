package com.iw3.tpfinal.grupoTeyo.model.business.interfaces;

import java.util.List;


import com.iw3.tpfinal.grupoTeyo.model.Detalle;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.BusinessException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.FoundException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.NotFoundException;

public interface IDetalleBusiness {

    // Método para listar todos los detalles de una orden específica
    public List<Detalle> listByOrden(long ordenId) throws NotFoundException, BusinessException;

    // Método para cargar(o traer) un detalle por su ID
    public Detalle load(long id) throws NotFoundException, BusinessException;

    // Método para agregar un nuevo detalle
    public Detalle add(Detalle detalle) throws FoundException, BusinessException;


}
