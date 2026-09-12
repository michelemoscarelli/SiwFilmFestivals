package it.uniroma3.siw.siw_film_festival.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.uniroma3.siw.siw_film_festival.dto.FilmPaginaDto;
import it.uniroma3.siw.siw_film_festival.model.Film;
import it.uniroma3.siw.siw_film_festival.service.FilmService;

// Sola lettura, consumato dal frontend React (elenco e ricerca dei film, paginati). Restituisce
// sempre FilmPaginaDto/FilmDto, mai le entity direttamente, per evitare cicli di serializzazione
// sulle collezioni bidirezionali.
@RestController
@RequestMapping("/rest/film")
public class FilmRestController {

	@Autowired
	private FilmService filmService;

	@GetMapping
	public FilmPaginaDto getFilm(@RequestParam(value = "titolo", required = false) String titolo,
			@RequestParam(value = "pagina", defaultValue = "1") int pagina) {
		Page<Film> risultato = this.filmService.cercaFilmPerTitoloPagina(titolo, pagina);
		return new FilmPaginaDto(risultato, pagina);
	}
}
