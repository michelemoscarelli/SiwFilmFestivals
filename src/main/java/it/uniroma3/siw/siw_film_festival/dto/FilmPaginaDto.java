package it.uniroma3.siw.siw_film_festival.dto;

import java.util.List;

import org.springframework.data.domain.Page;

import it.uniroma3.siw.siw_film_festival.model.Film;

// Risposta paginata di /rest/film, consumata dal componente React FilmList per mostrare i
// controlli di paginazione (10 film per pagina) anche sui risultati filtrati per titolo.
public class FilmPaginaDto {

	private final List<FilmDto> contenuto;
	private final int paginaCorrente;
	private final int totalePagine;
	private final long totaleElementi;

	public FilmPaginaDto(Page<Film> pagina, int paginaCorrente) {
		this.contenuto = pagina.getContent().stream().map(FilmDto::new).toList();
		this.paginaCorrente = paginaCorrente;
		this.totalePagine = Math.max(pagina.getTotalPages(), 1);
		this.totaleElementi = pagina.getTotalElements();
	}

	public List<FilmDto> getContenuto() {
		return contenuto;
	}

	public int getPaginaCorrente() {
		return paginaCorrente;
	}

	public int getTotalePagine() {
		return totalePagine;
	}

	public long getTotaleElementi() {
		return totaleElementi;
	}
}
