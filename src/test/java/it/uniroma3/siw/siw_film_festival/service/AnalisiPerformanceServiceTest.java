package it.uniroma3.siw.siw_film_festival.service;

import static org.junit.jupiter.api.Assumptions.assumeFalse;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import it.uniroma3.siw.siw_film_festival.model.Festival;
import it.uniroma3.siw.siw_film_festival.repository.FestivalRepository;

// Script sperimentale richiesto dalla sez. 8.2 del PDF ("un semplice test Java, un test JUnit o un
// componente eseguibile all'interno dell'applicazione Spring Boot"): eseguibile durante la prova
// orale con un semplice "Run Test", stampa su console il confronto tra le strategie di fetch sul
// caso d'uso "caricamento del programma di un festival con film e sale" (vedi
// AnalisiPerformanceService per il dettaglio delle query e la spiegazione di ciascuna strategia).
// Non fa asserzioni sui tempi (il PDF chiede di osservare/discutere il comportamento, non un
// benchmark rigoroso): il test "fallisce" solo se lo script non riesce proprio a girare.
@SpringBootTest
class AnalisiPerformanceServiceTest {

	@Autowired
	private AnalisiPerformanceService analisiPerformanceService;

	@Autowired
	private FestivalRepository festivalRepository;

	@Test
	void confrontaStrategieFetchProgrammaFestival() {
		List<Festival> festival = this.festivalRepository.findAll();
		assumeFalse(festival.isEmpty(), "Nessun festival in database: avviare l'app almeno una volta "
			+ "cosi' il DataLoader popola i dati di esempio, poi rieseguire il test.");

		Long festivalId = festival.get(0).getId();
		this.analisiPerformanceService.confrontaStrategieFetchProgrammaFestival(festivalId);
	}
}
