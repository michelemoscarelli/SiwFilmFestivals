package it.uniroma3.siw.siw_film_festival.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// Usato sia per l'inserimento sia per la modifica di una recensione da parte dell'utente autore.
public class RecensioneDto {

	@NotBlank(message = "Il testo della recensione e' obbligatorio")
	private String testo;

	@NotNull(message = "Il voto e' obbligatorio")
	@Min(value = 1, message = "Il voto minimo e' 1")
	@Max(value = 10, message = "Il voto massimo e' 10")
	private Integer voto;

	public String getTesto() {
		return testo;
	}

	public void setTesto(String testo) {
		this.testo = testo;
	}

	public Integer getVoto() {
		return voto;
	}

	public void setVoto(Integer voto) {
		this.voto = voto;
	}
}
