package com.iw3.tpfinal.grupoTeyo.model.business.implementations;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iw3.tpfinal.grupoTeyo.model.Alarma;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.BusinessException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.FoundException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.NotFoundException;
import com.iw3.tpfinal.grupoTeyo.model.business.interfaces.IAlarmaBusiness;
import com.iw3.tpfinal.grupoTeyo.model.persistence.AlarmaRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AlarmaBusiness implements IAlarmaBusiness{

// Básicamente traigo el DAO (Repository) para poder usarlo en los métodos de la interfaz
    @Autowired // Para que Spring lo inyecte automáticamente
    private AlarmaRepository alarmaDAO;

    @Override // Método para listar todas las alarmas de una orden específica
    public List<Alarma> listByOrden(long ordenId) throws NotFoundException, BusinessException{ 
        
        Optional<List<Alarma>> alarmasEncontradas; // Lista para almacenar las alarmas encontradas

        try {
            alarmasEncontradas = alarmaDAO.findByOrdenId(ordenId); // findByOrdenId() viene de la interfaz AlarmaRepository
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).message(e.getMessage()).build();
            // Reenvío la excepción como BusinessException usando el patrón Builder
        }
        if (alarmasEncontradas.isEmpty()) { // Si no se encontraron alarmas
            throw NotFoundException.builder().message("No se encontraron alarmas para la orden con ID: " + ordenId).build();
        }
        return alarmasEncontradas.get();
    }

    @Override
    public Alarma load(long id) throws NotFoundException, BusinessException {
        Optional<Alarma> alarmaEncontrada;
        try {
            alarmaEncontrada = alarmaDAO.findById(id); // findById() ya viene implementado en JpaRepository
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).message(e.getMessage()).build();
            // Reenvío la excepción como BusinessException usando el patrón Builder
        }
        if (alarmaEncontrada.isEmpty()) {
            throw NotFoundException.builder().message("No se encontró la alarma con ID: " + id).build();
        }
        return alarmaEncontrada.get();
    }

    @Override
    public Alarma add(Alarma alarma) throws FoundException, BusinessException {
        try {
            load(alarma.getId());
            throw FoundException.builder().message("La alarma con ID: " + alarma.getId() + " ya existe").build();
        } catch (NotFoundException e) {
        }
        try {
            return alarmaDAO.save(alarma); // save() ya viene implementado en JpaRepository
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).message(e.getMessage()).build();
            // Reenvío la excepción como BusinessException usando el patrón Builder
        }
    }

    public Alarma update(Alarma alarma) throws NotFoundException, BusinessException {
        load(alarma.getId()); // Verifico que la alarma exista, si no lanza NotFoundException
        try {
            return alarmaDAO.save(alarma); // save() ya viene implementado en JpaRepository
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).message(e.getMessage()).build();
            // Reenvío la excepción como BusinessException usando el patrón Builder
        }
    }

}
