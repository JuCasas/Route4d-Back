package com.back.route4d.algoritmo.dijkstra;

public class Node implements Comparable<Node> {
    int first;
    int second;

    public Node(int first, int second) {
        this.first = first;
        this.second = second;
    }

    // comparador para la cola de prioridad del algoritmo Dijkstra
    public int compareTo(Node other) {
        if(second > other.second) {
            return 1;
        }

        if(second == other.second) {
            return 0;
        }

        return -1;
    }
};
