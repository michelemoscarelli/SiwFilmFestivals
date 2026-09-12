package it.uniroma3.siw.siw_film_festival.security;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.siw_film_festival.model.Credenziali;
import it.uniroma3.siw.siw_film_festival.model.Utente;
import it.uniroma3.siw.siw_film_festival.service.CredenzialiService;
import it.uniroma3.siw.siw_film_festival.service.UtenteService;
import jakarta.transaction.Transactional;

// Login con Google (OAuth2/OIDC), opzionale: al primo accesso con un account Google mai visto
// prima, crea automaticamente Credenziali (password casuale, l'utente entrera' sempre via Google)
// e il relativo Utente anagrafico, con ruolo USER_ROLE.
@Service
public class CustomOidcUserService extends OidcUserService {

	@Autowired
	private UtenteService utenteService;

	@Autowired
	private CredenzialiService credenzialiService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	@Transactional
	public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
		OidcUser oidcUser = super.loadUser(userRequest);

		String email = oidcUser.getEmail();
		if (email == null) {
			throw new OAuth2AuthenticationException("Email non disponibile dall'account Google");
		}

		Utente utente = this.utenteService.getUtenteByEmail(email);
		if (utente == null) {
			utente = this.registraNuovoUtenteDaGoogle(oidcUser, email);
		}

		Collection<GrantedAuthority> authorities =
			List.of(new SimpleGrantedAuthority(utente.getCredenziali().getRuolo().name()));

		return new DefaultOidcUser(authorities, oidcUser.getIdToken(), oidcUser.getUserInfo());
	}

	private Utente registraNuovoUtenteDaGoogle(OidcUser oidcUser, String email) {
		Credenziali credenziali = new Credenziali();
		credenziali.setUsername(email);
		credenziali.setPassword(this.passwordEncoder.encode(UUID.randomUUID().toString()));
		credenziali.setRuolo(Ruolo.USER_ROLE);
		this.credenzialiService.saveCredenziali(credenziali);

		Utente nuovo = new Utente();
		nuovo.setNome(oidcUser.getGivenName() != null ? oidcUser.getGivenName() : email.split("@")[0]);
		nuovo.setCognome(oidcUser.getFamilyName() != null ? oidcUser.getFamilyName() : "");
		nuovo.setEmail(email);
		nuovo.setCredenziali(credenziali);

		this.utenteService.salvaUtente(nuovo);
		return nuovo;
	}
}
