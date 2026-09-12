package it.uniroma3.siw.siw_film_festival.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import it.uniroma3.siw.siw_film_festival.model.Proiezione;

public interface ProiezioneRepository extends JpaRepository<Proiezione, Long> {

	// --- Caso d'uso "programma di un festival con film e sale" ---------------------------------
	// Le tre varianti sotto restituiscono esattamente lo stesso risultato logico (le proiezioni di
	// un festival, ordinate) ma con strategie di fetch diverse: sono usate da AnalisiPerformanceService
	// per confrontare il numero di query SQL eseguite quando si accede a film/sala di ogni proiezione.

	// 1) Default: le relazioni @ManyToOne di Proiezione sono FetchType.LAZY -> ogni accesso a
	//    proiezione.getFilm()/getSala() dopo il caricamento genera una SELECT aggiuntiva (N+1).
	List<Proiezione> findByFestivalIdOrderByDataAscOraAsc(Long festivalId);

	// 2) JOIN FETCH esplicito: un'unica query SQL con i JOIN su film e sala.
	@Query("SELECT p FROM Proiezione p JOIN FETCH p.film JOIN FETCH p.sala "
		+ "WHERE p.festival.id = :festivalId ORDER BY p.data ASC, p.ora ASC")
	List<Proiezione> findByFestivalIdWithJoinFetch(@Param("festivalId") Long festivalId);

	// 3) @EntityGraph dichiarativo: stesso risultato di JOIN FETCH ma dichiarato sull'annotazione.
	@EntityGraph(attributePaths = { "film", "sala" })
	@Query("SELECT p FROM Proiezione p WHERE p.festival.id = :festivalId ORDER BY p.data ASC, p.ora ASC")
	List<Proiezione> findByFestivalIdWithEntityGraph(@Param("festivalId") Long festivalId);

	// --- Controllo disponibilita' sala (vincolo di business) ------------------------------------
	// Le proiezioni della stessa sala nello stesso giorno vengono caricate con il film (per
	// conoscerne la durata) cosi' il controllo di sovrapposizione, che richiede di sommare la
	// durata del film all'orario di inizio, puo' essere fatto in modo semplice lato Java.
	@Query("SELECT p FROM Proiezione p JOIN FETCH p.film "
		+ "WHERE p.sala.id = :salaId AND p.data = :data AND p.stato <> it.uniroma3.siw.siw_film_festival.model.StatoProiezione.CANCELLED")
	List<Proiezione> findBySalaIdAndDataConflitti(@Param("salaId") Long salaId, @Param("data") LocalDate data);

	// Stessa query della precedente, ma esclude una proiezione specifica (usata in fase di modifica).
	@Query("SELECT p FROM Proiezione p JOIN FETCH p.film "
		+ "WHERE p.sala.id = :salaId AND p.data = :data AND p.id <> :proiezioneId "
		+ "AND p.stato <> it.uniroma3.siw.siw_film_festival.model.StatoProiezione.CANCELLED")
	List<Proiezione> findBySalaIdAndDataConflittiEscludendo(@Param("salaId") Long salaId, @Param("data") LocalDate data,
			@Param("proiezioneId") Long proiezioneId);

	List<Proiezione> findAllByOrderByDataAscOraAsc();

	// Usata da ProiezioneService.getProiezioni() (elenco admin): stesso risultato di
	// findAllByOrderByDataAscOraAsc() ma con film e sala gia' caricati in un'unica query, per non
	// avere un accesso N+1 quando la vista legge p.film.titolo/p.sala.nome per ogni riga.
	@Query("SELECT p FROM Proiezione p JOIN FETCH p.film JOIN FETCH p.sala ORDER BY p.data ASC, p.ora ASC")
	List<Proiezione> findAllWithJoinFetchOrderByDataAscOraAsc();

	// Stessa query, paginata (10 per pagina): usata da admin/proiezioni.html. Sicuro combinare
	// JOIN FETCH con Pageable qui perche' film e sala sono relazioni *-to-one (non collection).
	@Query(value = "SELECT p FROM Proiezione p JOIN FETCH p.film JOIN FETCH p.sala ORDER BY p.data ASC, p.ora ASC",
		countQuery = "SELECT COUNT(p) FROM Proiezione p")
	Page<Proiezione> findPaginaWithJoinFetch(Pageable pageable);

	// --- Caso d'uso "ricerca delle proiezioni per data" (bonus sez. 13 del PDF) -----------------
	// Attraversa tutti i festival (non solo uno), quindi carica anche il festival oltre a film e
	// sala per poter mostrare/linkare tutto nella vista di ricerca senza N+1.
	@Query("SELECT p FROM Proiezione p JOIN FETCH p.film JOIN FETCH p.sala JOIN FETCH p.festival "
		+ "WHERE p.data = :data ORDER BY p.ora ASC")
	List<Proiezione> findByDataWithDettagli(@Param("data") LocalDate data);
}
