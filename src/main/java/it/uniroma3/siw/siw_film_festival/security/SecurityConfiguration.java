package it.uniroma3.siw.siw_film_festival.security;

import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

	private final DataSource dataSource;

	public SecurityConfiguration(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Bean
	public UserDetailsService userDetailsService() {
		JdbcUserDetailsManager manager = new JdbcUserDetailsManager(dataSource);
		manager.setUsersByUsernameQuery("SELECT username, password, 1 as enabled FROM credenziali WHERE username=?");
		manager.setAuthoritiesByUsernameQuery("SELECT username, ruolo FROM credenziali WHERE username=?");
		return manager;
	}

	// Consente al frontend React in sviluppo (Vite su localhost:5173) di chiamare le API REST.
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(List.of("http://localhost:5173"));
		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
		configuration.setAllowedHeaders(List.of("*"));

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/rest/**", configuration);
		return source;
	}

	@Bean
	protected SecurityFilterChain configure(final HttpSecurity httpSecurity,
			final CustomOidcUserService customOidcUserService,
			final Optional<ClientRegistrationRepository> clientRegistrationRepository) throws Exception {

		httpSecurity.cors(Customizer.withDefaults());

		httpSecurity.authorizeHttpRequests(authorize -> {
			authorize.requestMatchers(HttpMethod.GET, "/admin/**").hasAnyAuthority(Ruolo.ADMIN_ROLE.name());
			authorize.requestMatchers(HttpMethod.POST, "/admin/**").hasAnyAuthority(Ruolo.ADMIN_ROLE.name());

			authorize.requestMatchers(HttpMethod.GET,
				"/", "/festival", "/festival/*", "/festival/*/film", "/festival/*/programma",
				"/film", "/film/*", "/film/*/recensioni", "/registi/*", "/utente/*", "/proiezioni",
				"/login", "/registrazione",
				"/oauth2/**", "/login/oauth2/**",
				"/css/**", "/images/**", "/favicon.ico", "/uploads/locandine/**",
				"/rest/film", "/rest/film/**", "/film-app/**", "/error",
				// Documentazione API REST : pagina
				// interattiva /swagger-ui.html + lo spec JSON che la alimenta.
				"/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**"
			).permitAll();

			authorize.requestMatchers(HttpMethod.POST, "/registrazione", "/login").permitAll();
			authorize.anyRequest().authenticated();
		});

		httpSecurity.formLogin(form -> {
			form.loginPage("/login").permitAll();
			form.successHandler((request, response, authentication) -> {
				boolean isAdmin = authentication.getAuthorities().stream()
					.anyMatch(authority -> authority.getAuthority().equals(Ruolo.ADMIN_ROLE.name()));
				if (isAdmin) {
					response.sendRedirect("/admin/dashboard");
				} else {
					response.sendRedirect("/area-personale");
				}
			});
			form.failureUrl("/login?error=true");
		});

		// Login con Google attivato solo se e' presente una registrazione OAuth2 client valida
		// (application-secrets.properties): senza credenziali Google l'app si avvia comunque e
		// resta disponibile il solo login classico username/password.
		if (clientRegistrationRepository.isPresent()) {
			httpSecurity.oauth2Login(oauth2 -> {
				oauth2.loginPage("/login");
				oauth2.userInfoEndpoint(userInfo -> userInfo.oidcUserService(customOidcUserService));
				oauth2.successHandler((request, response, authentication) -> {
					boolean isAdmin = authentication.getAuthorities().stream()
						.anyMatch(authority -> authority.getAuthority().equals(Ruolo.ADMIN_ROLE.name()));
					if (isAdmin) {
						response.sendRedirect("/admin/dashboard");
					} else {
						response.sendRedirect("/area-personale");
					}
				});
				oauth2.failureUrl("/login?error=true");
			});
		}

		httpSecurity.logout(logout -> {
			logout.logoutUrl("/logout");
			logout.logoutSuccessUrl("/");
			logout.invalidateHttpSession(true);
			logout.deleteCookies("JSESSIONID");
			logout.clearAuthentication(true);
			logout.permitAll();
		});

		return httpSecurity.build();
	}
}
