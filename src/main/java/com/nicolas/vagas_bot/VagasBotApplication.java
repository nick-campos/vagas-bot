package com.nicolas.vagas_bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling; // Novo import

@SpringBootApplication
@EnableScheduling // Nova anotação
public class VagasBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(VagasBotApplication.class, args);
	}

}
