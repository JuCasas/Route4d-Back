package com.back.route4d;

import com.back.route4d.algoritmo.Algoritmo;
import com.back.route4d.services.FilesStorageService;
import com.back.route4d.tasks.PedidoTask;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class Route4dApplication implements CommandLineRunner {

	private static PedidoTask pedidoTask;
	private static ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(7);

	@Resource
	FilesStorageService storageService;
	public static void main(String[] args) {
		SpringApplication.run(Route4dApplication.class, args);
		pedidoTask = new PedidoTask();
		scheduledExecutorService.scheduleAtFixedRate(pedidoTask,3,10, TimeUnit.SECONDS);
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

	@Override
	public void run(String... arg) throws Exception {
		storageService.deleteAll();
		storageService.init();
	}
}
