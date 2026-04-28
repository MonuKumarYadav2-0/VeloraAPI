package in.scalive.Velora.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import in.scalive.Velora.entity.Role;
import in.scalive.Velora.entity.User;
import in.scalive.Velora.repository.UserRepository;

@Configuration
class DataInitializer {
	@Bean
	CommandLineRunner init(UserRepository userRepo, PasswordEncoder passwordEncoder) {
	    return args -> {

	        User admin = userRepo.findByRole(Role.ADMIN)
	                .orElseGet(() -> {
	                    User newAdmin = new User();
	                    newAdmin.setFullName("Admin");
	                    newAdmin.setRole(Role.ADMIN);
	                    newAdmin.setPassword(passwordEncoder.encode("admin@123"));
	                    System.out.println("✅ Admin created");
	                    return newAdmin;
	                });

	        String defaultEmail = "admin@velora.com";

	        if (admin.getEmail() == null || !admin.getEmail().equals(defaultEmail)) {
	            admin.setEmail(defaultEmail);
	            System.out.println("♻️ Admin email updated");
	        }

	        if (!"Admin".equals(admin.getFullName())) {
	            admin.setFullName("Admin");
	        }

	        userRepo.save(admin);
	    };
	}
}