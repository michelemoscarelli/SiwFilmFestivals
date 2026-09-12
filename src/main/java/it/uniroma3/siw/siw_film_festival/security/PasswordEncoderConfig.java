package it.uniroma3.siw.siw_film_festival.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

// Bean separato da SecurityConfiguration di proposito: CustomOidcUserService dipende da
// PasswordEncoder, e SecurityConfiguration dipende (nel metodo securityFilterChain) da
// CustomOidcUserService per l'oauth2Login. Se il bean PasswordEncoder restasse dentro
// SecurityConfiguration si creerebbe un ciclo che Spring rifiuta a runtime.
@Configuration
public class PasswordEncoderConfig {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
