package com.iw3.tpfinal.grupoTeyo.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ordenes")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Orden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    /*momento en que se recibe la orden desde el
    sistema externo*/
	@Column(columnDefinition = "DATETIME NOT NULL")
	private Date fechaRecepcion;
	
	/*registro del pesaje vacío (tara).*/
	@Column(columnDefinition = "DATETIME NOT NULL")
	private Date fechaPesajeInicial;
	
	/*(turno de carga,*/
	@Column(columnDefinition = "DATETIME NOT NULL")
	private Date fechaPrevistaCarga;
	
	/*momento del primer registro válido de detalle.*/
	@Column(columnDefinition = "DATETIME")
	private Date fechaInicioCarga;
	
	/*Momento de recepción del pesaje final.*/
	@Column(columnDefinition = "DATETIME")
	private Date fechaFinCarga;
	
	/*Momento de recepción del pesaje final.*/
	@Column(columnDefinition = "DATETIME")
	private Date fechaPesajeFinal;
	
	/*Kilos a cargar*/
	private Double preset;
	
	/*Peso camino vacio*/
	private Double tara;
	
	@OneToOne
	@JoinColumn(name="id_cliente", nullable = true)
	private Cliente cliente;
	
	@OneToOne
	@JoinColumn(name="id_cliente", nullable = true)
	private Camion camion;
	
	@OneToOne
	@JoinColumn(name="id_cliente", nullable = true)
	private Chofer chofer;
	
	@OneToOne
	@JoinColumn(name="id_cliente", nullable = true)
	private Producto producto;
}
