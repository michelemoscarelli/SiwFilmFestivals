package it.uniroma3.siw.siw_film_festival.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import it.uniroma3.siw.siw_film_festival.model.Festival;

public interface FestivalRepository extends JpaRepository<Festival, Long> {

	// Ordina i festival per data di inizio piu' recente, usato dove serve l'elenco completo
	// (dropdown/select, conteggi in dashboard).
	List<Festival> findAllByOrderByDataInizioDesc();

	// Stessa query, paginata (10 per pagina): usata dalle viste con elenco (pubblica e admin).
	Page<Festival> findAllByOrderByDataInizioDesc(Pageable pageable);

	// I 3 festival non ancora iniziati piu' vicini nel tempo, per la sezione "Festival in primo
	// piano" della home. "Top3" limita il risultato direttamente in query (LIMIT 3).
	List<Festival> findTop3ByDataInizioGreaterThanEqualOrderByDataInizioAsc(LocalDate oggi);
}
