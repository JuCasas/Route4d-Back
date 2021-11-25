package com.back.route4d.algoritmo.dijkstra;

import com.back.route4d.helper.Helper;
import com.back.route4d.model.CallesBloqueadas;
import com.back.route4d.model.Ruta;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class Dijkstra {

    // número máximo de vértices
    private final int MAX = 3622; // 71 * 51 + 1

    // valor para la distancia infinita inicial
    // es suficiente que supere máximo valor del costo en alguna de las aristas
    private final int INF = 1 << 30; // TODO: considerar cambiar a un valor distinto de 2 a la 30

    // número de vértices del grafo
    private int vertexCount;

    // lista de adyacencia
    private List<List<Node>> adjacencyList = new ArrayList<>();

    // lista que contiene calles bloqueados
    private List<CallesBloqueadas> closedRoadsList;

    // distancias del vértice inicial al vértice cuyo ID es el índice especificado
    private int distance[] = new int[MAX];

    // arreglo booleano para indicar si los vértices fueron visitados
    private boolean visited[] = new boolean[MAX];

    // arreglo de vértices previos para añadir nodos a las rutas
    private int previous[] = new int[MAX];

    // cola de prioridad en la que el menor valor está a la cabeza
    private PriorityQueue<Node> queue = new PriorityQueue<>();

    // flag para determinar si ya se ejecutó el algoritmo
    private boolean hasRun;

    /**
     * Prepara el algoritmo Dijkstra usando los datos especificados
     *
     * @param  vertexCount      número de vértices del grafo
     * @param  closedRoadsList  lista de calles bloqueados
     */
    public Dijkstra(int vertexCount, List<CallesBloqueadas> closedRoadsList) {
        this.vertexCount = vertexCount;
        this.closedRoadsList = closedRoadsList;

        // se inicializa la lista de adyacencia
        for (int i = 0; i <= vertexCount; ++i) {
            adjacencyList.add(new ArrayList<>());
        }

        hasRun = false; // el algoritmo aún no se ha ejecutado
    }

    /**
     * Inicializa los arreglos a ser utilizados en el algoritmo Dijkstra
     */
    private void initialize() {
        for (int i = 0; i <= vertexCount; ++i) {
            distance[i] = INF;   // se inicializan las distancias con un valor muy grande
            visited[i] = false;  // se indica que ningún vértice ha sido visitado aún
            previous[i] = -1;    // se inicializan todos los vértices previos con -1
        }
    }

    /**
     * Agrega aristas entre nodos
     *
     * @param  initial  nodo de origen
     * @param  end      nodo de destino
     */
    public void addEdge(int initial, int end) {
        adjacencyList.get(initial).add(new Node(end, 1));
    }

    /**
     * Realiza el proceso de relajación de Dijkstra
     *
     * @param  current   ID del vértice actual
     * @param  adjacent  ID del vértice adyacente
     * @param  cost    costo desde vértice actual hasta el adyacente
     */
    private void relaxation(int current, int adjacent, int cost) {
        // si la distancia hasta el vértice actual más el costo de su arista hacia un adyacente
        // es menor a la distancia anteriormente calculada hasta dicho vértice adyacente,
        // entonces se ha encontrado una ruta más corta
        if (distance[current] + cost < distance[adjacent]) {
            distance[adjacent] = distance[current] + cost;      // se actualiza la distancia hasta el adyacente
            previous[adjacent] = current;                       // se marca el actual como previo al adyacente
            queue.add(new Node(adjacent, distance[adjacent]));  // se agrega el adyacente a la cola
        }
    }

    /**
     * Ejecuta el algoritmo Dijkstra
     *
     * @param  initial      ID del vértice inicial
     * @param  currentTime  tiempo transcurrido al momento de ejecutar el algoritmo
     * @param  speed        velocidad de desplazamiento
     */
    public void run(int initial, int currentTime, int speed) {
        int current, adjacent, cost, timeElapsed;

        // se inicializan los arreglos
        initialize();

        // se agrega el vértice inicial a la cola y se establece la distancia hacia él en 0
        queue.add(new Node(initial, 0));
        distance[initial] = 0;

        while (!queue.isEmpty()) {
            // se obtiene y remueve el ID del nodo con menor costo de la cola
            // TODO: considerar juntar las dos líneas siguientes
            current = queue.element().getID();
            queue.remove();

            // se continúan sacando nodos de la cola si el actual ya fue visitado
            if (visited[current]) {
                continue;
            }

            // se indica que ya se visitó el vértice actual
            visited[current] = true;

            // se actualiza el tiempo transcurrido
            timeElapsed = distance[current] * 60 / (speed);
            timeElapsed += currentTime;

            // determina si el nodo actual está bloqueado
            boolean isBlocked = Helper.isBlocked(timeElapsed, current, closedRoadsList);

            // se recorren los vértices adyacentes al actual
            for (int i = 0; i < adjacencyList.get(current).size(); i++) {
                adjacent = adjacencyList.get(current).get(i).getID();

                // si está bloqueado, el costo se establece en un número muy grande para evitar la relajación
                if (isBlocked) {
                    cost = INF;
                }

                // si no está bloqueado, se toma el costo de la arista que conecta dicho vértice con el actual
                else {
                    cost = adjacencyList.get(current).get(i).getCost();
                }

                // se realiza la relajación solo si el vértice adyacente no ha sido visitado aún
                if (!visited[adjacent]) {
                    relaxation(current, adjacent, cost);
                }
            }
        }

        hasRun = true; // el algoritmo ya se ha ejecutado
    }

    /**
     * Agrega los nodos correspondientes a la ruta
     *
     * @param  destination  ID del nodo de destino
     * @param  path         ruta a modificar
     * @param  type         1 para camino de ida, 2 para camino de regreso
     */
    public void addNodesToPath(int destination, Ruta path, int type) {
        if (hasRun) {
            addNodes(destination, path, type);
        }
        else {
            System.out.println("El algoritmo Dijkstra no se ha ejecutado.");
        }
    }

    /**
     * Agrega de manera recursiva los nodos a la ruta
     *
     * @param  destination  ID del nodo de destino
     * @param  path         ruta a modificar
     * @param  type         1 para camino de ida, 2 para camino de regreso
     */
    private void addNodes(int destination, Ruta path, int type) {
        // se busca que no existan nodos previos para finalizar con la recursión
        if (previous[destination] != -1) {
            addNodes(previous[destination], path, type);
        }

        // TODO: revisar la posibilidad de unir los ifs
        if (previous[destination] != -1) {
            if (type == 1) {
                path.addNodo(destination);
            }
            else {
                path.addNodoRetorno(destination);
            }
        }
    }
}
