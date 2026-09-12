package it.uniroma3.siw.siw_film_festival.dto.admin;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// Usato sia per la creazione sia per la modifica di un festival lato admin.
public class FestivalFormDto {

	@NotBlank(message = "Il nome e' obbligatorio")
	private String nome;

	@NotNull(message = "L'anno e' obbligatorio")
	private Integer anno;

	@NotBlank(message = "La citta' e' obbligatoria")
	private String citta;

	// @DateTimeFormat(iso = DATE) forza il pattern ISO "yyyy-MM-dd" sia in lettura che in
	// scrittura: senza questa annotazione Spring formatta/parsifica il LocalDate secondo il
	// locale di sistema (es. it-IT -> "dd/MM/yy"), che pero' non e' il formato che un
	// <input type="date"> HTML5 richiede - il campo risulterebbe sempre vuoto in una pagina di
	// modifica (il browser scarta silenziosamente un value in formato non ISO) e il valore
	// digitato non verrebbe piu' interpretato correttamente al submit.
	@NotNull(message = "La data di inizio e' obbligatoria")
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate dataInizio;

	@NotNull(message = "La data di fine e' obbligatoria")
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate dataFine;

	private String descrizione;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Integer getAnno() {
		return anno;
	}

	public void setAnno(Integer anno) {
		this.anno = anno;
	}

	public String getCitta() {
		return citta;
	}

	public void setCitta(String citta) {
		this.citta = citta;
	}

	public LocalDate getDataInizio() {
		return dataInizio;
	}

	public void setDataInizio(LocalDate dataInizio) {
		this.dataInizio = dataInizio;
	}

	public LocalDate getDataFine() {
		return dataFine;
	}

	public void setDataFine(LocalDate dataFine) {
		this.dataFine = dataFine;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}
}
