package com.back.route4d.helper;

import com.back.route4d.model.CallesBloqueadas;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Clase con funciones de ayuda para el resto del código
 */
public class Helper {
    /**
     * Obtiene mes y año a partir del nombre del archivo de nodos bloqueados
     *
     * @param   fileName  cadena con la ruta completa del archivo
     *
     * @return  cadena con el mes y el año correspondientes
     */
    public static String getLockedNodesDateFromName(String fileName) {
        File file = new File(fileName);
        String name = file.getName();
        String strYearMonth = name.substring(0, 4) + "-" + name.substring(4, 6);

        return strYearMonth;
    }

    /**
     * Obtiene mes y año a partir del nombre del archivo de pedidos
     *
     * @param   fileName  cadena con la ruta completa del archivo
     *
     * @return  cadena con el mes y el año correspondientes
     */
    public static String getOrdersDateFromName(String fileName) {
        File file = new File(fileName);
        String name = file.getName();
        String strYearMonth = name.substring(6, 10) + "-" + name.substring(10, 12);

        return strYearMonth;
    }

    /**
     * Convierte una fecha del tipo LocalDateTime a minutos del tipo int
     *
     * @param   ldt  fecha del tipo LocalDateTime
     *
     * @return  la fecha convertida a los minutos que pasaron desde el inicio del año
     */
    public static Integer convertLocalDateTimeToMinutes(LocalDateTime ldt) {
        LocalDateTime d1 = LocalDateTime.of(2021, Month.JANUARY, 1, 0, 0);

        return (int) ChronoUnit.MINUTES.between(d1, ldt);
    }

    /**
     * Convierte minutos del tipo int a una fecha del tipo LocalDateTime
     *
     * @param   mins  cantidad de minutos que pasaron desde el inicio del año
     *
     * @return  los minutos convertidos a la fecha correspondiente
     */
    public static LocalDateTime convertMinutesToLocalDateTime(int mins) {
        LocalDateTime d1 = LocalDateTime.of(2021, Month.JANUARY, 1, 0, 0);

        return d1.plus(Duration.of(mins, ChronoUnit.MINUTES));
    }

    /**
     * Verifica si un nodo está bloqueado
     *
     * @param   currentTime      tiempo transcurrido en minutos
     * @param   nodeID           ID del nodo
     * @param   closedRoadsList  lista de calles bloqueadas
     *
     * @return  true o false dependiendo de si el nodo está bloqueado o no
     */
    public static boolean isBlocked(int currentTime, int nodeID, List<CallesBloqueadas> closedRoadsList) {
        // Se recorre la lista de calles bloqueadas
        for (CallesBloqueadas closedRoad : closedRoadsList) {
            // El nodo estará bloqueado si se encuentra en la calle bloqueada en la duración del bloqueo
            if (currentTime >= closedRoad.getMinutosInicio() && currentTime < closedRoad.getMinutosFin()) {
                return closedRoad.estaNodo(nodeID);
            }
        }

        return false;
    }
}
