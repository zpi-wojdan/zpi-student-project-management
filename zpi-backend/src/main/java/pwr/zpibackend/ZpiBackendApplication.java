package pwr.zpibackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(exclude = { UserDetailsServiceAutoConfiguration.class })
@EnableAsync
@EnableScheduling
@EnableTransactionManagement
public class ZpiBackendApplication {
	public static void main(String[] args) {
		SpringApplication.run(ZpiBackendApplication.class, args);
	}

}
