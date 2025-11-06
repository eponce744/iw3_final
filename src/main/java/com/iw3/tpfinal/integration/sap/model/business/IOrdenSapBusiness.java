package com.iw3.tpfinal.integration.sap.model.business;

public interface IOrdenSapBusiness {
	public OrdenSap load(String codSap) throws NotFoundException, BusinessException;
	public List<OrdenSap> list() throws BusinessException;
	public OrdenSap add(OrdenSap product) throws FoundException, BusinessException;
	
	//public OrdenSap addExternal(String json) throws FoundException, BusinessException;
}
