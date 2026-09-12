package it.uniroma3.siw.siw_film_festival.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.siw_film_festival.model.Credenziali;
import it.uniroma3.siw.siw_film_festival.repository.CredenzialiRepository;

@Service
@Transactional
public class CredenzialiService {

	private final CredenzialiRepository credenzialiRepository;

	public CredenzialiService(CredenzialiRepository credenzialiRepository) {
		this.credenzialiRepository = credenzialiRepository;
	}

	@Transactional(readOnly = true)
	public Credenziali getCredenzialiByUsername(String username) {
		return this.credenzialiRepository.findByUsername(username);
	}

	@Transactional
	public Credenziali saveCredenziali(Credenziali credenziali) {
		return this.credenzialiRepository.save(credenziali);
	}
}
