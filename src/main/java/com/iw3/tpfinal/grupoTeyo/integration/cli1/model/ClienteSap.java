package com.iw3.tpfinal.grupoTeyo.integration.cli1.model;

import com.iw3.tpfinal.grupoTeyo.model.Cliente;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="sap_clientes")
@PrimaryKeyJoinColumn(name="id_cliente") //PrimaryKey (foranea) de la subclase, apunta a PrimaryKey de Clase principal
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ClienteSap extends Cliente{

	@Column(nullable=false, unique=true)
	private String codClienteSap;

}
