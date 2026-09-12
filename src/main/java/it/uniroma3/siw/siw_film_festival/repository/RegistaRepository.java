package it.uniroma3.siw.siw_film_festival.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import it.uniroma3.siw.siw_film_festival.model.Regista;

public interface RegistaRepository extends JpaRepository<Regista, Long> {

	// Elenco registi paginato (10 per pagina), ordinato per cognome/nome; usato da admin/registi.html.
	@Query(value = "SELECT r FROM Regista r ORDER BY r.cognome ASC, r.nome ASC",
		countQuery = "SELECT COUNT(r) FROM Regista r")
	Page<Regista> findPagina(Pageable pageable);
}
