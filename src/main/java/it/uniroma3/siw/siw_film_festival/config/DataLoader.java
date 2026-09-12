package it.uniroma3.siw.siw_film_festival.config;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.siw_film_festival.model.Credenziali;
import it.uniroma3.siw.siw_film_festival.model.Festival;
import it.uniroma3.siw.siw_film_festival.model.Film;
import it.uniroma3.siw.siw_film_festival.model.Proiezione;
import it.uniroma3.siw.siw_film_festival.model.Regista;
import it.uniroma3.siw.siw_film_festival.model.Recensione;
import it.uniroma3.siw.siw_film_festival.model.Sala;
import it.uniroma3.siw.siw_film_festival.model.StatoProiezione;
import it.uniroma3.siw.siw_film_festival.model.Utente;
import it.uniroma3.siw.siw_film_festival.repository.CredenzialiRepository;
import it.uniroma3.siw.siw_film_festival.repository.FestivalRepository;
import it.uniroma3.siw.siw_film_festival.repository.FilmRepository;
import it.uniroma3.siw.siw_film_festival.repository.ProiezioneRepository;
import it.uniroma3.siw.siw_film_festival.repository.RegistaRepository;
import it.uniroma3.siw.siw_film_festival.repository.RecensioneRepository;
import it.uniroma3.siw.siw_film_festival.repository.SalaRepository;
import it.uniroma3.siw.siw_film_festival.repository.UtenteRepository;
import it.uniroma3.siw.siw_film_festival.security.Ruolo;

// Popola dati di esempio in modo idempotente (ogni metodo seedXxx() controlla repository.count()
// prima di inserire), cosi' e' sicuro riavviare l'applicazione piu' volte senza duplicare i dati.
@Component
public class DataLoader implements CommandLineRunner {

	@Autowired
	private RegistaRepository registaRepository;
	@Autowired
	private SalaRepository salaRepository;
	@Autowired
	private FestivalRepository festivalRepository;
	@Autowired
	private FilmRepository filmRepository;
	@Autowired
	private ProiezioneRepository proiezioneRepository;
	@Autowired
	private UtenteRepository utenteRepository;
	@Autowired
	private CredenzialiRepository credenzialiRepository;
	@Autowired
	private RecensioneRepository recensioneRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	@Transactional
	public void run(String... args) {
		List<Utente> utenti = seedUtenti();
		List<Regista> registi = seedRegisti();
		List<Sala> sale = seedSale();
		List<Festival> festival = seedFestival();
		List<Film> film = seedFilm(registi, festival);
		seedProiezioni(festival, film, sale);
		seedRecensioni(film, utenti);
	}

	// --- Utenti (admin + utente demo), con Credenziali ------------------------------------------

	private record UtenteSeed(String nome, String cognome, String email, String username, Ruolo ruolo) {}

	private static final List<UtenteSeed> UTENTI_ESEMPIO = List.of(
		new UtenteSeed("Admin", "SIW", "admin@siwfilmfestival.it", "admin", Ruolo.ADMIN_ROLE),
		new UtenteSeed("Mario", "Rossi", "mario.rossi@example.com", "mario", Ruolo.USER_ROLE),
		new UtenteSeed("Giulia", "Bianchi", "giulia.bianchi@example.com", "giulia", Ruolo.USER_ROLE)
	);

	private List<Utente> seedUtenti() {
		if (this.utenteRepository.count() > 0) {
			return this.utenteRepository.findAll();
		}
		List<Utente> creati = new ArrayList<>();
		for (UtenteSeed seed : UTENTI_ESEMPIO) {
			Credenziali credenziali = new Credenziali();
			credenziali.setUsername(seed.username());
			credenziali.setPassword(this.passwordEncoder.encode("password"));
			credenziali.setRuolo(seed.ruolo());
			this.credenzialiRepository.save(credenziali);

			Utente utente = new Utente();
			utente.setNome(seed.nome());
			utente.setCognome(seed.cognome());
			utente.setEmail(seed.email());
			utente.setCredenziali(credenziali);
			creati.add(this.utenteRepository.save(utente));
		}
		return creati;
	}

	// --- Registi ---------------------------------------------------------------------------------

	private record RegistaSeed(String nome, String cognome, LocalDate dataNascita, String nazionalita) {}

	private static final List<RegistaSeed> REGISTI_ESEMPIO = List.of(
		new RegistaSeed("Federico", "Fellini", LocalDate.of(1920, 1, 20), "Italia"),
		new RegistaSeed("Agnes", "Varda", LocalDate.of(1928, 5, 30), "Francia"),
		new RegistaSeed("Akira", "Kurosawa", LocalDate.of(1910, 3, 23), "Giappone"),
		new RegistaSeed("Sofia", "Coppola", LocalDate.of(1971, 5, 14), "Stati Uniti")
	);

	private List<Regista> seedRegisti() {
		if (this.registaRepository.count() > 0) {
			return this.registaRepository.findAll();
		}
		List<Regista> creati = new ArrayList<>();
		for (RegistaSeed seed : REGISTI_ESEMPIO) {
			Regista regista = new Regista();
			regista.setNome(seed.nome());
			regista.setCognome(seed.cognome());
			regista.setDataNascita(seed.dataNascita());
			regista.setNazionalita(seed.nazionalita());
			creati.add(this.registaRepository.save(regista));
		}
		return creati;
	}

	// --- Sale ------------------------------------------------------------------------------------

	private record SalaSeed(String nome, String indirizzo, Integer capienza) {}

	private static final List<SalaSeed> SALE_ESEMPIO = List.of(
		new SalaSeed("Sala Grande", "Via Roma 1, Roma", 300),
		new SalaSeed("Sala Cinema Moderno", "Via Garibaldi 12, Roma", 150),
		new SalaSeed("Arena all'aperto", "Piazza del Popolo, Roma", 500)
	);

	private List<Sala> seedSale() {
		if (this.salaRepository.count() > 0) {
			return this.salaRepository.findAll();
		}
		List<Sala> create = new ArrayList<>();
		for (SalaSeed seed : SALE_ESEMPIO) {
			Sala sala = new Sala();
			sala.setNome(seed.nome());
			sala.setIndirizzo(seed.indirizzo());
			sala.setCapienza(seed.capienza());
			create.add(this.salaRepository.save(sala));
		}
		return create;
	}

	// --- Festival --------------------------------------------------------------------------------

	private record FestivalSeed(String nome, Integer anno, String citta, LocalDate dataInizio, LocalDate dataFine, String descrizione) {}

	private static final List<FestivalSeed> FESTIVAL_ESEMPIO = List.of(
		new FestivalSeed("Roma Film Fest", 2026, "Roma",
			LocalDate.of(2026, 10, 12), LocalDate.of(2026, 10, 20),
			"Il principale festival cinematografico della capitale, giunto alla ventunesima edizione."),
		new FestivalSeed("Festival del Cinema Indipendente", 2026, "Torino",
			LocalDate.of(2026, 11, 5), LocalDate.of(2026, 11, 10),
			"Una vetrina dedicata al cinema d'autore e alle produzioni indipendenti.")
	);

	private List<Festival> seedFestival() {
		if (this.festivalRepository.count() > 0) {
			return this.festivalRepository.findAll();
		}
		List<Festival> creati = new ArrayList<>();
		for (FestivalSeed seed : FESTIVAL_ESEMPIO) {
			Festival festival = new Festival();
			festival.setNome(seed.nome());
			festival.setAnno(seed.anno());
			festival.setCitta(seed.citta());
			festival.setDataInizio(seed.dataInizio());
			festival.setDataFine(seed.dataFine());
			festival.setDescrizione(seed.descrizione());
			creati.add(this.festivalRepository.save(festival));
		}
		return creati;
	}

	// --- Film (assegnati a un regista e associati a uno o piu' festival) -------------------------

	private record FilmSeed(String titolo, Integer anno, Integer durata, String genere, String paese, int registaIndex, List<Integer> festivalIndici) {}

	private static final List<FilmSeed> FILM_ESEMPIO = List.of(
		new FilmSeed("La Dolce Vita", 1960, 174, "Drammatico", "Italia", 0, List.of(0)),
		new FilmSeed("Otto e Mezzo", 1963, 138, "Drammatico", "Italia", 0, List.of(0, 1)),
		new FilmSeed("Cleo dalle 5 alle 7", 1962, 90, "Drammatico", "Francia", 1, List.of(1)),
		new FilmSeed("I Vagabondi", 1985, 105, "Drammatico", "Francia", 1, List.of(0)),
		new FilmSeed("I Sette Samurai", 1954, 207, "Azione", "Giappone", 2, List.of(0)),
		new FilmSeed("Rashomon", 1950, 88, "Drammatico", "Giappone", 2, List.of(0, 1)),
		new FilmSeed("Lost in Translation", 2003, 102, "Commedia", "Stati Uniti", 3, List.of(1)),
		new FilmSeed("Marie Antoinette", 2006, 123, "Storico", "Stati Uniti", 3, List.of(0))
	);

	private List<Film> seedFilm(List<Regista> registi, List<Festival> festival) {
		if (this.filmRepository.count() > 0) {
			return this.filmRepository.findAll();
		}
		List<Film> creati = new ArrayList<>();
		for (FilmSeed seed : FILM_ESEMPIO) {
			Film film = new Film();
			film.setTitolo(seed.titolo());
			film.setAnno(seed.anno());
			film.setDurata(seed.durata());
			film.setGenere(seed.genere());
			film.setPaeseProduzione(seed.paese());
			film.setRegista(registi.get(seed.registaIndex()));
			for (Integer indiceFestival : seed.festivalIndici()) {
				film.aggiungiFestival(festival.get(indiceFestival));
			}
			creati.add(this.filmRepository.save(film));
		}
		return creati;
	}

	// --- Proiezioni: molte proiezioni sul primo festival, per rendere evidente il costo N+1 -------

	private void seedProiezioni(List<Festival> festival, List<Film> film, List<Sala> sale) {
		if (this.proiezioneRepository.count() > 0 || festival.isEmpty() || film.isEmpty() || sale.isEmpty()) {
			return;
		}
		Festival romaFilmFest = festival.get(0);
		List<Film> filmDelPrimoFestival = film.stream()
			.filter(f -> f.getFestival().contains(romaFilmFest))
			.toList();

		LocalDate giorno = romaFilmFest.getDataInizio();
		LocalTime[] orariGiorno = { LocalTime.of(15, 0), LocalTime.of(18, 0), LocalTime.of(21, 0) };

		int indiceOrario = 0;
		int indiceSala = 0;
		for (Film f : filmDelPrimoFestival) {
			// Ogni film viene proiettato due volte, in sale/orari diversi per non sovrapporsi.
			for (int volta = 0; volta < 2; volta++) {
				Proiezione proiezione = new Proiezione();
				proiezione.setFestival(romaFilmFest);
				proiezione.setFilm(f);
				proiezione.setSala(sale.get(indiceSala % sale.size()));
				proiezione.setData(giorno.plusDays(indiceOrario / orariGiorno.length));
				proiezione.setOra(orariGiorno[indiceOrario % orariGiorno.length]);
				proiezione.setStato(StatoProiezione.SCHEDULED);
				this.proiezioneRepository.save(proiezione);

				indiceOrario++;
				indiceSala++;
			}
		}

		// Un paio di proiezioni anche sul secondo festival, se presente.
		if (festival.size() > 1) {
			Festival secondoFestival = festival.get(1);
			List<Film> filmDelSecondoFestival = film.stream()
				.filter(f -> f.getFestival().contains(secondoFestival))
				.toList();
			LocalDate giornoSecondo = secondoFestival.getDataInizio();
			int i = 0;
			for (Film f : filmDelSecondoFestival) {
				Proiezione proiezione = new Proiezione();
				proiezione.setFestival(secondoFestival);
				proiezione.setFilm(f);
				proiezione.setSala(sale.get(i % sale.size()));
				proiezione.setData(giornoSecondo);
				proiezione.setOra(orariGiorno[i % orariGiorno.length]);
				proiezione.setStato(StatoProiezione.SCHEDULED);
				this.proiezioneRepository.save(proiezione);
				i++;
			}
		}
	}

	// --- Recensioni (una per utente demo, su film diversi, rispettando il vincolo di unicita') ----

	private void seedRecensioni(List<Film> film, List<Utente> utenti) {
		if (this.recensioneRepository.count() > 0 || film.isEmpty() || utenti.size() < 3) {
			return;
		}
		Utente mario = utenti.get(1);
		Utente giulia = utenti.get(2);

		Recensione r1 = new Recensione();
		r1.setFilm(film.get(0));
		r1.setAutore(mario);
		r1.setVoto(9);
		r1.setTesto("Un capolavoro senza tempo, la fotografia e' straordinaria.");
		r1.setData(LocalDate.now().minusDays(10));
		this.recensioneRepository.save(r1);

		Recensione r2 = new Recensione();
		r2.setFilm(film.get(0));
		r2.setAutore(giulia);
		r2.setVoto(8);
		r2.setTesto("Bellissimo, anche se un po' lento nella parte centrale.");
		r2.setData(LocalDate.now().minusDays(5));
		this.recensioneRepository.save(r2);

		if (film.size() > 2) {
			Recensione r3 = new Recensione();
			r3.setFilm(film.get(2));
			r3.setAutore(mario);
			r3.setVoto(7);
			r3.setTesto("Interessante ma non tra i miei preferiti del regista.");
			r3.setData(LocalDate.now().minusDays(2));
			this.recensioneRepository.save(r3);
		}
	}
}
