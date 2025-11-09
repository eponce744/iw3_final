package com.iw3.tpfinal.grupoTeyo.util;

import java.security.SecureRandom;

public class ActivacionPassword {

    private static final SecureRandom RANDOM = new SecureRandom();
    public static String generarActivacionPassword(){
        int numero = RANDOM.nextInt(90000) + 10000; // Genera un número aleatorio de 5 dígitos
        return Integer.toString(numero);
    }
}
