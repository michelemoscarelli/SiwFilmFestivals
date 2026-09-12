package it.uniroma3.siw.siw_film_festival.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import it.uniroma3.siw.siw_film_festival.model.Utente;

public interface UtenteRepository extends JpaRepository<Utente, Long> {

	Utente findByEmail(String email);

	Utente findByCredenzialiUsername(String username);

	// Elenco utenti paginato (10 per pagina), ordinato per cognome/nome; usato da admin/utenti.html.
	// JOIN FETCH sulle credenziali: la vista mostra anche username/ruolo per ogni riga.
	@Query(value = "SELECT u FROM Utente u JOIN FETCH u.credenziali ORDER BY u.cognome ASC, u.nome ASC",
		countQuery = "SELECT COUNT(u) FROM Utente u")
	Page<Utente> findPagina(Pageable pageable);
}
