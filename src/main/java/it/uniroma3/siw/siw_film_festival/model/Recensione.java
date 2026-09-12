package it.uniroma3.siw.siw_film_festival.model;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

// Vincolo "massimo una recensione per utente per film" garantito sia a livello di schema
// (unique constraint su autore_id + film_id) sia nel service (controllo esplicito prima del save).
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "autore_id", "film_id" }))
public class Recensione {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 2000)
	private String testo;

	@Column(nullable = false)
	private Integer voto;

	@Column(nullable = false)
	private LocalDate data;

	// Nullable per retrocompatibilita': le recensioni inserite prima che questo campo esistesse
	// non hanno un orario salvato. Usare getOraEffettiva() per la visualizzazione (mostra 00:00
	// quando l'orario non e' presente), non questo getter direttamente nei template.
	private LocalTime ora;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "film_id", nullable = false)
	private Film film;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "autore_id", nullable = false)
	private Utente autore;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

	public LocalDate getData() {
		return data;
	}

	public void setData(LocalDate data) {
		this.data = data;
	}

	public LocalTime getOra() {
		return ora;
	}

	public void setOra(LocalTime ora) {
		this.ora = ora;
	}

	// Da usare nei template al posto di getOra(): le recensioni salvate prima dell'introduzione di
	// questo campo hanno ora == null, e per retrocompatibilita' vanno mostrate con orario 00:00
	// invece di lasciare un buco in pagina.
	public LocalTime getOraEffettiva() {
		return this.ora != null ? this.ora : LocalTime.MIDNIGHT;
	}

	public Film getFilm() {
		return film;
	}

	public void setFilm(Film film) {
		this.film = film;
	}

	public Utente getAutore() {
		return autore;
	}

	public void setAutore(Utente autore) {
		this.autore = autore;
	}

	@Override
	public int hashCode() {
		return (this.id == null) ? 0 : this.id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		Recensione other = (Recensione) obj;
		return this.id != null && this.id.equals(other.id);
	}
}
