package pwr.zpibackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ZpiBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZpiBackendApplication.class, args);
	}

}
