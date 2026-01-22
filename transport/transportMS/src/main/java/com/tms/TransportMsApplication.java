package com.tms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.tms.JwtSecurity.entity.Role;
import com.tms.JwtSecurity.entity.User;
import com.tms.JwtSecurity.repository.UserRepository;

@SpringBootApplication
public class TransportMsApplication implements CommandLineRunner ,WebMvcConfigurer
{
	
	@Autowired
	private UserRepository repo;

	public static void main(String[] args) {
		SpringApplication.run(TransportMsApplication.class, args);
	}
	
    @Override
	public void addCorsMappings(CorsRegistry registry) {
	registry.addMapping("/**").allowedOrigins("http://localhost:3000","http://localhost:5000").allowedMethods("POST","GET","PUT","DELETE");
    	WebMvcConfigurer.super.addCorsMappings(registry);
	}
    
	@Override
	public void run(String... args) throws Exception {
		User adminAcc=repo.findByRole(Role.ADMIN);
		if(adminAcc==null) {
			User user=new User();
			user.setEmail("admin@gmail.com");
			user.setFirstName("admin");
			user.setSecondName("admin");
			user.setPassword(new BCryptPasswordEncoder().encode("admin"));
			user.setRole(Role.ADMIN);
			repo.save(user);
		}
		
	}

}
