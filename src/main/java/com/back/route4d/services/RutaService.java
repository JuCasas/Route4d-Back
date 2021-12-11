package com.back.route4d.services;

import com.back.route4d.model.Ruta;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

public interface RutaService {
    Ruta saveRuta(Ruta ruta);
    HashMap enviarRutas();
    int unlinkPedidoFromRuta(int pedidoId);
    void setRutaAsAveriada(LocalDateTime tiempo, int vehiculoId);
}
