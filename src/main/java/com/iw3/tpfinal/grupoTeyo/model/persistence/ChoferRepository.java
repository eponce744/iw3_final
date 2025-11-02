package com.iw3.tpfinal.grupoTeyo.model.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.iw3.tpfinal.grupoTeyo.model.Chofer;
import java.util.Optional;


@Repository
public interface ChoferRepository extends JpaRepository<Chofer, Long> {

    Optional<Chofer> findByDocumento(String documento);

    Optional<Chofer> findByDocumentoAndIdNot(String documento, long id);

}
