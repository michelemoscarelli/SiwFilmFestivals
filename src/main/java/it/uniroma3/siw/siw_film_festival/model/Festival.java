package it.uniroma3.siw.siw_film_festival.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;

@Entity
public class Festival {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String nome;

	@Column(nullable = false)
	private Integer anno;

	@Column(nullable = false)
	private String citta;

	@Column(nullable = false)
	private LocalDate dataInizio;

	@Column(nullable = false)
	private LocalDate dataFine;

	@Column(length = 2000)
	private String descrizione;

	// Lato inverso della relazione molti-a-molti: il lato proprietario (con @JoinTable) e' Film,
	// stesso pattern di Squadra (proprietaria) / Torneo (mappedBy) in FootballSiw.
	@ManyToMany(mappedBy = "festival")
	private Collection<Film> film = new ArrayList<>();

	@OneToMany(mappedBy = "festival")
	private Collection<Proiezione> proiezioni = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

	public Collection<Film> getFilm() {
		return film;
	}

	public Collection<Proiezione> getProiezioni() {
		return proiezioni;
	}

	@Override
	public int hashCode() {
		return (this.id == null) ? 0 : this.id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		Festival other = (Festival) obj;
		return this.id != null && this.id.equals(other.id);
	}
}
