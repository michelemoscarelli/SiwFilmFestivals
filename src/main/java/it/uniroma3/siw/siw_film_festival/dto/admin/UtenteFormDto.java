package it.uniroma3.siw.siw_film_festival.dto.admin;

import it.uniroma3.siw.siw_film_festival.security.Ruolo;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// Usato dall'admin per modificare i dati anagrafici e il ruolo di un utente. Non tocca username
// o password: quelle restano cose che solo l'utente stesso cambia dalla propria area personale.
public class UtenteFormDto {

	@NotBlank(message = "Il nome e' obbligatorio")
	private String nome;

	@NotBlank(message = "Il cognome e' obbligatorio")
	private String cognome;

	@NotBlank(message = "L'email e' obbligatoria")
	@Email(message = "Email non valida")
	private String email;

	@NotNull(message = "Il ruolo e' obbligatorio")
	private Ruolo ruolo;

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

	public Ruolo getRuolo() {
		return ruolo;
	}

	public void setRuolo(Ruolo ruolo) {
		this.ruolo = ruolo;
	}
}
