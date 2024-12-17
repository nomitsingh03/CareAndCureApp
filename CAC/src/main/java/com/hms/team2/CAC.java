package com.hms.team2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"com.hms.team2.*"})
public class CAC {

	public static void main(String[] args) {
		SpringApplication.run(CAC.class, args);
		
	}

}
