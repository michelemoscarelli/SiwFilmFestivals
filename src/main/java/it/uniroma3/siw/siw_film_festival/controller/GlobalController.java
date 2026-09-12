package it.uniroma3.siw.siw_film_festival.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import it.uniroma3.siw.siw_film_festival.model.Utente;
import it.uniroma3.siw.siw_film_festival.service.UtenteService;

// Inietta l'utente autenticato (se presente) in tutti i Model delle viste Thymeleaf, cosi' ogni
// template puo' leggere ${utente} senza che ogni controller lo passi esplicitamente. La
// risoluzione vera e propria (login classico via UserDetails, o login Google via OidcUser) e' in
// UtenteService.getUtenteAutenticato: qui va usata solo per RENDERIZZARE le viste, mai come
// parametro "@ModelAttribute" di un metodo @GetMapping/@PostMapping altrove (vedi il commento su
// UtenteService.getUtenteAutenticato per il perche').
@ControllerAdvice
public class GlobalController {

	@Autowired
	private UtenteService utenteService;

	@ModelAttribute("utente")
	public Utente getUtenteLoggato(Authentication authentication) {
		return this.utenteService.getUtenteAutenticato(authentication);
	}
}
