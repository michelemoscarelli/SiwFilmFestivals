package it.uniroma3.siw.siw_film_festival.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CambioPasswordDto {

	@NotBlank(message = "Inserisci la password attuale")
	private String passwordAttuale;

	@NotBlank(message = "Inserisci la nuova password")
	@Size(min = 6, message = "La nuova password deve avere almeno 6 caratteri")
	private String passwordNuova;

	@NotBlank(message = "Conferma la nuova password")
	private String confermaPasswordNuova;

	public String getPasswordAttuale() {
		return passwordAttuale;
	}

	public void setPasswordAttuale(String passwordAttuale) {
		this.passwordAttuale = passwordAttuale;
	}

	public String getPasswordNuova() {
		return passwordNuova;
	}

	public void setPasswordNuova(String passwordNuova) {
		this.passwordNuova = passwordNuova;
	}

	public String getConfermaPasswordNuova() {
		return confermaPasswordNuova;
	}

	public void setConfermaPasswordNuova(String confermaPasswordNuova) {
		this.confermaPasswordNuova = confermaPasswordNuova;
	}
}
