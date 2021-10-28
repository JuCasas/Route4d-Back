package com.back.route4d.model;

import java.util.PriorityQueue;

public class Cluster implements Comparable<Cluster>{
    public Vehicle vehiculo;
    public int centroideX;
    public int centroideY;
    public int centroideZ;
    public Pedido firstPedido = null;
    public int capacidad = 0;
    public PriorityQueue<Pedido> pedidos;

    public void setClusterNo(Pedido pedido){
        pedidos.add(pedido);
        this.capacidad += pedido.getCantidad();
    }

    @Override
    public int compareTo(Cluster c) {
        if (this.firstPedido.getMinFaltantes() == c.firstPedido.getMinFaltantes()) {
            if (this.firstPedido.getCantidad() == c.firstPedido.getCantidad()) {
                return 1;
            }
            else if (this.firstPedido.getCantidad() < c.firstPedido.getCantidad()) {
                return 1;
            }
            else {
                return -1;
            }
        }
        else if (this.firstPedido.getMinFaltantes() > c.firstPedido.getMinFaltantes()) {
            return 1;
        }
        else {
            return -1;
        }
    }
}
