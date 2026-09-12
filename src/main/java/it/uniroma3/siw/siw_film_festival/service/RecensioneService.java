package it.uniroma3.siw.siw_film_festival.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.siw_film_festival.model.Film;
import it.uniroma3.siw.siw_film_festival.model.Recensione;
import it.uniroma3.siw.siw_film_festival.model.Utente;
import it.uniroma3.siw.siw_film_festival.repository.RecensioneRepository;

@Service
@Transactional
public class RecensioneService {

	private final RecensioneRepository recensioneRepository;

	public RecensioneService(RecensioneRepository recensioneRepository) {
		this.recensioneRepository = recensioneRepository;
	}

	@Transactional(readOnly = true)
	public List<Recensione> getRecensioniDiFilm(Long filmId) {
		return this.recensioneRepository.findByFilmId(filmId);
	}

	@Transactional(readOnly = true)
	public Optional<Recensione> getRecensione(Long id) {
		return this.recensioneRepository.findById(id);
	}

	// Recensioni scritte da un utente (con il film gia' caricato), usate sia dall'area personale
	// sia dalla scheda utente pubblica (/utente/{id}).
	@Transactional(readOnly = true)
	public List<Recensione> getRecensioniDiUtente(Long utenteId) {
		return this.recensioneRepository.findByAutoreId(utenteId);
	}

	@Transactional(readOnly = true)
	public boolean haGiaRecensito(Film film, Utente autore) {
		return this.recensioneRepository.findByFilmAndAutore(film, autore).isPresent();
	}

	// Vincolo di business "massimo una recensione per utente per film": controllo esplicito prima
	// del salvataggio, oltre al vincolo unique a livello di schema su (autore_id, film_id).
	@Transactional
	public Recensione inserisciRecensione(Film film, Utente autore, String testo, Integer voto) {
		if (this.haGiaRecensito(film, autore)) {
			throw new IllegalStateException("Hai gia' scritto una recensione per questo film.");
		}
		Recensione recensione = new Recensione();
		recensione.setFilm(film);
		recensione.setAutore(autore);
		recensione.setTesto(testo);
		recensione.setVoto(voto);
		recensione.setData(LocalDate.now());
		recensione.setOra(LocalTime.now());
		return this.recensioneRepository.save(recensione);
	}

	@Transactional
	public Recensione modificaRecensione(Recensione recensione, String testo, Integer voto) {
		recensione.setTesto(testo);
		recensione.setVoto(voto);
		return this.recensioneRepository.save(recensione);
	}

	@Transactional
	public void eliminaRecensione(Long id) {
		this.recensioneRepository.deleteById(id);
	}
}
