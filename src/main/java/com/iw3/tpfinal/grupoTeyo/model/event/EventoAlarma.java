package com.iw3.tpfinal.grupoTeyo.model.event;

import org.springframework.context.ApplicationEvent;

import com.iw3.tpfinal.grupoTeyo.auth.events.UserEvent.TypeEvent;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventoAlarma extends ApplicationEvent{
	
	public EventoAlarma(Object source) {
		super(source);
	}
	private TypeEvent typeEvent;
	private Object extraData;

}
