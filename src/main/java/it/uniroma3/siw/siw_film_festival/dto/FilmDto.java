package it.uniroma3.siw.siw_film_festival.dto;

import it.uniroma3.siw.siw_film_festival.model.Film;

// DTO piatto per l'endpoint REST /rest/film usato dal frontend React: espone solo campi scalari,
// deliberatamente SENZA il regista (scelta esplicita per ridurre complessita', vedi brief), cosi'
// da restare disaccoppiato dal caso d'uso dello script di analisi N+1 (che lavora su Proiezione).
public class FilmDto {

	private final Long id;
	private final String titolo;
	private final Integer anno;
	private final Integer durata;
	private final String genere;
	private final String paeseProduzione;

	public FilmDto(Film film) {
		this.id = film.getId();
		this.titolo = film.getTitolo();
		this.anno = film.getAnno();
		this.durata = film.getDurata();
		this.genere = film.getGenere();
		this.paeseProduzione = film.getPaeseProduzione();
	}

	public Long getId() {
		return id;
	}

	public String getTitolo() {
		return titolo;
	}

	public Integer getAnno() {
		return anno;
	}

	public Integer getDurata() {
		return durata;
	}

	public String getGenere() {
		return genere;
	}

	public String getPaeseProduzione() {
		return paeseProduzione;
	}
}
