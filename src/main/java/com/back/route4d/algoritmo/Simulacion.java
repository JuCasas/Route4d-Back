package com.back.route4d.algoritmo;

import com.back.route4d.algoritmo.dijkstra.Dijkstra;
import com.back.route4d.algoritmo.kmeans.Kmeans;
import com.back.route4d.firebase.FirebaseInitializer;
import com.google.cloud.firestore.CollectionReference;
import com.back.route4d.helper.Helper;
import com.back.route4d.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@Slf4j
public class Simulacion {

    public List<Pedido> listaPedidosTotales;
    public List<Pedido> listaPedidosEnCola;
    public List<Pedido> listaPedidosSinCumplir;
    public List<Pedido> listaPedidosEnRuta;
    public List<RutaFront> listaRutasEnRecorrido;
    public List<Ruta> listaRutas;
    public List<CallesBloqueadas> listaCallesBloqueadas;
    public List<Vehicle> listaVehiculoTipo1;
    public List<Vehicle> listaVehiculoTipo2;
    public List<Vehicle> listaVehiculoTipo3;
    public List<Vehicle> listaVehiculoTipo4;
    public List<Cluster> clusterResult;
    //0 -> colapso
    //1 -> 3 dias
    public int tipoSimulacion = 1;

    public volatile boolean collect = true;

    public Dijkstra dijkstraAlgorithm;
    public Kmeans kmeans;

    public Integer cantClusterVehiculoTipo1 = 0;
    public Integer cantClusterVehiculoTipo2 = 0;
    public Integer cantClusterVehiculoTipo3 = 0;
    public Integer cantClusterVehiculoTipo4 = 0;
    public Integer cantVehiculoTipo1 = 0;
    public Integer cantVehiculoTipo2 = 0;
    public Integer cantVehiculoTipo3 = 0;
    public Integer cantVehiculoTipo4 = 0;

    public Integer demandaTotal = 0;
    public double constantePenalidad = 1;

    public FileWriter archivo;

    @Autowired
    private FirebaseInitializer firebase;

    public Integer tiempoEnMinutosActual = 0;

    public Integer vehiculosDisponiblesTipo1 = 0;
    public Integer vehiculosDisponiblesTipo2 = 0;
    public Integer vehiculosDisponiblesTipo3 = 0;
    public Integer vehiculosDisponiblesTipo4 = 0;

    public double ganancia = 0.0;
    public Integer numPenalidades = 0;
    public double montoPenalidades = 0.0;
    public Integer numPedidoEntregados = 0;
    public double costoMantenimiento = 0;

    // Funciones para cargar y devolver pedidos y calles bloqueadas

    /**
     * Devuelve la lista de pedidos
     *
     * @return  la lista de pedidos
     */
    public List<Pedido> getOrders() {
        return listaPedidosTotales;
    }

    /**
     * Devuelve la lista de calles bloqueadas
     *
     * @return  la lista de calles bloqueadas
     */
    public List<CallesBloqueadas> getClosedRoads() {
        return listaCallesBloqueadas;
    }

    /**
     * Carga un archivo de pedidos
     *
     * @param   mpFile  archivo de pedidos
     *
     * @return  una cadena indicando la fecha y hora del último pedido cargado
     */
    public String uploadOrdersFile(MultipartFile mpFile) {
        try {
            File file = Helper.convertMultipartFileToFile(mpFile);
            uploadAllOrders(file);
            file.delete();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-M-d HH:mm:ss");
            return listaPedidosTotales.get(listaPedidosTotales.size()-1).getFechaPedido().format(dtf);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Cambiar tipo de simulacion
     *
     * @param   tipo indicara el tipo simulacion
     *
     * @return  mensaje de confirmacion
     */
    public String updateSimulacionTipo(int tipo) {
        tipoSimulacion = tipo;
        if(tipo==1) return "Simulacion 3 dias";
        return "Simulacion hasta el colapso";
    }


    /**
     * Carga un archivo de calles bloqueadas
     *
     * @param   mpFile  archivo de calles bloqueadas
     *
     * @return  una cadena indicando si el proceso fue exitoso
     */
    public String uploadClosedRoadsFile(MultipartFile mpFile) {
        try {
            File file = Helper.convertMultipartFileToFile(mpFile);
            uploadAllClosedRoads(file);
            file.delete();
            return "Done!";
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Carga los contenidos del archivo de pedidos
     *
     * @param   file  archivo de pedidos
     */
    private void uploadAllOrders(File file) throws FileNotFoundException {
        Scanner sc = new Scanner(file);
        String strYearMonth = Helper.getOrdersDateFromName(file.getName());
        int id = 1;
        listaPedidosTotales = new ArrayList<>();

        listaPedidosSinCumplir = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-d H:m:s");

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            Pedido pedido = getOrderFromLine(line, strYearMonth, formatter);
            pedido.setId(id);
            //TODO 3 dias
            if (tipoSimulacion == 1 && pedido.getFechaPedido().getDayOfMonth()>3)break;
            listaPedidosTotales.add(pedido);
            id++;
        }

        Collections.sort(listaPedidosTotales,
                (p1,p2) -> (int)ChronoUnit.MINUTES.between(p2.getFechaPedido(), p1.getFechaPedido()));
        sc.close();
    }

    /**
     * Carga los contenidos del archivo de calles bloqueadas
     *
     * @param   file  archivo de calles bloqueadas
     */
    private void uploadAllClosedRoads(File file) throws FileNotFoundException {
        Scanner sc = new Scanner(file);
        String strYearMonth = Helper.getLockedNodesDateFromName(file.getName());
        int id = 1;
        listaCallesBloqueadas = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-d H:m:s");

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            CallesBloqueadas calleBloqueada = getClosedRoadFromLine(line, strYearMonth, formatter);
            calleBloqueada.setId(id);
            listaCallesBloqueadas.add(calleBloqueada);
            id++;
        }

        sc.close();
    }

    /**
     * Obtiene un pedido a partir de una línea del archivo
     *
     * @param   line          línea del archivo
     * @param   strYearMonth  cadena que contiene el año y mes del archivo
     * @param   formatter     formato para la fecha de la línea
     *
     * @return  el pedido de la línea
     */
    private Pedido getOrderFromLine(String line, String strYearMonth, DateTimeFormatter formatter) {
        final String[] tokens = line.trim().split(",");
        final String[] date = tokens[0].trim().split(":");
        final int day = Integer.parseInt(date[0]);
        final int hour = Integer.parseInt(date[1]);
        final int min = Integer.parseInt(date[2]);
        final int x = Integer.parseInt(tokens[1]);
        final int y = Integer.parseInt(tokens[2]);
        int demand = Integer.parseInt(tokens[3]);
        final int remaining = Integer.parseInt(tokens[4]);

        if(demand>=25){
            demand = 20;
        }

        String strDate = strYearMonth + "-" + day + " " + hour + ":" + min + ":0";
        LocalDateTime orderDate = LocalDateTime.parse(strDate, formatter);
        LocalDateTime limitDate = orderDate.plus(Duration.of(remaining, ChronoUnit.HOURS));

        int minutosFaltantes =  (int) ChronoUnit.MINUTES.between(LocalDateTime.parse("2021-01-01 00:00:00", formatter), limitDate);

        Pedido pedido = new Pedido(0, x, y, demand, minutosFaltantes, orderDate, limitDate, 0);

        demandaTotal += demand;

        return pedido;
    }

    /**
     * Obtiene una calle bloqueada a partir de una línea del archivo
     *
     * @param   line          línea del archivo
     * @param   strYearMonth  cadena que contiene el año y mes del archivo
     * @param   formatter     formato para la fecha de la línea
     *
     * @return  la calle bloqueada de la línea
     */
    private CallesBloqueadas getClosedRoadFromLine(String line, String strYearMonth, DateTimeFormatter formatter) {
        final String[] tokens = line.trim().split(",");
        final String[] plazo = tokens[0].trim().split("-");
        final String[] inicio = plazo[0].trim().split(":");
        final String[] fin = plazo[1].trim().split(":");
        final int diaIni = Integer.parseInt(inicio[0]);
        final int diaFin = Integer.parseInt(fin[0]);
        final int horaIni = Integer.parseInt(inicio[1]);
        final int horaFin = Integer.parseInt(fin[1]);
        final int minIni = Integer.parseInt(inicio[2]);
        final int minFin = Integer.parseInt(fin[2]);
        String strDateIni = strYearMonth + "-" + diaIni + " " + horaIni + ":" + minIni + ":0";
        String strDateFin = strYearMonth + "-" + diaFin + " " + horaFin + ":" + minFin + ":0";
        LocalDateTime dateIni = LocalDateTime.parse(strDateIni, formatter);
        LocalDateTime dateFin = LocalDateTime.parse(strDateFin, formatter);

        final int len = tokens.length - 1;
        final String[] strCoords = Arrays.copyOfRange(tokens, 1, len + 1);
        final int[] coords = new int[len];

        for (int i = 0; i < len; i++) {
            coords[i] = Integer.parseInt(strCoords[i]); // pasando a enteros
        }

        CallesBloqueadas calleBloqueada = new CallesBloqueadas(0, Helper.convertLocalDateTimeToMinutes(dateIni),
                Helper.convertLocalDateTimeToMinutes(dateFin));

        // Agregando el identificador del nodo a la calle bloqueada

        for (int i = 0; i < len - 2; i += 2) {
            int x = coords[i];
            int y = coords[i + 1];

            int x2 = coords[i + 2];
            int y2 = coords[i + 3];

            if (y2 - y == 0) {
                for (int j = x; j <= x2; j++) {
                    calleBloqueada.addNode(j + 71 * y + 1);
                }
            } else {
                if (x2 - x == 0) {
                    for (int k = y; k <= y2; k++) {
                        calleBloqueada.addNode(x + 71 * k + 1);
                    }
                }
            }
        }

        return calleBloqueada;
    }

    // Funciones para simulación
    public void inicializar() {
        try {
            archivo = new FileWriter("firestore.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        listaRutasEnRecorrido = new ArrayList<>();
        listaPedidosEnCola = new ArrayList<>();
        listaPedidosEnRuta = new ArrayList<>();
        vehiculosDisponiblesTipo1 = 2;
        vehiculosDisponiblesTipo2 = 4;
        vehiculosDisponiblesTipo3 = 4;
        vehiculosDisponiblesTipo4 = 10;

        listaVehiculoTipo1 = new ArrayList<>();
        listaVehiculoTipo2 = new ArrayList<>();
        listaVehiculoTipo3 = new ArrayList<>();
        listaVehiculoTipo4 = new ArrayList<>();

        for (int i = 0; i < vehiculosDisponiblesTipo4; i++) {
            Vehicle vehiculo = new Vehicle();
            TipoVehiculo tipo = new TipoVehiculo();
            vehiculo.setPlaca("D"+Integer.toString(i));
            tipo.setIdTipo(4);
            tipo.setCapacidad(5.0);
            tipo.setPesoBruto(1.0);
            tipo.setVelocidad(50.0);
            tipo.setNombre("Tipo D");
            vehiculo.setTipo(tipo);
            vehiculo.setEstado(0);
            vehiculo.setIdVehiculo(i);
            listaVehiculoTipo4.add(vehiculo);
        }

        for (int i = 0; i < vehiculosDisponiblesTipo3; i++) {
            Vehicle vehiculo = new Vehicle();
            TipoVehiculo tipo = new TipoVehiculo();
            vehiculo.setPlaca("C"+Integer.toString(i));
            tipo.setIdTipo(3);
            tipo.setCapacidad(10.0);
            tipo.setPesoBruto(1.5);
            tipo.setVelocidad(50.0);
            tipo.setNombre("Tipo C");
            vehiculo.setTipo(tipo);
            vehiculo.setEstado(0);
            vehiculo.setIdVehiculo(i);
            listaVehiculoTipo3.add(vehiculo);
        }

        for (int i = 0; i < vehiculosDisponiblesTipo2; i++) {
            Vehicle vehiculo = new Vehicle();
            TipoVehiculo tipo = new TipoVehiculo();
            vehiculo.setPlaca("B"+Integer.toString(i));
            tipo.setIdTipo(2);
            tipo.setCapacidad(15.0);
            tipo.setPesoBruto(2.0);
            tipo.setVelocidad(50.0);
            tipo.setNombre("Tipo B");
            vehiculo.setTipo(tipo);
            vehiculo.setEstado(0);
            vehiculo.setIdVehiculo(i);
            listaVehiculoTipo2.add(vehiculo);
        }

        for (int i = 0; i < vehiculosDisponiblesTipo1; i++) {
            Vehicle vehiculo = new Vehicle();
            TipoVehiculo tipo = new TipoVehiculo();
            vehiculo.setPlaca("A"+Integer.toString(i));
            tipo.setIdTipo(1);
            tipo.setCapacidad(25.0);
            tipo.setPesoBruto(2.5);
            tipo.setVelocidad(50.0);
            tipo.setNombre("Tipo A");
            vehiculo.setTipo(tipo);
            vehiculo.setEstado(0);
            vehiculo.setIdVehiculo(i);
            listaVehiculoTipo1.add(vehiculo);
        }
        obtenerListaAdyacente();
        simular();
    }

    public void configurarParametros(int numVehiculoTipo1, int numVehiculoTipo2, int numVehiculoTipo3,
                                     int numVehiculoTipo4, double constPenalidad) {
        vehiculosDisponiblesTipo1 = numVehiculoTipo1;
        vehiculosDisponiblesTipo2 = numVehiculoTipo2;
        vehiculosDisponiblesTipo3 = numVehiculoTipo3;
        vehiculosDisponiblesTipo4 = numVehiculoTipo4;
        constantePenalidad = constPenalidad;
    }

    // SECCION RELACIONADA NETAMENTE CON LA SIMULACION DE ENTREGA DE PEDIDOS
    public void simular(){
        // TODO: enviar data Firestore
        enviarDataFirestore();

        while(true) {
            int caso = obtenerCasoSimulacion();
            if(caso == 0) break;
            if(caso == 1) casoNuevoPedido();
            if(caso == 2) casoEntregaPedido();
            if(caso == 3) casoTerminoRuta();
            cantVehiculoTipo1 = vehiculosDisponiblesTipo1;
            cantVehiculoTipo2 = vehiculosDisponiblesTipo2;
            cantVehiculoTipo3 = vehiculosDisponiblesTipo3;
            cantVehiculoTipo4 = vehiculosDisponiblesTipo4;
        }

        // TODO: enviar data Firestore fin
//        enviarDataFirestoreFin();
    }

    public Integer obtenerCasoSimulacion(){
        int minutosNuevoPedido = Integer.MAX_VALUE;
        int minutosPedidoEntregado = Integer.MAX_VALUE;
        int minutosTerminoRuta = Integer.MAX_VALUE;

        if(listaPedidosTotales.size() != 0) minutosNuevoPedido = getMinutesFromLocalDateTime(listaPedidosTotales.get(0).getFechaPedido());
        if(listaPedidosEnRuta.size() != 0) minutosPedidoEntregado = listaPedidosEnRuta.get(0).getTiempoEntrega();
        if(listaRutasEnRecorrido.size() != 0) minutosTerminoRuta = listaRutasEnRecorrido.get(0).getTiempoFin();

        if(montoPenalidades >= constantePenalidad) {
            return 0;
        }
        if(minutosNuevoPedido == minutosPedidoEntregado && minutosNuevoPedido == minutosTerminoRuta && minutosNuevoPedido == Integer.MAX_VALUE){
            return 0;
        }
        else if(minutosNuevoPedido <= minutosPedidoEntregado && minutosNuevoPedido <= minutosTerminoRuta){
            tiempoEnMinutosActual = minutosNuevoPedido;
            return 1;
        }
        else if(minutosPedidoEntregado <= minutosNuevoPedido && minutosPedidoEntregado <= minutosTerminoRuta){
            tiempoEnMinutosActual = minutosPedidoEntregado;
            return 2;
        }
        else{
            tiempoEnMinutosActual = minutosTerminoRuta;
            return 3;
        }
    }

    public void casoNuevoPedido(){
        //añadir todos los pedidos entrantes a la lista de pedidos en cola
        //TODO CAMBIAR
        for(int i = 0; i< listaPedidosTotales.size(); i++){
            if((int)getMinutesFromLocalDateTime(listaPedidosTotales.get(i).getFechaPedido()) <= (tiempoEnMinutosActual+60*2)){
                listaPedidosEnCola.add(listaPedidosTotales.get(i));
                listaPedidosTotales.remove(i);
            }
            else break;
        }

        //enviar data a firestore
        try {
            archivo.write("Se agregó un nuevo pedido\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        enviarDataFirestore();

        //vemos si hay vehiculos disponibles
        if((vehiculosDisponiblesTipo1+vehiculosDisponiblesTipo2+vehiculosDisponiblesTipo3+vehiculosDisponiblesTipo4) > 0){
            ejecutarAlgoritmo();
            try {
                archivo.write("Se asignaron rutas\n");
            } catch (IOException e) {
                e.printStackTrace();
            }

            enviarDataFirestore();
        }
    }

    public void casoEntregaPedido(){
        for(int i=0; i<listaPedidosEnRuta.size(); i++){
            Pedido pedido = listaPedidosEnRuta.get(0);
            if(pedido.getTiempoEntrega() == tiempoEnMinutosActual){
                numPedidoEntregados++;
                if(getMinutesFromLocalDateTime(pedido.getFechaLimite()) < pedido.getTiempoEntrega()){
                    //TODO Enviar info de colapso logistico
                    listaPedidosSinCumplir.add(pedido);
                }
                listaPedidosEnRuta.remove(0);
            }
            else break;
        }
        //enviar data a firestore
        try {
            archivo.write("Se entregó un pedido\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        enviarDataFirestore();
    }

    public void casoTerminoRuta(){
        //TODO PARADA PARA RECOGER INFO
        if(listaRutasEnRecorrido.get(0).tiempoFin.equals(tiempoEnMinutosActual)) collect = false;

        while (!collect);

        for(int i=0; i<listaRutasEnRecorrido.size(); i++){
            RutaFront rutaFront = listaRutasEnRecorrido.get(0);
            if(rutaFront.tiempoFin.equals(tiempoEnMinutosActual)){
                try {
                    archivo.write("Tipo vehiculo que retorna: " + rutaFront.vehiculo.getTipo().getIdTipo() + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(rutaFront.getVehiculo().getTipo().getIdTipo() == 1){
                    try {
                        listaVehiculoTipo1.get(rutaFront.vehiculo.getIdVehiculo()).setEstado(0);
                        archivo.write("Terminó un vehiculo tipo 1\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    vehiculosDisponiblesTipo1++;
//                    costoMantenimiento += Configuraciones.costoKmAuto * rutaFront.recorridoEnKm;
                }
                else if (rutaFront.getVehiculo().getTipo().getIdTipo() == 2){
                    try {
                        listaVehiculoTipo2.get(rutaFront.vehiculo.getIdVehiculo()).setEstado(0);
                        archivo.write("Terminó un vehiculo tipo 2\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    vehiculosDisponiblesTipo2++;
//                    costoMantenimiento += Configuraciones.costoKmAuto * rutaFront.recorridoEnKm;
                }
                else if (rutaFront.getVehiculo().getTipo().getIdTipo() == 3){
                    try {
                        listaVehiculoTipo3.get(rutaFront.vehiculo.getIdVehiculo()).setEstado(0);
                        archivo.write("Terminó un vehiculo tipo 3\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    vehiculosDisponiblesTipo3++;
//                    costoMantenimiento += Configuraciones.costoKmAuto * rutaFront.recorridoEnKm;
                }
                else{
                    try {
                        listaVehiculoTipo4.get(rutaFront.vehiculo.getIdVehiculo()).setEstado(0);
                        archivo.write("Terminó un vehiculo tipo 4\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    vehiculosDisponiblesTipo4++;
                }

                listaRutasEnRecorrido.remove(0);
            }
            else break;
        }

        //enviar data a firestore
        try {
            archivo.write("Se terminó una ruta\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        enviarDataFirestore();

        //vemos si hay vehiculos disponibles
        if(listaPedidosEnCola.size() > 0){
            ejecutarAlgoritmo();

            //enviar data a firestore
            try {
                archivo.write("Se asignaron rutas\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
            enviarDataFirestore();
        }
    }

    public Integer getMinutesFromLocalDateTime(LocalDateTime ldt){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d H:m:s");
        LocalDateTime tiempoInicio = LocalDateTime.parse("2021-1-1 0:0:0", formatter);
        return Math.toIntExact(ChronoUnit.MINUTES.between(tiempoInicio, ldt));
    }

    public void enviarDataFirestore(){

//        try {
//            archivo.write("-----------------------------------------" + "\n");
//            archivo.write("tiempo:                       " + tiempoEnMinutosActual + "\n");
//            archivo.write("vehiculosDisponiblesTipo1:    " + vehiculosDisponiblesTipo1 + "\n");
//            archivo.write("vehiculosDisponiblesTipo2:    " + vehiculosDisponiblesTipo2 + "\n");
//            archivo.write("vehiculosDisponiblesTipo3:    " + vehiculosDisponiblesTipo3 + "\n");
//            archivo.write("vehiculosDisponiblesTipo4:    " + vehiculosDisponiblesTipo4 + "\n");
//            archivo.write("NumPedidosCola:               " + listaPedidosEnCola.size() + "\n");
//            archivo.write("NumPedidosFaltantes:          " + listaPedidosTotales.size() + "\n");
//            archivo.write("NumPedidosEntregados:         " + numPedidoEntregados + "\n");
//            archivo.write("ganancia:                     " + ganancia + "\n");
//            archivo.write("numPenalidades:               " + numPenalidades + "\n");
//            archivo.write("montoPenalidades:             " + montoPenalidades + "\n");
//            archivo.write("costoMantenimiento:           " + costoMantenimiento + "\n");
//            archivo.write("-----------------------------------------" + "\n");
//        } catch (IOException e) {
//            System.out.println("An error occurred.");
//            e.printStackTrace();
//        }
        System.out.println("-----------------------------------------");
        System.out.println("tiempo:                       " + tiempoEnMinutosActual);
        System.out.println("vehiculosDisponiblesTipo1:    " + vehiculosDisponiblesTipo1);
        System.out.println("vehiculosDisponiblesTipo2:    " + vehiculosDisponiblesTipo2);
        System.out.println("vehiculosDisponiblesTipo3:    " + vehiculosDisponiblesTipo3);
        System.out.println("vehiculosDisponiblesTipo4:    " + vehiculosDisponiblesTipo4);
        System.out.println("NumPedidosCola:               " + listaPedidosEnCola.size());
        System.out.println("NumPedidosFaltantes:          " + listaPedidosTotales.size());
        System.out.println("NumPedidosEntregados:         " + numPedidoEntregados);
//        System.out.println("ganancia:                     " + ganancia);
//        System.out.println("numPenalidades:               " + numPenalidades);
//        System.out.println("montoPenalidades:             " + montoPenalidades);
//        System.out.println("costoMantenimiento:           " + costoMantenimiento);
        System.out.println("-----------------------------------------");

//        Map<String, Object> respuesta = new HashMap<>();
//        respuesta.put("vehiculosDisponiblesTipo1", vehiculosDisponiblesTipo1);
//        respuesta.put("vehiculosDisponiblesTipo2", vehiculosDisponiblesTipo2);
//        respuesta.put("vehiculosDisponiblesTipo3", vehiculosDisponiblesTipo3);
//        respuesta.put("vehiculosDisponiblesTipo4", vehiculosDisponiblesTipo4);
//        respuesta.put("NumPedidosCola", listaPedidosEnCola.size());
//        respuesta.put("NumPedidosFaltantes", listaPedidosTotales.size());
//        respuesta.put("NumPedidosEntregados", numPedidoEntregados);
//        respuesta.put("ganancia", ganancia);
//        respuesta.put("numPenalidades", numPenalidades);
//        respuesta.put("montoPenalidades", montoPenalidades);
//        respuesta.put("costoMantenimiento", costoMantenimiento);
//        respuesta.put("tiempo", tiempoEnMinutosActual);

//        CollectionReference respuestas = firebase.getFirestore().collection("datosgenerales");
//        ApiFuture<WriteResult> writeResultApiFuture = respuestas.document().create(respuesta);
//        try {
//            writeResultApiFuture.get();
//        } catch (InterruptedException | ExecutionException e) {
//            e.printStackTrace();
//        }
    }

//    private void enviarDataFirestoreFin(){
//        Map<String, Object> respuesta = new HashMap<>();
//        respuesta.put("vehiculosDisponiblesTipo1", vehiculosDisponiblesTipo1);
//        respuesta.put("vehiculosDisponiblesTipo2", vehiculosDisponiblesTipo2);
//        respuesta.put("vehiculosDisponiblesTipo3", vehiculosDisponiblesTipo3);
//        respuesta.put("vehiculosDisponiblesTipo4", vehiculosDisponiblesTipo4);
//        respuesta.put("NumPedidosCola", listaPedidosEnCola.size());
//        respuesta.put("NumPedidosFaltantes", listaPedidosTotales.size());
//        respuesta.put("NumPedidosEntregados", numPedidoEntregados);
//        respuesta.put("ganancia", ganancia);
//        respuesta.put("numPenalidades", numPenalidades);
//        respuesta.put("montoPenalidades", montoPenalidades);
//        respuesta.put("costoMantenimiento", costoMantenimiento);
//        respuesta.put("tiempo", 1000000);
//        CollectionReference respuestas = firebase.getFirestore().collection("datosgenerales");
//        ApiFuture<WriteResult> writeResultApiFuture = respuestas.document().create(respuesta);
//        try {
//            writeResultApiFuture.get();
//        } catch (InterruptedException | ExecutionException e) {
//            e.printStackTrace();
//        }
// }



    public void obtenerListaAdyacente(){
        int origen, destino;
        InputStream grafo = getClass().getClassLoader().getResourceAsStream("grafo.txt");
        Scanner sc = new Scanner( grafo );
        dijkstraAlgorithm = new Dijkstra(Configuraciones.V, listaCallesBloqueadas);
        for( int i = 0 ; i < Configuraciones.E ; ++i ){
            origen = sc.nextInt() +1;
            destino = sc.nextInt() +1;
            dijkstraAlgorithm.addEdge(origen, destino);
        }
    }

    public void ejecutarAlgoritmo(){
        obtenerCantidadClusters();
        kmeans = new Kmeans(cantVehiculoTipo1, cantVehiculoTipo2, cantVehiculoTipo3, cantVehiculoTipo4);
        obtenerPedidosClusterizados();
        obtenerRutas();
        asignarRutas();
    }

    public void obtenerCantidadClusters(){

        cantVehiculoTipo1 = vehiculosDisponiblesTipo1;
        cantVehiculoTipo2 = vehiculosDisponiblesTipo2;
        cantVehiculoTipo3 = vehiculosDisponiblesTipo3;
        cantVehiculoTipo4 = vehiculosDisponiblesTipo4;

        int k = (int) (0.9 * (demandaTotal / (cantVehiculoTipo1 * 2.5 + cantVehiculoTipo2 * 2.0 +
                cantVehiculoTipo3 * 1.5 + cantVehiculoTipo4 * 1.0)));

        if(k > 10) k = 10;
        if(k < 3) k = 3;

        cantClusterVehiculoTipo1= cantVehiculoTipo1*k;
        cantClusterVehiculoTipo2= cantVehiculoTipo2*k;
        cantClusterVehiculoTipo3= cantVehiculoTipo3*k;
        cantClusterVehiculoTipo4= cantVehiculoTipo4*k;

    }

    public void obtenerPedidosClusterizados(){
        int cantClusters = cantClusterVehiculoTipo1+cantClusterVehiculoTipo2+cantClusterVehiculoTipo3+cantClusterVehiculoTipo4;
        List<Vehicle> vehiculos = inicializarVehiculos();

        List<Cluster> clustersList = inicializarClusters(vehiculos);
        List<Cluster> clustersAns = inicializarClusters(vehiculos);

        clusterResult = kmeans.kmeans(listaPedidosEnCola,clustersList,cantClusters,clustersAns);
        kmeans.getOptimo(listaPedidosEnCola,clustersAns,cantClusters);
    }

    public List<Vehicle> inicializarVehiculos() {
        List<Vehicle> lista = new ArrayList<>();

        for (int i = 0; i < cantClusterVehiculoTipo4; i++) {
            Vehicle vehiculo = new Vehicle();
            TipoVehiculo tipo = new TipoVehiculo();
            tipo.setIdTipo(4);
            tipo.setCapacidad(5.0);
            tipo.setPesoBruto(1.0);
            tipo.setVelocidad(50.0);
            tipo.setNombre("Tipo D");
            vehiculo.setTipo(tipo);
            lista.add(vehiculo);
        }

        for (int i = 0; i < cantClusterVehiculoTipo3; i++) {
            Vehicle vehiculo = new Vehicle();
            TipoVehiculo tipo = new TipoVehiculo();
            tipo.setIdTipo(3);
            tipo.setCapacidad(10.0);
            tipo.setPesoBruto(1.5);
            tipo.setVelocidad(50.0);
            tipo.setNombre("Tipo C");
            vehiculo.setTipo(tipo);
            lista.add(vehiculo);
        }

        for (int i = 0; i < cantClusterVehiculoTipo2; i++) {
            Vehicle vehiculo = new Vehicle();
            TipoVehiculo tipo = new TipoVehiculo();
            tipo.setIdTipo(2);
            tipo.setCapacidad(15.0);
            tipo.setPesoBruto(2.0);
            tipo.setVelocidad(50.0);
            tipo.setNombre("Tipo B");
            vehiculo.setTipo(tipo);
            lista.add(vehiculo);
        }

        for (int i = 0; i < cantClusterVehiculoTipo1; i++) {
            Vehicle vehiculo = new Vehicle();
            TipoVehiculo tipo = new TipoVehiculo();
            tipo.setIdTipo(1);
            tipo.setCapacidad(25.0);
            tipo.setPesoBruto(2.5);
            tipo.setVelocidad(50.0);
            tipo.setNombre("Tipo A");
            vehiculo.setTipo(tipo);
            lista.add(vehiculo);
        }
        return lista;
    }

    public List<Cluster> inicializarClusters(List<Vehicle> vehiculos) {
        List<Cluster> lista = new ArrayList<Cluster>();
        for (Vehicle vehiculo : vehiculos) {
            Cluster cluster = new Cluster();
            cluster.pedidos = new PriorityQueue<Pedido>(500, new Comparator<Pedido>() {
                public int compare(Pedido i, Pedido j) {
                    if (Math.abs(i.getX() - Configuraciones.almacenX) + Math.abs(i.getY() - Configuraciones.almacenY) > Math
                            .abs(j.getX() - Configuraciones.almacenX) + Math.abs(j.getY() - Configuraciones.almacenY))
                        return 1;
                    else if (Math.abs(i.getX() - Configuraciones.almacenX) + Math.abs(i.getY() - Configuraciones.almacenY) < Math
                            .abs(j.getX() - Configuraciones.almacenX) + Math.abs(j.getY() - Configuraciones.almacenY))
                        return -1;
                    else if (i.getCantidad() > j.getCantidad())
                        return 1;
                    else if (i.getCantidad() < j.getCantidad())
                        return -1;
                    else
                        return 1;
                }
            });
            cluster.centroideX = 0;
            cluster.centroideY = 0;
            cluster.vehiculo = vehiculo;
            lista.add(cluster);
        }
        return lista;
    }


    public void obtenerRutas(){

        int tiempoMinutosInicio = tiempoEnMinutosActual;

        //para calcular el tiempo máximo de entrega
        int maximoTiempo = -1;

        //inicializamos la lista de rutas
        listaRutas = new ArrayList< Ruta >();

        for(Cluster cluster:clusterResult){
            //asignamos el tiempo en minutos en que iniciamos a correr el algoritmo
            int tiempoMinutos = tiempoMinutosInicio;
            if(cluster.firstPedido == null) continue;
            //imprimos en forma de reporte la información relacionada a la ruta

            //incializamos la ruta
            Ruta ruta = new Ruta(cluster.vehiculo, cluster.capacidad);

            //seteamos el origen a nuestro almacén
            int origen = Configuraciones.almacen;

            //nos servirá para hallar un ruta si estamos en un nodo bloqueado
            int ultimoViable = Configuraciones.almacen;

            //para el firstPedido
            if(cluster.firstPedido != null){
                Pedido pedido = cluster.firstPedido;
                ruta.addPedido(pedido);

                double pesoCarga = 0;
                for(Pedido pedidoUnitario:cluster.pedidos){
                    pesoCarga+=pedidoUnitario.getCantidad();
                }

                boolean estaBloqueada = Helper.isBlocked(tiempoMinutos, origen, listaCallesBloqueadas);

                if(estaBloqueada){
                    origen = ultimoViable;
                    // if(pedido.id == 271) System.out.println("Sí está bloqueada");
                }

                dijkstraAlgorithm.run( origen, tiempoMinutos, (int) Math.round(cluster.vehiculo.getTipo().getVelocidad()));

                int tamanoIni = ruta.recorrido.size();

                dijkstraAlgorithm.addNodesToPath(pedido.getNodoId(), ruta, 1);

                int tamanoFin = ruta.recorrido.size();

                if(tamanoFin - tamanoIni >= 2) {
                    ultimoViable = ruta.recorrido.get(ruta.recorrido.size() - 2);
                }

                int tiempoEnLlegar = (tamanoFin - tamanoIni-1) * 60 / ((int) Math.round(cluster.vehiculo.getTipo().getVelocidad()));

                tiempoMinutos += tiempoEnLlegar;

                ruta.pedidos.get(ruta.pedidos.size()-1).setTiempoEntrega(tiempoMinutos);
                ruta.pedidos.get(ruta.pedidos.size()-1).setFechaEntrega(LocalDateTime.of(
                        2021, Month.JANUARY, 1, 0, 0).plus(tiempoMinutos,ChronoUnit.MINUTES));
                ruta.pedidos.get(ruta.pedidos.size()-1).setConsumoPetroleo(cluster.vehiculo.getTipo().getPesoBruto()+(pesoCarga)*ruta.recorrido.size()/150);


                origen = pedido.getNodoId();
            }

            //iteramos mientras sacamos pedidos de la cola de prioridad del cluster
            //ordenados por distancia manhattan al almacén
            while(!cluster.pedidos.isEmpty()){

                //extraemos un pedido del cluster
                Pedido pedido = cluster.pedidos.poll();
                ruta.addPedido(pedido);
                //imprimir información del pedido
                double pesoCarga = 0;
                for(Pedido pedidoUnitario:cluster.pedidos){
                    pesoCarga+=pedidoUnitario.getCantidad();
                }

                //verificamos si nos encontramos en un nodo bloqueado
                //esto puede ocurrir ya que hemos entregado un pedido en un nodo bloqueado
                //o si el almancén es un nodo bloqueado
                boolean estaBloqueada = Helper.isBlocked(tiempoMinutos, origen, listaCallesBloqueadas);

                if(estaBloqueada){
                    origen = ultimoViable;
                    ruta.addNodo(origen);
                }

                //corremos el algoritmo de dijkstra
                dijkstraAlgorithm.run( origen, tiempoMinutos, (int) Math.round(cluster.vehiculo.getTipo().getVelocidad()) );

                //tamano antes de la nueva parte de la ruta
                int tamanoIni = ruta.recorrido.size();

                //obtenemos la ruta en un array
                dijkstraAlgorithm.addNodesToPath(pedido.getNodoId(), ruta, 1);

                //tamano luego de la nueva parte de la ruta
                int tamanoFin = ruta.recorrido.size();

                // para obtener el último nodo que no está bloqueado si es que acabamos de entregar un pedido en un nodo bloqueado
                if(tamanoFin - tamanoIni >= 2) {
                    ultimoViable = ruta.recorrido.get(ruta.recorrido.size() - 2);
                }

                //calculamos el tiempo que tomó en llegar
                int tiempoEnLlegar = (tamanoFin - tamanoIni-1) * 60 / ((int) Math.round(cluster.vehiculo.getTipo().getVelocidad()));


                // calculamos el nuevo tiempo en el que nos encontramos
                tiempoMinutos += tiempoEnLlegar;

                ruta.pedidos.get(ruta.pedidos.size()-1).setTiempoEntrega(tiempoMinutos);
                ruta.pedidos.get(ruta.pedidos.size()-1).setFechaEntrega(LocalDateTime.of(
                        2021, Month.JANUARY, 1, 0, 0).plus(tiempoMinutos,ChronoUnit.MINUTES));
                ruta.pedidos.get(ruta.pedidos.size()-1).setConsumoPetroleo(cluster.vehiculo.getTipo().getPesoBruto()+(pesoCarga)*ruta.recorrido.size()/150);


                // System.out.println("Pedido id: " + pedido.id + " " + pedido.x + " " + pedido.y);
                //cambiamos el origen
                origen = pedido.getNodoId();
            }

            //tiempo que tomó realizar la entrega
            int diferenciaTiempo = tiempoMinutos - tiempoMinutosInicio;

            if(diferenciaTiempo > maximoTiempo){
                maximoTiempo = diferenciaTiempo;
            }

            if(cluster.firstPedido != null){
                // System.out.println("Ruta recorrido: " + ruta.recorrido);
                origen = ruta.recorrido.get(ruta.recorrido.size() - 1);
                boolean estaBloqueada = Helper.isBlocked(tiempoMinutos, origen, listaCallesBloqueadas);

                if(estaBloqueada){
                    origen = ultimoViable;
                    ruta.addNodoRetorno(origen);
                }

                dijkstraAlgorithm.run( origen, tiempoMinutos, (int) Math.round(cluster.vehiculo.getTipo().getVelocidad()) );

                int tamanoIni = ruta.retorno.size();

                dijkstraAlgorithm.addNodesToPath(Configuraciones.almacen, ruta, 2);
            }
            listaRutas.add(ruta);
        }
        // System.out.println("Máximo tiempo de entrega: " + maximoTiempo + " minutos");
        // System.out.println("Número de rutas: " + listaRutas.size());
    }

    public void asignarRutaTipo(int idTipo, int cantVehiculos){
        for(int i=0; i<cantVehiculos; i++){
            int minimo = Integer.MAX_VALUE;
            int contador = 0;
            int minCont = -1;
            for(Ruta ruta: listaRutas){
                if(ruta.vehiculo.getTipo().getIdTipo() == idTipo  && minimo > getMinutesFromLocalDateTime(ruta.plazoEntrega)){
                    minimo = getMinutesFromLocalDateTime(ruta.plazoEntrega);
                    minCont = contador;
                }
                contador++;
            }
            if(minCont == -1) break;
            for(Pedido pedido: listaRutas.get(minCont).pedidos){
                listaPedidosEnRuta.add(pedido);
                listaPedidosEnCola.remove(pedido);
            }

            Vehicle vehicleAsignar = new Vehicle();
            switch (idTipo){
                case 1:
                    for (Vehicle vehicle:listaVehiculoTipo1){
                        if(vehicle.getEstado()==0){
                            vehicle.setEstado(1);
                            vehicleAsignar = vehicle;
                            break;
                        }
                    }
                    vehiculosDisponiblesTipo1--;
                    break;
                case 2:
                    for (Vehicle vehicle:listaVehiculoTipo2){
                        if(vehicle.getEstado()==0){
                            vehicle.setEstado(1);
                            vehicleAsignar = vehicle;
                            break;
                        }
                    }
                    vehiculosDisponiblesTipo2--;
                    break;
                case 3:
                    for (Vehicle vehicle:listaVehiculoTipo3){
                        if(vehicle.getEstado()==0){
                            vehicle.setEstado(1);
                            vehicleAsignar = vehicle;
                            break;
                        }
                    }
                    vehiculosDisponiblesTipo3--;
                    break;
                case 4:
                    for (Vehicle vehicle:listaVehiculoTipo4){
                        if(vehicle.getEstado()==0){
                            vehicle.setEstado(1);
                            vehicleAsignar = vehicle;
                            break;
                        }
                    }
                    vehiculosDisponiblesTipo4--;
                    break;
            }



            Ruta ruta = listaRutas.get(minCont);
            vehicleAsignar.setCapacidadActual(ruta.capacidad);
            RutaFront rutaFront = new RutaFront(vehicleAsignar,ruta.capacidad);

            List<Map<String,Integer>> recorridoEnviar = new ArrayList<>();
            List<Map<String,Integer>> retornoEnviar = new ArrayList<>();
            for (int nodoRecorrido:ruta.recorrido){
                int x = (nodoRecorrido - 1) % 71;
                int y = (nodoRecorrido - 1) / 71;
                Map<String ,Integer> map=new HashMap<String,Integer>();
                map.put("x",x);
                map.put("y",y);
                recorridoEnviar.add(map);
            }

            for (int nodoRetorno: ruta.retorno){
                int x = (nodoRetorno - 1) % 71;
                int y = (nodoRetorno - 1) / 71;
                Map<String ,Integer> map=new HashMap<String,Integer>();
                map.put("x",x);
                map.put("y",y);
                retornoEnviar.add(map);
            }

//            (tamanoFin - tamanoIni-1) * 60 / ((int) Math.round(cluster.vehiculo.getTipo().getVelocidad()));
            rutaFront.setTiempoFin((int) (tiempoEnMinutosActual+((ruta.recorrido.size()+ruta.retorno.size()-1)*60/ruta.vehiculo.getTipo().getVelocidad())));
            rutaFront.pedidos.addAll(ruta.pedidos);
            rutaFront.recorrido.addAll(recorridoEnviar);
            rutaFront.retorno.addAll(retornoEnviar);


            listaRutas.remove(ruta);
            listaRutasEnRecorrido.add(rutaFront);

//            switch (idTipo){
//                case 1:
//                    vehiculosDisponiblesTipo1--;
//                    break;
//                case 2:
//                    vehiculosDisponiblesTipo2--;
//                    break;
//                case 3:
//                    vehiculosDisponiblesTipo3--;
//                    break;
//                case 4:
//                    vehiculosDisponiblesTipo4--;
//                    break;
//            }

        }

    }

    public void asignarRutas() {
        asignarRutaTipo(1,cantVehiculoTipo1);
        asignarRutaTipo(2,cantVehiculoTipo2);
        asignarRutaTipo(3,cantVehiculoTipo3);
        asignarRutaTipo(4,cantVehiculoTipo4);

        Collections.sort(listaPedidosEnCola);
        Collections.sort(listaRutasEnRecorrido);
    }

    public void reiniciarSimulacion(){
//        CollectionReference collection = firebase.getFirestore().collection("datosgenerales");
//        firebase.getFirestore().recursiveDelete(collection);
//        firebase.getFirestore().recursiveDelete(collection);

        listaPedidosTotales = new ArrayList<>();
        listaPedidosEnCola = new ArrayList<>();
        listaPedidosSinCumplir= new ArrayList<>();
        listaPedidosEnRuta= new ArrayList<>();
        listaRutasEnRecorrido= new ArrayList<>();
        listaRutas= new ArrayList<>();
        listaCallesBloqueadas= new ArrayList<>();
        listaVehiculoTipo1= new ArrayList<>();
        listaVehiculoTipo2= new ArrayList<>();
        listaVehiculoTipo3= new ArrayList<>();
        listaVehiculoTipo4= new ArrayList<>();
        clusterResult= new ArrayList<>();

        cantClusterVehiculoTipo1 = 0;
        cantClusterVehiculoTipo2 = 0;
        cantClusterVehiculoTipo3 = 0;
        cantClusterVehiculoTipo4 = 0;
        cantVehiculoTipo1 = 0;
        cantVehiculoTipo2 = 0;
        cantVehiculoTipo3 = 0;
        cantVehiculoTipo4 = 0;
        demandaTotal = 0;
        tiempoEnMinutosActual = 0;
        vehiculosDisponiblesTipo1 = 0;
        vehiculosDisponiblesTipo2 = 0;
        vehiculosDisponiblesTipo3 = 0;
        vehiculosDisponiblesTipo4 = 0;
        ganancia = 0.0;
        numPenalidades = 0;
        montoPenalidades = 0.0;
        numPedidoEntregados = 0;
        costoMantenimiento = 0;
        constantePenalidad = 1;
    }
}
