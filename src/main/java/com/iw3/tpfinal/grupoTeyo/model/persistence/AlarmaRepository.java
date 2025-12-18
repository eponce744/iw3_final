package com.iw3.tpfinal.grupoTeyo.model.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.iw3.tpfinal.grupoTeyo.model.Alarma;
import com.iw3.tpfinal.grupoTeyo.model.Detalle;

@Repository
public interface AlarmaRepository extends JpaRepository<Alarma, Long>{

    Optional<List<Alarma>> findByOrdenId(long ordenId);

}
