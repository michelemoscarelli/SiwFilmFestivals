package it.uniroma3.siw.siw_film_festival.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.siw_film_festival.model.Regista;
import it.uniroma3.siw.siw_film_festival.repository.RegistaRepository;

@Service
@Transactional
public class RegistaService {

	public static final int DIMENSIONE_PAGINA = 10;

	private final RegistaRepository registaRepository;

	public RegistaService(RegistaRepository registaRepository) {
		this.registaRepository = registaRepository;
	}

	// Elenco completo, usato per i menu a tendina (scelta del regista in aggiungi/modifica film) e
	// per il conteggio in dashboard: non e' una vista con elenco, quindi non va paginato.
	@Transactional(readOnly = true)
	public List<Regista> getRegisti() {
		return this.registaRepository.findAll();
	}

	// Elenco paginato (10 per pagina), usato da admin/registi.html.
	@Transactional(readOnly = true)
	public Page<Regista> getRegistiPagina(int pagina) {
		return this.registaRepository.findPagina(PageRequest.of(pagina - 1, DIMENSIONE_PAGINA));
	}

	@Transactional(readOnly = true)
	public Optional<Regista> getRegista(Long id) {
		return this.registaRepository.findById(id);
	}

	@Transactional
	public Regista salvaRegista(Regista regista) {
		return this.registaRepository.save(regista);
	}

	@Transactional
	public void eliminaRegista(Long id) {
		this.registaRepository.deleteById(id);
	}
}
