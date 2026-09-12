package it.uniroma3.siw.siw_film_festival.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.siw_film_festival.model.Utente;
import it.uniroma3.siw.siw_film_festival.repository.UtenteRepository;

@Service
@Transactional
public class UtenteService {

	public static final int DIMENSIONE_PAGINA = 10;

	private final UtenteRepository utenteRepository;

	public UtenteService(UtenteRepository utenteRepository) {
		this.utenteRepository = utenteRepository;
	}

	@Transactional(readOnly = true)
	public Utente getUtenteByEmail(String email) {
		return this.utenteRepository.findByEmail(email);
	}

	@Transactional(readOnly = true)
	public Utente getUtenteByUsername(String username) {
		return this.utenteRepository.findByCredenzialiUsername(username);
	}

	// Risolve l'Utente applicativo a partire dall'Authentication di Spring Security (sia login
	// classico via UserDetails/JdbcUserDetailsManager, sia login Google via OidcUser).
	//
	// Va usato al posto di far dichiarare a un metodo @GetMapping/@PostMapping un parametro
	// "@ModelAttribute("utente") Utente utente": quel pattern, anche se "utente" e' gia' presente
	// nel Model (messo li' da GlobalController), fa comunque passare l'oggetto attraverso il
	// WebDataBinder di Spring, che prova a fare il bind di QUALSIASI request param o path variable
	// con un nome che corrisponde a un setter dell'entita' - incluso un {id} nell'URL che in realta'
	// si riferisce a tutt'altra risorsa (es. l'id di una recensione), sovrascrivendo Utente.setId(...)
	// e corrompendo l'identificatore dell'entita' gestita da Hibernate (JpaSystemException
	// "Identifier ... was altered" al momento del flush). Authentication invece non passa mai dal
	// data binder: e' un argomento speciale risolto da Spring Security, sempre sicuro da usare.
	@Transactional(readOnly = true)
	public Utente getUtenteAutenticato(Authentication authentication) {
		if (authentication == null || !authentication.isAuthenticated()) {
			return null;
		}
		Object principal = authentication.getPrincipal();
		if (principal instanceof OidcUser oidcUser) {
			return this.getUtenteByEmail(oidcUser.getEmail());
		}
		if (principal instanceof UserDetails userDetails) {
			return this.getUtenteByUsername(userDetails.getUsername());
		}
		return null;
	}

	@Transactional(readOnly = true)
	public Optional<Utente> getUtente(Long id) {
		return this.utenteRepository.findById(id);
	}

	// Elenco completo, usato solo per il conteggio in dashboard: non e' una vista con elenco,
	// quindi non va paginato.
	@Transactional(readOnly = true)
	public List<Utente> getUtenti() {
		return this.utenteRepository.findAll();
	}

	// Elenco paginato (10 per pagina), usato da admin/utenti.html.
	@Transactional(readOnly = true)
	public Page<Utente> getUtentiPagina(int pagina) {
		return this.utenteRepository.findPagina(PageRequest.of(pagina - 1, DIMENSIONE_PAGINA));
	}

	@Transactional
	public Utente salvaUtente(Utente utente) {
		return this.utenteRepository.save(utente);
	}

	@Transactional
	public void eliminaUtente(Long id) {
		this.utenteRepository.deleteById(id);
	}
}
