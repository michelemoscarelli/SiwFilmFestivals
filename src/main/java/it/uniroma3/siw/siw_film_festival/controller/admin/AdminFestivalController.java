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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.uniroma3.siw.siw_film_festival.dto.admin.FestivalFormDto;
import it.uniroma3.siw.siw_film_festival.model.Festival;
import it.uniroma3.siw.siw_film_festival.model.Film;
import it.uniroma3.siw.siw_film_festival.service.FestivalService;
import it.uniroma3.siw.siw_film_festival.service.FilmService;
import jakarta.validation.Valid;

// Casi d'uso admin sui festival: creazione/modifica/eliminazione di un festival e
// associazione/rimozione di film a un festival.
@Controller
public class AdminFestivalController {

	@Autowired
	private FestivalService festivalService;
	@Autowired
	private FilmService filmService;

	// --- Festival --------------------------------------------------------------------------

	@GetMapping("/admin/festival")
	public String elencoFestival(@RequestParam(value = "pagina", defaultValue = "1") int pagina, Model model) {
		Page<Festival> paginaFestival = this.festivalService.getFestivalPagina(pagina);
		model.addAttribute("elencoFestival", paginaFestival.getContent());
		model.addAttribute("paginaCorrente", pagina);
		model.addAttribute("totalePagine", Math.max(paginaFestival.getTotalPages(), 1));
		return "admin/festival";
	}

	@GetMapping("/admin/festival/aggiungi")
	public String formAggiungiFestival(Model model) {
		if (!model.containsAttribute("festivalFormDto")) {
			model.addAttribute("festivalFormDto", new FestivalFormDto());
		}
		return "admin/aggiungi_festival";
	}

	@PostMapping("/admin/festival/aggiungi")
	public String aggiungiFestival(@Valid @ModelAttribute("festivalFormDto") FestivalFormDto dto, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "admin/aggiungi_festival";
		}
		Festival festival = new Festival();
		this.copiaCampi(dto, festival);
		this.festivalService.salvaFestival(festival);
		return "redirect:/admin/festival";
	}

	@GetMapping("/admin/festival/{id}/modifica")
	public String formModificaFestival(@PathVariable("id") Long id, Model model) {
		Festival festival = this.festivalService.getFestival(id)
			.orElseThrow(() -> new NoSuchElementException("Festival non trovato: id " + id));
		if (!model.containsAttribute("festivalFormDto")) {
			FestivalFormDto dto = new FestivalFormDto();
			dto.setNome(festival.getNome());
			dto.setAnno(festival.getAnno());
			dto.setCitta(festival.getCitta());
			dto.setDataInizio(festival.getDataInizio());
			dto.setDataFine(festival.getDataFine());
			dto.setDescrizione(festival.getDescrizione());
			model.addAttribute("festivalFormDto", dto);
		}
		model.addAttribute("festival", festival);
		return "admin/modifica_festival";
	}

	@PostMapping("/admin/festival/{id}/modifica")
	public String modificaFestival(@PathVariable("id") Long id,
			@Valid @ModelAttribute("festivalFormDto") FestivalFormDto dto, BindingResult bindingResult, Model model) {
		Festival festival = this.festivalService.getFestival(id)
			.orElseThrow(() -> new NoSuchElementException("Festival non trovato: id " + id));
		if (bindingResult.hasErrors()) {
			model.addAttribute("festival", festival);
			return "admin/modifica_festival";
		}
		this.copiaCampi(dto, festival);
		this.festivalService.salvaFestival(festival);
		return "redirect:/admin/festival";
	}

	@PostMapping("/admin/festival/{id}/elimina")
	public String eliminaFestival(@PathVariable("id") Long id) {
		this.festivalService.eliminaFestival(id);
		return "redirect:/admin/festival";
	}

	private void copiaCampi(FestivalFormDto dto, Festival festival) {
		festival.setNome(dto.getNome());
		festival.setAnno(dto.getAnno());
		festival.setCitta(dto.getCitta());
		festival.setDataInizio(dto.getDataInizio());
		festival.setDataFine(dto.getDataFine());
		festival.setDescrizione(dto.getDescrizione());
	}

	// --- Associazione film-festival / eliminazione film da un festival ---------------------

	@GetMapping("/admin/festival/{id}/associa-film")
	public String formAssociaFilm(@PathVariable("id") Long id, Model model) {
		Festival festival = this.festivalService.getFestival(id)
			.orElseThrow(() -> new NoSuchElementException("Festival non trovato: id " + id));
		model.addAttribute("festival", festival);
		model.addAttribute("filmPartecipanti", this.filmService.getFilmDiFestival(id));
		model.addAttribute("tuttiIFilm", this.filmService.getFilm());
		return "admin/associa_film";
	}

	@PostMapping("/admin/festival/{id}/associa-film")
	public String associaFilm(@PathVariable("id") Long id, @RequestParam("filmId") Long filmId,
			RedirectAttributes redirectAttributes) {
		Festival festival = this.festivalService.getFestival(id)
			.orElseThrow(() -> new NoSuchElementException("Festival non trovato: id " + id));
		Film film = this.filmService.getFilm(filmId)
			.orElseThrow(() -> new NoSuchElementException("Film non trovato: id " + filmId));
		this.festivalService.associaFilm(festival, film);
		redirectAttributes.addFlashAttribute("esito", "Film associato al festival.");
		return "redirect:/admin/festival/" + id + "/associa-film";
	}

	@PostMapping("/admin/festival/{id}/rimuovi-film/{filmId}")
	public String rimuoviFilmDaFestival(@PathVariable("id") Long id, @PathVariable("filmId") Long filmId) {
		Festival festival = this.festivalService.getFestival(id)
			.orElseThrow(() -> new NoSuchElementException("Festival non trovato: id " + id));
		Film film = this.filmService.getFilm(filmId)
			.orElseThrow(() -> new NoSuchElementException("Film non trovato: id " + filmId));
		this.festivalService.rimuoviFilm(festival, film);
		return "redirect:/admin/festival/" + id + "/associa-film";
	}
}
