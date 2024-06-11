package lms.user.management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class LmsUserManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(LmsUserManagementApplication.class, args);
	}

}
