/*package com.iw3.tpfinal.grupoTeyo.integration.cli3.model.business.implementations;


import com.iw3.tpfinal.grupoTeyo.integration.cli3.model.business.interfaces.IDetalleCli3Business;

import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

public class DetalleCli3Business {

	@Service
	@Slf4j
	public class DetalleCli3Business implements IDetalleCli3Business {

	    @Autowired
	    private IOrderBusiness orderBusiness;

	    @Autowired
	    private IDetailBusiness detailBusiness;

	    @Autowired
	    private DetailRepository detailDAO;

	    /*
	     //Este método Recibe un Detalle y lo carga en la bd 
	
	    @Override
	    public void add(Detalle detalle) throws FoundException, BusinessException, NotFoundException {
	        long currentTime = System.currentTimeMillis();
	        Orden ordenEncontrada = ordenBusiness.load(detalle.getOrder().getId());
	        Optional<List<Detalle>> detailsOptional = detailDAO.findByOrderId(detail.getOrder().getId());

	        DetailWsWrapper detailWsWrapper = new DetailWsWrapper();
	        detailWsWrapper.setTimeStamp(new Date(currentTime));
	        detailWsWrapper.setAccumulatedMass(detail.getAccumulatedMass());
	        detailWsWrapper.setDensity(detail.getDensity());
	        detailWsWrapper.setTemperature(detail.getTemperature());
	        detailWsWrapper.setFlowRate(detail.getFlowRate());

	        if ((detailsOptional.isPresent() && !detailsOptional.get().isEmpty())) {
	            Date lastTimeStamp = orderFound.getFuelingEndDate();
	            if (checkFrequency(currentTime, lastTimeStamp)) {
	                detail.setTimeStamp(new Date(currentTime));
	                Detail savedDetail = detailBusiness.add(detail);
	                orderFound.setFuelingEndDate(new Date(currentTime));
	                orderBusiness.update(orderFound);

	                detailWsWrapper.setId(savedDetail.getId());
	                // Envío de detalle de carga a clientes (WebSocket)
	                wSock.convertAndSend("/topic/details/order/" + detail.getOrder().getId(), detailWsWrapper);
	            }
	        } else {
	            detail.setTimeStamp(new Date(currentTime));
	            detailBusiness.add(detail);
	            orderFound.setFuelingStartDate(new Date(currentTime));
	            orderFound.setFuelingEndDate(new Date(currentTime));
	            orderBusiness.update(orderFound);


	            // Envío de detalle de carga a clientes (WebSocket)
	            wSock.convertAndSend("/topic/details/order/" + detail.getOrder().getId(), detailWsWrapper);
	        }
	    }

	    //////////////////////////////////////////////////////////////////////////////////////////////////////
	    //////////////////////////////////////////// UTILIDADES  ////////////////////////////////////////////
	    /////////////////////////////////////////////////////////////////////////////////////////////////////

	    @Value("${loading.details.saving.frequency}")
	    private long SAVE_INTERVAL_MS;

	    private boolean checkFrequency(long currentTime, Date lastTimeStamp) {
	        return currentTime - lastTimeStamp.getTime() >= SAVE_INTERVAL_MS;
	    }
} */