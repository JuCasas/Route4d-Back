package com.back.route4d.algoritmo;

import com.back.route4d.algoritmo.dijkstra.Dijkstra;
import com.back.route4d.algoritmo.kmeans.Kmeans;
import com.back.route4d.helper.Helper;
import com.back.route4d.model.*;
import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import javax.transaction.Transactional;

import com.back.route4d.repository.CallesBloqueadasRepository;
import com.back.route4d.repository.PedidoRepository;
import com.back.route4d.repository.RutaRepository;
import com.back.route4d.repository.VehicleRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.buf.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Slf4j
@Transactional
@Component
public class Algoritmo {
    public List<Pedido> listaPedidos;

    public List<Vehicle> listaVehiculoDB;

    public List<Vehicle> listaVehiculoTipo1;
    public List<Vehicle> listaVehiculoTipo2;
    public List<Vehicle> listaVehiculoTipo3;
    public List<Vehicle> listaVehiculoTipo4;

    public List<Ruta> listaRutasTipo1;
    public List<Ruta> listaRutasTipo2;
    public List<Ruta> listaRutasTipo3;
    public List<Ruta> listaRutasTipo4;

    public List<Cluster> clusterResult;
    public List<CallesBloqueadas> listaCallesBloqueadas;
    public List<CallesBloqueadasFront> listaCallesBloqueadasFront;
    public List<Ruta> listaRutas;
    public List<RutaFront> listaRutasFront;
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

    // TODO: cambiar esto por valores de front
    public Integer diaInfSimulacion = 1;
    public Integer diaSupSimulacion = 3;

    @Autowired
    private VehicleRepository vehicleService;
    @Autowired
    private PedidoRepository pedidoRepository;
    @Autowired
    private RutaRepository rutaRepository;
    @Autowired
    private CallesBloqueadasRepository callesBloqueadasRepository;

    public Algoritmo(PedidoRepository pedidoRepository, VehicleRepository vehicleRepository,
                     RutaRepository rutaRepository, CallesBloqueadasRepository callesBloqueadasRepository) {
        this.pedidoRepository = pedidoRepository;
        this.vehicleService = vehicleRepository;
        this.rutaRepository = rutaRepository;
        this.callesBloqueadasRepository = callesBloqueadasRepository;
    }

    public HashMap resolver(){

        generarRutas();

        for(Ruta ruta:listaRutas){
            ruta.setRecorridoString(ruta.recorrido.stream().map(Object::toString)
                    .collect(Collectors.joining(",")));
            ruta.setRetornoString(ruta.retorno.stream().map(Object::toString)
                    .collect(Collectors.joining(",")));
        }

        rutaRepository.saveAll(listaRutas);

        System.out.println("RUTAS EN REPOSITORIO");

        listaRutasFront = new ArrayList<RutaFront>();

//        for (Ruta ruta:listaRutas){
//            List<Map<String,Integer>> recorridoEnviar = new ArrayList<>();
//            List<Map<String,Integer>> retornoEnviar = new ArrayList<>();
//            for (int nodoRecorrido:ruta.recorrido){
//                int x = (nodoRecorrido - 1) % 71;
//                int y = (nodoRecorrido - 1) / 71;
//                Map<String ,Integer> map=new HashMap<String,Integer>();
//                map.put("x",x);
//                map.put("y",y);
//                recorridoEnviar.add(map);
//            }
//
//            for (int nodoRetorno: ruta.retorno){
//                int x = (nodoRetorno - 1) % 71;
//                int y = (nodoRetorno - 1) / 71;
//                Map<String ,Integer> map=new HashMap<String,Integer>();
//                map.put("x",x);
//                map.put("y",y);
//                retornoEnviar.add(map);
//            }
//
//            RutaFront rutaFront = new RutaFront(ruta.vehiculo,ruta.capacidad);
//            //TODO FIX
//            rutaFront.setTiempoFin(ruta.getPlazoEntrega().getMinute());
//            rutaFront.pedidos.addAll(ruta.pedidos);
//            rutaFront.recorrido.addAll(recorridoEnviar);
//            rutaFront.retorno.addAll(retornoEnviar);
//
//            listaRutasFront.add(rutaFront);
//        }
//
//        String nodos = callesBloqueadas.getNodos();
//        List<String> listaNodos = new ArrayList<String>(Arrays.asList(nodos.split(",")));
//
//        for (String s : listaNodos) {
//            int nodo = Integer.valueOf(s);
//
//            int x = (nodo - 1) % 71;
//            int y = (nodo - 1) / 71;
//
//            boolean encontrado = false;
//            for (CallesBloqueadasFront bloqueoRevisar : listaCallesBloqueadasFront) {
//                if (bloqueoRevisar.esBloqueo(x,y)){
//                    bloqueoRevisar.addTime(fechaInicio,fechaFin);
//                    encontrado = true;
//                    break;
//                }
//            }
//
//            if(!encontrado) {
//                CallesBloqueadasFront calleFront = new CallesBloqueadasFront(x,y);
//                calleFront.addTime(fechaInicio,fechaFin);
//                listaCallesBloqueadasFront.add(calleFront);
//            }
//        }

        HashMap<String,Object> enviar;
        enviar = new HashMap<>();
        enviar.put("Rutas",listaRutasFront);

        return (HashMap) enviar;
    }

    /**
     * Inicializa las variables necesarias para ejecutar el algoritmo
     *
     * @return una cadena que indica el resultado del intento de inicialización
     */
    public String inicializar() {
        // Inicializando listas de vehículos
        LocalDateTime tiempoAhora = LocalDateTime.now();
        listaVehiculoDB = vehicleService.getAvailableByType(tiempoAhora);

        listaVehiculoTipo1 = new ArrayList<>();
        listaVehiculoTipo2 = new ArrayList<>();
        listaVehiculoTipo3 = new ArrayList<>();
        listaVehiculoTipo4 = new ArrayList<>();

        for(Vehicle vehicle:listaVehiculoDB){
            switch (vehicle.getTipo().getIdTipo()){
                case 1:
                    listaVehiculoTipo1.add(vehicle);
                    break;
                case 2:
                    listaVehiculoTipo2.add(vehicle);
                    break;
                case 3:
                    listaVehiculoTipo3.add(vehicle);
                    break;
                case 4:
                    listaVehiculoTipo4.add(vehicle);
                    break;
            }
        }

        // Sin vehículos

        if (listaVehiculoTipo1.size() == 0 && listaVehiculoTipo2.size() == 0 &&
                listaVehiculoTipo3.size() == 0 && listaVehiculoTipo4.size() == 0) {
            return "No hay vehículos disponibles para las rutas";
        }

        listaPedidos = pedidoRepository.findLessThanDate(LocalDateTime.now(),0);

        // Sin pedidos
        if (listaPedidos.size() == 0) {
            return "No hay pedidos en cola";
        }

        // Obteniendo la cantidad de clusters
        obtenerCantidadClusters();

        // Para agrupar en clusters
        kmeans = new Kmeans(cantVehiculoTipo1, cantVehiculoTipo2, cantVehiculoTipo3, cantVehiculoTipo4);

        // Obteniendo las calles bloqueadas
        obtenerCallesBloqueadas();

        // Obteniendo la lista de adyacencia
        obtenerListaAdyacente();

        return "correcto";
    }

    /**
     * Genera las rutas a partir de los datos obtenidos
     *
     * @return cadena que indica que la generación de rutas fue exitosa
     */
    public String generarRutas() {
        LocalDateTime tiempo1, tiempo2;

        tiempo1 = LocalDateTime.now();

        obtenerPedidosClusterizados();
        obtenerRutas();
        asignarRutas();

        tiempo2 = LocalDateTime.now();

        System.out.print("Tiempo de ejecución del algoritmo: ");
        System.out.println(((tiempo2.getMinute()*60 + tiempo2.getSecond()) - (tiempo1.getMinute()*60+tiempo1.getSecond())) + " segundos");

        return "Rutas generadas exitosamente";
    }

    /**
     * Obtiene la lista de pedidos a partir de un archivo de texto
     */
    public void obtenerListaPedidos() {
        try {
            // Para lectura del archivo
            String fileName = "/ventas202112.txt";
            final BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(fileName)));
            String strYearMonth = Helper.getOrdersDateFromName(fileName); // datos del nombre del archivo
            String line; // línea del archivo
            int id = 1; // contador para identificador
            listaPedidos = new ArrayList<>(); // para almacenar pedidos
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-d H:m:s");

            // Leyendo datos del archivo
            while ((line = br.readLine()) != null) {
                final String[] tokens = line.trim().split(",");
                final String[] date = tokens[0].trim().split(":");
                final int day = Integer.parseInt(date[0]);

                // Para simulación de 3 días
//                if (day >= diaInfSimulacion && day <= diaSupSimulacion) {

                    final int hour = Integer.parseInt(date[1]);
                    final int min = Integer.parseInt(date[2]);
                    final int x = Integer.parseInt(tokens[1]);
                    final int y = Integer.parseInt(tokens[2]);
                    int demand = Integer.parseInt(tokens[3]);
                    int remaining = Integer.parseInt(tokens[4]);
                    String strDate = strYearMonth + "-" + day + " " + hour + ":" + min + ":0";
                    LocalDateTime orderDate = LocalDateTime.parse(strDate, formatter);
                    LocalDateTime limitDate = orderDate.plus(Duration.of(remaining, ChronoUnit.HOURS));

                    remaining = Helper.convertLocalDateTimeToMinutes(limitDate);

                    Pedido pedido = new Pedido(id++, x, y, demand, remaining, orderDate, limitDate, 0);
                    listaPedidos.add(pedido);
//                }
//                else if (day > diaSupSimulacion) {
//                    break;
//                }
            }

            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Obtiene la lista de calles bloqueadas a partir de la base de datos
     */
    public void obtenerCallesBloqueadas() {
        listaCallesBloqueadas = callesBloqueadasRepository.findAll();

        for (CallesBloqueadas calleBloqueada : listaCallesBloqueadas) {
            String nodos = calleBloqueada.getNodos();
            List<String> listaNodos = new ArrayList<String>(Arrays.asList(nodos.split(",")));
            calleBloqueada.setConjuntoNodos(new HashSet<>());

            for (String s : listaNodos) {
                int nodo = Integer.valueOf(s);
                calleBloqueada.addNode(nodo);
            }
        }
    }

    /**
     * Obtiene la cantidad de clusters necesarios para el algoritmo
     */
    public void obtenerCantidadClusters() {
        cantVehiculoTipo1 = listaVehiculoTipo1.size();
        cantVehiculoTipo2 = listaVehiculoTipo2.size();
        cantVehiculoTipo3 = listaVehiculoTipo3.size();
        cantVehiculoTipo4 = listaVehiculoTipo4.size();

        System.out.println(listaVehiculoTipo1.size() + " " + listaVehiculoTipo2.size() + " " + listaVehiculoTipo3.size()
                + " " + listaVehiculoTipo4.size());

        int k = (int) (1 * (listaPedidos.size() / (cantVehiculoTipo1 * 2.5 + cantVehiculoTipo2 * 2.0 +
                cantVehiculoTipo3 * 1.5 + cantVehiculoTipo4 * 1.0)));

        if (k > 10)
            k = 10;
        if (k < 3)
            k = 3;

        cantClusterVehiculoTipo1 = cantVehiculoTipo1 * 1;
        cantClusterVehiculoTipo2 = cantVehiculoTipo2 * 1;
        cantClusterVehiculoTipo3 = cantVehiculoTipo3 * 1;
        cantClusterVehiculoTipo4 = cantVehiculoTipo4 * 1;
    }

    /**
     * Obtiene la lista de adyacencia a partir de un archivo de texto
     */
    public void obtenerListaAdyacente() {
        int origen, destino;
        InputStream grafo = getClass().getClassLoader().getResourceAsStream("grafo.txt");
        Scanner sc = new Scanner(grafo);

        dijkstraAlgorithm = new Dijkstra(Configuraciones.V, listaCallesBloqueadas);
        for (int i = 0; i < Configuraciones.E; i++) {
            origen = sc.nextInt() + 1;
            destino = sc.nextInt() + 1;
            dijkstraAlgorithm.addEdge(origen, destino);
        }

        sc.close();
    }

    /**
     * Distribuye los pedidos en clusters
     */
    public void obtenerPedidosClusterizados() {
        int cantClusters = cantClusterVehiculoTipo1 + cantClusterVehiculoTipo2 + cantClusterVehiculoTipo3 + cantClusterVehiculoTipo4;

        List<Vehicle> vehiculos = inicializarVehiculos();

        // inicializar clusters
        List<Cluster> clustersList = inicializarClusters(vehiculos);
        List<Cluster> clustersAns = inicializarClusters(vehiculos);

        // clusterización
        clusterResult = kmeans.kmeans(listaPedidos, clustersList, cantClusters, clustersAns);
        Double SSE = kmeans.getOptimo(listaPedidos, clustersAns, cantClusters);
        System.out.println("------------------------------------------------------");
        System.out.println("Rutas calculadas con un SSE=" + SSE);
        System.out.println("------------------------------------------------------");
        System.out.println();
        System.out.println();
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
            vehiculo.setTipo(tipo);
            lista.add(vehiculo);
        }

        return lista;
    }

    /**
     * Inicializa los clusters
     */
    public List<Cluster> inicializarClusters(List<Vehicle> vehiculos) {
        List<Cluster> lista = new ArrayList<Cluster>();
        for (Vehicle vehiculo : vehiculos) {
            Cluster cluster = new Cluster();
            // TODO ENTENDER
            cluster.pedidos = new PriorityQueue<Pedido>(500, new Comparator<Pedido>() {
                // override compare method
                public int compare(Pedido i, Pedido j) {
                    // if(i.minFaltantes > j.minFaltantes) return 1;
                    // else if (i.minFaltantes < j.minFaltantes) return -1;
                    // else return 0;
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

    /**
     * Obtiene las rutas
     */
    public void obtenerRutas() {
        // calculamos el tiempo en minutos en que iniciamos a correr el algoritmo
        LocalDateTime tiempo = LocalDateTime.now();
        LocalDateTime d1 = LocalDateTime.of(2021, Month.JANUARY, 1, 0, 0);
        int tiempoMinutosInicio = (int) ChronoUnit.MINUTES.between(d1, tiempo);

        // para calcular el tiempo máximo de entrega
        int maximoTiempo = -1;

        // inicializamos la lista de rutas
        listaRutas = new ArrayList<Ruta>();

        for (Cluster cluster : clusterResult) {
            // asignamos el tiempo en minutos en que iniciamos a correr el algoritmo
            int tiempoMinutos = tiempoMinutosInicio;
            int duracionRecorrido = 0;
            int duracionRetorno = 0;

            if (cluster.firstPedido == null)
                continue;
            // imprimos en forma de reporte la información relacionada a la ruta
            System.out.println("------------------------------------------------------");
            System.out.println("Cluster: " + cluster.vehiculo.getTipo().getIdTipo());
            System.out.println("Capacidad: " + cluster.capacidad + "/" + cluster.vehiculo.getTipo().getCapacidad());
            // System.out.println("Tiempo inicial en minutos: " + tiempoMinutos);
            System.out.println("------------------------------------------------------");

            // incializamos la ruta
            Ruta ruta = new Ruta(cluster.vehiculo, cluster.capacidad);

            // TODO cambiar a posicion del actual vehiculo seteamos el origen a nuestro almacén
            int origen = Configuraciones.almacen;

            // nos servirá para hallar un ruta si estamos en un nodo bloqueado
            int ultimoViable = Configuraciones.almacen;

            // para el firstPedido
            if (cluster.firstPedido != null) {
                Pedido pedido = cluster.firstPedido;
                ruta.addPedido(pedido);

                double pesoCarga = 0;
                for(Pedido pedidoUnitario:cluster.pedidos){
                    pesoCarga+=pedidoUnitario.getCantidad();
                }
                System.out.println("x:  " + pedido.getX() + "   y: " + pedido.getY() + "   z: " + pedido.getMinFaltantes()
                        + "   cant: " + pedido.getCantidad() + "   idNodo: " + pedido.getNodoId());

                boolean estaBloqueada = Helper.isBlocked(tiempoMinutos, origen, listaCallesBloqueadas);

                if (estaBloqueada) {
                    System.out.println("Bloqueado!");
                    origen = ultimoViable;
                }

                dijkstraAlgorithm.run(origen, tiempoMinutos, (int) Math.round(cluster.vehiculo.getTipo().getVelocidad()));
                System.out.printf("Ruta: ");

                int tamanoIni = ruta.recorrido.size();

                dijkstraAlgorithm.addNodesToPath(pedido.getNodoId(), ruta, 1);

                int tamanoFin = ruta.recorrido.size();

                if (tamanoFin - tamanoIni >= 2) {
                    ultimoViable = ruta.recorrido.get(ruta.recorrido.size() - 2);
                }

                int tiempoEnLlegar = (tamanoFin - tamanoIni - 1) * 60
                        / ((int) Math.round(cluster.vehiculo.getTipo().getVelocidad()));
                System.out.println("Nodos recorridos: " + (tamanoFin - tamanoIni - 1) + "   Tiempo llegada en minutos: "
                        + tiempoEnLlegar + " minutos");

                duracionRecorrido += tiempoEnLlegar;

                //TODO actualizar
                pedido.setFechaEntrega(LocalDateTime.now().plus(tiempoEnLlegar,ChronoUnit.MINUTES));
                pedido.setConsumoPetroleo(cluster.vehiculo.getTipo().getPesoBruto()+(pesoCarga)*ruta.recorrido.size()/150);

                tiempoMinutos += tiempoEnLlegar;

                origen = pedido.getNodoId();

                if (cluster.pedidos.size() != 0)
                    System.out.println();
            }

            // iteramos mientras sacamos pedidos de la cola de prioridad del cluster
            // ordenados por distancia manhattan al almacén
            while (!cluster.pedidos.isEmpty()) {

                double pesoCarga = 0;
                for(Pedido pedidoUnitario:cluster.pedidos){
                    pesoCarga+=pedidoUnitario.getCantidad();
                }
                // extraemos un pedido del cluster
                Pedido pedido = cluster.pedidos.poll();
                ruta.addPedido(pedido);
                // imprimir información del pedido
                System.out.println("x:  " + pedido.getX() + "   y: " + pedido.getY() + "   z: " + pedido.getMinFaltantes()
                        + "   cant: " + pedido.getCantidad() + "   idNodo: " + pedido.getNodoId());

                // verificamos si nos encontramos en un nodo bloqueado
                // esto puede ocurrir ya que hemos entregado un pedido en un nodo bloqueado
                // o si el almancén es un nodo bloqueado
                boolean estaBloqueada = Helper.isBlocked(tiempoMinutos, origen, listaCallesBloqueadas);

                if (estaBloqueada) {
                    System.out.println("Bloqueado!");
                    origen = ultimoViable;
                    ruta.addNodo(origen);
                }

                // corremos el algoritmo de dijkstra
                dijkstraAlgorithm.run(origen, tiempoMinutos, (int) Math.round(cluster.vehiculo.getTipo().getVelocidad()));
                System.out.printf("Ruta: ");

                // tamano antes de la nueva parte de la ruta
                int tamanoIni = ruta.recorrido.size();

                // obtenemos la ruta en un array
                dijkstraAlgorithm.addNodesToPath(pedido.getNodoId(), ruta, 1);

                // tamano luego de la nueva parte de la ruta
                int tamanoFin = ruta.recorrido.size();

                // para obtener el último nodo que no está bloqueado si es que acabamos de
                // entregar un pedido en un nodo bloqueado
                if (tamanoFin - tamanoIni >= 2) {
                    ultimoViable = ruta.recorrido.get(ruta.recorrido.size() - 2);
                }

                // calculamos el tiempo que tomó en llegar
                int tiempoEnLlegar = (tamanoFin - tamanoIni - 1) * 60
                        / ((int) Math.round(cluster.vehiculo.getTipo().getVelocidad()));
                System.out.println("Nodos recorridos: " + (tamanoFin - tamanoIni - 1) + "   Tiempo llegada en minutos: "
                        + tiempoEnLlegar + " minutos");

                duracionRecorrido += tiempoEnLlegar;
                //TODO actualizar
                pedido.setFechaEntrega(LocalDateTime.now().plus(tiempoEnLlegar,ChronoUnit.MINUTES));
                pedido.setConsumoPetroleo(cluster.vehiculo.getTipo().getPesoBruto()+(pesoCarga)*ruta.recorrido.size()/150);

                // calculamos el nuevo tiempo en el que nos encontramos
                tiempoMinutos += tiempoEnLlegar;

                // cambiamos el origen
                origen = pedido.getNodoId();

                // detalle estético, la última línea no imprime una nueva en el reporte
                if (cluster.pedidos.size() != 0)
                    System.out.println();
            }

            // tiempo que tomó realizar la entrega
            int diferenciaTiempo = tiempoMinutos - tiempoMinutosInicio;

            if (diferenciaTiempo > maximoTiempo) {
                maximoTiempo = diferenciaTiempo;
            }
            System.out.println("------------------------------------------------------");
            System.out.println("Tiempo de entrega: " + diferenciaTiempo + " minutos");
            System.out.println("------------------------------------------------------");

            if (cluster.firstPedido != null) {
                System.out.println("Camino de retorno al almacén:  ");

                origen = ruta.recorrido.get(ruta.recorrido.size() - 1);
                boolean estaBloqueada = Helper.isBlocked(tiempoMinutos, origen, listaCallesBloqueadas);

                if (estaBloqueada) {
                    System.out.println("Bloqueado!");
                    origen = ultimoViable;
                    ruta.addNodoRetorno(origen);
                }

                dijkstraAlgorithm.run(origen, tiempoMinutos, (int) Math.round(cluster.vehiculo.getTipo().getVelocidad()));

                int tamanoIni = ruta.retorno.size(); // FALTA ENTENDER EL TAMANOINI AQUI

                dijkstraAlgorithm.addNodesToPath(almacenAIr(origen), ruta, 2);

                // tamano luego de la nueva parte de la ruta
                int tamanoFin = ruta.retorno.size();

                // calculamos el tiempo que tomó en llegar
                int tiempoEnLlegar = (tamanoFin - tamanoIni - 1) * 60
                        / ((int) Math.round(cluster.vehiculo.getTipo().getVelocidad()));
                System.out.println("Nodos recorridos: " + (tamanoFin - tamanoIni - 1) + "   Tiempo llegada de retorno en minutos: "
                        + tiempoEnLlegar + " minutos");

                duracionRetorno += tiempoEnLlegar;

            }

            System.out.println();
            System.out.println();
            ruta.setDuracionMinutosRecorrido(duracionRecorrido);
            ruta.setDuracionMinutosRetorno(duracionRetorno);
            ruta.setDuracion_minutos(duracionRecorrido+duracionRetorno);
            listaRutas.add(ruta);
        }
        System.out.println("Máximo tiempo de entrega: " + maximoTiempo + " minutos");
    }


    public int almacenAIr(int idOrigen){
        int minimo = 1 << 30;
        int x = (idOrigen - 1) % 71;
        int y = (idOrigen - 1) / 71;
        int idEnviar = 0;

        if (Math.abs(x - Configuraciones.almacenX) + Math.abs(y - Configuraciones.almacenY)<minimo){
            idEnviar = Configuraciones.almacen;
            minimo = Math.abs(x - Configuraciones.almacenX) + Math.abs(y - Configuraciones.almacenY);
        }
        if (Math.abs(x - Configuraciones.norteX) + Math.abs(y - Configuraciones.norteY)<minimo){
            idEnviar = Configuraciones.norte;
            minimo = Math.abs(x - Configuraciones.norteX) + Math.abs(y - Configuraciones.norteY);
        }
        if (Math.abs(x - Configuraciones.surX) + Math.abs(y - Configuraciones.surY)<minimo){
            idEnviar = Configuraciones.sur;
            minimo = Math.abs(x - Configuraciones.surX) + Math.abs(y - Configuraciones.surY);
        }
        return Configuraciones.almacen;
    }

    public void asignarRutaTipo(int idTipo,int cantVehiculos){
        for (int i = 0; i < cantVehiculos; i++) {
            LocalDateTime minimo = LocalDateTime.of(2034,12,30,12,12);
            int contador = 0;
            int minCont = -1;
            for(Ruta ruta: listaRutas){
                if(ruta.vehiculo.getTipo().getIdTipo() == idTipo && ruta.vehiculo.getIdVehiculo()==0 && minimo.isAfter(ruta.plazoEntrega)){
                    minimo = ruta.plazoEntrega;
                    minCont = contador;
                }
                contador++;
            }
            if(minCont == -1) break;

            Vehicle vehicleAsignar = new Vehicle();
            switch (idTipo){
                case 1:
                    vehicleAsignar = listaVehiculoTipo1.get(i);
                    break;
                case 2:
                    vehicleAsignar = listaVehiculoTipo2.get(i);
                    break;
                case 3:
                    vehicleAsignar = listaVehiculoTipo3.get(i);
                    break;
                case 4:
                    vehicleAsignar = listaVehiculoTipo4.get(i);
                    break;
            }

            listaRutas.get(minCont).setVehiculo(vehicleAsignar);

            LocalDateTime timeNow = LocalDateTime.now();
            listaRutas.get(minCont).setFechaInicioRecorrido(timeNow);
            listaRutas.get(minCont).setFechaInicioRetorno(timeNow.plus(listaRutas.get(minCont).duracionMinutosRecorrido,ChronoUnit.MINUTES));
            listaRutas.get(minCont).setFechaFinRetorno(timeNow.plus(listaRutas.get(minCont).duracionMinutosRecorrido +
                    listaRutas.get(minCont).duracionMinutosRetorno ,ChronoUnit.MINUTES));

            for (Pedido pedidoActualizar:listaRutas.get(minCont).pedidos){
                pedidoActualizar.setVehicle(vehicleAsignar);
                pedidoActualizar.setEstado(1);
                pedidoRepository.save(pedidoActualizar);
            }
        }
    }

    /**
     * Asigna las rutas
     */
    public void asignarRutas() {
        asignarRutaTipo(1,cantVehiculoTipo1);
        asignarRutaTipo(2,cantVehiculoTipo2);
        asignarRutaTipo(3,cantVehiculoTipo3);
        asignarRutaTipo(4,cantVehiculoTipo4);
    }
}
