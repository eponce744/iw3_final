package com.iw3.tpfinal.grupoTeyo.model;

import java.util.ArrayList;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "productos")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    // Código externo para integración con sistemas terceros (opcional)
    @Column(name = "codigo_externo", length = 100, unique = true, nullable = true)
    private String codigoExterno;

    // Nombre obligatorio
    @Column(nullable = false, length = 200)
    private String nombre;

    // Descripción opcional
    @Column(nullable = true, length = 1000)
    private String descripcion;

    // Indica si el producto es una entidad compuesta (tiene componentes)
    @Column(nullable = false)
    private boolean compuesto = false;

    // Componentes cuando el producto es compuesto (relación self-referenciada)
    @ManyToMany
    @JoinTable(
        name = "producto_componentes",
        joinColumns = @JoinColumn(name = "producto_id"),
        inverseJoinColumns = @JoinColumn(name = "componente_id")
    )
    private ArrayList<Producto> componentes = new ArrayList<>();

}

