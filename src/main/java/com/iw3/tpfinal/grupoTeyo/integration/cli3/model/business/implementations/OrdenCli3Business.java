package com.iw3.tpfinal.grupoTeyo.integration.cli3.model.business.implementations;

import com.iw3.tpfinal.grupoTeyo.integration.cli3.model.business.interfaces.IOrdenCli3Business;
import com.iw3.tpfinal.grupoTeyo.model.Detalle;
import com.iw3.tpfinal.grupoTeyo.model.Orden;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.*;
import com.iw3.tpfinal.grupoTeyo.model.business.implementations.OrdenBusiness;
import com.iw3.tpfinal.grupoTeyo.model.persistence.OrdenRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

/*  VER QUE SE PUEDE APLICAR Y QUE NO
@Service
@Slf4j
public class OrdenCli3Business implements IOrdenCli3Business {

	@Autowired
    private OrdenRepository orderDAO;

    @Autowired
    private OrdenBusiness orderBusiness;

    @Override
    public Orden validacionPassword(int password) throws NotFoundException, BusinessException {
        Optional<Orden> orden;

        try {
      //      order = ordenDAO.findByActivatePassword(password); Agregar esto al OrdenRepository
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BusinessException("Error al recuperar orden", e);
        }

        if (orden.isEmpty()) {
            throw new NotFoundException("Orden no econtrada");
        }
        // checkOrderStatus(order.get()); Agregar esto al OrdenRepository si es necesario
        return orden.get();
    }

    @Override
    public Order receiveDetails(Detail detail) throws NotFoundException, BusinessException, UnProcessableException, ConflictException {
        Order orderFound = orderBusiness.load(detail.getOrder().getId());

        // Validaciones de negocio
        if (orderFound.getStatus() != Order.Status.REGISTERED_INITIAL_WEIGHING) {
            throw new ConflictException("Estado de orden no válido");
        }
        if (detail.getFlowRate() < 0) {
            throw new UnProcessableException("Caudal no válido");
        }
        if (detail.getAccumulatedMass() < orderFound.getLastAccumulatedMass()) {
            throw new UnProcessableException("Masa acumulada no válida");
        }
        if (detail.getTemperature() > orderFound.getProduct().getThresholdTemperature()) {
            if (!alarmBusiness.isAlarmAccepted(orderFound.getId())) {
                applicationEventPublisher.publishEvent(new AlarmEvent(detail, AlarmEvent.TypeEvent.TEMPERATURE_EXCEEDED));
            }
        }

        Date currentTime = new Date(System.currentTimeMillis());
        DetailWsWrapper detailWsWrapper = new DetailWsWrapper();

        // Actualizacion de cabecera de orden
        orderFound.setLastTimeStamp(currentTime);
        orderFound.setLastAccumulatedMass(detail.getAccumulatedMass());
        orderFound.setLastDensity(detail.getDensity());
        orderFound.setLastTemperature(detail.getTemperature());
        orderFound.setLastFlowRate(detail.getFlowRate());
        orderDAO.save(orderFound);

        // todo topico para graficos en tiempo real
        detailWsWrapper.setTimeStamp(currentTime);
        detailWsWrapper.setAccumulatedMass(detail.getAccumulatedMass());
        detailWsWrapper.setDensity(detail.getDensity());
        detailWsWrapper.setTemperature(detail.getTemperature());
        detailWsWrapper.setFlowRate(detail.getFlowRate());
        wSock.convertAndSend("/topic/details/graphs/order/" + orderFound.getId(), detailWsWrapper);

        // Evento para manejar el almacenamiento de detalle
        applicationEventPublisher.publishEvent(new DetailEvent(detail, DetailEvent.TypeEvent.SAVE_DETAIL));

        return orderFound;
    }

    @Override
    public Order closeOrder(Long orderId) throws BusinessException, NotFoundException, ConflictException {
        Optional<Order> order;

        try {
            order = orderDAO.findByIdAndStatus(orderId, Order.Status.REGISTERED_INITIAL_WEIGHING);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BusinessException("Error al recuperar orden", e);
        }
        if (order.isEmpty()) {
            throw new NotFoundException("Orden no econtrada");
        }
        checkOrderStatus(order.get());
        order.get().setStatus(Order.Status.ORDER_CLOSED);
        order.get().setActivatePassword(null);
        return orderDAO.save(order.get());
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////// UTILIDADES  ////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////

    private void checkOrderStatus(Order order) throws ConflictException {
        if (order.getStatus() != Order.Status.REGISTERED_INITIAL_WEIGHING) {
            throw new ConflictException("Estado de orden no válido");
        }
    }
	
}*/
