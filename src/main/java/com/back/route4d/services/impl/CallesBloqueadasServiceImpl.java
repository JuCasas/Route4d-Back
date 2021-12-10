package com.back.route4d.services.impl;

import com.back.route4d.helper.Helper;
import com.back.route4d.model.CallesBloqueadas;
import com.back.route4d.model.CallesBloqueadasFront;
import com.back.route4d.repository.CallesBloqueadasRepository;
import com.back.route4d.services.CallesBloqueadasService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Service
public class CallesBloqueadasServiceImpl implements CallesBloqueadasService {
    private CallesBloqueadasRepository callesBloqueadasRepository;

    public CallesBloqueadasServiceImpl(CallesBloqueadasRepository callesBloqueadasRepository) {
        super();
        this.callesBloqueadasRepository = callesBloqueadasRepository;
    }

    @Override
    public HashMap enviarBloqueos() {
        List<CallesBloqueadas> listaCallesBloqueadas = callesBloqueadasRepository.findAll();
        List<CallesBloqueadasFront> listaCallesBloqueadasFront = new ArrayList<>();

        // para el día de hoy
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime tomorrow = today.plusDays(1);

        int yearToday = today.getYear();
        Month monthToday = today.getMonth();
        int dayToday = today.getDayOfMonth();

        int yearTomorrow = tomorrow.getYear();
        Month monthTomorrow = tomorrow.getMonth();
        int dayTomorrow = tomorrow.getDayOfMonth();

        LocalDateTime start = LocalDateTime.of(yearToday, monthToday, dayToday, 0, 0);
        LocalDateTime finish = LocalDateTime.of(yearTomorrow, monthTomorrow, dayTomorrow, 0, 0);

        for (CallesBloqueadas callesBloqueadas : listaCallesBloqueadas) {
            Integer minutosInicio = callesBloqueadas.getMinutosInicio();
            Integer minutosFin = callesBloqueadas.getMinutosFin();
            LocalDateTime fechaInicio = Helper.convertMinutesToLocalDateTime(minutosInicio);
            LocalDateTime fechaFin = Helper.convertMinutesToLocalDateTime(minutosFin);

            // verificando si la fecha está en rango
            boolean fechaInicioEnRango = fechaInicio.isAfter(start) && fechaInicio.isBefore(finish);
            boolean fechaFinEnRango = fechaFin.isAfter(start) && fechaFin.isBefore(finish);
            boolean bloqueoEnRango = fechaInicio.isBefore(start) && fechaFin.isAfter(finish);
            if (fechaInicioEnRango || fechaFinEnRango || bloqueoEnRango) {
                String nodos = callesBloqueadas.getNodos();
                List<String> listaNodos = new ArrayList<String>(Arrays.asList(nodos.split(",")));

                for (String s : listaNodos) {
                    int nodo = Integer.valueOf(s);

                    int x = (nodo - 1) % 71;
                    int y = (nodo - 1) / 71;

                    boolean encontrado = false;
                    for (CallesBloqueadasFront bloqueoRevisar : listaCallesBloqueadasFront) {
                        if (bloqueoRevisar.esBloqueo(x,y)){
                            bloqueoRevisar.addTime(fechaInicio,fechaFin);
                            encontrado = true;
                            break;
                        }
                    }

                    if(!encontrado) {
                        CallesBloqueadasFront calleFront = new CallesBloqueadasFront(x,y);
                        calleFront.addTime(fechaInicio,fechaFin);
                        listaCallesBloqueadasFront.add(calleFront);
                    }
                }
            }
        }
        HashMap<String, Object> listaBloqueos = new HashMap<>();
        listaBloqueos.put("Bloqueos", listaCallesBloqueadasFront);

        return listaBloqueos;
    }
}
