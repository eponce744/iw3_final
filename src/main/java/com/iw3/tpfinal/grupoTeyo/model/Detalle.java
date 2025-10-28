package com.iw3.tpfinal.grupoTeyo.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="detalle")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Detalle {
    
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

	private double masaAcumulada;
	
	private double densidad;
	
	private double temperatura;
	
	private double caudal;
	
	/*Estampa de tiempo: momento de recepción del último dato*/
	@Column(columnDefinition = "DATETIME")
	private Date fechaUltimoDato;
	
	@ManyToOne
	@JoinColumn(name="id_orden", nullable = true)
	private Orden orden;
}
