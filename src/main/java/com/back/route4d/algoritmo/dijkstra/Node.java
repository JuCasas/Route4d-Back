package com.back.route4d.algoritmo.dijkstra;

public class Node implements Comparable<Node> {
    private int ID;
    private int cost;

    public Node(int ID, int cost) {
        this.ID = ID;
        this.cost = cost;
    }

    public int getID() {
        return ID;
    }

    public int getCost() {
        return cost;
    }

    // comparador para la cola de prioridad del algoritmo Dijkstra
    public int compareTo(Node other) {
        if(cost > other.cost) {
            return 1;
        }

        if(cost == other.cost) {
            return 0;
        }

        return -1;
    }
};
