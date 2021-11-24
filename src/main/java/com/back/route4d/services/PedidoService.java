package com.back.route4d.services;

import com.back.route4d.model.Pedido;

import java.time.LocalDateTime;
import java.util.List;

public interface PedidoService {
    Pedido savePedido(Pedido pedido);
    List<Pedido> getAllPedidos();
    Pedido getPedidoById(int id);
    Pedido updatePedido(Pedido pedido, int id);
    List<Pedido> findByDate(LocalDateTime inicio, LocalDateTime fin);
    void deletePedido(int id);
}
