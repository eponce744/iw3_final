package com.iw3.tpfinal.grupoTeyo.integration.cli1.model.business.implementations;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iw3.tpfinal.grupoTeyo.integration.cli1.model.OrdenCli1;
import com.iw3.tpfinal.grupoTeyo.integration.cli1.model.OrdenCli1JsonDeserializer;
import com.iw3.tpfinal.grupoTeyo.integration.cli1.model.business.interfaces.IOrdenCli1Business;
import com.iw3.tpfinal.grupoTeyo.integration.cli1.model.persistence.OrdenCli1Repository;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.BusinessException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.FoundException;

import com.iw3.tpfinal.grupoTeyo.model.business.interfaces.ICamionBusiness;
import com.iw3.tpfinal.grupoTeyo.model.business.interfaces.IChoferBusiness;
import com.iw3.tpfinal.grupoTeyo.model.business.interfaces.IClienteBusiness;
import com.iw3.tpfinal.grupoTeyo.model.business.interfaces.IProductoBusiness;
import com.iw3.tpfinal.grupoTeyo.model.business.interfaces.IOrdenBusiness;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.NotFoundException;
import com.iw3.tpfinal.grupoTeyo.util.JsonUtiles;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrdenCli1Business implements IOrdenCli1Business {

    @Autowired(required = false)
    private OrdenCli1Repository ordenDAO;

    @Override
    public OrdenCli1 load(String codSap) throws NotFoundException, BusinessException {
            Optional<OrdenCli1> r;
            try {
                    r = ordenDAO.findOneByCodSap(codSap);
            } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    throw BusinessException.builder().ex(e).build();
            }
            if (r.isEmpty()) {
                    throw NotFoundException.builder().message("No se encuentra la Orden codSap=" + codSap).build();
            }
            return r.get();
    }

    @Override
    public List<OrdenCli1> list() throws BusinessException {
            try {
                    return ordenDAO.findAll();
            } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    throw BusinessException.builder().ex(e).build();
            }
    }

    @Autowired
    private IOrdenBusiness ordenBaseBusiness;

    @Override
    public OrdenCli1 add(OrdenCli1 orden) throws FoundException, BusinessException {

            try {
                    ordenBaseBusiness.load(orden.getId());
                    throw FoundException.builder().message("Se encontró la Orden id=" + orden.getId()).build();
            } catch (NotFoundException e) {
            }

            if (ordenDAO.findOneByCodSap(orden.getCodSap()).isPresent()) {
                    throw FoundException.builder().message("Se encontró el Orden código=" + orden.getCodSap()).build();
            }


            try {
                    return ordenDAO.save(orden);
            } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    throw BusinessException.builder().ex(e).build();
            }
    }

	@Autowired(required = false)	
	private IClienteBusiness clienteBusiness;
	
	@Autowired(required = false)
	private ICamionBusiness camionBusiness;
	
	@Autowired(required = false)
	private IChoferBusiness choferBusiness;
	
	@Autowired(required = false)
	private IProductoBusiness productoBusiness;
	
	
    @Override
    public OrdenCli1 addExternal(String json) throws FoundException, BusinessException {
            ObjectMapper mapper = JsonUtiles.getObjectMapper(OrdenCli1.class,
                            new OrdenCli1JsonDeserializer(
                            		OrdenCli1.class, 
                            		clienteBusiness, 
                            		camionBusiness, 
                            		choferBusiness, 
                            		productoBusiness),null);
            OrdenCli1 orden = null;
            try {
                    orden = mapper.readValue(json, OrdenCli1.class);
            } catch (JsonProcessingException e) {
                    log.error(e.getMessage(), e);
                    throw BusinessException.builder().ex(e).build();
            }
            
            return add(orden);

    }
}
