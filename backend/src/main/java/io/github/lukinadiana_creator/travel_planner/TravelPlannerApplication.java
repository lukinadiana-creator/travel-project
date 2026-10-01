package io.github.lukinadiana_creator.travel_planner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TravelPlannerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TravelPlannerApplication.class, args);
	}

}
