package com.iw3.tpfinal.grupoTeyo.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.iw3.tpfinal.grupoTeyo.auth.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="alarmas")
@Inheritance(strategy = InheritanceType.JOINED)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Alarma {

    public enum Estado{
        PENDIENTE,
        ATENDIDA
    }

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;    

    @Column(length = 500, nullable = true)
    private String observacion;
    
    @Enumerated(EnumType.STRING)
    @Column()
    private Estado estado;

	@ManyToOne // Relación muchos a uno con Orden (muchos detalles pertenecen a una orden)
	@JsonBackReference //"Lado hijo"-Para evitar referencia circular al serializar a JSON
	@JoinColumn(name="id_orden", nullable = true)
	private Orden orden;

    @Column(columnDefinition = "DATETIME",nullable = true)
    private Date fecha;

    @Column(nullable = false)
    private Double temperaturaRegistrada;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private User usuario;
}
