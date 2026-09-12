package it.uniroma3.siw.siw_film_festival.controller;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import it.uniroma3.siw.siw_film_festival.dto.RecensioneDto;
import it.uniroma3.siw.siw_film_festival.model.Film;
import it.uniroma3.siw.siw_film_festival.service.FilmService;
import it.uniroma3.siw.siw_film_festival.service.RecensioneService;

@Controller
public class FilmController {

	@Autowired
	private FilmService filmService;

	@Autowired
	private RecensioneService recensioneService;

	// Elenco film: renderizzato lato React (vedi FilmRestController), qui solo la pagina host.
	@GetMapping("/film")
	public String elencoFilm() {
		return "film/elenco";
	}

	// Caso d'uso pubblico: dettaglio film, con regista, festival, proiezioni e recensioni.
	// Usa getFilmConDettagli (JOIN FETCH su tutte le relazioni lette dalla vista) invece di
	// getFilm(id), per evitare l'N+1 che si avrebbe scorrendo film.proiezioni nel template.
	@GetMapping("/film/{id}")
	public String dettaglioFilm(@PathVariable("id") Long id, Model model) {
		Film film = this.filmService.getFilmConDettagli(id);
		model.addAttribute("film", film);
		model.addAttribute("recensioni", this.recensioneService.getRecensioniDiFilm(id));
		if (!model.containsAttribute("recensioneDto")) {
			model.addAttribute("recensioneDto", new RecensioneDto());
		}
		return "film/dettaglio";
	}

	// Caso d'uso pubblico: recensioni di un film (vista dedicata).
	@GetMapping("/film/{id}/recensioni")
	public String recensioniDiFilm(@PathVariable("id") Long id, Model model) {
		Film film = this.filmService.getFilm(id)
			.orElseThrow(() -> new NoSuchElementException("Film non trovato: id " + id));
		model.addAttribute("film", film);
		model.addAttribute("recensioni", this.recensioneService.getRecensioniDiFilm(id));
		return "film/recensioni";
	}
}
