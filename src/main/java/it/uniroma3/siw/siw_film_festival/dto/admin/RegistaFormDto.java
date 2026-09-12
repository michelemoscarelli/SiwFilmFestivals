package it.uniroma3.siw.siw_film_festival.dto.admin;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RegistaFormDto {

	@NotBlank(message = "Il nome e' obbligatorio")
	private String nome;

	@NotBlank(message = "Il cognome e' obbligatorio")
	private String cognome;

	// Vedi il commento su FestivalFormDto.dataInizio: senza @DateTimeFormat(iso=DATE) il campo
	// non risulterebbe pre-compilato in una pagina di modifica (formato non compatibile con
	// <input type="date">).
	@NotNull(message = "La data di nascita e' obbligatoria")
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate dataNascita;

	@NotBlank(message = "La nazionalita' e' obbligatoria")
	private String nazionalita;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCognome() {
		return cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	public LocalDate getDataNascita() {
		return dataNascita;
	}

	public void setDataNascita(LocalDate dataNascita) {
		this.dataNascita = dataNascita;
	}

	public String getNazionalita() {
		return nazionalita;
	}

	public void setNazionalita(String nazionalita) {
		this.nazionalita = nazionalita;
	}
}
