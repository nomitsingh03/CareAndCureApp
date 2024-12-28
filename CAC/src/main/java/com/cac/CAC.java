package com.cac;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.cac.*"})
public class CAC {

	public static void main(String[] args) {
		SpringApplication.run(CAC.class, args);
		
	}

}
