package com.iw3.tpfinal.grupoTeyo.integration.tms.model.business.implementations;

import com.iw3.tpfinal.grupoTeyo.integration.tms.model.business.interfaces.IOrdenCli2Business;
import com.iw3.tpfinal.grupoTeyo.model.Orden;
import com.iw3.tpfinal.grupoTeyo.model.Producto;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.BusinessException;
import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.NotFoundException;
import com.iw3.tpfinal.grupoTeyo.model.business.implementations.DetalleBusiness;
import com.iw3.tpfinal.grupoTeyo.model.business.interfaces.IOrdenBusiness;
import com.iw3.tpfinal.grupoTeyo.model.persistence.OrdenRepository;
import com.iw3.tpfinal.grupoTeyo.util.ActivacionPassword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class OrdenCli2Business implements IOrdenCli2Business {

    @Autowired
    private OrdenRepository ordenRepository;

    @Autowired
    private IOrdenBusiness ordenBusiness;

    @Autowired
    private DetalleBusiness detalleBusiness;

    @Override
    public Orden registrarPesajeInicial(String numeroOrden, Double pesajeInicial) throws BusinessException, NotFoundException {
        Optional<Orden> ordenEncontrada;

        try {
            ordenEncontrada = ordenRepository.findByCamion_PatenteAndEstado(patente, Orden.Estado.PENDIENTE_PESAJE_INICIAL);
        } catch (Exception e) {
            //log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (ordenEncontrada.isEmpty()) {
            throw NotFoundException.builder().message("No se encuentra orden para cargar en camion con patente " + patente).build();
        }

        int password;
        do {
            password = Integer.parseInt(ActivacionPassword.generarActivacionPassword());
        } while (ordenRepository.findByActivarPassword(password).isPresent());


        ordenEncontrada.get().setActivacionPassword(password);
        ordenEncontrada.get().setInicialPesaje(pesajeInicial);
        ordenEncontrada.get().setFechaInicioCarga(new Date(System.currentTimeMillis()));
        ordenEncontrada.get().setEstado(Orden.Estado.PESAJE_INICIAL_REGISTRADO);
        ordenBusiness.update(ordenEncontrada.get());

        return ordenEncontrada.get();
    }

    @Override
    public Orden registrarPesajeFinal(String numeroOrden, Double pesajeFinal) throws BusinessException, NotFoundException {
        Optional<Orden> ordenActiva = Optional.empty();

        Orden orden = ordenActiva.get();
        orden.setFinalPesaje(pesajeFinal);
        orden.setFechaPesajeFinal(new Date(System.currentTimeMillis()));
        orden.setEstado(Orden.Estado.PESAJE_FINAL_REGISTRADO);

        Double pesajeInicial = orden.getInicialPesaje();
        Double cargaProducto = orden.getMasaAcumuladaFinal();
        Double totalPesaje = pesajeFinal - pesajeInicial;
        Double diferenciaCaudal = cargaProducto - totalPesaje;
        Double temperaturaPromedio = detalleBusiness.calcularPromedioTemperatura(orden.getId());
        Double densidadPromedio = detalleBusiness.calcularPromedioDensidad(orden.getId());
        Double caudalPromedio = detalleBusiness.calcularPromedioCaudal(orden.getId());
        Producto producto = orden.getProducto();

        ordenBusiness.update(orden);
        return orden;
    }
}
