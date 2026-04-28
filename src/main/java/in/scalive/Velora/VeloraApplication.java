package in.scalive.Velora;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class VeloraApplication {

	public static void main(String[] args) {
		SpringApplication.run(VeloraApplication.class, args);
		

		        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

		        String rawPassword = "admin@123";
		        String encodedPassword = encoder.encode(rawPassword);

		        System.out.println("Raw Password: " + rawPassword);
		        System.out.println("Encoded Password: " + encodedPassword);
		    
	}

}
