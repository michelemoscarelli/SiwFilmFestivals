package it.uniroma3.siw.siw_film_festival.config;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Espone la cartella esterna app.upload.dir.locandine (fuori dal classpath, vedi
// application.properties - cosi' le immagini caricate sopravvivono a un rebuild) come risorse
// statiche raggiungibili da /uploads/locandine/**. Stesso pattern di SIWHotel-definitivo per le
// immagini delle camere.
@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Value("${app.upload.dir.locandine}")
	private String uploadDir;

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
		registry.addResourceHandler("/uploads/locandine/**")
			.addResourceLocations("file:" + uploadPath + "/");
	}
}
