package com.iw3.tpfinal.grupoTeyo.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Locale;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public final class JsonUtiles {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static ObjectMapper getObjectMapper(Class clazz, StdSerializer ser, String dateFormat) {
		ObjectMapper mapper = new ObjectMapper();
		String defaultFormat = "yyyy-MM-dd'T'HH:mm:ssZ";
		if (dateFormat != null)
			defaultFormat = dateFormat;
		SimpleDateFormat df = new SimpleDateFormat(defaultFormat, Locale.getDefault());
		SimpleModule module = new SimpleModule();
		if (ser != null) {
			module.addSerializer(clazz, ser);
		}
		mapper.setDateFormat(df);
		mapper.registerModule(module);
		return mapper;

	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static ObjectMapper getObjectMapper(Class clazz, StdDeserializer deser, String dateFormat) {
		ObjectMapper mapper = new ObjectMapper();
		String defaultFormat = "yyyy-MM-dd'T'HH:mm:ssZ";
		if (dateFormat != null)
			defaultFormat = dateFormat;
		SimpleDateFormat df = new SimpleDateFormat(defaultFormat, Locale.getDefault());
		SimpleModule module = new SimpleModule();
		if (deser != null) {
			module.addDeserializer(clazz, deser);
		}
		mapper.setDateFormat(df);
		mapper.registerModule(module);
		return mapper;
	}

	/**
	 * Obtiene una cadena con la siguiente lógica:
	 * 1) Busca en cada uno de los atributos definidos en el arreglo "attrs",
	 *    el primero que encuentra será el valor retornado.
	 * 2) Si no se encuentra ninguno de los atributos del punto 1), se
	 *    retorna "defaultValue".
	 * Ejemplo: supongamos que "node" represente: {"code":"c1, "codigo":"c11", "stock":true}
	 *   getString(node, String[]{"codigo","cod"},"-1") retorna: "cl1"
	 *   getString(node, String[]{"cod_prod","c_prod"},"-1") retorna: "-1"
	 * @param node
	 * @param attrs
	 * @param defaultValue
	 * @return
	 */

	public static String getString(JsonNode node, String[] attrs, String defaultValue) {
		String r = null;
		for (String attr : attrs) {
			if (node.get(attr) != null) {
				r = node.get(attr).asText();
				break;
			}
		}
		if (r == null)
			r = defaultValue;
		return r;
	}

	public static double getDouble(JsonNode node, String[] attrs, double defaultValue) {
		Double r = null;
		for (String attr : attrs) {
			if (node.get(attr) != null && node.get(attr).isDouble()) {
				r = node.get(attr).asDouble();
				break;
			}
		}
		if (r == null)
			r = defaultValue;
		return r;
	}

	public static boolean getBoolean(JsonNode node, String[] attrs, boolean defaultValue) {
		Boolean r = null;
		for (String attr : attrs) {
			if (node.get(attr) != null && node.get(attr).isBoolean()) {
				r = node.get(attr).asBoolean();
				break;
			}
		}
		if (r == null)
			r = defaultValue;
		return r;
	}

	public static JsonNode getNode(JsonNode node, String[] attrs, JsonNode defaultValue) {
		JsonNode targetNode = null;

        for (String attr : attrs) {
            if (node.has(attr) && node.get(attr).isObject()) {
                targetNode = node.get(attr);
                break;
            }
        }
		if(targetNode == null) {
			return defaultValue;
		}
		
		return targetNode;
	}

	public static Long getLong(JsonNode node, String[] attrs, Long defaultValue) {
		Long r = null;
		for (String attr : attrs) {
			if (node.get(attr) != null && node.get(attr).isLong()) {
				r = node.get(attr).asLong();
				break;
			}
		}
		if (r == null)
			r = defaultValue;
		return r;
	}

	public static int[] getInt(JsonNode node, String[] attrs, int defaultValue) {
		if (node == null || attrs == null) {
        	return new int[]{ defaultValue };
    	}
	
    	for (String attr : attrs) {
    	    JsonNode attrNode = node.get(attr);
    	    if (attrNode != null && attrNode.isArray()) {
    	        int[] result = new int[attrNode.size()];
    	        for (int i = 0; i < attrNode.size(); i++) {
    	            result[i] = attrNode.get(i).asInt();
    	        }
    	        return result;
    	    }
    	}

    	return new int[]{ defaultValue };
	}

    public static Date getDate(JsonNode node, String[] attrs, String defaultValue) {
        Date parsedDate = null;

        // Formatos de fecha para intentar
        SimpleDateFormat[] formats = {
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault()), // Con zona horaria
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())   // Sin zona horaria
        };

        // Intentar obtener la fecha desde uno de los atributos
        for (String attr : attrs) {
            if (node.get(attr) != null) {
                String dateStr = node.get(attr).asText();
                for (SimpleDateFormat format : formats) {
                    try {
                        parsedDate = format.parse(dateStr);
                        if (parsedDate != null) {
                            return parsedDate; // Si parsea correctamente, devolvemos la fecha
                        }
                    } catch (ParseException e) {
                        // Continuar con el siguiente formato si falla el parseo
                    }
                }
            }
        }

        // Si no se encontró un valor válido, intentar con el valor por defecto
        if (defaultValue != null) {
            for (SimpleDateFormat format : formats) {
                try {
                    parsedDate = format.parse(defaultValue);
                    if (parsedDate != null) {
                        return parsedDate; // Si parsea el default, devolverlo
                    }
                } catch (ParseException e) {
                    //  Si falla el default, seguir intentando con otros formatos
                }
            }
        }
        return parsedDate; // Si no se pudo parsear, devolver null
    }

}
