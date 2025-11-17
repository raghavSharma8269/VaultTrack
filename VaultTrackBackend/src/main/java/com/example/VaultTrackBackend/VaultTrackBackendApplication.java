package com.example.VaultTrackBackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class VaultTrackBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(VaultTrackBackendApplication.class, args);
	}

}
