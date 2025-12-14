package com.iw3.tpfinal.grupoTeyo.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "productos")
@Inheritance(strategy = InheritanceType.JOINED) //Definimos la estrategia de implementacion de Herencia
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    // Nombre obligatorio
    @Column(nullable = false, length = 200, unique = true)
    private String nombre;

    // Descripción opcional
    @Column(nullable = true, length = 1000)
    private String descripcion;
    
    //Valor umbral de temperatura
    @Column(nullable = false)
    private Double umbralTemperatura;

}

