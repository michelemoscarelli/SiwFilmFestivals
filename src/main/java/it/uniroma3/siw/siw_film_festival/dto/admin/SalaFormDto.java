package it.uniroma3.siw.siw_film_festival.dto.admin;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SalaFormDto {

	@NotBlank(message = "Il nome e' obbligatorio")
	private String nome;

	@NotBlank(message = "L'indirizzo e' obbligatorio")
	private String indirizzo;

	@NotNull(message = "La capienza e' obbligatoria")
	@Min(value = 1, message = "La capienza deve essere almeno 1")
	private Integer capienza;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getIndirizzo() {
		return indirizzo;
	}

	public void setIndirizzo(String indirizzo) {
		this.indirizzo = indirizzo;
	}

	public Integer getCapienza() {
		return capienza;
	}

	public void setCapienza(Integer capienza) {
		this.capienza = capienza;
	}
}
