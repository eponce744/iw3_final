package com.iw3.tpfinal.grupoTeyo.integration.cli1.model.business.interfaces;

import java.util.List;

import com.iw3.tpfinal.grupoTeyo.integration.cli1.model.OrdenCli1;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.BusinessException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.FoundException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.NotFoundException;

public interface IOrdenCli1Business {
	public OrdenCli1 load(String codSap) throws NotFoundException, BusinessException;
	public List<OrdenCli1> list() throws BusinessException;
	public OrdenCli1 add(OrdenCli1 product) throws FoundException, BusinessException;
	
	//Servicio que recibe un String de tipo Json que posiblemente crea un nuevo objeto OrdenSap
	public OrdenCli1 addExternal(String json) throws FoundException, BusinessException;
}
