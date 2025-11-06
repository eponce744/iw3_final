package com.iw3.tpfinal.integration.sap.model.business;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.iw3.tpfinal.integration.sap.model.OrdenSap;
import com.iw3.tpfinal.integration.sap.model.OrdenSapJsonDeserializer;
//import com.iw3.tpfinal.integration.sap.model.OrdenSapSlimView; //Ver que es esto y si lo necesitamos!!!
import com.iw3.tpfinal.integration.sap.model.persistence.OrdenSapRepository;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.BusinessException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.FoundException;

import com.iw3.tpfinal.grupoTeyo.model.business.interfaces.ICamionBusiness;
import com.iw3.tpfinal.grupoTeyo.model.business.interfaces.IChoferBusiness;
import com.iw3.tpfinal.grupoTeyo.model.business.interfaces.IClienteBusiness;
import com.iw3.tpfinal.grupoTeyo.model.business.interfaces.IProductoBusiness;
import com.iw3.tpfinal.grupoTeyo.model.business.interfaces.IDetalleBusiness;
import com.iw3.tpfinal.grupoTeyo.model.business.interfaces.IOrdenBusiness;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.NotFoundException;
import com.iw3.tpfinal.grupoTeyo.util.JsonUtiles;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrdenSapBusiness implements IOrdenSapBusiness {

    @Autowired(required = false)
    private OrdenSapRepository ordenDAO;

    @Override
    public OrdenSap load(String codCli1) throws NotFoundException, BusinessException {
            Optional<OrdenSap> r;
            try {
                    r = ordenDAO.findOneByCodSap(codCli1);
            } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    throw BusinessException.builder().ex(e).build();
            }
            if (r.isEmpty()) {
                    throw NotFoundException.builder().message("No se encuentra el Producto codSap=" + codSap).build();
            }
            return r.get();
    }

    @Override
    public List<OrdenSap> list() throws BusinessException {
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
    public OrdenSap add(OrdenSap orden) throws FoundException, BusinessException {

            try {
                    ordenBaseBusiness.load(orden.getId());
                    throw FoundException.builder().message("Se encontró el Producto id=" + orden.getId()).build();
            } catch (NotFoundException e) {
            }

            if (ordenDAO.findOneByCodSap(orden.getCodSap()).isPresent()) {
                    throw FoundException.builder().message("Se encontró el Producto código=" + orden.getCodSap()).build();
            }


            try {
                    return ordenDAO.save(orden);
            } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    throw BusinessException.builder().ex(e).build();
            }
    }

    @Autowired(required = false)
    private ICategoryBusiness categoryBusiness;

    @Override
    public OrdenSap addExternal(String json) throws FoundException, BusinessException {
            ObjectMapper mapper = JsonUtiles.getObjectMapper(OrdenSap.class,
                            new OrdenSapJsonDeserializer(OrdenSap.class, categoryBusiness),null);
            OrdenSap product = null;
            try {
                    product = mapper.readValue(json, OrdenSap.class);
            } catch (JsonProcessingException e) {
                    log.error(e.getMessage(), e);
                    throw BusinessException.builder().ex(e).build();
            }

            return add(product);

    }
}
