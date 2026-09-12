package it.uniroma3.siw.siw_film_festival.dto.admin;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class FilmFormDto {

	@NotBlank(message = "Il titolo e' obbligatorio")
	private String titolo;

	@NotNull(message = "L'anno e' obbligatorio")
	private Integer anno;

	@NotNull(message = "La durata e' obbligatoria")
	private Integer durata;

	@NotBlank(message = "Il genere e' obbligatorio")
	private String genere;

	@NotBlank(message = "Il paese di produzione e' obbligatorio")
	private String paeseProduzione;

	@NotNull(message = "Il regista e' obbligatorio")
	private Long registaId;

	// Facoltativo: file immagine caricato dall'admin per la locandina (niente URL testuale, vedi
	// FilmService.salvaFilm/salvaFileLocandina). La validazione "e' davvero un'immagine" e' fatta
	// nel controller (stesso pattern di isImmagineValida in SIWHotel), non con un'annotazione qui,
	// perche' Bean Validation non ha un vincolo pronto per il contenuto di un MultipartFile.
	private MultipartFile locandina;

	public String getTitolo() {
		return titolo;
	}

	public void setTitolo(String titolo) {
		this.titolo = titolo;
	}

	public Integer getAnno() {
		return anno;
	}

	public void setAnno(Integer anno) {
		this.anno = anno;
	}

	public Integer getDurata() {
		return durata;
	}

	public void setDurata(Integer durata) {
		this.durata = durata;
	}

	public String getGenere() {
		return genere;
	}

	public void setGenere(String genere) {
		this.genere = genere;
	}

	public String getPaeseProduzione() {
		return paeseProduzione;
	}

	public void setPaeseProduzione(String paeseProduzione) {
		this.paeseProduzione = paeseProduzione;
	}

	public Long getRegistaId() {
		return registaId;
	}

	public void setRegistaId(Long registaId) {
		this.registaId = registaId;
	}

	public MultipartFile getLocandina() {
		return locandina;
	}

	public void setLocandina(MultipartFile locandina) {
		this.locandina = locandina;
	}
}
