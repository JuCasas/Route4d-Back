package com.back.route4d;

import com.back.route4d.algoritmo.Algoritmo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;

@SpringBootApplication
public class Route4dApplication {

	public static void main(String[] args) {
		SpringApplication.run(Route4dApplication.class, args);
//		Algoritmo algoritmo = new Algoritmo();
//		ArrayList list = algoritmo.resolver();
//		System.out.println(list);

	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**").allowedOrigins("/**").allowedMethods("*").allowedHeaders("*");
			}
		};
	}
}
