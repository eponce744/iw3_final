package com.iw3.tpfinal.grupoTeyo.model.event;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.iw3.tpfinal.grupoTeyo.model.Alarma;

//import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class EventoAlarmaListener implements ApplicationListener<EventoAlarma> {

	@Override
	public void onApplicationEvent(EventoAlarma event) {
			handleAlarma(event);
	}

	private void handleAlarma(EventoAlarma event) {
		Alarma alarma = (Alarma) event.getSource();
		//HttpServletRequest request=(HttpServletRequest) event.getExtraData();
		log.debug("Evento ALARMA fecha: '{}', temperatura={}", alarma.getFecha(), alarma.getTemperaturaRegistrada());
	}
}
