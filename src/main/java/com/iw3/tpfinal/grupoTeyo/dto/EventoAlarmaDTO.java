package com.iw3.tpfinal.grupoTeyo.dto;

import java.util.Date;

import com.iw3.tpfinal.grupoTeyo.model.Alarma;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class EventoAlarmaDTO {
	
	private long id;
	
	private long ordenId;

    private String observacion;
    
    private Alarma.Estado estado;

    private Date fecha;

    private Double temperaturaRegistrada;

    private String usuario;
}
