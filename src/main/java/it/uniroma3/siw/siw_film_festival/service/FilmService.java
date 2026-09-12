package it.uniroma3.siw.siw_film_festival.service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.siw_film_festival.model.Film;
import it.uniroma3.siw.siw_film_festival.repository.FilmRepository;

@Service
@Transactional
public class FilmService {

	public static final int DIMENSIONE_PAGINA = 10;

	// Percorso base di /uploads/locandine: le locandine caricate dall'admin vengono salvate qui su
	// disco e servite via WebConfig, che mappa questa stessa cartella su /uploads/locandine/**.
	// Stesso pattern di CameraService in SIWHotel-definitivo.
	@Value("${app.upload.dir.locandine}")
	private String uploadDir;

	private final FilmRepository filmRepository;

	public FilmService(FilmRepository filmRepository) {
		this.filmRepository = filmRepository;
	}

	// Elenco completo, usato per i menu a tendina (associazione film-festival) e per il conteggio in
	// dashboard: non e' una vista con elenco, quindi non va paginato.
	@Transactional(readOnly = true)
	public List<Film> getFilm() {
		return this.filmRepository.findAll();
	}

	// Elenco paginato (10 per pagina), usato da admin/film.html.
	@Transactional(readOnly = true)
	public Page<Film> getFilmPagina(int pagina) {
		return this.filmRepository.findPagina(PageRequest.of(pagina - 1, DIMENSIONE_PAGINA));
	}

	// Elenco e ricerca dei film per il frontend React (FilmRestController), paginato (10 per
	// pagina) sia con filtro sia senza: senza filtro riusa la query con regista gia' caricato
	// (findPagina), col filtro usa la variante di ricerca per titolo con lo stesso ordinamento.
	@Transactional(readOnly = true)
	public Page<Film> cercaFilmPerTitoloPagina(String titolo, int pagina) {
		PageRequest pageRequest = PageRequest.of(pagina - 1, DIMENSIONE_PAGINA);
		if (titolo == null || titolo.isBlank()) {
			return this.filmRepository.findPagina(pageRequest);
		}
		return this.filmRepository.findByTitoloContainingIgnoreCaseOrderByTitoloAsc(titolo, pageRequest);
	}

	@Transactional(readOnly = true)
	public List<Film> getFilmDiFestival(Long festivalId) {
		return this.filmRepository.findByFestivalId(festivalId);
	}

	@Transactional(readOnly = true)
	public Optional<Film> getFilm(Long id) {
		return this.filmRepository.findById(id);
	}

	// Usato dalla pagina di dettaglio film (regista, festival, proiezioni con sala/festival,
	// recensioni): due query separate con JOIN FETCH invece di una singola findById(id) "nuda",
	// perche' Film.festival e Film.proiezioni sono entrambe collezioni List (bag) e Hibernate non
	// permette il JOIN FETCH di due bag diverse nella stessa query (MultipleBagFetchException).
	// Le due query popolano lo stesso oggetto Film gestito nella persistence context della
	// transazione, quindi il risultato restituito ha gia' tutto cio' che serve alla vista.
	@Transactional(readOnly = true)
	public Film getFilmConDettagli(Long id) {
		Film film = this.filmRepository.findByIdWithProiezioni(id)
			.orElseThrow(() -> new NoSuchElementException("Film non trovato: id " + id));
		this.filmRepository.findByIdWithFestival(id);
		return film;
	}

	@Transactional
	public Film salvaFilm(Film film) {
		return this.filmRepository.save(film);
	}

	// Sovraccarico usato da admin/aggiungi_film e admin/modifica_film: se e' stato caricato un file
	// per la locandina, lo salva su disco e aggiorna il percorso sull'entita', eliminando il file
	// precedente in caso di sostituzione. Se "locandina" e' assente/vuoto (campo facoltativo nel
	// form), si comporta come salvaFilm(film) e lascia la locandina esistente invariata.
	@Transactional
	public Film salvaFilm(Film film, MultipartFile locandina) {
		if (locandina != null && !locandina.isEmpty()) {
			String vecchioPercorso = film.getLocandina();
			String nuovoPercorso = this.salvaFileLocandina(locandina);
			film.setLocandina(nuovoPercorso);
			if (vecchioPercorso != null) {
				this.eliminaFileLocandina(vecchioPercorso);
			}
		}
		return this.filmRepository.save(film);
	}

	@Transactional
	public void eliminaFilm(Long id) {
		this.filmRepository.findById(id).ifPresent(film -> {
			if (film.getLocandina() != null) {
				this.eliminaFileLocandina(film.getLocandina());
			}
		});
		this.filmRepository.deleteById(id);
	}

	// Scrive il MultipartFile su disco con un nome generato (evita collisioni/percorsi arbitrari
	// passati dal client) e restituisce il percorso pubblico da salvare in DB.
	private String salvaFileLocandina(MultipartFile file) {
		try {
			Path cartella = Paths.get(this.uploadDir);
			Files.createDirectories(cartella);

			String nomeOriginale = file.getOriginalFilename();
			String estensione = "";
			// serve per prendere l'estensione del file originale, se presente, e aggiungerla al nome generato
			if (nomeOriginale != null && nomeOriginale.contains(".")) {
				estensione = nomeOriginale.substring(nomeOriginale.lastIndexOf('.'));
			}
			String nomeFile = UUID.randomUUID() + estensione;

			// il file viene scritto su disco nella cartella di upload
			file.transferTo(cartella.resolve(nomeFile));
			
			// il percorso pubblico viene restituito per essere salvato in DB
			return "/uploads/locandine/" + nomeFile;
		} catch (IOException e) {
			throw new UncheckedIOException("Errore durante il salvataggio della locandina", e);
		}
	}

	// Elimina il file fisico corrispondente a un percorso salvato in precedenza. Se il file non
	// esiste piu' (o il percorso non e' uno dei nostri upload) non blocca l'operazione.
	private void eliminaFileLocandina(String percorso) {
		if (percorso == null || !percorso.startsWith("/uploads/locandine/")) {
			return;
		}
		String nomeFile = percorso.substring("/uploads/locandine/".length());
		try {
			Files.deleteIfExists(Paths.get(this.uploadDir).resolve(nomeFile));
		} catch (IOException e) {
			// Non propaghiamo: l'eliminazione dal DB deve comunque andare a buon fine anche se il
			// file su disco risulta gia' mancante o non cancellabile.
		}
	}
}
