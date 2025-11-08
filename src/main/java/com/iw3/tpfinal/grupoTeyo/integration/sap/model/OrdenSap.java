package com.iw3.tpfinal.grupoTeyo.integration.sap.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.iw3.tpfinal.grupoTeyo.model.Orden;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="sap_ordenes")
@PrimaryKeyJoinColumn(name="id_orden") //PrimaryKey (foranea) de la subclase, apunta a PrimaryKey de Clase principal
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrdenSap extends Orden{
	
	@Column(nullable=false, unique=true)
	private String codSap;
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "America/Argentina/Buenos_Aires")
	private Date fechaPrevistaCarga;

}
