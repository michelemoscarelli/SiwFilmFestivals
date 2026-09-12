package it.uniroma3.siw.siw_film_festival.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegistrazioneDto {

	@NotBlank(message = "Il nome e' obbligatorio")
	private String nome;

	@NotBlank(message = "Il cognome e' obbligatorio")
	private String cognome;

	@NotBlank(message = "L'email e' obbligatoria")
	@Email(message = "Email non valida")
	private String email;

	@NotBlank(message = "Lo username e' obbligatorio")
	private String username;

	@NotBlank(message = "La password e' obbligatoria")
	@Size(min = 6, message = "La password deve avere almeno 6 caratteri")
	private String password;

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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
