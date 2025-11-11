package com.iw3.tpfinal.grupoTeyo.model.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.iw3.tpfinal.grupoTeyo.model.Detalle;

@Repository
public interface DetalleRepository extends JpaRepository<Detalle, Long>{

    // Método para encontrar detalles por el ID de la orden
    Optional<List<Detalle>> findByOrdenId(long ordenId);

    @Query("SELECT AVG(d.temperatura) FROM Detalle d WHERE d.orden.id = :ordenId")
    Double findAverageTemperaturaByOrdenId(long ordenId);

    @Query("SELECT AVG(d.densidad) FROM detalle d WHERE d.orden.id = :ordenId")
    Double findAverageDensidadByOrdenId(long ordenId);

    @Query("SELECT AVG(d.caudal) FROM detalle d WHERE d.orden.id = :ordenId")
    Double findAverageCaudalByOrdenId(long ordenId);

}
