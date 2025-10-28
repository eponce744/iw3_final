package com.iw3.tpfinal.grupoTeyo.model.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.iw3.tpfinal.grupoTeyo.model.Orden;

@Repository
public interface OrdenRepository extends JpaRepository<Orden, Long> {

}
