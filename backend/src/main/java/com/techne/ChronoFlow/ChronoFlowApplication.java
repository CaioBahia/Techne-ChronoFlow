package com.techne.ChronoFlow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling

public class ChronoFlowApplication {

    public static void main(String[] args) {
		SpringApplication.run(ChronoFlowApplication.class, args);
	}

}
