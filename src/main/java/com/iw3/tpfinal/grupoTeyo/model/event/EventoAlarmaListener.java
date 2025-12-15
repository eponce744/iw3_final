package com.iw3.tpfinal.grupoTeyo.model.event;

import java.time.Instant;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.iw3.tpfinal.grupoTeyo.dto.EventoAlarmaDTO;
import com.iw3.tpfinal.grupoTeyo.model.Alarma;

//import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import org.springframework.messaging.simp.SimpMessagingTemplate;

@Slf4j
@Component
public class EventoAlarmaListener implements ApplicationListener<EventoAlarma> {

	private final SimpMessagingTemplate messagingTemplate;
	
    public EventoAlarmaListener(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }
	
	@Override
	public void onApplicationEvent(EventoAlarma event) {
			handleAlarma(event);
	}

	private void handleAlarma(EventoAlarma event) {
		Alarma alarma = (Alarma) event.getSource();
		//HttpServletRequest request=(HttpServletRequest) event.getExtraData();
		log.error("🔥 publishEvent(EventoAlarma) EJECUTADO");//Prueba, borrar despues esta linea
		log.debug("Evento ALARMA fecha: '{}', temperatura={}", alarma.getFecha(), alarma.getTemperaturaRegistrada());
		
        EventoAlarmaDTO dto = new EventoAlarmaDTO(
                alarma.getFecha(),
                alarma.getTemperaturaRegistrada()
        );
        
        messagingTemplate.convertAndSend("/topic/eventos-alarma", dto);
	}
}
