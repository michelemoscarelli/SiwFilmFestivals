package it.uniroma3.siw.siw_film_festival.service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.siw_film_festival.dto.admin.RisultatoAnalisiFetch;
import it.uniroma3.siw.siw_film_festival.model.Proiezione;
import it.uniroma3.siw.siw_film_festival.repository.ProiezioneRepository;
import jakarta.persistence.EntityManagerFactory;

/**
 * Script sperimentale di confronto strategie di fetch (sez. 8.2 del PDF), applicato al caso d'uso
 * "caricamento del programma di un festival con film e sale": elenco delle proiezioni di un
 * festival, per ciascuna delle quali si accede sempre a titolo del film e nome della sala (come
 * farebbe la vista Thymeleaf del programma). Confronta:
 * 1) fetch di default (relazioni @ManyToOne LAZY) -> N+1 query
 * 2) JOIN FETCH esplicito nella query JPQL -> 1 query
 * 3) @EntityGraph dichiarativo sul metodo di repository -> 1 query
 * usando le Statistics di Hibernate per contare le query SQL preparate e System.nanoTime() per il tempo.
 */
@Service
public class AnalisiPerformanceService {

	private final ProiezioneRepository proiezioneRepository;
	private final EntityManagerFactory entityManagerFactory;

	public AnalisiPerformanceService(ProiezioneRepository proiezioneRepository, EntityManagerFactory entityManagerFactory) {
		this.proiezioneRepository = proiezioneRepository;
		this.entityManagerFactory = entityManagerFactory;
	}

	// @Transactional (sez. 7 del PDF: "i metodi del Service Layer devono essere opportunamente
	// annotati con @Transactional") tiene aperta la sessione Hibernate per l'intera durata del
	// confronto, incluso l'accesso a p.getFilm()/p.getSala() dentro misura() per la strategia LAZY.
	// Senza questa annotazione il metodo "funzionava per caso" solo quando chiamato da una richiesta
	// HTTP (grazie all'Open Session In View del controller), ma falliva con
	// LazyInitializationException se invocato direttamente, ad esempio da un test JUnit.
	@Transactional(readOnly = true)
	public List<RisultatoAnalisiFetch> confrontaStrategieFetchProgrammaFestival(Long festivalId) {
		List<RisultatoAnalisiFetch> risultati = new ArrayList<>();

		risultati.add(misura(
			"Default (findByFestivalId senza fetch join) - N+1",
			"Le relazioni @ManyToOne di Proiezione (festival, film, sala) sono dichiarate "
				+ "fetch = FetchType.LAZY: la query base carica solo le proiezioni del festival, poi ogni "
				+ "accesso a film o sala (come farebbe la vista Thymeleaf del programma) attiva una SELECT "
				+ "aggiuntiva per riga (N+1 query).",
			() -> this.proiezioneRepository.findByFestivalIdOrderByDataAscOraAsc(festivalId)
		));

		risultati.add(misura(
			"JOIN FETCH esplicito",
			"Una singola query JPQL con JOIN FETCH carica proiezioni, film e sala in un'unica SELECT con JOIN SQL.",
			() -> this.proiezioneRepository.findByFestivalIdWithJoinFetch(festivalId)
		));

		risultati.add(misura(
			"@EntityGraph",
			"Stesso risultato del JOIN FETCH, ma dichiarato in modo dichiarativo con @EntityGraph sul metodo "
				+ "di repository: Hibernate genera comunque un'unica query con i JOIN necessari.",
			() -> this.proiezioneRepository.findByFestivalIdWithEntityGraph(festivalId)
		));

		// Requisito sez. 8.2 del PDF, punto 4: "produrre un output semplice e leggibile sulla
		// console", nello stesso formato dell'esempio riportato nel testo (=== titolo === seguito
		// da strategia/righe caricate/query SQL/tempo per ciascuna strategia).
		this.stampaSuConsole(risultati);

		return risultati;
	}

	private void stampaSuConsole(List<RisultatoAnalisiFetch> risultati) {
		System.out.println();
		System.out.println("=== Test accesso al programma di un festival (film e sale) ===");
		System.out.println();
		int numeroStrategia = 1;
		for (RisultatoAnalisiFetch r : risultati) {
			System.out.println("Strategia " + numeroStrategia + ": " + r.getStrategia());
			System.out.println("Proiezioni caricate: " + r.getNumeroRisultati());
			System.out.println("Query SQL: " + r.getNumeroQuery());
			System.out.println("Tempo: " + Math.round(r.getTempoMs()) + " ms");
			System.out.println();
			numeroStrategia++;
		}
	}

	private RisultatoAnalisiFetch misura(String nomeStrategia, String descrizione, Supplier<List<Proiezione>> operazione) {
		Statistics statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
		statistics.setStatisticsEnabled(true);
		statistics.clear();

		long inizio = System.nanoTime();
		List<Proiezione> proiezioni = operazione.get();

		// Simula l'accesso a film e sala fatto dalla vista Thymeleaf del programma del festival.
		for (Proiezione p : proiezioni) {
			if (p.getFilm() != null) {
				p.getFilm().getTitolo();
			}
			if (p.getSala() != null) {
				p.getSala().getNome();
			}
		}
		long fine = System.nanoTime();

		long numeroQuery = statistics.getPrepareStatementCount();
		double tempoMs = (fine - inizio) / 1_000_000.0;

		return new RisultatoAnalisiFetch(nomeStrategia, descrizione, numeroQuery, tempoMs, proiezioni.size());
	}
}
