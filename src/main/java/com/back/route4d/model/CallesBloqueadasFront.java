package com.back.route4d.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class CallesBloqueadasFront {
    private Integer x;
    private Integer y;
    public List<Map<String,LocalDateTime>> tiempos;

    public CallesBloqueadasFront(Integer x, Integer y) {
        this.x = x;
        this.y = y;
        this.tiempos = new ArrayList<> ();
    }
    public CallesBloqueadasFront(){
        this.tiempos = new ArrayList<>();
    }

    public void addTime(LocalDateTime ini, LocalDateTime fin){
        Map<String,LocalDateTime> map = new HashMap<String,LocalDateTime>();
        map.put("inicio",ini);
        map.put("fin",fin);
        tiempos.add(map);
    }

    public boolean esBloqueo(int x,int y){
        if (this.x == x && this.y==y) return true;
        return false;
    }


}
