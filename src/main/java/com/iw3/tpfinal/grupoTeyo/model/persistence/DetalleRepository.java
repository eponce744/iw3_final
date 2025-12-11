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

    @Query("SELECT AVG(d.densidad) FROM Detalle d WHERE d.orden.id = :ordenId")
    Double findAverageDensidadByOrdenId(long ordenId);

    @Query("SELECT AVG(d.caudal) FROM Detalle d WHERE d.orden.id = :ordenId")
    Double findAverageCaudalByOrdenId(long ordenId);
    /**
     * Devuelve la fecha del último detalle guardado (MAX de fechaUltimoDato) para la orden indicada.
     * Equivalente JPQL a: SELECT MAX(d.fecha_ultimo_dato) FROM detalle d WHERE d.id_orden = :ordenId
     */
    @Query("SELECT MAX(d.fechaUltimoDato) FROM Detalle d WHERE d.orden.id = :ordenId")
    java.util.Date findMaxFechaUltimoDatoByOrdenId(long ordenId);

}
