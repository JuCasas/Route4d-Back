package com.back.route4d.algoritmo;

import com.back.route4d.algoritmo.dijkstra.Dijkstra;
import com.back.route4d.algoritmo.kmeans.Kmeans;
import com.back.route4d.model.*;
// import com.back.route4d.repository.AlgoritmoRepository;
// import com.back.route4d.repository.UsuarioRepository;
import java.io.*;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired; // para servicios

@Service
@Slf4j
@Transactional
public class Algoritmo {
    public List<Pedido> listaPedidos;

    public List<Vehicle> listaVehiculoTipo1;
    public List<Vehicle> listaVehiculoTipo2;
    public List<Vehicle> listaVehiculoTipo3;
    public List<Vehicle> listaVehiculoTipo4;

    public List<Cluster> clusterResult;
    public List<CallesBloqueadas> listaCallesBloqueadas;
    public List<Ruta> listaRutas;
    public Dijkstra dijkstraAlgorithm;
    public Kmeans kmeans;

    public Integer cantidadProductos = 0;

    public Integer cantClusterVehiculoTipo1 = 0;
    public Integer cantClusterVehiculoTipo2 = 0;
    public Integer cantClusterVehiculoTipo3 = 0;
    public Integer cantClusterVehiculoTipo4 = 0;
    public Integer cantVehiculoTipo1 = 0;
    public Integer cantVehiculoTipo2 = 0;
    public Integer cantVehiculoTipo3 = 0;
    public Integer cantVehiculoTipo4 = 0;

    /**
     * Inicializa las variables necesarias para ejecutar el algoritmo
     * 
     * @return una cadena que indica el resultado del intento de inicialización
     */
    public String inicializar() {
        // Inicializando listas de vehículos
        listaVehiculoTipo1 = initializeVehicleList(1);
        listaVehiculoTipo2 = initializeVehicleList(2);
        listaVehiculoTipo3 = initializeVehicleList(3);
        listaVehiculoTipo4 = initializeVehicleList(4);

        // Obteniendo la lista de pedidos
        obtenerListaPedidos();

        // Sin vehículos
        if (listaVehiculoTipo1.size() == 0 && listaVehiculoTipo2.size() == 0 &&
            listaVehiculoTipo3.size() == 0 && listaVehiculoTipo4.size() == 0) {
            return "No hay vehículos disponibles para las rutas";
        }

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
     * Inicializa una lista de vehículos
     * 
     * @param typeId identificador del tipo de vehículo
     * @return lista con vehículos agregados
     */
    public List<Vehicle> initializeVehicleList(int typeId) {
        List<Vehicle> list = new ArrayList<>();
        int count;
        String type;
        double capacity;
        double weight;

        if (typeId == 1) {
            count = 2;
            capacity = 25.0;
            weight = 2.5;
        } else if (typeId == 2) {
            count = 4;
            capacity = 20.0;
            weight = 2.0;
        } else if (typeId == 3) {
            count = 4;
            capacity = 15.0;
            weight = 1.5;
        } else {
            count = 10;
            capacity = 10.0;
            weight = 1.0;
        }

        for (int i = 0; i < count; i++) {
            Vehicle vehiculo = new Vehicle();

            // agregando datos de tipo
            TipoVehiculo tipo = new TipoVehiculo();
            tipo.setIdTipo(typeId);
            tipo.setCapacidad(capacity);
            tipo.setPesoBruto(weight);
            tipo.setVelocidad(50.0);

            vehiculo.setTipo(tipo);
            list.add(vehiculo);
        }

        return list;
    };

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
//        asignarRutas();

        tiempo2 = LocalDateTime.now();

        System.out.print("Tiempo de ejecución del algoritmo: ");
        System.out.println((tiempo2.getSecond() - tiempo1.getSecond()) + " segundos");

        return "Rutas generadas exitosamente";
    }

    /**
     * Obtiene mes y año a partir del nombre del archivo de nodos bloqueados
     * 
     * @param fileName cadena con la ruta completa del archivo
     * @return cadena con el mes y el año correspondientes
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
     * @param fileName cadena con la ruta completa del archivo
     * @return cadena con el mes y el año correspondientes
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
     * @param ldt fecha del tipo LocalDateTime
     * @return la fecha convertida a los minutos que pasaron desde el inicio del año
     */
    private Integer convertLocalDateTimeToMinutes(LocalDateTime ldt) {
        LocalDateTime d1 = LocalDateTime.of(2021, Month.JANUARY, 1, 0, 0);

        return (int) ChronoUnit.MINUTES.between(d1, ldt);
    }

    /**
     * Obtiene la lista de pedidos a partir de un archivo de texto
     */
    public void obtenerListaPedidos() {
        try {
            // Para lectura del archivo
            String fileName = "src/main/resources/ventas202212.txt";
            final BufferedReader br = new BufferedReader(new FileReader(fileName));
            String strYearMonth = getOrdersDateFromName(fileName); // datos del nombre del archivo
            String line; // línea del archivo
            int id = 1; // contador para identificador
            listaPedidos = new ArrayList<Pedido>(); // para almacenar pedidos
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-d H:m:s");

            // Leyendo datos del archivo

            while ((line = br.readLine()) != null) {
                final String[] tokens = line.trim().split(",");
                final String[] date = tokens[0].trim().split(":");
                final int day = Integer.parseInt(date[0]);
                final int hour = Integer.parseInt(date[1]);
                final int min = Integer.parseInt(date[2]);
                final int x = Integer.parseInt(tokens[1]);
                final int y = Integer.parseInt(tokens[2]);
                final int demand = Integer.parseInt(tokens[3]);
                final int remaining = Integer.parseInt(tokens[4]);
                String strDate = strYearMonth + "-" + day + " " + hour + ":" + min + ":0";
                LocalDateTime orderDate = LocalDateTime.parse(strDate, formatter);

                Pedido pedido = new Pedido(id++, x, y, demand, remaining, orderDate);
                listaPedidos.add(pedido);
            }

            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Obtiene la lista de calles bloqueadas a partir de un archivo de texto
     */
    public void obtenerCallesBloqueadas() {
        try {
            String fileName = "src/main/resources/202209bloqueadas.txt";
            final BufferedReader br = new BufferedReader(new FileReader(fileName));
            String strYearMonth = getLockedNodesDateFromName(fileName);
            String line;
            int id = 1; // para el identificador de la calle bloqueada
            listaCallesBloqueadas = new ArrayList<CallesBloqueadas>(); // para calles bloqueadas
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-d H:m:s");

            while ((line = br.readLine()) != null) {
                final String[] tokens = line.trim().split(",");
                final String[] plazo = tokens[0].trim().split("-");
                final String[] inicio = plazo[0].trim().split(":");
                final String[] fin = plazo[1].trim().split(":");
                final int diaIni = Integer.parseInt(inicio[0]);
                final int horaIni = Integer.parseInt(inicio[1]);
                final int minIni = Integer.parseInt(inicio[2]);
                final int diaFin = Integer.parseInt(fin[0]);
                final int horaFin = Integer.parseInt(fin[1]);
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

                CallesBloqueadas calleBloqueada = new CallesBloqueadas(id++, convertLocalDateTimeToMinutes(dateIni),
                        convertLocalDateTimeToMinutes(dateFin));

                // Agregando el identificador del nodo a la calle bloqueada
                for (int i = 0; i < len; i += 2) {
                    int x = coords[i];
                    int y = coords[i + 1];
                    calleBloqueada.addNode(x + 71 * y + 1);
                }

                listaCallesBloqueadas.add(calleBloqueada);
            }

            br.close();
        } catch (IOException e) {
            e.printStackTrace();
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

        int k = (int) (0.9 * (cantidadProductos / (cantVehiculoTipo1 * 2.5 + cantVehiculoTipo2 * 2.0 +
                    cantVehiculoTipo3 * 1.5 + cantVehiculoTipo4 * 1.0)));

        if (k > 10)
            k = 10;
        if (k < 3)
            k = 3;

        cantClusterVehiculoTipo1 = cantVehiculoTipo1 * k;
        cantClusterVehiculoTipo2 = cantVehiculoTipo2 * k;
        cantClusterVehiculoTipo3 = cantVehiculoTipo3 * k;
        cantClusterVehiculoTipo4 = cantVehiculoTipo4 * k;
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
        for (int i = 0; i < cantClusterVehiculoTipo2; i++) {
            Vehicle vehiculo = new Vehicle();
            TipoVehiculo tipo = new TipoVehiculo();
            tipo.setIdTipo(2);
            tipo.setCapacidad(20.0);
            tipo.setPesoBruto(2.0);
            tipo.setVelocidad(50.0);
            vehiculo.setTipo(tipo);
            lista.add(vehiculo);
        }
        for (int i = 0; i < cantClusterVehiculoTipo3; i++) {
            Vehicle vehiculo = new Vehicle();
            TipoVehiculo tipo = new TipoVehiculo();
            tipo.setIdTipo(3);
            tipo.setCapacidad(15.0);
            tipo.setPesoBruto(1.5);
            tipo.setVelocidad(50.0);
            vehiculo.setTipo(tipo);
            lista.add(vehiculo);
        }
        for (int i = 0; i < cantClusterVehiculoTipo4; i++) {
            Vehicle vehiculo = new Vehicle();
            TipoVehiculo tipo = new TipoVehiculo();
            tipo.setIdTipo(4);
            tipo.setCapacidad(10.0);
            tipo.setPesoBruto(1.0);
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
                    if (Math.abs(i.x - Configuraciones.almacenX) + Math.abs(i.y - Configuraciones.almacenY) > Math
                            .abs(j.x - Configuraciones.almacenX) + Math.abs(j.y - Configuraciones.almacenY))
                        return 1;
                    else if (Math.abs(i.x - Configuraciones.almacenX) + Math.abs(i.y - Configuraciones.almacenY) < Math
                            .abs(j.x - Configuraciones.almacenX) + Math.abs(j.y - Configuraciones.almacenY))
                        return -1;
                    else if (i.cantidad > j.cantidad)
                        return 1;
                    else if (i.cantidad < j.cantidad)
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
            if (cluster.firstPedido == null)
                continue;
            // imprimos en forma de reporte la información relacionada a la ruta
            System.out.println("------------------------------------------------------");
            System.out.println("Cluster: " + cluster.vehiculo.getTipo());
            System.out.println("Capacidad: " + cluster.capacidad + "/" + cluster.vehiculo.getCapacidad());
            // System.out.println("Tiempo inicial en minutos: " + tiempoMinutos);
            System.out.println("------------------------------------------------------");

            // incializamos la ruta
            Ruta ruta = new Ruta(cluster.vehiculo, cluster.capacidad);

            // seteamos el origen a nuestro almacén
            int origen = Configuraciones.almacen;

            // nos servirá para hallar un ruta si estamos en un nodo bloqueado
            int ultimoViable = Configuraciones.almacen;

            // para el firstPedido
            if (cluster.firstPedido != null) {
                Pedido pedido = cluster.firstPedido;
                ruta.addPedido(pedido);

                System.out.println("x:  " + pedido.x + "   y: " + pedido.y + "   z: " + pedido.minFaltantes
                        + "   cant: " + pedido.cantidad + "   idNodo: " + pedido.getNodoId());

                boolean estaBloqueada = estaBloqueada(tiempoMinutos, origen);

                if (estaBloqueada) {
                    System.out.println("Bloqueado!");
                    origen = ultimoViable;
                }

                dijkstraAlgorithm.dijkstra(origen, tiempoMinutos, (int) Math.round(cluster.vehiculo.getVelocidad()));
                System.out.printf("Ruta: ");

                int tamanoIni = ruta.recorrido.size();

                dijkstraAlgorithm.printShortestPath(pedido.getNodoId(), ruta, 1);

                int tamanoFin = ruta.recorrido.size();

                if (tamanoFin - tamanoIni >= 2) {
                    ultimoViable = ruta.recorrido.get(ruta.recorrido.size() - 2);
                }

                int tiempoEnLlegar = (tamanoFin - tamanoIni - 1) * 60
                        / ((int) Math.round(cluster.vehiculo.getVelocidad()));
                System.out.println("Nodos recorridos: " + (tamanoFin - tamanoIni - 1) + "   Tiempo llegada en minutos: "
                        + tiempoEnLlegar + " minutos");

                tiempoMinutos += tiempoEnLlegar;

                origen = pedido.getNodoId();

                if (cluster.pedidos.size() != 0)
                    System.out.println();
            }

            // iteramos mientras sacamos pedidos de la cola de prioridad del cluster
            // ordenados por distancia manhattan al almacén
            while (!cluster.pedidos.isEmpty()) {

                // extraemos un pedido del cluster
                Pedido pedido = cluster.pedidos.poll();
                ruta.addPedido(pedido);
                // imprimir información del pedido
                System.out.println("x:  " + pedido.x + "   y: " + pedido.y + "   z: " + pedido.minFaltantes
                        + "   cant: " + pedido.cantidad + "   idNodo: " + pedido.getNodoId());

                // verificamos si nos encontramos en un nodo bloqueado
                // esto puede ocurrir ya que hemos entregado un pedido en un nodo bloqueado
                // o si el almancén es un nodo bloqueado
                boolean estaBloqueada = estaBloqueada(tiempoMinutos, origen);

                if (estaBloqueada) {
                    System.out.println("Bloqueado!");
                    origen = ultimoViable;
                    ruta.addNodo(origen);
                }

                // corremos el algoritmo de dijkstra
                dijkstraAlgorithm.dijkstra(origen, tiempoMinutos, (int) Math.round(cluster.vehiculo.getVelocidad()));
                System.out.printf("Ruta: ");

                // tamano antes de la nueva parte de la ruta
                int tamanoIni = ruta.recorrido.size();

                // obtenemos la ruta en un array
                dijkstraAlgorithm.printShortestPath(pedido.getNodoId(), ruta, 1);

                // tamano luego de la nueva parte de la ruta
                int tamanoFin = ruta.recorrido.size();

                // para obtener el último nodo que no está bloqueado si es que acabamos de
                // entregar un pedido en un nodo bloqueado
                if (tamanoFin - tamanoIni >= 2) {
                    ultimoViable = ruta.recorrido.get(ruta.recorrido.size() - 2);
                }

                // calculamos el tiempo que tomó en llegar
                int tiempoEnLlegar = (tamanoFin - tamanoIni - 1) * 60
                        / ((int) Math.round(cluster.vehiculo.getVelocidad()));
                System.out.println("Nodos recorridos: " + (tamanoFin - tamanoIni - 1) + "   Tiempo llegada en minutos: "
                        + tiempoEnLlegar + " minutos");

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
                boolean estaBloqueada = estaBloqueada(tiempoMinutos, origen);

                if (estaBloqueada) {
                    System.out.println("Bloqueado!");
                    origen = ultimoViable;
                    ruta.addNodoRetorno(origen);
                }

                dijkstraAlgorithm.dijkstra(origen, tiempoMinutos, (int) Math.round(cluster.vehiculo.getVelocidad()));

                int tamanoIni = ruta.retorno.size(); // FALTA ENTENDER EL TAMANOINI AQUI

                dijkstraAlgorithm.printShortestPath(Configuraciones.almacen, ruta, 2);
            }

            System.out.println();
            System.out.println();
            listaRutas.add(ruta);
        }
        System.out.println("Máximo tiempo de entrega: " + maximoTiempo + " minutos");
    }

    /**
     * Verifica si un nodo está bloqueado
     * 
     * @param tiempoMinutos el tiempo que lleva ejecutándose el algoritmo
     * @param nodoId        identificador del nodo a verificar
     * 
     * @return true o false dependiendo de si el nodo está bloqueado o no
     */
    private boolean estaBloqueada(int tiempoMinutos, int nodoId) {
        for (CallesBloqueadas par : listaCallesBloqueadas) {
            if ((tiempoMinutos >= par.getMinutosInicio()) && (tiempoMinutos < par.getMinutosFin())) {
                return par.estaNodo(nodoId);
            }
        }
        return false;
    }
}
