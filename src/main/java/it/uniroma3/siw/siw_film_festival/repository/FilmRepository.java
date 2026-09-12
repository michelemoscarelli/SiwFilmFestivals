package it.uniroma3.siw.siw_film_festival.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import it.uniroma3.siw.siw_film_festival.model.Film;

public interface FilmRepository extends JpaRepository<Film, Long> {

	// Elenco film paginato (10 per pagina) con regista gia' caricato (evita N+1 su film.regista.nome
	// in admin/film.html), ordinato per titolo; usato da admin/film.html.
	@Query(value = "SELECT f FROM Film f JOIN FETCH f.regista ORDER BY f.titolo ASC",
		countQuery = "SELECT COUNT(f) FROM Film f")
	Page<Film> findPagina(Pageable pageable);

	// Query derivata: ricerca per titolo (bonus sez. 13 PDF), usata da FilmRestController.
	List<Film> findByTitoloContainingIgnoreCase(String titolo);

	// Stessa ricerca, paginata (10 per pagina): usata dall'elenco React (FilmRestController), che
	// pagina anche i risultati filtrati per titolo, non solo l'elenco completo.
	Page<Film> findByTitoloContainingIgnoreCaseOrderByTitoloAsc(String titolo, Pageable pageable);

	// Film partecipanti a un festival, con relativo regista gia' caricato (evita N+1 in dettaglio festival).
	@Query("SELECT DISTINCT f FROM Film f JOIN FETCH f.regista JOIN f.festival fest WHERE fest.id = :festivalId ORDER BY f.titolo ASC")
	List<Film> findByFestivalId(@Param("festivalId") Long festivalId);

	// Le due query seguenti alimentano FilmService.getFilmConDettagli(id): Film.festival e
	// Film.proiezioni sono entrambe collezioni List (bag), quindi non possono essere caricate con
	// JOIN FETCH nella stessa query (Hibernate lancerebbe MultipleBagFetchException) e vanno
	// separate in due SELECT distinte sullo stesso id. LEFT JOIN FETCH (non JOIN FETCH semplice) su
	// entrambe le collezioni: un film senza ancora nessuna proiezione programmata o non ancora
	// associato a un festival deve comunque comparire, non sparire per via di un inner join.
	@Query("SELECT f FROM Film f LEFT JOIN FETCH f.proiezioni p LEFT JOIN FETCH p.sala LEFT JOIN FETCH p.festival WHERE f.id = :id")
	Optional<Film> findByIdWithProiezioni(@Param("id") Long id);

	@Query("SELECT f FROM Film f JOIN FETCH f.regista LEFT JOIN FETCH f.festival WHERE f.id = :id")
	Optional<Film> findByIdWithFestival(@Param("id") Long id);
}
