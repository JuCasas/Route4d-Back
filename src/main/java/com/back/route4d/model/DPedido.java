package com.back.route4d.model;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class DPedido implements Comparable<DPedido> {

    public int id;
    public int idCluster;
    public int x;
    public int y;
    public int cantidad;
    public int minFaltantes;
    public LocalDateTime fechaPedido;
    public LocalDateTime fechaLimite;
    public int tiempoEntregaRealizada;

    public DPedido(int id, int x, int y, int cantidad, int minFaltantes) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.cantidad = cantidad;
        this.minFaltantes = minFaltantes;
    }

    public DPedido(int id, int x, int y, int cantidad, int minFaltantes, LocalDateTime fechaPedido) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.cantidad = cantidad;
        this.minFaltantes = minFaltantes;
        this.fechaPedido = fechaPedido;
    }

    public int getNodoId() {
        return this.x + 71 * this.y + 1;
    }

    @Override
    public String toString() {
        return this.getNodoId() + " Cantidad: " + this.cantidad + " Minutos Faltantes: " + this.minFaltantes;
    }

    @Override
    public int compareTo(DPedido p) {
        if (this.minFaltantes == p.minFaltantes) {
            if (this.cantidad == p.cantidad)
                return 0;
            else if (this.cantidad < p.cantidad)
                return 1;
            else
                return -1;
        } else if (this.minFaltantes > p.minFaltantes)
            return 1;
        else
            return -1;
    }
}
