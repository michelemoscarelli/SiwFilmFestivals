package it.uniroma3.siw.siw_film_festival.controller;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.siw_film_festival.dto.CambioPasswordDto;
import it.uniroma3.siw.siw_film_festival.dto.CambioUsernameDto;
import it.uniroma3.siw.siw_film_festival.dto.ModificaDatiPersonaliDto;
import it.uniroma3.siw.siw_film_festival.model.Credenziali;
import it.uniroma3.siw.siw_film_festival.model.Utente;
import it.uniroma3.siw.siw_film_festival.security.Ruolo;
import it.uniroma3.siw.siw_film_festival.service.CredenzialiService;
import it.uniroma3.siw.siw_film_festival.service.RecensioneService;
import it.uniroma3.siw.siw_film_festival.service.UtenteService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
public class UtenteController {

	@Autowired
	private UtenteService utenteService;

	@Autowired
	private RecensioneService recensioneService;

	@Autowired
	private CredenzialiService credenzialiService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	// Utente recuperato da Authentication (mai da un parametro "@ModelAttribute("utente") Utente"
	// - vedi il commento in RecensioneController per il perche' e' pericoloso).
	@GetMapping("/area-personale")
	public String areaPersonale(Authentication authentication, Model model) {
		Utente utente = this.utenteService.getUtenteAutenticato(authentication);
		if (utente == null) {
			return "redirect:/logout";
		}
		model.addAttribute("recensioni", this.recensioneService.getRecensioniDiUtente(utente.getId()));
		return "utente/area_personale";
	}

	// Caso d'uso pubblico: scheda di un utente registrato, simile all'area personale ma senza
	// l'email e senza le azioni di modifica/eliminazione (quelle restano private, sopra).
	@GetMapping("/utente/{id}")
	public String profiloPubblico(@PathVariable("id") Long id, Model model) {
		Utente utente = this.utenteService.getUtente(id)
			.orElseThrow(() -> new NoSuchElementException("Utente non trovato: id " + id));
		model.addAttribute("utenteProfilo", utente);
		model.addAttribute("recensioni", this.recensioneService.getRecensioniDiUtente(id));
		return "utente/profilo_pubblico";
	}

	// --- Profilo (dati personali, username, password) - "menu" raggiungibile dall'area personale --
	//
	// Tutti i metodi sotto verificano che l'Utente risolto da Authentication esista ancora: se
	// l'account e' stato eliminato (o se lo username in sessione non corrisponde piu' a nessun
	// utente, es. dopo un cambio username che non ha aggiornato correttamente il contesto di
	// sicurezza) redirigono a /logout invece di andare in NullPointerException. cambioUsername
	// inoltre aggiorna esplicitamente il SecurityContext dopo il salvataggio, cosi' la sessione
	// corrente resta valida con il nuovo username senza bisogno di un nuovo login.

	@GetMapping("/area-personale/profilo")
	public String profilo(Authentication authentication, Model model) {
		Utente utente = this.utenteService.getUtenteAutenticato(authentication);
		if (utente == null) {
			return "redirect:/logout";
		}
		model.addAttribute("utenteProfilo", utente);
		return "utente/profilo";
	}

	@GetMapping("/area-personale/profilo/modifica-dati-personali")
	public String formModificaDatiPersonali(Authentication authentication, Model model) {
		Utente utente = this.utenteService.getUtenteAutenticato(authentication);
		if (utente == null) {
			return "redirect:/logout";
		}
		if (!model.containsAttribute("modificaDatiPersonaliDto")) {
			ModificaDatiPersonaliDto dto = new ModificaDatiPersonaliDto();
			dto.setNome(utente.getNome());
			dto.setCognome(utente.getCognome());
			dto.setEmail(utente.getEmail());
			model.addAttribute("modificaDatiPersonaliDto", dto);
		}
		return "utente/modifica_dati_personali";
	}

	@PostMapping("/area-personale/profilo/modifica-dati-personali")
	public String modificaDatiPersonali(Authentication authentication,
			@Valid @ModelAttribute("modificaDatiPersonaliDto") ModificaDatiPersonaliDto dto, BindingResult bindingResult,
			Model model) {
		if (bindingResult.hasErrors()) {
			return "utente/modifica_dati_personali";
		}
		Utente utente = this.utenteService.getUtenteAutenticato(authentication);
		if (utente == null) {
			return "redirect:/logout";
		}

		Utente altroConStessaEmail = this.utenteService.getUtenteByEmail(dto.getEmail());
		if (altroConStessaEmail != null && !altroConStessaEmail.getId().equals(utente.getId())) {
			model.addAttribute("erroreEmail", "Questa email e' gia' usata da un altro account.");
			return "utente/modifica_dati_personali";
		}

		utente.setNome(dto.getNome());
		utente.setCognome(dto.getCognome());
		utente.setEmail(dto.getEmail());
		this.utenteService.salvaUtente(utente);

		return "redirect:/area-personale/profilo?successo=Dati aggiornati";
	}

	@GetMapping("/area-personale/profilo/cambio-username")
	public String formCambioUsername(Authentication authentication, Model model) {
		Utente utente = this.utenteService.getUtenteAutenticato(authentication);
		if (utente == null) {
			return "redirect:/logout";
		}
		if (!model.containsAttribute("cambioUsernameDto")) {
			CambioUsernameDto dto = new CambioUsernameDto();
			dto.setNuovoUsername(utente.getCredenziali().getUsername());
			model.addAttribute("cambioUsernameDto", dto);
		}
		return "utente/cambio_username";
	}

	@PostMapping("/area-personale/profilo/cambio-username")
	public String cambioUsername(Authentication authentication,
			@Valid @ModelAttribute("cambioUsernameDto") CambioUsernameDto dto, BindingResult bindingResult, Model model) {
		if (bindingResult.hasErrors()) {
			return "utente/cambio_username";
		}
		Utente utente = this.utenteService.getUtenteAutenticato(authentication);
		if (utente == null) {
			return "redirect:/logout";
		}
		Credenziali credenziali = utente.getCredenziali();

		Credenziali altreConStessoUsername = this.credenzialiService.getCredenzialiByUsername(dto.getNuovoUsername());
		if (altreConStessoUsername != null && !altreConStessoUsername.getId().equals(credenziali.getId())) {
			model.addAttribute("erroreUsername", "Questo username e' gia' in uso.");
			return "utente/cambio_username";
		}

		credenziali.setUsername(dto.getNuovoUsername());
		this.credenzialiService.saveCredenziali(credenziali);

		// FONDAMENTALE: lo username e' la chiave con cui Spring Security identifica l'utente nella
		// sessione. Se non aggiorniamo qui anche il SecurityContext, la sessione corrente continua
		// a "cercare" il vecchio username ad ogni richiesta (es. in GlobalController/UtenteService.
		// getUtenteAutenticato) finche' non si rifa' login - e nel frattempo ogni pagina che legge
		// l'utente loggato smette di funzionare. Questo aggiornamento vale solo per il login
		// classico (UserDetails/JdbcUserDetailsManager): gli utenti Google (OidcUser) sono
		// identificati per email, che il cambio username non tocca.
		if (authentication.getPrincipal() instanceof UserDetails) {
			UserDetails nuovoUserDetails = User.withUsername(credenziali.getUsername())
				.password(credenziali.getPassword())
				.authorities(credenziali.getRuolo().name())
				.build();
			Authentication nuovaAuthentication = new UsernamePasswordAuthenticationToken(
				nuovoUserDetails, authentication.getCredentials(), nuovoUserDetails.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(nuovaAuthentication);
		}

		return "redirect:/area-personale/profilo?successo=Username aggiornato";
	}

	@GetMapping("/area-personale/profilo/cambio-password")
	public String formCambioPassword(Model model) {
		if (!model.containsAttribute("cambioPasswordDto")) {
			model.addAttribute("cambioPasswordDto", new CambioPasswordDto());
		}
		return "utente/cambio_password";
	}

	@PostMapping("/area-personale/profilo/cambio-password")
	public String cambioPassword(Authentication authentication,
			@Valid @ModelAttribute("cambioPasswordDto") CambioPasswordDto dto, BindingResult bindingResult, Model model) {
		if (bindingResult.hasErrors()) {
			return "utente/cambio_password";
		}
		if (!dto.getPasswordNuova().equals(dto.getConfermaPasswordNuova())) {
			model.addAttribute("erroreConferma", "Le password inserite non coincidono.");
			return "utente/cambio_password";
		}

		Utente utente = this.utenteService.getUtenteAutenticato(authentication);
		if (utente == null) {
			return "redirect:/logout";
		}
		Credenziali credenziali = utente.getCredenziali();

		if (!this.passwordEncoder.matches(dto.getPasswordAttuale(), credenziali.getPassword())) {
			model.addAttribute("erroreAttuale", "La password attuale non e' corretta.");
			return "utente/cambio_password";
		}

		credenziali.setPassword(this.passwordEncoder.encode(dto.getPasswordNuova()));
		this.credenzialiService.saveCredenziali(credenziali);

		return "redirect:/area-personale/profilo?successo=Password aggiornata";
	}

	// Auto-eliminazione dell'account da parte dell'utente stesso (come in SIWHotel). Un admin non
	// puo' farlo da qui: si bloccherebbe fuori da solo, stesso motivo per cui AdminUtenteController
	// impedisce l'auto-eliminazione dal pannello di amministrazione.
	@PostMapping("/area-personale/profilo/elimina")
	public String eliminaProprioAccount(Authentication authentication, HttpServletRequest request) {
		Utente utente = this.utenteService.getUtenteAutenticato(authentication);
		if (utente == null) {
			return "redirect:/logout";
		}
		if (utente.getCredenziali().getRuolo() == Ruolo.ADMIN_ROLE) {
			return "redirect:/area-personale/profilo?erroreEliminazione=Un amministratore non puo' eliminare il proprio account.";
		}

		this.utenteService.eliminaUtente(utente.getId());

		// Logout forzato: l'utente non esiste piu' nel DB, quindi la sessione va distrutta subito,
		// altrimenti ogni richiesta successiva proverebbe a ri-risolverlo (vedi GlobalController) e
		// otterrebbe una sessione "stale" invece di essere reindirizzata pulita al login.
		SecurityContextHolder.clearContext();
		request.getSession().invalidate();
		return "redirect:/";
	}
}
