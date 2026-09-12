package it.uniroma3.siw.siw_film_festival.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.siw_film_festival.model.Festival;
import it.uniroma3.siw.siw_film_festival.model.Film;
import it.uniroma3.siw.siw_film_festival.model.Proiezione;
import it.uniroma3.siw.siw_film_festival.model.Sala;
import it.uniroma3.siw.siw_film_festival.model.StatoProiezione;
import it.uniroma3.siw.siw_film_festival.repository.ProiezioneRepository;

@Service
@Transactional
public class ProiezioneService {

	public static final int DIMENSIONE_PAGINA = 10;

	private final ProiezioneRepository proiezioneRepository;

	public ProiezioneService(ProiezioneRepository proiezioneRepository) {
		this.proiezioneRepository = proiezioneRepository;
	}

	// Elenco completo con film e sala gia' caricati in un'unica query (JOIN FETCH), per evitare
	// l'N+1 che si avrebbe accedendo a p.film/p.sala per ogni riga.
	@Transactional(readOnly = true)
	public List<Proiezione> getProiezioni() {
		return this.proiezioneRepository.findAllWithJoinFetchOrderByDataAscOraAsc();
	}

	// Elenco paginato (10 per pagina), con lo stesso fetch ottimizzato: usato da admin/proiezioni.html.
	@Transactional(readOnly = true)
	public Page<Proiezione> getProiezioniPagina(int pagina) {
		return this.proiezioneRepository.findPaginaWithJoinFetch(PageRequest.of(pagina - 1, DIMENSIONE_PAGINA));
	}

	@Transactional(readOnly = true)
	public Optional<Proiezione> getProiezione(Long id) {
		return this.proiezioneRepository.findById(id);
	}

	// Programma di un festival, con film e sala gia' caricati (JOIN FETCH): festival/dettaglio.html
	// e festival/programma.html accedono a p.film.titolo/p.sala.nome per ogni riga, quindi qui serve
	// il fetch ottimizzato (l'unica versione a fetch di default resta quella dedicata all'analisi
	// N+1 in AnalisiPerformanceService, che la usa apposta come baseline).
	@Transactional(readOnly = true)
	public List<Proiezione> getProgrammaFestival(Long festivalId) {
		return this.proiezioneRepository.findByFestivalIdWithJoinFetch(festivalId);
	}

	// Caso d'uso pubblico "ricerca delle proiezioni per data" (bonus sez. 13 del PDF): tutte le
	// proiezioni di una certa data, di qualunque festival, con film/sala/festival gia' caricati.
	@Transactional(readOnly = true)
	public List<Proiezione> getProiezioniPerData(LocalDate data) {
		return this.proiezioneRepository.findByDataWithDettagli(data);
	}

	// Verifica se una proiezione nella sala indicata, in una certa data/ora, si sovrapporrebbe a
	// una proiezione gia' programmata (la durata della proiezione e' quella del film proiettato).
	@Transactional(readOnly = true)
	public boolean esisteConflittoSala(Sala sala, LocalDate data, LocalTime ora, Film film, Long proiezioneDaEscludere) {
		List<Proiezione> candidate = (proiezioneDaEscludere == null)
			? this.proiezioneRepository.findBySalaIdAndDataConflitti(sala.getId(), data)
			: this.proiezioneRepository.findBySalaIdAndDataConflittiEscludendo(sala.getId(), data, proiezioneDaEscludere);

		LocalDateTime nuovoInizio = LocalDateTime.of(data, ora);
		LocalDateTime nuovoFine = nuovoInizio.plusMinutes(film.getDurata());

		for (Proiezione esistente : candidate) {
			LocalDateTime inizioEsistente = LocalDateTime.of(esistente.getData(), esistente.getOra());
			LocalDateTime fineEsistente = inizioEsistente.plusMinutes(esistente.getFilm().getDurata());
			boolean sovrapposte = nuovoInizio.isBefore(fineEsistente) && nuovoFine.isAfter(inizioEsistente);
			if (sovrapposte) {
				return true;
			}
		}
		return false;
	}

	// Isolamento SERIALIZABLE per evitare che due richieste concorrenti programmino la stessa sala
	// nello stesso intervallo temporale (stesso pattern di PrenotazioneService/PartitaService).
	@Transactional(isolation = Isolation.SERIALIZABLE)
	public Proiezione programmaProiezione(Festival festival, Film film, Sala sala, LocalDate data, LocalTime ora) {
		if (this.esisteConflittoSala(sala, data, ora, film, null)) {
			throw new IllegalStateException("La sala e' gia' occupata da un'altra proiezione in questo orario.");
		}
		Proiezione proiezione = new Proiezione();
		proiezione.setFestival(festival);
		proiezione.setFilm(film);
		proiezione.setSala(sala);
		proiezione.setData(data);
		proiezione.setOra(ora);
		proiezione.setStato(StatoProiezione.SCHEDULED);
		return this.proiezioneRepository.save(proiezione);
	}

	// Il controllo di sovrapposizione sala viene saltato quando il nuovo stato e' CANCELLED: una
	// proiezione cancellata non occupa piu' la sala (la query di conflitto la esclude comunque per
	// le ALTRE proiezioni, ma non ha senso bloccare la modifica se e' proprio questa che si sta
	// cancellando).
	@Transactional(isolation = Isolation.SERIALIZABLE)
	public Proiezione modificaProiezione(Proiezione proiezione, Film film, Sala sala, LocalDate data, LocalTime ora,
			StatoProiezione stato) {
		if (stato != StatoProiezione.CANCELLED && this.esisteConflittoSala(sala, data, ora, film, proiezione.getId())) {
			throw new IllegalStateException("La sala e' gia' occupata da un'altra proiezione in questo orario.");
		}
		proiezione.setFilm(film);
		proiezione.setSala(sala);
		proiezione.setData(data);
		proiezione.setOra(ora);
		proiezione.setStato(stato);
		return this.proiezioneRepository.save(proiezione);
	}

	@Transactional
	public void cancellaProiezione(Proiezione proiezione) {
		proiezione.setStato(StatoProiezione.CANCELLED);
		this.proiezioneRepository.save(proiezione);
	}

	@Transactional
	public void eliminaProiezione(Long id) {
		this.proiezioneRepository.deleteById(id);
	}
}
