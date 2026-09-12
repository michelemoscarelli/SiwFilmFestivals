package it.uniroma3.siw.siw_film_festival.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.siw_film_festival.model.Festival;
import it.uniroma3.siw.siw_film_festival.model.Film;
import it.uniroma3.siw.siw_film_festival.repository.FestivalRepository;

@Service
@Transactional
public class FestivalService {

	public static final int DIMENSIONE_PAGINA = 10;

	private final FestivalRepository festivalRepository;

	public FestivalService(FestivalRepository festivalRepository) {
		this.festivalRepository = festivalRepository;
	}

	// Elenco completo, usato per i menu a tendina (es. selezione festival nella pagina di analisi)
	// e per i conteggi in dashboard: non e' una vista con elenco, quindi non va paginato.
	@Transactional(readOnly = true)
	public List<Festival> getFestival() {
		return this.festivalRepository.findAllByOrderByDataInizioDesc();
	}

	// Elenco paginato (10 per pagina), usato dalle viste con elenco (festival/elenco.html pubblica
	// e admin/festival.html).
	@Transactional(readOnly = true)
	public Page<Festival> getFestivalPagina(int pagina) {
		return this.festivalRepository.findAllByOrderByDataInizioDesc(PageRequest.of(pagina - 1, DIMENSIONE_PAGINA));
	}

	@Transactional(readOnly = true)
	public Optional<Festival> getFestival(Long id) {
		return this.festivalRepository.findById(id);
	}

	// I prossimi 3 festival non ancora iniziati, per la sezione "Festival in primo piano" della home.
	@Transactional(readOnly = true)
	public List<Festival> getFestivalInPrimoPiano() {
		return this.festivalRepository.findTop3ByDataInizioGreaterThanEqualOrderByDataInizioAsc(LocalDate.now());
	}

	@Transactional
	public Festival salvaFestival(Festival festival) {
		return this.festivalRepository.save(festival);
	}

	@Transactional
	public void eliminaFestival(Long id) {
		this.festivalRepository.deleteById(id);
	}

	// Caso d'uso admin "associazione film-festival".
	@Transactional
	public void associaFilm(Festival festival, Film film) {
		film.aggiungiFestival(festival);
	}

	// Caso d'uso admin "eliminazione film da un festival" (non elimina il film, solo l'associazione).
	@Transactional
	public void rimuoviFilm(Festival festival, Film film) {
		film.rimuoviFestival(festival);
	}
}
