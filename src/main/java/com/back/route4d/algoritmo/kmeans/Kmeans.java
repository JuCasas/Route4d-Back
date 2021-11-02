package com.back.route4d.algoritmo.kmeans;

import com.back.route4d.model.Cluster;
import com.back.route4d.model.Pedido;
import com.back.route4d.model.Configuraciones;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
public class Kmeans {
    // Higher precision means earlier termination
    // and higher error
    static final Double PRECISION = 0.0;
    static final Double FACTORTIEMPO = 1.5;

    public Integer cantVehiculosTipo1;
    public Integer cantVehiculosTipo2;
    public Integer cantVehiculosTipo3;
    public Integer cantVehiculosTipo4;

    public Kmeans(Integer cant1, Integer cant2, Integer cant3, Integer cant4) {
        this.cantVehiculosTipo1 = cant1;
        this.cantVehiculosTipo2 = cant2;
        this.cantVehiculosTipo3 = cant3;
        this.cantVehiculosTipo4 = cant4;
    }

    /* K-Means++ implementation, initializes K centroids from data */
    private List<Cluster> kmeanspp(List<Cluster> clusters) {
        for (Cluster cluster : clusters) {
            cluster.centroideX = (int) (Math.random() * 51);
            cluster.centroideY = (int) (Math.random() * 71);
            cluster.centroideZ = (int) (Math.random() * 1440);
        }

        return clusters;
    }

    private double distancia(List<Cluster> clusters, Pedido pedido, int i) {

        int y2 = clusters.get(i).centroideY;
        int x2 = clusters.get(i).centroideX;
        int z2 = clusters.get(i).centroideZ;

        int y1 = pedido.getY();
        int x1 = pedido.getX();
        int z1 = pedido.getMinFaltantes();

        double distance;
        distance = Math.abs(y2 - y1) + Math.abs(x2 - x1);
        return distance;
    }

    private double euclideanDistanceXCluster(Cluster cluster, Pedido pedido) {

        int y2 = cluster.centroideY;
        int x2 = cluster.centroideX;
        int z2 = cluster.centroideZ;

        int y1 = pedido.getY();
        int x1 = pedido.getX();
        int z1 = pedido.getMinFaltantes();

        double distance;
        distance = Math.abs(y2 - y1) + Math.abs(x2 - x1);
        return distance;
    }

    private double calculateTotalSSE(List<Cluster> clusters) {
        double totalsse = 0;
        int totalVehiculosTipo1 = 0;
        int totalVehiculosTipo2 = 0;
        int totalVehiculosTipo3 = 0;
        int totalVehiculosTipo4 = 0;

        List<Pedido> listaVehiculosTipo1 = new ArrayList<>();
        List<Pedido> listaVehiculosTipo2 = new ArrayList<>();
        List<Pedido> listaVehiculosTipo3 = new ArrayList<>();
        List<Pedido> listaVehiculosTipo4 = new ArrayList<>();

        int cont = 0;
        for (Cluster cluster : clusters) {
            if (cluster.firstPedido == null) {
                cont++;
                continue;
            }
            cluster.firstPedido.setIdCluster(cont);

            if (cluster.vehiculo.getTipo().getIdTipo() == 1) {
                listaVehiculosTipo1.add(cluster.firstPedido);
            }
            if (cluster.vehiculo.getTipo().getIdTipo() == 2) {
                listaVehiculosTipo2.add(cluster.firstPedido);
            }
            if (cluster.vehiculo.getTipo().getIdTipo() == 3) {
                listaVehiculosTipo3.add(cluster.firstPedido);
            }
            if (cluster.vehiculo.getTipo().getIdTipo() == 4) {
                listaVehiculosTipo4.add(cluster.firstPedido);
            }

            cont++;
        }

        Collections.sort(listaVehiculosTipo1);
        Collections.sort(listaVehiculosTipo2);
        Collections.sort(listaVehiculosTipo3);
        Collections.sort(listaVehiculosTipo4);

        for (Pedido pedido : listaVehiculosTipo1) {
            if (totalVehiculosTipo1 < cantVehiculosTipo1) {
                totalsse = totalsse + SSE(clusters.get(pedido.getIdCluster()));
                totalVehiculosTipo1++;
            }
        }
        for (Pedido pedido : listaVehiculosTipo2) {
            if (totalVehiculosTipo2 < cantVehiculosTipo2) {
                totalsse = totalsse + SSE(clusters.get(pedido.getIdCluster()));
                totalVehiculosTipo2++;
            }
        }
        for (Pedido pedido : listaVehiculosTipo3) {
            if (totalVehiculosTipo3 < cantVehiculosTipo3) {
                totalsse = totalsse + SSE(clusters.get(pedido.getIdCluster()));
                totalVehiculosTipo3++;
            }
        }
        for (Pedido pedido : listaVehiculosTipo4) {
            if (totalVehiculosTipo4 < cantVehiculosTipo4) {
                totalsse = totalsse + SSE(clusters.get(pedido.getIdCluster()));
                totalVehiculosTipo4++;
            }
        }

        return totalsse;
    }

    private double SSE(Cluster cluster) {

        double sumX = cluster.firstPedido.getX();
        double sumY = cluster.firstPedido.getY();
        double sum = 0;
        double media = euclideanDistanceXCluster(cluster, cluster.firstPedido);
        int cant = 1;

        // media
        for (Pedido pedido : cluster.pedidos) {
            cant++;
            media = media + euclideanDistanceXCluster(cluster, pedido);
        }
        if (cant != 0)
            media = media / cant;
        else
            media = 0;

        for (Pedido pedido : cluster.pedidos) {
            sum = sum + Math.pow((euclideanDistanceXCluster(cluster, pedido) - media), 2);
        }

        return sum;
    }

    private void cleanPedidos(List<Cluster> clusters) {
        for (Cluster cluster : clusters) {
            cluster.firstPedido = null;
            cluster.capacidad = 0;
            cluster.pedidos = new PriorityQueue<Pedido>(40, new Comparator<Pedido>() {
                // override compare method
                public int compare(Pedido i, Pedido j) {

                    // if(i.minFaltantes > j.minFaltantes) return 1;
                    // else if (i.minFaltantes < j.minFaltantes) return -1;
                    // else return 0;

                    if (Math.abs(i.getX() - Configuraciones.almacenX) + Math.abs(i.getY() - Configuraciones.almacenY) > Math
                            .abs(j.getX() - Configuraciones.almacenX) + Math.abs(j.getY() - Configuraciones.almacenY)) {
                        return 1;
                    }
                    else if (Math.abs(i.getX() - Configuraciones.almacenX) + Math.abs(i.getY() - Configuraciones.almacenY) < Math
                            .abs(j.getX() - Configuraciones.almacenX) + Math.abs(j.getY() - Configuraciones.almacenY)) {
                        return -1;
                    }
                    else if (i.getCantidad() > j.getCantidad()) {
                        return 1;
                    }
                    else if (i.getCantidad() < j.getCantidad()) {
                        return -1;
                    }
                    else {
                        return 1;
                    }
                }
            });
        }
    }

    private void recomputeCentroids(List<Cluster> clusters) {
        int promX, promY, promZ, cant;
        for (Cluster cluster : clusters) {
            if (cluster.firstPedido == null) {
                cluster.centroideX = (int) (Math.random() * 51);
                cluster.centroideY = (int) (Math.random() * 71);
                cluster.centroideZ = (int) (Math.random() * 1440);
                continue;
            }
            promX = cluster.firstPedido.getX();
            promY = cluster.firstPedido.getY();
            promZ = cluster.firstPedido.getMinFaltantes();
            cant = 1;

            for (Pedido pedido : cluster.pedidos) {
                cant = cant + 1;
                promX = promX + pedido.getX();
                promY = promY + pedido.getY();
                promZ = promZ + pedido.getMinFaltantes();
            }
            if (cant != 0) {
                cluster.centroideY = promY / cant;
                cluster.centroideX = promX / cant;
                cluster.centroideZ = promZ / cant;
            }
        }
    }

    /*
     * K-Means itself, it takes a dataset and a number K and adds class numbers to
     * records in the dataset
     */
    public List<Cluster> kmeans(List<Pedido> pedidos, List<Cluster> clusters, int K, List<Cluster> clusterAns) {

        int runs = 100;
        double SSE = Double.MAX_VALUE; // Double.MAX_VALUE;
        while (runs-- != 0) {
            // Initialize Sum of Squared Errors to max, we'll lower it at each iteration
            cleanPedidos(clusters);
            // Select K initial centroids
            kmeanspp(clusters);
            int iterations = 30;
            while (iterations-- != 0) {
                // Assign observations to centroids
                // var records = data.getRecords();
                cleanPedidos(clusters);
                // For each record
                for (Pedido pedido : pedidos) {
                    double minDist = Double.MAX_VALUE;
                    // Find the centroid at a minimum distance from it and add the record to its
                    // cluster
                    int asigned = -1;
                    boolean first = false;
                    for (int i = 0; i < K; i++) {
                        double dist = distancia(clusters, pedido, i);
                        if (dist < minDist) {
                            if (pedido.getCantidad() + clusters.get(i).capacidad <= clusters.get(i).vehiculo
                                    .getTipo().getCapacidad()) {
                                minDist = dist;
                                if (asigned > -1) {
                                    if (first)
                                        clusters.get(asigned).firstPedido = null;
                                    else
                                        clusters.get(asigned).pedidos.remove(pedido);
                                    clusters.get(asigned).capacidad -= pedido.getCantidad();
                                }
                                if (clusters.get(i).firstPedido == null) {
                                    clusters.get(i).firstPedido = pedido;
                                    clusters.get(i).capacidad += pedido.getCantidad();
                                    asigned = i;
                                    first = true;
                                } else {
                                    clusters.get(i).setClusterNo(pedido);
                                    asigned = i;
                                    first = false;
                                }
                            }
                        }
                    }
                    // log.info( first ? "true" : "false");
                }
                // log.info( "Cluster: " + clusters.get(0).firstPedido);
                // Exit condition, SSE changed less than PRECISION parameter
                Double newSSE = calculateTotalSSE(clusters);

                if (newSSE < SSE) {
                    SSE = newSSE;
                    for (int i = 0; i < clusters.size(); i++) {

                        clusterAns.get(i).centroideX = clusters.get(i).centroideX;
                        clusterAns.get(i).centroideY = clusters.get(i).centroideY;
                        clusterAns.get(i).centroideZ = clusters.get(i).centroideZ;
                    }
                    System.out.println("newSSE: " + newSSE);
                }

                // Recompute centroids according to new cluster assignments
                recomputeCentroids(clusters);
            }
        }
        System.out.println("Rutas calculadas con un SSE=" + SSE);
        return clusterAns;
    }

    public Double getOptimo(List<Pedido> pedidos, List<Cluster> clusters, int K) {
        for (Pedido pedido : pedidos) {
            Double minDist = Double.MAX_VALUE;
            // Find the centroid at a minimum distance from it and add the record to its
            // cluster
            int asigned = -1;
            boolean first = false;
            for (int i = 0; i < K; i++) {
                Double dist = distancia(clusters, pedido, i);
                if (dist < minDist) {
                    if (pedido.getCantidad() + clusters.get(i).capacidad <= clusters.get(i).vehiculo.getTipo().getCapacidad()) {
                        minDist = dist;
                        if (asigned > -1) {
                            if (first)
                                clusters.get(asigned).firstPedido = null;
                            else
                                clusters.get(asigned).pedidos.remove(pedido);
                            clusters.get(asigned).capacidad -= pedido.getCantidad();
                        }
                        if (clusters.get(i).firstPedido == null) {
                            clusters.get(i).firstPedido = pedido;
                            clusters.get(i).capacidad += pedido.getCantidad();
                            asigned = i;
                            first = true;
                        } else {
                            clusters.get(i).setClusterNo(pedido);
                            asigned = i;
                            first = false;
                        }
                    }
                }
            }
        }
        return calculateTotalSSE(clusters);
    }
}