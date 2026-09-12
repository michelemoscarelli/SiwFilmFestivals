package it.uniroma3.siw.siw_film_festival.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import it.uniroma3.siw.siw_film_festival.model.Credenziali;

public interface CredenzialiRepository extends JpaRepository<Credenziali, Long> {

	Credenziali findByUsername(String username);
}
