package com.iw3.tpfinal.integration.sap.model;

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
@PrimaryKeyJoinColumn(name="id_orden")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrdenSap extends Orden{
	
	@Column(nullable=false, unique=true)
	private String codSap1;

}
