package com.back.route4d;

import com.back.route4d.algoritmo.Algoritmo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Route4dApplication {

	public static void main(String[] args) {
//		SpringApplication.run(Route4dApplication.class, args);
		Algoritmo algoritmo = new Algoritmo();
		algoritmo.inicializar();
		algoritmo.generarRutas();
	}

}
