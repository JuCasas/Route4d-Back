package com.back.route4d.services.impl;

import com.back.route4d.exception.ResourceNotFoundException;
import com.back.route4d.model.Pedido;
import com.back.route4d.repository.PedidoRepository;
import com.back.route4d.services.PedidoService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PedidoServiceImpl implements PedidoService {

    private PedidoRepository pedidoRepository;

    public PedidoServiceImpl(PedidoRepository pedidoRepository) {
        super();
        this.pedidoRepository = pedidoRepository;
    }

    @Override
    public Pedido savePedido(Pedido pedido) {
        return pedidoRepository.save(pedido);
    }

    @Override
    public List<Pedido> getAllPedidos() {
        return pedidoRepository.findAll();
    }

    @Override
    public Pedido getPedidoById(int id) {
        return pedidoRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException("Pedido","Id",id));
    }

    @Override
    public Pedido updatePedido(Pedido pedido, int id) {
        Pedido existingPedido = pedidoRepository.findById(id).orElseThrow(
                ()-> new ResourceNotFoundException("Pedido","Id",id));

        existingPedido.setX(pedido.getX());
        existingPedido.setY(pedido.getY());
        existingPedido.setIdCluster(pedido.getIdCluster());
        existingPedido.setCantidad(pedido.getCantidad());
        existingPedido.setMinFaltantes(pedido.getMinFaltantes());
        existingPedido.setFechaPedido(pedido.getFechaPedido());
        existingPedido.setFechaLimite(pedido.getFechaLimite());
        existingPedido.setTiempoEntrega(pedido.getTiempoEntrega());

        pedidoRepository.save(existingPedido);
        return existingPedido;
    }

    @Override
    public List<Pedido> findByDate(LocalDateTime inicio, LocalDateTime fin) {
        return pedidoRepository.findByDate(inicio,fin,1);
    }

    @Override
    public void deletePedido(int id) {
        //Pedido exists?
        pedidoRepository.findById(id).orElseThrow(
                ()-> new ResourceNotFoundException("Pedido","Id",id));

        pedidoRepository.deleteById(id);
    }

    @Override
    public void desasignarPedido(int id) {
        Pedido existingPedido = pedidoRepository.findById(id).orElseThrow(
                ()-> new ResourceNotFoundException("Pedido","Id",id));

        existingPedido.setEstado(0);

        pedidoRepository.save(existingPedido);
    }
}
