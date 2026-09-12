package it.uniroma3.siw.siw_film_festival.dto.admin;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.format.annotation.DateTimeFormat;

import it.uniroma3.siw.siw_film_festival.model.StatoProiezione;
import jakarta.validation.constraints.NotNull;

public class ProiezioneFormDto {

	@NotNull(message = "Il film e' obbligatorio")
	private Long filmId;

	@NotNull(message = "La sala e' obbligatoria")
	private Long salaId;

	// Non obbligatorio: alla creazione di una proiezione lo stato e' sempre SCHEDULED (il form di
	// aggiunta non mostra questo campo); e' significativo solo nel form di modifica, dove l'admin
	// puo' cambiarlo esplicitamente (es. per segnarla COMPLETED dopo la proiezione, o CANCELLED).
	private StatoProiezione stato;

	// Vedi il commento su FestivalFormDto.dataInizio: senza @DateTimeFormat il campo non
	// risulterebbe pre-compilato in una pagina di modifica ne' interpretato correttamente al
	// submit, perche' <input type="date">/<input type="time"> richiedono rispettivamente il
	// formato ISO "yyyy-MM-dd" e "HH:mm", non il formato locale-dipendente usato di default.
	@NotNull(message = "La data e' obbligatoria")
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate data;

	@NotNull(message = "L'ora e' obbligatoria")
	@DateTimeFormat(pattern = "HH:mm")
	private LocalTime ora;

	public Long getFilmId() {
		return filmId;
	}

	public void setFilmId(Long filmId) {
		this.filmId = filmId;
	}

	public Long getSalaId() {
		return salaId;
	}

	public void setSalaId(Long salaId) {
		this.salaId = salaId;
	}

	public LocalDate getData() {
		return data;
	}

	public void setData(LocalDate data) {
		this.data = data;
	}

	public LocalTime getOra() {
		return ora;
	}

	public void setOra(LocalTime ora) {
		this.ora = ora;
	}

	public StatoProiezione getStato() {
		return stato;
	}

	public void setStato(StatoProiezione stato) {
		this.stato = stato;
	}
}
