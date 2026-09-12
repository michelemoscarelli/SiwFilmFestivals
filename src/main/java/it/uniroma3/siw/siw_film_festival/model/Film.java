package it.uniroma3.siw.siw_film_festival.model;

import java.util.ArrayList;
import java.util.Collection;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Film {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String titolo;

	@Column(nullable = false)
	private Integer anno;

	@Column(nullable = false)
	private Integer durata;

	@Column(nullable = false)
	private String genere;

	@Column(nullable = false)
	private String paeseProduzione;

	// Percorso pubblico della locandina caricata dall'admin (es. "/uploads/locandine/<uuid>.jpg"),
	// servito come risorsa statica da WebConfig. Nullable: non tutti i film hanno una locandina.
	private String locandina;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	private Regista regista;

	// Lato proprietario della relazione molti-a-molti con Festival: un film partecipa a piu' festival.
	@ManyToMany(cascade = CascadeType.MERGE)
	@JoinTable(name = "film_festival",
		joinColumns = @JoinColumn(name = "film_id"),
		inverseJoinColumns = @JoinColumn(name = "festival_id"))
	private Collection<Festival> festival = new ArrayList<>();

	@OneToMany(mappedBy = "film")
	private Collection<Proiezione> proiezioni = new ArrayList<>();

	@OneToMany(mappedBy = "film", cascade = CascadeType.REMOVE)
	private Collection<Recensione> recensioni = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

	public String getLocandina() {
		return locandina;
	}

	public void setLocandina(String locandina) {
		this.locandina = locandina;
	}

	public Regista getRegista() {
		return regista;
	}

	public void setRegista(Regista regista) {
		this.regista = regista;
	}

	public Collection<Festival> getFestival() {
		return festival;
	}

	// Controllo di non-duplicazione: essendo "festival" una List (non un Set, per poter fare
	// JOIN FETCH senza problemi di ordinamento), un secondo click su "associa" senza questo
	// controllo aggiungerebbe una riga duplicata nella tabella ponte film_festival.
	public void aggiungiFestival(Festival festival) {
		if (!this.festival.contains(festival)) {
			this.festival.add(festival);
		}
	}

	// removeIf (non remove) per ripulire in un colpo solo eventuali associazioni duplicate residue
	// create prima del controllo aggiunto in aggiungiFestival().
	public void rimuoviFestival(Festival festival) {
		this.festival.removeIf(f -> f.equals(festival));
	}

	public Collection<Proiezione> getProiezioni() {
		return proiezioni;
	}

	public Collection<Recensione> getRecensioni() {
		return recensioni;
	}

	@Override
	public int hashCode() {
		return (this.id == null) ? 0 : this.id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		Film other = (Film) obj;
		return this.id != null && this.id.equals(other.id);
	}
}
