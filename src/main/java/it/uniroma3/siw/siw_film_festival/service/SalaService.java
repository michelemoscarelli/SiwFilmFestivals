package it.uniroma3.siw.siw_film_festival.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.siw_film_festival.model.Sala;
import it.uniroma3.siw.siw_film_festival.repository.SalaRepository;

@Service
@Transactional
public class SalaService {

	public static final int DIMENSIONE_PAGINA = 10;

	private final SalaRepository salaRepository;

	public SalaService(SalaRepository salaRepository) {
		this.salaRepository = salaRepository;
	}

	// Elenco completo, usato per i menu a tendina (scelta della sala in programma/modifica
	// proiezione) e per il conteggio in dashboard: non e' una vista con elenco, quindi non va paginato.
	@Transactional(readOnly = true)
	public List<Sala> getSale() {
		return this.salaRepository.findAll();
	}

	// Elenco paginato (10 per pagina), usato da admin/sale.html.
	@Transactional(readOnly = true)
	public Page<Sala> getSalePagina(int pagina) {
		return this.salaRepository.findPagina(PageRequest.of(pagina - 1, DIMENSIONE_PAGINA));
	}

	@Transactional(readOnly = true)
	public Optional<Sala> getSala(Long id) {
		return this.salaRepository.findById(id);
	}

	@Transactional
	public Sala salvaSala(Sala sala) {
		return this.salaRepository.save(sala);
	}

	@Transactional
	public void eliminaSala(Long id) {
		this.salaRepository.deleteById(id);
	}
}
