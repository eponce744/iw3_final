package com.iw3.tpfinal.grupoTeyo.controllers;

public final class Constants {
    public static final String URL_API = "/api";
    public static final String URL_API_VERSION = "/v1";
    public static final String URL_BASE = URL_API + URL_API_VERSION;

    //Servicios
    public static final String URL_CAMIONES = URL_BASE + "/camiones";
    public static final String URL_CHOFERES = URL_BASE + "/choferes";
    public static final String URL_CLIENTES = URL_BASE + "/clientes";
    public static final String URL_ORDENES = URL_BASE + "/ordenes";
    public static final String URL_DETALLES = URL_BASE + "/detalles";
    public static final String URL_PRODUCTOS = URL_BASE + "/productos";
    
    //Integracion
    public static final String URL_INTEGRATION = URL_BASE + "/integration";
    public static final String URL_INTEGRATION_SAP = URL_INTEGRATION + "/sap";
    public static final String URL_INTEGRATION_TMS = URL_INTEGRATION + "/tms";
    public static final String URL_INTEGRATION_CLI3 = URL_INTEGRATION + "/cli3";
    
    //Login
    public static final String URL_LOGIN = URL_BASE + "/login";
}
