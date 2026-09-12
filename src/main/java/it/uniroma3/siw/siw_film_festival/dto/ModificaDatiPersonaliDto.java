package it.uniroma3.siw.siw_film_festival.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class ModificaDatiPersonaliDto {

	@NotBlank(message = "Il nome e' obbligatorio")
	private String nome;

	@NotBlank(message = "Il cognome e' obbligatorio")
	private String cognome;

	@NotBlank(message = "L'email e' obbligatoria")
	@Email(message = "Email non valida")
	private String email;

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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
