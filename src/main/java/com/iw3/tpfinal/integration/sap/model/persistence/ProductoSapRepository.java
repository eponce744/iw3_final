package com.iw3.tpfinal.integration.sap.model.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.iw3.tpfinal.integration.sap.model.ProductoSap;

@Repository
public interface ProductoSapRepository extends JpaRepository<ProductoSap, Long>{
	Optional<ProductoSap> findOneByCodSap(String codSap);
}
