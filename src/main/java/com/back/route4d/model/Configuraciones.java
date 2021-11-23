package com.back.route4d.model;

public class Configuraciones {
    public static int almacenX = 12; // coordenada X del almacén
    public static int almacenY = 8; // coordenada Y del almacén
    public static int almacen = almacenX + almacenY * 71 + 1; // id del nodo del almacén
    public static int norteX = 42; // coordenada X del almacén
    public static int norteY = 42 ; // coordenada Y del almacén
    public static int norte = norteX + norteY * 71 + 1; // id del nodo del almacén
    public static int surX = 63; // coordenada X del almacén
    public static int surY = 3; // coordenada Y del almacén
    public static int sur = surX + surY * 71 + 1; // id del nodo del almacén
    public static int V = 3621; // cantidad de vértices del grafo
    public static int E = 14240; // cantidad de aristas del grafo
    public static double penalidad = 20.0;
    public static double precio = 200.0;
}
