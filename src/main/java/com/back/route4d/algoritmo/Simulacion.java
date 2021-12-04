package com.back.route4d.algoritmo;

import com.back.route4d.algoritmo.dijkstra.Dijkstra;
import com.back.route4d.algoritmo.kmeans.Kmeans;
//import com.google.api.core.ApiFuture;
//import com.google.cloud.firestore.CollectionReference;
//import com.google.cloud.firestore.QueryDocumentSnapshot;
//import com.google.cloud.firestore.QuerySnapshot;
//import com.google.cloud.firestore.WriteResult;
//import com.paqhoy.algoritmoAlgorutas.algoritmo.dijkstra.Dijkstra;
//import com.paqhoy.algoritmoAlgorutas.algoritmo.kmeans.Kmeans;
//import com.paqhoy.algoritmoAlgorutas.firebase.FirebaseInitializer;
//import com.paqhoy.algoritmoAlgorutas.model.*;
import com.back.route4d.helper.Helper;
import com.back.route4d.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@Slf4j
public class Simulacion {

    public List<Pedido> listaPedidosTotales;
    public List<Pedido> listaPedidosEnCola;
    public List<Pedido> listaPedidosEnRuta;
    public List<Cluster> clusterResult;
    public List<CallesBloqueadas> listaCallesBloqueadas;
    public List<Ruta> listaRutas;
    public List<RutaFront> listaRutasEnRecorrido;
    public Dijkstra dijkstraAlgorithm;
    public Kmeans kmeans;

    public Integer cantClusterMotos = 0;
    public Integer cantClusterAutos = 0;
    public Integer cantAutos = 0;
    public Integer cantMotos = 0;

    public Integer demandaTotal = 0;

    public double constantePenalidad = 1;

    public FileWriter archivo;

//    @Autowired
//    private FirebaseInitializer firebase;

    // VARIABLES QUE SE ENVIARAN A FIRESTORE PARA LA SIMULACION

    public Integer tiempoEnMinutosActual = 0;
    public Integer autosDisponibles = 0;
    public Integer motosDisponibles = 0;
    public double ganancia = 0.0;
    public Integer numPenalidades = 0;
    public double montoPenalidades = 0.0;
    public Integer numPedidoEntregados = 0;
    public double costoMantenimiento = 0;

    public List<Pedido> getPedidos(){
        return listaPedidosTotales;
    }

    public List<CallesBloqueadas> getListaCallesBloqueadas(){
        return listaCallesBloqueadas;
    }

    public String subirArchivoPedidos(MultipartFile file){
        try {
            File fileObj = convertMultiPartFileToFile(file);
            getAllPedidos(fileObj);
            fileObj.delete();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-M-d H:m:s");
            return listaPedidosTotales.get(listaPedidosTotales.size()-1).getFechaPedido().format(dtf);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private File convertMultiPartFileToFile(MultipartFile file){
        File convertedFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            log.error("Error converting multipartFile to File", e);
        }
        return convertedFile;
    }

    private void getAllPedidos(File fileObj) throws FileNotFoundException {
        Scanner sc = new Scanner(fileObj);
        String strYearMonth = Helper.getOrdersDateFromName(fileObj.getName());
        int id = 1;
        listaPedidosTotales = new ArrayList<>();
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            Pedido pedido = getPedidoFromLine(line, strYearMonth);
            pedido.setId(id);
            listaPedidosTotales.add(pedido);
            id++;
        }
        Collections.sort(listaPedidosTotales,
                (p1,p2) -> (int)ChronoUnit.MINUTES.between(p2.getFechaPedido(), p1.getFechaPedido()));
        sc.close();
    }

    private Pedido getPedidoFromLine(String line, String strYearMonth){

        int day = getIntFromLine(line,":");
        line = line.substring( line.indexOf(':') + 1 );
        int hour = getIntFromLine(line,":");
        line = line.substring( line.indexOf(':') + 1 );
        int min = getIntFromLine(line, ",");
        line = line.substring( line.indexOf(',') + 1 );
        int x = getIntFromLine(line, ",");
        line = line.substring( line.indexOf(',') + 1 );
        int y = getIntFromLine(line, ",");
        line = line.substring( line.indexOf(',') + 1 );
        int demand = getIntFromLine(line, ",");
        line = line.substring( line.indexOf(',') + 1 );
        int remaining = Integer.parseInt(line);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-d H:m:s");

        String strDate = strYearMonth + "-" + day + " " + hour + ":" + min + ":0";
        LocalDateTime orderDate = LocalDateTime.parse(strDate, formatter);
        LocalDateTime limitDate = orderDate.plus(Duration.of(remaining, ChronoUnit.HOURS));

        int minutosFaltantes =  (int) ChronoUnit.MINUTES.between(LocalDateTime.parse("2021-01-01 00:00:00", formatter), limitDate);

        Pedido pedido = new Pedido(0, x, y, demand, minutosFaltantes, orderDate, limitDate, 0);

        demandaTotal += demand;

        return pedido;
    }

    private CallesBloqueadas getCalleBloqueadaFromLine(String line, String strYearMonth, DateTimeFormatter formatter) {
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

    private Integer getIntFromLine(String line, String c){
        int indexChar = line.indexOf(c);
        return Integer.parseInt( line.substring( 0, indexChar ) );
    }

    public String subirArchivoCallesBloqueadas (MultipartFile file) {
        try {
            File fileObj = convertMultiPartFileToFile(file);
            getCallesBloqueadas(fileObj);
            fileObj.delete();
            return "Done!";
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void getCallesBloqueadas(File fileObj) throws FileNotFoundException {
        Scanner sc = new Scanner(fileObj);
        String strYearMonth = Helper.getLockedNodesDateFromName(fileObj.getName());
        int id = 1;
        listaCallesBloqueadas = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-d H:m:s");

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            CallesBloqueadas calleBloqueada = getCalleBloqueadaFromLine(line, strYearMonth, formatter);
            calleBloqueada.setId(id);
            listaCallesBloqueadas.add(calleBloqueada);
            id++;
        }
        sc.close();
    }

//
//    // SECCION RELACIONADA NETAMENTE A LA INICIALIZACION DE LA SIMULACION
//
//    public void inicializar(SimulacionParametros parametros){
//        try {
//            archivo = new FileWriter("firestore.txt");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        listaRutasEnRecorrido = new ArrayList<>();
//        listaPedidosEnCola = new ArrayList<>();
//        listaPedidosEnRuta = new ArrayList<>();
//        configurarParametros(parametros.autos,parametros.motos,parametros.constantePenalidad);
//        obtenerListaAdyacente();
//        simular();
//    }
//
//    public void configurarParametros(int numeroAutos, int numeroMotos, double constPenalidad){
//        autosDisponibles = numeroAutos;
//        motosDisponibles = numeroMotos;
//        constantePenalidad = constPenalidad;
//    }
//
//    public void obtenerListaAdyacente(){
//        int origen, destino;
//        InputStream grafo = getClass().getClassLoader().getResourceAsStream("grafo.txt");
//        Scanner sc = new Scanner( grafo );
//        dijkstraAlgorithm = new Dijkstra(Configuraciones.V, listaCallesBloqueadas);
//        for( int i = 0 ; i < Configuraciones.E ; ++i ){
//            origen = sc.nextInt() +1;
//            destino = sc.nextInt() +1;
//            dijkstraAlgorithm.addEdge(origen, destino);
//        }
//    }
//
//    // SECCION RELACIONADA NETAMENTE CON LA SIMULACION DE ENTREGA DE PEDIDOS
//
//    public void simular(){
//        enviarDataFirestore();
//        while(true) {
//            int caso = obtenerCasoSimulacion();
//            if(caso == 0) break;
//            if(caso == 1) casoNuevoPedido();
//            if(caso == 2) casoEntregaPedido();
//            if(caso == 3) casoTerminoRuta();
//            cantMotos = motosDisponibles;
//            cantAutos = autosDisponibles;
//        }
//        enviarDataFirestoreFin();
//    }
//
//    public Integer obtenerCasoSimulacion(){
//        int minutosNuevoPedido = Integer.MAX_VALUE;
//        int minutosPedidoEntregado = Integer.MAX_VALUE;
//        int minutosTerminoRuta = Integer.MAX_VALUE;
//
//        if(listaPedidosTotales.size() != 0) minutosNuevoPedido = getMinutesFromLocalDateTime(listaPedidosTotales.get(0).fechaPedido);
//        if(listaPedidosEnRuta.size() != 0) minutosPedidoEntregado = listaPedidosEnRuta.get(0).tiempoMinutosEntrega;
//        if(listaRutasEnRecorrido.size() != 0) minutosTerminoRuta = listaRutasEnRecorrido.get(0).tiempoMinutosFin;
//
//        if(montoPenalidades >= constantePenalidad) {
//            return 0;
//        }
//        if(minutosNuevoPedido == minutosPedidoEntregado && minutosNuevoPedido == minutosTerminoRuta && minutosNuevoPedido == Integer.MAX_VALUE){
//            return 0;
//        }
//        else if(minutosNuevoPedido <= minutosPedidoEntregado && minutosNuevoPedido <= minutosTerminoRuta){
//            tiempoEnMinutosActual = minutosNuevoPedido;
//            return 1;
//        }
//        else if(minutosPedidoEntregado <= minutosNuevoPedido && minutosPedidoEntregado <= minutosTerminoRuta){
//            tiempoEnMinutosActual = minutosPedidoEntregado;
//            return 2;
//        }
//        else{
//            tiempoEnMinutosActual = minutosTerminoRuta;
//            return 3;
//        }
//    }
//
//    public void casoNuevoPedido(){
//        //añadir todos los pedidos entrantes a la lista de pedidos en cola
//        for(int i = 0; i< listaPedidosTotales.size(); i++){
//            if((int)getMinutesFromLocalDateTime(listaPedidosTotales.get(0).fechaPedido) == (tiempoEnMinutosActual)){
//                listaPedidosEnCola.add(listaPedidosTotales.get(0));
//                listaPedidosTotales.remove(0);
//            }
//            else break;
//        }
//
//        //enviar data a firestore
//        try {
//            archivo.write("Se agregó un nuevo pedido\n");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        enviarDataFirestore();
//
//        //vemos si hay vehiculos disponibles
//        if((autosDisponibles + motosDisponibles) > 0){
//            ejecutarAlgoritmo();
//            try {
//                archivo.write("Se asignaron rutas\n");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            enviarDataFirestore();
//        }
//    }
//
//    public void casoEntregaPedido(){
//        for(int i=0; i<listaPedidosEnRuta.size(); i++){
//            SPedido sPedido = listaPedidosEnRuta.get(0);
//            if((int)sPedido.tiempoMinutosEntrega == tiempoEnMinutosActual){
//                numPedidoEntregados++;
//                ganancia += sPedido.cantidad * Configuraciones.precio;
//                if(sPedido.tiempoMinutosLimite < sPedido.tiempoMinutosEntrega){
//                    numPenalidades++;
//                    montoPenalidades += Configuraciones.penalidad *
//                            (int)((sPedido.tiempoMinutosEntrega - sPedido.tiempoMinutosLimite)/60 + 1);
//                }
//                listaPedidosEnRuta.remove(0);
//            }
//            else break;
//        }
//
//        //enviar data a firestore
//        try {
//            archivo.write("Se entregó un pedido\n");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        enviarDataFirestore();
//    }
//
//    public void casoTerminoRuta(){
//        for(int i=0; i<listaRutasEnRecorrido.size(); i++){
//            SRuta sRuta = listaRutasEnRecorrido.get(0);
//            if(sRuta.tiempoMinutosFin.equals(tiempoEnMinutosActual)){
//                try {
//                    archivo.write("Tipo vehiculo que retorna: " + sRuta.tipoVehiculo + "\n");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                if(sRuta.tipoVehiculo == 1){
//                    try {
//                        archivo.write("Terminó un auto\n");
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    autosDisponibles++;
//                    costoMantenimiento += Configuraciones.costoKmAuto * sRuta.recorridoEnKm;
//                }
//                else{
//                    try {
//                        archivo.write("Terminó una moto\n");
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    motosDisponibles++;
//                    costoMantenimiento += Configuraciones.costoKmMoto * sRuta.recorridoEnKm;
//                }
//                listaRutasEnRecorrido.remove(0);
//            }
//            else break;
//        }
//
//        //enviar data a firestore
//        try {
//            archivo.write("Se terminó una ruta\n");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        enviarDataFirestore();
//
//        //vemos si hay vehiculos disponibles
//        if(listaPedidosEnCola.size() > 0){
//            ejecutarAlgoritmo();
//
//            //enviar data a firestore
//            try {
//                archivo.write("Se asignaron rutas\n");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            enviarDataFirestore();
//        }
//    }

    public Integer getMinutesFromLocalDateTime(LocalDateTime ldt){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d H:m:s");
        LocalDateTime tiempoInicio = LocalDateTime.parse("2021-1-1 0:0:0", formatter);
        return Math.toIntExact(ChronoUnit.MINUTES.between(tiempoInicio, ldt));
    }

//    public void enviarDataFirestore(){
//
//        try {
//            archivo.write("-----------------------------------------" + "\n");
//            archivo.write("tiempo:               " + tiempoEnMinutosActual + "\n");
//            archivo.write("autosDisponibles:     " + autosDisponibles + "\n");
//            archivo.write("motosDisponibles:     " + motosDisponibles + "\n");
//            archivo.write("NumPedidosCola:       " + listaPedidosEnCola.size() + "\n");
//            archivo.write("NumPedidosFaltantes:  " + listaPedidosTotales.size() + "\n");
//            archivo.write("NumPedidosEntregados: " + numPedidoEntregados + "\n");
//            archivo.write("ganancia:             " + ganancia + "\n");
//            archivo.write("numPenalidades:       " + numPenalidades + "\n");
//            archivo.write("montoPenalidades:     " + montoPenalidades + "\n");
//            archivo.write("costoMantenimiento:   " + costoMantenimiento + "\n");
//            archivo.write("-----------------------------------------" + "\n");
//        } catch (IOException e) {
//            System.out.println("An error occurred.");
//            e.printStackTrace();
//        }
//        System.out.println("-----------------------------------------");
//        System.out.println("tiempo:               " + tiempoEnMinutosActual);
//        System.out.println("autosDisponibles:     " + autosDisponibles);
//        System.out.println("motosDisponibles:     " + motosDisponibles);
//        System.out.println("NumPedidosCola:       " + listaPedidosEnCola.size());
//        System.out.println("NumPedidosFaltantes:  " + listaPedidosTotales.size());
//        System.out.println("NumPedidosEntregados: " + numPedidoEntregados);
//        System.out.println("ganancia:             " + ganancia);
//        System.out.println("numPenalidades:       " + numPenalidades);
//        System.out.println("montoPenalidades:     " + montoPenalidades);
//        System.out.println("costoMantenimiento:   " + costoMantenimiento);
//        System.out.println("-----------------------------------------");
//
//        Map<String, Object> respuesta = new HashMap<>();
//        respuesta.put("autosDisponibles", autosDisponibles);
//        respuesta.put("motosDisponibles", motosDisponibles);
//        respuesta.put("NumPedidosCola", listaPedidosEnCola.size());
//        respuesta.put("NumPedidosFaltantes", listaPedidosTotales.size());
//        respuesta.put("NumPedidosEntregados", numPedidoEntregados);
//        respuesta.put("ganancia", ganancia);
//        respuesta.put("numPenalidades", numPenalidades);
//        respuesta.put("montoPenalidades", montoPenalidades);
//        respuesta.put("costoMantenimiento", costoMantenimiento);
//        respuesta.put("tiempo", tiempoEnMinutosActual);
//
//        CollectionReference respuestas = firebase.getFirestore().collection("datosgenerales");
//        ApiFuture<WriteResult> writeResultApiFuture = respuestas.document().create(respuesta);
//        try {
//            writeResultApiFuture.get();
//        } catch (InterruptedException | ExecutionException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void enviarDataFirestoreFin(){
//        Map<String, Object> respuesta = new HashMap<>();
//        respuesta.put("autosDisponibles", autosDisponibles);
//        respuesta.put("motosDisponibles", motosDisponibles);
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
//    }
//
//    // SECCION RELACIONADA NETAMENTE AL ALGORITMO
//
//    public void ejecutarAlgoritmo(){
//        obtenerCantidadClusters();
//        kmeans = new Kmeans(cantMotos, cantAutos);
//        obtenerPedidosClusterizados();
//        obtenerRutas();
//        asignarRutas();
//    }
//
//    public void obtenerCantidadClusters(){
//        cantMotos = motosDisponibles;   //5
//        cantAutos = autosDisponibles;   //5
//        //0.9 * (10/5*4+5*25)
//        int k = (int) (0.9 * (cantidadProductos / ( cantMotos * 4 + cantAutos * 25 )));
//        if(k > 10) k = 10;
//        if(k < 3) k = 3;
//        // 15  15
//        cantClusterMotos = cantMotos * k;
//        cantClusterAutos = cantAutos * k;
//    }
//
//    public void obtenerPedidosClusterizados(){
//        int cantClusters = cantClusterMotos + cantClusterAutos;
//        List<AVehiculo> vehiculos = inicializarVehiculos();
//
//        //inicializar clusters
//        List<Cluster> clustersList = inicializarClusters(vehiculos);
//        List<Cluster> clustersAns = inicializarClusters(vehiculos);
//
//        //Clusterizacion
//        clusterResult = kmeans.kmeans(listaPedidosEnCola,clustersList,cantClusters,clustersAns);
//        Double SSE = kmeans.getOptimo(listaPedidosEnCola,clustersAns,cantClusters);
//    }
//
//    public List<AVehiculo> inicializarVehiculos() {
//        List<AVehiculo> lista = new ArrayList<>();
//        for(int i=0; i<cantClusterMotos;i++){
//            AVehiculo vehiculo = new AVehiculo();
//            vehiculo.setCapacidad(4);
//            vehiculo.setCosto_km(3.0);
//            vehiculo.setVelocidad(60.00);
//            vehiculo.setTipo_id(2);
//            lista.add(vehiculo);
//        }
//        for(int i=0; i<cantClusterAutos;i++){
//            AVehiculo vehiculo = new AVehiculo();
//            vehiculo.setCapacidad(25);
//            vehiculo.setCosto_km(5.0);
//            vehiculo.setVelocidad(30.00);
//            vehiculo.setTipo_id(1);
//            lista.add(vehiculo);
//        }
//        return lista;
//    }
//
//    public List<Cluster> inicializarClusters(List<AVehiculo>  vehiculos){
//        List<Cluster> lista = new ArrayList<Cluster>();
//        for(AVehiculo vehiculo: vehiculos){
//            Cluster cluster =  new Cluster();
//            cluster.pedidos = new PriorityQueue<APedido>(500,
//                    new Comparator<APedido>(){
//                        //override compare method
//                        public int compare(APedido i, APedido j){
//                            if(Math.abs(i.x - Configuraciones.almacenX) + Math.abs(i.y - Configuraciones.almacenY) > Math.abs(j.x - Configuraciones.almacenX) + Math.abs(j.y - Configuraciones.almacenY)) return 1;
//                            else if (Math.abs(i.x - Configuraciones.almacenX) + Math.abs(i.y - Configuraciones.almacenY) < Math.abs(j.x - Configuraciones.almacenX) + Math.abs(j.y - Configuraciones.almacenY)) return -1;
//                            else if (i.cantidad > j.cantidad) return 1;
//                            else if (i.cantidad < j.cantidad) return -1;
//                            else return 1;
//                        }
//                    }
//            );
//            cluster.centroideX = 0;
//            cluster.centroideY = 0;
//            cluster.vehiculo = vehiculo;
//            lista.add(cluster);
//        }
//        return lista;
//    }
//
//    public void obtenerRutas(){
//
//        int tiempoMinutosInicio = tiempoEnMinutosActual;
//
//        //para calcular el tiempo máximo de entrega
//        int maximoTiempo = -1;
//
//        //inicializamos la lista de rutas
//        listaRutas = new ArrayList< Ruta >();
//
//        for(Cluster cluster:clusterResult){
//            //asignamos el tiempo en minutos en que iniciamos a correr el algoritmo
//            int tiempoMinutos = tiempoMinutosInicio;
//            if(cluster.firstPedido == null) continue;
//            //imprimos en forma de reporte la información relacionada a la ruta
//
//            //incializamos la ruta
//            Ruta ruta = new Ruta(cluster.vehiculo, cluster.capacidad);
//
//            //seteamos el origen a nuestro almacén
//            int origen = Configuraciones.almacen;
//
//            //nos servirá para hallar un ruta si estamos en un nodo bloqueado
//            int ultimoViable = Configuraciones.almacen;
//
//            //para el firstPedido
//            if(cluster.firstPedido != null){
//                APedido pedido = cluster.firstPedido;
//                ruta.addPedido(pedido);
//
//                boolean estaBloqueada = estaBloqueada(tiempoMinutos, origen);
//
//                if(estaBloqueada){
//                    origen = ultimoViable;
//                    // if(pedido.id == 271) System.out.println("Sí está bloqueada");
//                }
//
//                dijkstraAlgorithm.dijkstra( origen, tiempoMinutos, (int) Math.round(cluster.vehiculo.getVelocidad()));
//
//                int tamanoIni = ruta.recorrido.size();
//
//                dijkstraAlgorithm.printShortestPath(pedido.getNodoId(), ruta, 1);
//
//                int tamanoFin = ruta.recorrido.size();
//
//                if(tamanoFin - tamanoIni >= 2) {
//                    ultimoViable = ruta.recorrido.get(ruta.recorrido.size() - 2);
//                }
//
//                int tiempoEnLlegar = (tamanoFin - tamanoIni-1) * 60 / ((int) Math.round(cluster.vehiculo.getVelocidad()));
//
//                tiempoMinutos += tiempoEnLlegar;
//
//                ruta.pedidos.get(ruta.pedidos.size()-1).tiempoEntregaRealizada = tiempoMinutos;
//                // System.out.println("Pedido id: " + pedido.id + " " + pedido.x + " " + pedido.y);
//                origen = pedido.getNodoId();
//            }
//
//            //iteramos mientras sacamos pedidos de la cola de prioridad del cluster
//            //ordenados por distancia manhattan al almacén
//            while(!cluster.pedidos.isEmpty()){
//
//                //extraemos un pedido del cluster
//                APedido pedido = cluster.pedidos.poll();
//                ruta.addPedido(pedido);
//                //imprimir información del pedido
//
//                //verificamos si nos encontramos en un nodo bloqueado
//                //esto puede ocurrir ya que hemos entregado un pedido en un nodo bloqueado
//                //o si el almancén es un nodo bloqueado
//                boolean estaBloqueada = estaBloqueada(tiempoMinutos, origen);
//
//                if(estaBloqueada){
//                    origen = ultimoViable;
//                    ruta.addNodo(origen);
//                }
//
//                //corremos el algoritmo de dijkstra
//                dijkstraAlgorithm.dijkstra( origen, tiempoMinutos, (int) Math.round(cluster.vehiculo.getVelocidad()) );
//
//                //tamano antes de la nueva parte de la ruta
//                int tamanoIni = ruta.recorrido.size();
//
//                //obtenemos la ruta en un array
//                dijkstraAlgorithm.printShortestPath(pedido.getNodoId(), ruta, 1);
//
//                //tamano luego de la nueva parte de la ruta
//                int tamanoFin = ruta.recorrido.size();
//
//                // para obtener el último nodo que no está bloqueado si es que acabamos de entregar un pedido en un nodo bloqueado
//                if(tamanoFin - tamanoIni >= 2) {
//                    ultimoViable = ruta.recorrido.get(ruta.recorrido.size() - 2);
//                }
//
//                //calculamos el tiempo que tomó en llegar
//                int tiempoEnLlegar = (tamanoFin - tamanoIni-1) * 60 / ((int) Math.round(cluster.vehiculo.getVelocidad()));
//
//
//                // calculamos el nuevo tiempo en el que nos encontramos
//                tiempoMinutos += tiempoEnLlegar;
//
//                ruta.pedidos.get(ruta.pedidos.size()-1).tiempoEntregaRealizada = tiempoMinutos;
//                // System.out.println("Pedido id: " + pedido.id + " " + pedido.x + " " + pedido.y);
//                //cambiamos el origen
//                origen = pedido.getNodoId();
//            }
//
//            //tiempo que tomó realizar la entrega
//            int diferenciaTiempo = tiempoMinutos - tiempoMinutosInicio;
//
//            if(diferenciaTiempo > maximoTiempo){
//                maximoTiempo = diferenciaTiempo;
//            }
//
//            if(cluster.firstPedido != null){
//                // System.out.println("Ruta recorrido: " + ruta.recorrido);
//                origen = ruta.recorrido.get(ruta.recorrido.size() - 1);
//                boolean estaBloqueada = estaBloqueada(tiempoMinutos, origen);
//
//                if(estaBloqueada){
//                    origen = ultimoViable;
//                    ruta.addNodoRetorno(origen);
//                }
//
//                dijkstraAlgorithm.dijkstra( origen, tiempoMinutos, (int) Math.round(cluster.vehiculo.getVelocidad()) );
//
//                int tamanoIni = ruta.retorno.size();
//
//                dijkstraAlgorithm.printShortestPath(Configuraciones.almacen, ruta, 2);
//            }
//            listaRutas.add(ruta);
//        }
//        // System.out.println("Máximo tiempo de entrega: " + maximoTiempo + " minutos");
//        // System.out.println("Número de rutas: " + listaRutas.size());
//    }
//
//    public void asignarRutas(){
//        log.info("Asignar rutas: ");
//        log.info("cantAutos: " + cantAutos);
//        log.info("cantMotos: " + cantMotos);
//        for(int i=0; i<cantAutos; i++){
//            int minimo = Integer.MAX_VALUE;
//            int contador = 0;
//            int minCont = -1;
//            for(Ruta ruta: listaRutas){
//                if(ruta.vehiculo.getTipo_id() == 1 && ruta.chofer == null && minimo > ruta.tiempoMin){
//                    minimo = ruta.tiempoMin;
//                    minCont = contador;
//                }
//                contador++;
//            }
//            if(minCont == -1) break;
//            for(APedido pedido: listaRutas.get(minCont).pedidos){
//                SPedido sPedido = new SPedido();
//                sPedido.id = pedido.id;
//                sPedido.tiempoMinutosEntrega = pedido.tiempoEntregaRealizada;
//                sPedido.tiempoMinutosLimite = getMinutesFromLocalDateTime(pedido.fechaLimite);
//                sPedido.cantidad = pedido.cantidad;
//                listaPedidosEnRuta.add(sPedido);
//                listaPedidosEnCola.remove(pedido);
//            }
//            Ruta ruta = listaRutas.get(minCont);
//            ruta.chofer = new Usuario();
//            SRuta sRuta = new SRuta();
//            sRuta.tipoVehiculo = 1;
//            sRuta.recorridoEnKm = ruta.recorrido.size() + ruta.retorno.size();
//            sRuta.tiempoMinutosFin = tiempoEnMinutosActual + sRuta.recorridoEnKm*2;
//            listaRutasEnRecorrido.add(sRuta);
//            autosDisponibles--;
//        }
//
//        for(int i=0; i<cantMotos; i++){
//            int minimo = Integer.MAX_VALUE;
//            int contador = 0;
//            int minCont = -1;
//            for(Ruta ruta: listaRutas){
//                if(ruta.vehiculo.getTipo_id() == 2 && ruta.chofer == null && minimo > ruta.tiempoMin){
//                    minimo = ruta.tiempoMin;
//                    minCont = contador;
//                }
//                contador++;
//            }
//            if(minCont == -1) break;
//            for(APedido pedido: listaRutas.get(minCont).pedidos){
//                SPedido sPedido = new SPedido();
//                sPedido.id = pedido.id;
//                sPedido.tiempoMinutosEntrega = pedido.tiempoEntregaRealizada;
//                sPedido.tiempoMinutosLimite = getMinutesFromLocalDateTime(pedido.fechaLimite);
//                sPedido.cantidad = pedido.cantidad;
//                listaPedidosEnRuta.add(sPedido);
//                listaPedidosEnCola.remove(pedido);
//            }
//            Ruta ruta = listaRutas.get(minCont);
//            ruta.chofer = new Usuario();
//            SRuta sRuta = new SRuta();
//            sRuta.tipoVehiculo = 2;
//            sRuta.recorridoEnKm = ruta.recorrido.size() + ruta.retorno.size();
//            sRuta.tiempoMinutosFin = tiempoEnMinutosActual + sRuta.recorridoEnKm;
//            listaRutasEnRecorrido.add(sRuta);
//            motosDisponibles--;
//        }
//        Collections.sort(listaPedidosEnCola);
//        Collections.sort(listaRutasEnRecorrido);
//    }
//
//    private boolean estaBloqueada(int tiempoMinutos, int nodoId){
//        for( CallesBloqueadas par : listaCallesBloqueadas ){
//            if( ( tiempoMinutos >= par.getMinutosInicio() ) && ( tiempoMinutos < par.getMinutosFin() ) ){
//                return par.estaNodo(nodoId);
//            }
//        }
//        return false;
//    }
//
//
//    public void reiniciarSimulacion(){
////        CollectionReference collection = firebase.getFirestore().collection("datosgenerales");
////        firebase.getFirestore().recursiveDelete(collection);
////        firebase.getFirestore().recursiveDelete(collection);
//        cantClusterMotos = 0;
//        cantClusterAutos = 0;
//        cantAutos = 0;
//        cantMotos = 0;
//        cantidadProductos = 0;
//        tiempoEnMinutosActual = 0;
//        autosDisponibles = 0;
//        motosDisponibles = 0;
//        ganancia = 0.0;
//        numPenalidades = 0;
//        montoPenalidades = 0.0;
//        numPedidoEntregados = 0;
//        costoMantenimiento = 0;
//        constantePenalidad = 1;
//    }
}
