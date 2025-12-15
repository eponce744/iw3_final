package com.iw3.tpfinal.grupoTeyo.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EventoAlarmaDTO {
	
	private Date timestamp;
	private Double temperatura;
}
