package com.iw3.tpfinal.grupoTeyo.model;

import java.util.Date;
import java.util.Set;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ordenes")
@Inheritance(strategy = InheritanceType.JOINED) //Definimos la estrategia de implementacion de Herencia
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Orden {

	public enum Estado {
		PENDIENTE_PESAJE_INICIAL,
		PESAJE_INICIAL_REGISTRADO,
        PESAJE_FINAL_REGISTRADO,
		CERRADA_PARA_CARGA,
		FINALIZADA
	}


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    /*momento en que se recibe la orden desde el
    sistema externo*/
    @Column(columnDefinition = "DATETIME",nullable = true)
	private Date fechaRecepcion;
	
	/*registro del pesaje vacío (tara).*/
    @Column(columnDefinition = "DATETIME",nullable = true)
	private Date fechaPesajeInicial;
	
	/*(turno de carga,*/
    @Column(columnDefinition = "DATETIME",nullable = true)
	private Date fechaPrevistaCarga;
	
	/*momento del primer registro válido de detalle.*/
    @Column(columnDefinition = "DATETIME",nullable = true)
	private Date fechaInicioCarga;
	
	/*Momento de recepción del pesaje final.*/
    @Column(columnDefinition = "DATETIME",nullable = true)
	private Date fechaFinCarga;
	
	/*Momento de recepción del pesaje final.*/
    @Column(columnDefinition = "DATETIME",nullable = true)
	private Date fechaPesajeFinal;

    private double inicialPesaje;

    private double finalPesaje;


    @Enumerated(EnumType.STRING)
    @Column()
    private Estado estado;

    @Column(unique = true)
    private Integer activarPassword;


	/*Kilos a cargar*/
	private Double preset;
	
	/*Peso camino vacio*/
	private Double tara;
	
	@ManyToOne // Relación muchos a uno con Cliente (muchas ordenes pueden tener un cliente)
	@JoinColumn(name = "id_cliente", nullable = true)
	private Cliente cliente;

	@ManyToOne // Relación muchos a uno con Camion (muchas ordenes pueden tener un camion)
	@JoinColumn(name = "id_camion", nullable = true)
	private Camion camion;

	@ManyToOne // Relación muchos a uno con Chofer (muchas ordenes pueden tener un chofer)
	@JoinColumn(name = "id_chofer", nullable = true)
	private Chofer chofer;

	@ManyToOne // Relación muchos a uno con Producto (muchas ordenes pueden tener un producto)
	@JoinColumn(name = "id_producto", nullable = true)
	private Producto producto;

	@JsonManagedReference //"Lado padre"-Para evitar referencia circular al serializar a JSON (Es decir infinito anidamiento)
	@OneToMany(mappedBy = "orden") // Relación uno a muchos con Detalle (es decir una orden tiene muchos detalles)
	private Set<Detalle> detalles;
}
