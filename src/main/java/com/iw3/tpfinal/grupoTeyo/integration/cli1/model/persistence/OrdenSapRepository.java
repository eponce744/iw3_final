package com.iw3.tpfinal.grupoTeyo.integration.cli1.model.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.iw3.tpfinal.grupoTeyo.integration.cli1.model.OrdenSap;

@Repository
public interface OrdenSapRepository extends JpaRepository<OrdenSap, Long>{
	Optional<OrdenSap> findOneByCodSap(String codSap) ;

}
