package it.uniroma3.siw.siw_film_festival.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CambioUsernameDto {

	@NotBlank(message = "Il nuovo username e' obbligatorio")
	@Size(min = 3, message = "Lo username deve avere almeno 3 caratteri")
	private String nuovoUsername;

	public String getNuovoUsername() {
		return nuovoUsername;
	}

	public void setNuovoUsername(String nuovoUsername) {
		this.nuovoUsername = nuovoUsername;
	}
}
