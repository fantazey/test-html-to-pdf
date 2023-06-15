package ru.fantazey.generatepdf;

import com.itextpdf.text.DocumentException;
import lombok.AllArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.io.IOException;

@SpringBootApplication

public class GeneratePdfApplication {
	public static void main(String[] args) {
		SpringApplication.run(GeneratePdfApplication.class, args);
	}

	@EnableWebMvc
	@Configuration
	public class MyConfig implements WebMvcConfigurer {

		@Override
		public void addResourceHandlers(ResourceHandlerRegistry registry) {
			registry.addResourceHandler("/fonts/**").addResourceLocations("classpath:fonts/");
			registry.addResourceHandler("/css/**").addResourceLocations("classpath:css/");
			registry.addResourceHandler("/images/**").addResourceLocations("classpath:images/");
		}
	}
}
