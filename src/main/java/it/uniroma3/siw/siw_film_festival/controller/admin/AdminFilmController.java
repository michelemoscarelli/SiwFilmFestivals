package it.uniroma3.siw.siw_film_festival.controller.admin;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.siw_film_festival.dto.admin.FilmFormDto;
import it.uniroma3.siw.siw_film_festival.model.Film;
import it.uniroma3.siw.siw_film_festival.model.Regista;
import it.uniroma3.siw.siw_film_festival.service.FilmService;
import it.uniroma3.siw.siw_film_festival.service.RegistaService;
import jakarta.validation.Valid;

// Casi d'uso admin sui film: creazione/modifica/eliminazione, con caricamento facoltativo
// della locandina.
@Controller
public class AdminFilmController {

	@Autowired
	private FilmService filmService;
	@Autowired
	private RegistaService registaService;

	@GetMapping("/admin/film")
	public String elencoFilmAdmin(@RequestParam(value = "pagina", defaultValue = "1") int pagina, Model model) {
		Page<Film> paginaFilm = this.filmService.getFilmPagina(pagina);
		model.addAttribute("elencoFilm", paginaFilm.getContent());
		model.addAttribute("paginaCorrente", pagina);
		model.addAttribute("totalePagine", Math.max(paginaFilm.getTotalPages(), 1));
		return "admin/film";
	}

	@GetMapping("/admin/film/aggiungi")
	public String formAggiungiFilm(Model model) {
		if (!model.containsAttribute("filmFormDto")) {
			model.addAttribute("filmFormDto", new FilmFormDto());
		}
		model.addAttribute("registi", this.registaService.getRegisti());
		return "admin/aggiungi_film";
	}

	@PostMapping("/admin/film/aggiungi")
	public String aggiungiFilm(@Valid @ModelAttribute("filmFormDto") FilmFormDto dto, BindingResult bindingResult, Model model) {
		this.validaLocandina(dto, bindingResult);
		if (bindingResult.hasErrors()) {
			model.addAttribute("registi", this.registaService.getRegisti());
			return "admin/aggiungi_film";
		}
		Regista regista = this.registaService.getRegista(dto.getRegistaId())
			.orElseThrow(() -> new NoSuchElementException("Regista non trovato: id " + dto.getRegistaId()));
		Film film = new Film();
		this.copiaCampi(dto, film, regista);
		this.filmService.salvaFilm(film, dto.getLocandina());
		return "redirect:/admin/film";
	}

	@GetMapping("/admin/film/{id}/modifica")
	public String formModificaFilm(@PathVariable("id") Long id, Model model) {
		Film film = this.filmService.getFilm(id)
			.orElseThrow(() -> new NoSuchElementException("Film non trovato: id " + id));
		if (!model.containsAttribute("filmFormDto")) {
			FilmFormDto dto = new FilmFormDto();
			dto.setTitolo(film.getTitolo());
			dto.setAnno(film.getAnno());
			dto.setDurata(film.getDurata());
			dto.setGenere(film.getGenere());
			dto.setPaeseProduzione(film.getPaeseProduzione());
			dto.setRegistaId(film.getRegista().getId());
			model.addAttribute("filmFormDto", dto);
		}
		model.addAttribute("film", film);
		model.addAttribute("registi", this.registaService.getRegisti());
		return "admin/modifica_film";
	}

	@PostMapping("/admin/film/{id}/modifica")
	public String modificaFilm(@PathVariable("id") Long id,
			@Valid @ModelAttribute("filmFormDto") FilmFormDto dto, BindingResult bindingResult, Model model) {
		Film film = this.filmService.getFilm(id)
			.orElseThrow(() -> new NoSuchElementException("Film non trovato: id " + id));
		this.validaLocandina(dto, bindingResult);
		if (bindingResult.hasErrors()) {
			model.addAttribute("film", film);
			model.addAttribute("registi", this.registaService.getRegisti());
			return "admin/modifica_film";
		}
		Regista regista = this.registaService.getRegista(dto.getRegistaId())
			.orElseThrow(() -> new NoSuchElementException("Regista non trovato: id " + dto.getRegistaId()));
		this.copiaCampi(dto, film, regista);
		this.filmService.salvaFilm(film, dto.getLocandina());
		return "redirect:/admin/film";
	}

	@PostMapping("/admin/film/{id}/elimina")
	public String eliminaFilm(@PathVariable("id") Long id) {
		this.filmService.eliminaFilm(id);
		return "redirect:/admin/film";
	}

	// Stesso controllo di SIWHotel (isImmagineValida): il file e' facoltativo, ma se presente deve
	// essere davvero un'immagine. Usa bindingResult.rejectValue cosi' l'errore si integra con la
	// stessa visualizzazione (#fields.hasErrors) gia' usata per gli altri campi del form.
	private void validaLocandina(FilmFormDto dto, BindingResult bindingResult) {
		MultipartFile locandina = dto.getLocandina();
		if (locandina == null || locandina.isEmpty()) {
			return;
		}
		String contentType = locandina.getContentType();
		if (contentType == null || !contentType.startsWith("image/")) {
			bindingResult.rejectValue("locandina", "locandina.invalid", "Seleziona un file immagine valido (jpg, png, ...).");
		}
	}

	private void copiaCampi(FilmFormDto dto, Film film, Regista regista) {
		film.setTitolo(dto.getTitolo());
		film.setAnno(dto.getAnno());
		film.setDurata(dto.getDurata());
		film.setGenere(dto.getGenere());
		film.setPaeseProduzione(dto.getPaeseProduzione());
		film.setRegista(regista);
	}
}
