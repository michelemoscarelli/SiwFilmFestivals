package it.uniroma3.siw.siw_film_festival.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import it.uniroma3.siw.siw_film_festival.model.Film;
import it.uniroma3.siw.siw_film_festival.model.Recensione;
import it.uniroma3.siw.siw_film_festival.model.Utente;

public interface RecensioneRepository extends JpaRepository<Recensione, Long> {

	@Query("SELECT r FROM Recensione r JOIN FETCH r.autore WHERE r.film.id = :filmId ORDER BY r.data DESC")
	List<Recensione> findByFilmId(@Param("filmId") Long filmId);

	// Recensioni scritte da un utente, con il film gia' caricato (evita N+1 sui link ai film
	// nella scheda utente pubblica). Usata da UtenteController.
	@Query("SELECT r FROM Recensione r JOIN FETCH r.film WHERE r.autore.id = :autoreId ORDER BY r.data DESC")
	List<Recensione> findByAutoreId(@Param("autoreId") Long autoreId);

	// Usata per far rispettare il vincolo "massimo una recensione per utente per film".
	Optional<Recensione> findByFilmAndAutore(Film film, Utente autore);

}
