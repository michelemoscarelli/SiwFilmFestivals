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

import it.uniroma3.siw.siw_film_festival.dto.admin.ProiezioneFormDto;
import it.uniroma3.siw.siw_film_festival.model.Festival;
import it.uniroma3.siw.siw_film_festival.model.Film;
import it.uniroma3.siw.siw_film_festival.model.Proiezione;
import it.uniroma3.siw.siw_film_festival.model.Sala;
import it.uniroma3.siw.siw_film_festival.model.StatoProiezione;
import it.uniroma3.siw.siw_film_festival.service.FestivalService;
import it.uniroma3.siw.siw_film_festival.service.FilmService;
import it.uniroma3.siw.siw_film_festival.service.ProiezioneService;
import it.uniroma3.siw.siw_film_festival.service.SalaService;
import jakarta.validation.Valid;

// Casi d'uso admin sulle proiezioni: programmazione (a partire da un festival), modifica
// (con controllo sovrapposizione sala nel service) e cancellazione/eliminazione.
@Controller
public class AdminProiezioneController {

	@Autowired
	private FestivalService festivalService;
	@Autowired
	private FilmService filmService;
	@Autowired
	private SalaService salaService;
	@Autowired
	private ProiezioneService proiezioneService;

	@GetMapping("/admin/proiezioni")
	public String elencoProiezioni(@RequestParam(value = "pagina", defaultValue = "1") int pagina, Model model) {
		Page<Proiezione> paginaProiezioni = this.proiezioneService.getProiezioniPagina(pagina);
		model.addAttribute("elencoProiezioni", paginaProiezioni.getContent());
		model.addAttribute("paginaCorrente", pagina);
		model.addAttribute("totalePagine", Math.max(paginaProiezioni.getTotalPages(), 1));
		return "admin/proiezioni";
	}

	@GetMapping("/admin/festival/{id}/proiezioni/aggiungi")
	public String formProgrammaProiezione(@PathVariable("id") Long id, Model model) {
		Festival festival = this.festivalService.getFestival(id)
			.orElseThrow(() -> new NoSuchElementException("Festival non trovato: id " + id));
		if (!model.containsAttribute("proiezioneFormDto")) {
			model.addAttribute("proiezioneFormDto", new ProiezioneFormDto());
		}
		model.addAttribute("festival", festival);
		model.addAttribute("filmDelFestival", this.filmService.getFilmDiFestival(id));
		model.addAttribute("sale", this.salaService.getSale());
		return "admin/programma_proiezione";
	}

	@PostMapping("/admin/festival/{id}/proiezioni/aggiungi")
	public String programmaProiezione(@PathVariable("id") Long id,
			@Valid @ModelAttribute("proiezioneFormDto") ProiezioneFormDto dto, BindingResult bindingResult, Model model) {
		Festival festival = this.festivalService.getFestival(id)
			.orElseThrow(() -> new NoSuchElementException("Festival non trovato: id " + id));
		if (bindingResult.hasErrors()) {
			model.addAttribute("festival", festival);
			model.addAttribute("filmDelFestival", this.filmService.getFilmDiFestival(id));
			model.addAttribute("sale", this.salaService.getSale());
			return "admin/programma_proiezione";
		}
		Film film = this.filmService.getFilm(dto.getFilmId())
			.orElseThrow(() -> new NoSuchElementException("Film non trovato: id " + dto.getFilmId()));
		Sala sala = this.salaService.getSala(dto.getSalaId())
			.orElseThrow(() -> new NoSuchElementException("Sala non trovata: id " + dto.getSalaId()));

		this.proiezioneService.programmaProiezione(festival, film, sala, dto.getData(), dto.getOra());
		return "redirect:/festival/" + id + "/programma";
	}

	// Il parametro "origine" ricorda da dove l'admin e' arrivato alla modifica (la lista
	// admin/proiezioni oppure la pagina pubblica del programma di un festival), cosi' dopo il
	// salvataggio si puo' tornare esattamente li' invece di atterrare sempre sulla stessa pagina.
	// Viaggia come query param sul link di ingresso e come campo hidden nel form (vedi template).
	@GetMapping("/admin/proiezioni/{id}/modifica")
	public String formModificaProiezione(@PathVariable("id") Long id,
			@RequestParam(value = "origine", required = false, defaultValue = "admin") String origine, Model model) {
		Proiezione proiezione = this.proiezioneService.getProiezione(id)
			.orElseThrow(() -> new NoSuchElementException("Proiezione non trovata: id " + id));
		if (!model.containsAttribute("proiezioneFormDto")) {
			ProiezioneFormDto dto = new ProiezioneFormDto();
			dto.setFilmId(proiezione.getFilm().getId());
			dto.setSalaId(proiezione.getSala().getId());
			dto.setData(proiezione.getData());
			dto.setOra(proiezione.getOra());
			dto.setStato(proiezione.getStato());
			model.addAttribute("proiezioneFormDto", dto);
		}
		model.addAttribute("proiezione", proiezione);
		model.addAttribute("filmDelFestival", this.filmService.getFilmDiFestival(proiezione.getFestival().getId()));
		model.addAttribute("sale", this.salaService.getSale());
		model.addAttribute("statiProiezione", StatoProiezione.values());
		model.addAttribute("origine", origine);
		return "admin/modifica_proiezione";
	}

	@PostMapping("/admin/proiezioni/{id}/modifica")
	public String modificaProiezione(@PathVariable("id") Long id,
			@Valid @ModelAttribute("proiezioneFormDto") ProiezioneFormDto dto, BindingResult bindingResult,
			@RequestParam(value = "origine", required = false, defaultValue = "admin") String origine, Model model) {
		Proiezione proiezione = this.proiezioneService.getProiezione(id)
			.orElseThrow(() -> new NoSuchElementException("Proiezione non trovata: id " + id));
		if (bindingResult.hasErrors()) {
			model.addAttribute("proiezione", proiezione);
			model.addAttribute("filmDelFestival", this.filmService.getFilmDiFestival(proiezione.getFestival().getId()));
			model.addAttribute("sale", this.salaService.getSale());
			model.addAttribute("statiProiezione", StatoProiezione.values());
			model.addAttribute("origine", origine);
			return "admin/modifica_proiezione";
		}
		Film film = this.filmService.getFilm(dto.getFilmId())
			.orElseThrow(() -> new NoSuchElementException("Film non trovato: id " + dto.getFilmId()));
		Sala sala = this.salaService.getSala(dto.getSalaId())
			.orElseThrow(() -> new NoSuchElementException("Sala non trovata: id " + dto.getSalaId()));

		this.proiezioneService.modificaProiezione(proiezione, film, sala, dto.getData(), dto.getOra(), dto.getStato());
		if ("programma".equals(origine)) {
			return "redirect:/festival/" + proiezione.getFestival().getId() + "/programma";
		}
		return "redirect:/admin/proiezioni";
	}

	@PostMapping("/admin/proiezioni/{id}/cancella")
	public String cancellaProiezione(@PathVariable("id") Long id) {
		Proiezione proiezione = this.proiezioneService.getProiezione(id)
			.orElseThrow(() -> new NoSuchElementException("Proiezione non trovata: id " + id));
		this.proiezioneService.cancellaProiezione(proiezione);
		return "redirect:/admin/proiezioni";
	}

	@PostMapping("/admin/proiezioni/{id}/elimina")
	public String eliminaProiezione(@PathVariable("id") Long id) {
		this.proiezioneService.eliminaProiezione(id);
		return "redirect:/admin/proiezioni";
	}
}
