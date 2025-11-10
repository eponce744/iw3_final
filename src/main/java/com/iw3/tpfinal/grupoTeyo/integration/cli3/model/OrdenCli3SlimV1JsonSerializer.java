package com.iw3.tpfinal.grupoTeyo.integration.cli3.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.iw3.tpfinal.grupoTeyo.model.Orden;

import java.io.IOException;

public class OrdenCli3SlimV1JsonSerializer  extends StdSerializer<Orden> {

    public OrdenCli3SlimV1JsonSerializer(Class<?> t, boolean dummy) {
        super(t, dummy);
    }

    @Override
    public void serialize(Orden orden, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("id", orden.getId());
        jsonGenerator.writeNumberField("preset", orden.getPreset());
        jsonGenerator.writeEndObject();
    }
}