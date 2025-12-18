package com.iw3.tpfinal.grupoTeyo.model.event;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventoAlarma extends ApplicationEvent{
	

	public enum TipoEvento{
		TEMPERATURA_EXCEDIDA
	}

	public EventoAlarma(Object source, TipoEvento tipoEvento) {
		super(source);
		this.tipoEvento = tipoEvento;
	}

	private TipoEvento tipoEvento;
}
