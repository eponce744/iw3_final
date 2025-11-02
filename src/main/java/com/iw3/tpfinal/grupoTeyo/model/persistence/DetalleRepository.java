package com.iw3.tpfinal.grupoTeyo.model.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.iw3.tpfinal.grupoTeyo.model.Detalle;

@Repository
public interface DetalleRepository extends JpaRepository<Detalle, Long>{

    // Método para encontrar detalles por el ID de la orden
    Optional<List<Detalle>> findByOrdenId(long ordenId);

}
