package com.iw3.tpfinal.grupoTeyo.integration.cli1.model.business.interfaces;

import java.util.List;

import com.iw3.tpfinal.grupoTeyo.integration.cli1.model.OrdenSap;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.BusinessException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.FoundException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.NotFoundException;

public interface IOrdenSapBusiness {
	public OrdenSap load(String codSap) throws NotFoundException, BusinessException;
	public List<OrdenSap> list() throws BusinessException;
	public OrdenSap add(OrdenSap product) throws FoundException, BusinessException;
	
	//Servicio que recibe un String de tipo Json que posiblemente crea un nuevo objeto OrdenSap
	public OrdenSap addExternal(String json) throws FoundException, BusinessException;
}
