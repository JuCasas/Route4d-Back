package com.back.route4d.services.impl;

import com.back.route4d.algoritmo.Algoritmo;
import com.back.route4d.services.AlgoritmoService;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@Service
public class AlgoritmoServiceImpl implements AlgoritmoService {

    public AlgoritmoServiceImpl() {
        super();
    }

    @Override
    public ArrayList enviarRutas() {
        Algoritmo algoritmo = new Algoritmo();
        ArrayList list = algoritmo.resolver();
        return list;
    }
}
