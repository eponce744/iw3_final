package com.iw3.tpfinal.grupoTeyo.model.business.interfaces;

import java.util.List;

import com.iw3.tpfinal.grupoTeyo.model.Alarma;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.BusinessException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.FoundException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.NotFoundException;

public interface IAlarmaBusiness {

    // Método para listar todos los alarmas
	//Deberia ser ejecutable solo por el Admin
    public List<Alarma> list() throws BusinessException;
    
    // Método para listar todas las alarmas de una orden específica
    public List<Alarma> listByOrden(long ordenId) throws NotFoundException, BusinessException;

    // Método para cargar(o traer) una alarma por su ID
    public Alarma load(long id) throws NotFoundException, BusinessException;

    // Método para agregar una nueva alarma
    public Alarma add(Alarma alarma) throws FoundException, BusinessException;

    // Método para modificar una alarma existente
    public Alarma update(Alarma alarma) throws NotFoundException, BusinessException;
    
    //Método para actualizar el Estado de una Alarma existente
    public void updateEstado(long id, Alarma.Estado nuevoEstado) throws NotFoundException, BusinessException;
}
