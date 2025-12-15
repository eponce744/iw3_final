package com.iw3.tpfinal.grupoTeyo.model.event;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventoAlarma extends ApplicationEvent{
	
	public EventoAlarma(Object source) {
		super(source);
	}
}
