package com.iw3.tpfinal.grupoTeyo.integration.cli2.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.iw3.tpfinal.grupoTeyo.model.Orden;
import com.iw3.tpfinal.grupoTeyo.model.business.interfaces.IDetalleBusiness;

import java.io.IOException;



public class OrdenCli2SlimV1JsonSerializer  extends StdSerializer<Orden> {

    private final IDetalleBusiness detalleBusiness;

    public OrdenCli2SlimV1JsonSerializer(Class<?> t, boolean dummy, IDetalleBusiness detalleBusiness) {
        super(t, dummy);
        this.detalleBusiness = detalleBusiness;
    }

    // Para realizar la conciciliacion

    @Override
    public void serialize(Orden orden, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("id", orden.getId());
        jsonGenerator.writeNumberField("preset", orden.getPreset());
        jsonGenerator.writeNumberField("Pesaje inicial", orden.getInicialPesaje());
        jsonGenerator.writeNumberField("Pesaje final", orden.getFinalPesaje());
        jsonGenerator.writeNumberField("Producto cargado", orden.getUltimaMasaAcumulada());
        jsonGenerator.writeNumberField("Neto por balanza", orden.getFinalPesaje() - orden.getInicialPesaje());
        jsonGenerator.writeNumberField("Diferencia entre balanza y caudalímetro", orden.getFinalPesaje() - orden.getInicialPesaje() - orden.getUltimaMasaAcumulada());
        if (detalleBusiness != null) {
            jsonGenerator.writeNumberField("Promedio de temperatura", detalleBusiness.calcularPromedioTemperatura(orden.getId()));
            jsonGenerator.writeNumberField("Promedio de densidad", detalleBusiness.calcularPromedioDensidad(orden.getId()));
            jsonGenerator.writeNumberField("Promedio de caudal", detalleBusiness.calcularPromedioCaudal(orden.getId()));
        }
        jsonGenerator.writeEndObject();
    }
}