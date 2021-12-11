package com.back.route4d.services;

import com.back.route4d.model.Ruta;

import java.util.HashMap;

public interface RutaService {
    Ruta saveRuta(Ruta ruta);
    HashMap enviarRutas();
    int unlinkPedidoFromRuta(int pedidoId);
}
