package com.iw3.tpfinal.grupoTeyo.integration.sap.model;

import com.iw3.tpfinal.grupoTeyo.model.Chofer;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="sap_choferes")
@PrimaryKeyJoinColumn(name="id_chofer") //PrimaryKey (foranea) de la subclase, apunta a PrimaryKey de Clase principal
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChoferSap extends Chofer{

	@Column(nullable=false, unique=true)
	private String codChoferSap;

}
