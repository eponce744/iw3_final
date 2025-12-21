package com.iw3.tpfinal.grupoTeyo.model.event;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.iw3.tpfinal.grupoTeyo.dto.EventoAlarmaDTO;
import com.iw3.tpfinal.grupoTeyo.model.Alarma;
import com.iw3.tpfinal.grupoTeyo.model.Alarma.Estado;
import com.iw3.tpfinal.grupoTeyo.model.Detalle;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.BusinessException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.FoundException;
import com.iw3.tpfinal.grupoTeyo.model.business.interfaces.IAlarmaBusiness;
import com.iw3.tpfinal.grupoTeyo.util.EmailBusiness;

//import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import org.springframework.messaging.simp.SimpMessagingTemplate;

@Slf4j
@Component
public class EventoAlarmaListener implements ApplicationListener<EventoAlarma> {

	@Override
	public void onApplicationEvent(EventoAlarma event) {
		if (event.getTipoEvento().equals(EventoAlarma.TipoEvento.TEMPERATURA_EXCEDIDA) && event.getSource() instanceof Detalle) {
			handleAlarma((Detalle) event.getSource());
		}
	}

	@Autowired
	private EmailBusiness emailBusiness;

	@Autowired
	private IAlarmaBusiness alarmaBusiness;
	
	private SimpMessagingTemplate messagingTemplate;

	private void handleAlarma(Detalle detalle) {
		Alarma alarma = new Alarma();
		Date currentTime = new Date(System.currentTimeMillis());

		try {
			alarma.setEstado(Estado.PENDIENTE);
			alarma.setFecha(currentTime);
			alarma.setTemperaturaRegistrada(detalle.getTemperatura());
			alarma.setOrden(detalle.getOrden());
			alarmaBusiness.add(alarma);
		} catch (FoundException e) {
		} catch (BusinessException e) {
		}



        EventoAlarmaDTO dto = new EventoAlarmaDTO();
        dto.setId(alarma.getId());
        dto.setOrdenId(alarma.getOrden().getId());
        dto.setEstado(alarma.getEstado());
        dto.setTemperaturaRegistrada(alarma.getTemperaturaRegistrada());
        dto.setFecha(alarma.getFecha());
        //dto.setThresholdTemperature(alarma.getOrder().getProduct().getThresholdTemperature()); //todo tira null pointer ver que onda
        dto.setObservacion(alarma.getObservacion() != null ? alarma.getObservacion() : null);
        dto.setUsuario(
                alarma.getUsuario() != null && alarma.getUsuario().getUsername() != null
                        ? alarma.getUsuario().getUsername()
                        : null
        );
		log.info("EL USUARIO DE DTO ES: " + dto.getUsuario());

        String topic = "/topic/alarmas/orden/" + detalle.getOrden().getId();
        try {
            messagingTemplate.convertAndSend(topic, dto);
        } catch (Exception e) {
            log.error("Fallo el envío de la notificación", e);
        }


        String subject = "Temperatura Excedida Orden Nro " + detalle.getOrden().getId();
        String mensaje = String.format(
                """
                        ALERTA: Temperatura Excedida en la Orden Nro %s

                        Detalles de la Alerta:
                        ---------------------------------
                        Orden ID: %s
                        Fecha/Hora del Evento: %s
                        Temperatura Registrada: %.2f °C
                        Masa Acumulada: %.2f kg
                        Densidad: %.2f kg/m³
                        Caudal: %.2f Kg/h
                        ---------------------------------

                        Descripción: La temperatura del combustible ha superado el umbral establecido. \
                        Por favor, revise esta alerta lo antes posible para evitar inconvenientes.""",
                detalle.getOrden().getId(),
                detalle.getOrden().getId(),
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(currentTime),
                detalle.getTemperatura(),
                detalle.getMasaAcumulada(),
                detalle.getDensidad(),
                detalle.getCaudal()
        );

        try {
            emailBusiness.sendSimpleMessage("emanuel.simonb@gmail.com", subject, mensaje);
            log.info("Enviando mensaje '{}'", mensaje);
        } catch (BusinessException e) {
            log.error(e.getMessage(), e);
        }
	}
}
