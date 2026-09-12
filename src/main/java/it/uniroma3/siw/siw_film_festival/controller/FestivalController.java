package it.uniroma3.siw.siw_film_festival.controller;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import it.uniroma3.siw.siw_film_festival.model.Festival;
import it.uniroma3.siw.siw_film_festival.service.FestivalService;
import it.uniroma3.siw.siw_film_festival.service.FilmService;
import it.uniroma3.siw.siw_film_festival.service.ProiezioneService;

@Controller
public class FestivalController {

	@Autowired
	private FestivalService festivalService;

	@Autowired
	private FilmService filmService;

	@Autowired
	private ProiezioneService proiezioneService;

	// Caso d'uso pubblico: elenco festival, paginato (10 per pagina).
	@GetMapping("/festival")
	public String elencoFestival(@RequestParam(value = "pagina", defaultValue = "1") int pagina, Model model) {
		Page<Festival> paginaFestival = this.festivalService.getFestivalPagina(pagina);
		model.addAttribute("elencoFestival", paginaFestival.getContent());
		model.addAttribute("totaleFestival", paginaFestival.getTotalElements());
		model.addAttribute("paginaCorrente", pagina);
		model.addAttribute("totalePagine", Math.max(paginaFestival.getTotalPages(), 1));
		return "festival/elenco";
	}

	// Caso d'uso pubblico: dettaglio festival, con accesso a film e proiezioni.
	@GetMapping("/festival/{id}")
	public String dettaglioFestival(@PathVariable("id") Long id, Model model) {
		Festival festival = this.festivalService.getFestival(id)
			.orElseThrow(() -> new NoSuchElementException("Festival non trovato: id " + id));
		model.addAttribute("festival", festival);
		model.addAttribute("filmPartecipanti", this.filmService.getFilmDiFestival(id));
		model.addAttribute("programma", this.proiezioneService.getProgrammaFestival(id));
		return "festival/dettaglio";
	}

	// Caso d'uso pubblico: film partecipanti a un festival (vista dedicata).
	@GetMapping("/festival/{id}/film")
	public String filmDiFestival(@PathVariable("id") Long id, Model model) {
		Festival festival = this.festivalService.getFestival(id)
			.orElseThrow(() -> new NoSuchElementException("Festival non trovato: id " + id));
		model.addAttribute("festival", festival);
		model.addAttribute("filmPartecipanti", this.filmService.getFilmDiFestival(id));
		return "festival/film";
	}

	// Caso d'uso pubblico: programma delle proiezioni di un festival.
	@GetMapping("/festival/{id}/programma")
	public String programmaFestival(@PathVariable("id") Long id, Model model) {
		Festival festival = this.festivalService.getFestival(id)
			.orElseThrow(() -> new NoSuchElementException("Festival non trovato: id " + id));
		model.addAttribute("festival", festival);
		model.addAttribute("programma", this.proiezioneService.getProgrammaFestival(id));
		return "festival/programma";
	}
}
