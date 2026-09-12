package it.uniroma3.siw.siw_film_festival.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import it.uniroma3.siw.siw_film_festival.model.Sala;

public interface SalaRepository extends JpaRepository<Sala, Long> {

	// Elenco sale paginato (10 per pagina), ordinato per nome; usato da admin/sale.html.
	@Query(value = "SELECT s FROM Sala s ORDER BY s.nome ASC",
		countQuery = "SELECT COUNT(s) FROM Sala s")
	Page<Sala> findPagina(Pageable pageable);
}
