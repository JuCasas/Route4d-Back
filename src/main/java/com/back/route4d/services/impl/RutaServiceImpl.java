package com.back.route4d.services.impl;

import com.back.route4d.model.Ruta;
import com.back.route4d.model.RutaFront;
import com.back.route4d.repository.RutaRepository;
import com.back.route4d.services.RutaService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class RutaServiceImpl implements RutaService {

    private RutaRepository rutaRepository;

    public RutaServiceImpl(RutaRepository rutaRepository) {
        super();
        this.rutaRepository = rutaRepository;
    }

    @Override
    public Ruta saveRuta(Ruta ruta) {
        return rutaRepository.save(ruta);
    }

    @Override
    public HashMap enviarRutas() {
        List<Ruta> listaRutas = rutaRepository.getRutasActuales(LocalDateTime.now());

        List<RutaFront> listaRutasFront = new ArrayList<>();

        for (Ruta ruta : listaRutas) {
            List<Map<String,Integer>> recorridoEnviar = new ArrayList<>();
            List<Map<String,Integer>> retornoEnviar = new ArrayList<>();

            // obteniendo cadenas de recorridos
            String recorridoString = ruta.getRecorridoString();
            String retornoString = ruta.getRetornoString();

            List<String> recorridoStringNodos = new ArrayList<String>(Arrays.asList(recorridoString.split(",")));
            List<String> retornoStringNodos = new ArrayList<String>(Arrays.asList(retornoString.split(",")));

            for (String s : recorridoStringNodos) {
                int nodoRecorrido = Integer.valueOf(s);
                int x = (nodoRecorrido - 1) % 71;
                int y = (nodoRecorrido - 1) / 71;
                Map<String ,Integer> map=new HashMap<String,Integer>();
                map.put("x",x);
                map.put("y",y);
                recorridoEnviar.add(map);
            }

            for (String s : retornoStringNodos) {
                int nodoRetorno = Integer.valueOf(s);
                int x = (nodoRetorno - 1) % 71;
                int y = (nodoRetorno - 1) / 71;
                Map<String ,Integer> map=new HashMap<String,Integer>();
                map.put("x",x);
                map.put("y",y);
                retornoEnviar.add(map);
            }

            RutaFront rutaFront = new RutaFront(ruta.vehiculo, ruta.capacidad);

            rutaFront.setId(ruta.getId());
            rutaFront.setDuracion_minutos(ruta.duracion_minutos);
            rutaFront.setDuracionMinutosRecorrido(ruta.duracionMinutosRecorrido);
            rutaFront.setDuracionMinutosRetorno(ruta.duracionMinutosRetorno);

            rutaFront.setPlazoEntrega(ruta.plazoEntrega);
            rutaFront.setFechaInicioRecorrido(ruta.fechaInicioRecorrido);
            rutaFront.setFechaInicioRetorno(ruta.fechaInicioRetorno);
            rutaFront.setFechaFinRetorno(ruta.fechaFinRetorno);

            rutaFront.setTiempoFin(ruta.getPlazoEntrega().getMinute()); //
            rutaFront.pedidos.addAll(ruta.pedidos);
            rutaFront.recorrido.addAll(recorridoEnviar);
            rutaFront.retorno.addAll(retornoEnviar);

            listaRutasFront.add(rutaFront);
        }

        HashMap<String, Object> listaRutasEnviar = new HashMap<>();
        listaRutasEnviar.put("Rutas", listaRutasFront);

        return listaRutasEnviar;
    }

    @Override
    public int unlinkPedidoFromRuta(int pedidoId) {
        return rutaRepository.unlinkPedidoFromRuta(pedidoId);
    }

    @Override
    public void setRutaAsAveriada(LocalDateTime tiempo, int idVehiculo) {
        List<Ruta> rutas = rutaRepository.getRutasVehiculo(tiempo, idVehiculo);

        for (Ruta ruta : rutas) {
            ruta.setTipoRuta(1); // ruta con vehículo averiado
            rutaRepository.save(ruta);
        }
    }
}
