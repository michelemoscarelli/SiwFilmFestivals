package it.uniroma3.siw.siw_film_festival.controller;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.uniroma3.siw.siw_film_festival.dto.RecensioneDto;
import it.uniroma3.siw.siw_film_festival.model.Film;
import it.uniroma3.siw.siw_film_festival.model.Recensione;
import it.uniroma3.siw.siw_film_festival.model.Utente;
import it.uniroma3.siw.siw_film_festival.service.FilmService;
import it.uniroma3.siw.siw_film_festival.service.RecensioneService;
import it.uniroma3.siw.siw_film_festival.service.UtenteService;
import jakarta.validation.Valid;

// Casi d'uso per utenti registrati: inserimento/modifica/eliminazione di una propria recensione.
//
// L'utente autenticato viene sempre recuperato da UtenteService.getUtenteAutenticato(authentication),
// MAI con un parametro "@ModelAttribute("utente") Utente utente": quest'ultimo, pur leggendo un
// oggetto gia' presente nel Model (messo li' da GlobalController), lo fa comunque passare dal
// WebDataBinder di Spring, che prova a fare il bind di request param/path variable con lo stesso
// nome di un setter dell'entita' - incluso il path variable {id} di queste stesse rotte, che si
// riferisce all'id della RECENSIONE ma finiva per essere scritto anche su Utente.setId(...),
// corrompendo l'identificatore dell'entita' gestita da Hibernate (JpaSystemException "Identifier
// of an instance of 'Utente' was altered" al momento del salvataggio).
@Controller
public class RecensioneController {

	@Autowired
	private RecensioneService recensioneService;

	@Autowired
	private FilmService filmService;

	@Autowired
	private UtenteService utenteService;

	@PostMapping("/film/{filmId}/recensioni")
	public String inserisciRecensione(@PathVariable("filmId") Long filmId,
			@Valid @ModelAttribute("recensioneDto") RecensioneDto dto, BindingResult bindingResult,
			Authentication authentication, RedirectAttributes redirectAttributes) {

		Film film = this.filmService.getFilm(filmId)
			.orElseThrow(() -> new NoSuchElementException("Film non trovato: id " + filmId));
		Utente utente = this.utenteService.getUtenteAutenticato(authentication);
		if (utente == null) {
			return "redirect:/logout";
		}

		if (bindingResult.hasErrors()) {
			redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.recensioneDto", bindingResult);
			redirectAttributes.addFlashAttribute("recensioneDto", dto);
			return "redirect:/film/" + filmId;
		}

		try {
			this.recensioneService.inserisciRecensione(film, utente, dto.getTesto(), dto.getVoto());
		} catch (IllegalStateException e) {
			redirectAttributes.addFlashAttribute("erroreRecensione", e.getMessage());
		}
		return "redirect:/film/" + filmId;
	}

	@GetMapping("/recensioni/{id}/modifica")
	public String mostraFormModifica(@PathVariable("id") Long id, Authentication authentication, Model model) {
		Recensione recensione = this.recensioneService.getRecensione(id)
			.orElseThrow(() -> new NoSuchElementException("Recensione non trovata: id " + id));
		this.verificaAutore(recensione, this.utenteService.getUtenteAutenticato(authentication));

		RecensioneDto dto = new RecensioneDto();
		dto.setTesto(recensione.getTesto());
		dto.setVoto(recensione.getVoto());

		model.addAttribute("recensione", recensione);
		model.addAttribute("recensioneDto", dto);
		return "film/modifica_recensione";
	}

	@PostMapping("/recensioni/{id}/modifica")
	public String modificaRecensione(@PathVariable("id") Long id,
			@Valid @ModelAttribute("recensioneDto") RecensioneDto dto, BindingResult bindingResult,
			Authentication authentication, Model model) {

		Recensione recensione = this.recensioneService.getRecensione(id)
			.orElseThrow(() -> new NoSuchElementException("Recensione non trovata: id " + id));
		this.verificaAutore(recensione, this.utenteService.getUtenteAutenticato(authentication));

		if (bindingResult.hasErrors()) {
			model.addAttribute("recensione", recensione);
			return "film/modifica_recensione";
		}

		this.recensioneService.modificaRecensione(recensione, dto.getTesto(), dto.getVoto());
		return "redirect:/film/" + recensione.getFilm().getId();
	}

	@PostMapping("/recensioni/{id}/elimina")
	public String eliminaRecensione(@PathVariable("id") Long id, Authentication authentication) {
		Recensione recensione = this.recensioneService.getRecensione(id)
			.orElseThrow(() -> new NoSuchElementException("Recensione non trovata: id " + id));
		this.verificaAutore(recensione, this.utenteService.getUtenteAutenticato(authentication));

		Long filmId = recensione.getFilm().getId();
		this.recensioneService.eliminaRecensione(id);
		return "redirect:/film/" + filmId;
	}

	private void verificaAutore(Recensione recensione, Utente utente) {
		if (utente == null || recensione.getAutore() == null || !recensione.getAutore().getId().equals(utente.getId())) {
			throw new AccessDeniedException("Puoi modificare o eliminare solo le tue recensioni.");
		}
	}
}
