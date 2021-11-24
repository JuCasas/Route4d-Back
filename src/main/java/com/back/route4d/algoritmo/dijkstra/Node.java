package com.back.route4d.algoritmo.dijkstra;

public class Node implements Comparable<Node> {
    private int first;
    private int second;

    public Node(int first, int second) {
        this.first = first;
        this.second = second;
    }

    public int getFirst() {
        return first;
    }

    public void setFirst(int first) {
        this.first = first;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
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
