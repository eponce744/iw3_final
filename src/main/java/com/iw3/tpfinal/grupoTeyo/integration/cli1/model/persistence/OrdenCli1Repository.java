package com.iw3.tpfinal.grupoTeyo.integration.cli1.model.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.iw3.tpfinal.grupoTeyo.integration.cli1.model.OrdenCli1;

@Repository
public interface OrdenCli1Repository extends JpaRepository<OrdenCli1, Long>{
	Optional<OrdenCli1> findOneByCodSap(String codSap) ;

}
