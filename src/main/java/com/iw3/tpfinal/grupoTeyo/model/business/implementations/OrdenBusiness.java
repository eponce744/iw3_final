package com.iw3.tpfinal.grupoTeyo.model.business.implementations;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.iw3.tpfinal.grupoTeyo.model.Orden;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.BadRequestException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.BusinessException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.FoundException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.NotFoundException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.UnProcessableException;
import com.iw3.tpfinal.grupoTeyo.model.business.interfaces.IOrdenBusiness;
import com.iw3.tpfinal.grupoTeyo.model.persistence.OrdenRepository;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.InvalidityException;

import lombok.extern.slf4j.Slf4j;

@Service // Indica que esta clase es un servicio de negocio (y se va a instanciar cada
			// vez que quiera un IOrdenBusiness)
@Slf4j // Para usar el logger (log.info, log.error, etc)
public class OrdenBusiness implements IOrdenBusiness {

	// Básicamente traigo el DAO (Repository) para poder usarlo en los métodos de la
	// interfaz
	@Autowired // Para que Spring lo inyecte automáticamente
	private OrdenRepository ordenDAO;

	@Override
	public List<Orden> list() throws BusinessException {

		try {
			return ordenDAO.findAll(); // findAll() ya viene implementado en JpaRepository
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).message(e.getMessage()).build();
			// Reenvío la excepción como BusinessException usando el patrón Builder
		}

	}

	@Override
	public Page<Orden> list(Pageable pageable) throws BusinessException {
		try {
			return ordenDAO.findAll(pageable);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).message(e.getMessage()).build();
		}
	}

	@Override
	public Orden load(long id) throws NotFoundException, BusinessException {
		Optional<Orden> ordenEncontrada;
		try {
			ordenEncontrada = ordenDAO.findById(id); // findById() ya viene implementado en JpaRepository
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).message(e.getMessage()).build();
			// Reenvío la excepción como BusinessException usando el patrón Builder
		}
		if (ordenEncontrada.isEmpty()) {
			throw NotFoundException.builder().message("No se encontró la orden con ID: " + id).build();
		}
		return ordenEncontrada.get();
	}

	@Override
	public Orden add(Orden orden) throws FoundException, BusinessException {
		try {
			load(orden.getId());
			throw FoundException.builder().message("La orden con ID: " + orden.getId() + " ya existe").build();
		} catch (NotFoundException e) {
		}
		try {
			return ordenDAO.save(orden); // save() ya viene implementado en JpaRepository
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).message(e.getMessage()).build();
			// Reenvío la excepción como BusinessException usando el patrón Builder
		}
	}

	@Override
	public Orden update(Orden orden) throws NotFoundException, BusinessException {
		load(orden.getId()); // Verifico que la orden exista, si no lanza NotFoundException
		try {
			return ordenDAO.save(orden); // save() ya viene implementado en JpaRepository
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
			ordenDAO.deleteById(id);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).message(e.getMessage()).build();
			// Reenvío la excepción como BusinessException usando el patrón Builder
		}
	}

	@Override
	public void delete(Orden orden) throws NotFoundException, BusinessException {
		delete(orden.getId());
	}

	@Override
	public Orden conciliacion(Long idOrden)
			throws NotFoundException, BusinessException, InvalidityException, BadRequestException {
		// Usamos el propio método load de esta clase; no inyectamos IOrdenBusiness aquí
		// para evitar NPE
	    if (idOrden == null || idOrden <= 0) {
	        throw new BadRequestException("El id de la Orden no es válido o no fue provisto");
	    }
		Orden ordenRecibida = load(idOrden);
		if (ordenRecibida.getEstado() == null) {
			throw new NotFoundException("El estado de la orden es nulo o no fue asignado");
		}
		if (ordenRecibida.getEstado() != Orden.Estado.FINALIZADA) {
			throw new InvalidityException("El estado de la orden no es FINALIZADA");
		}
		return ordenRecibida;
	}

}
