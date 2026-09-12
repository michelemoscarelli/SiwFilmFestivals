package it.uniroma3.siw.siw_film_festival.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.uniroma3.siw.siw_film_festival.service.AnalisiPerformanceService;
import it.uniroma3.siw.siw_film_festival.service.FestivalService;
import it.uniroma3.siw.siw_film_festival.service.FilmService;
import it.uniroma3.siw.siw_film_festival.service.RegistaService;
import it.uniroma3.siw.siw_film_festival.service.SalaService;
import it.uniroma3.siw.siw_film_festival.service.UtenteService;

// Cruscotto admin (conteggi delle entita') e pagina di analisi delle strategie di fetch
// (script N+1: "programma di un festival con film e sale").
@Controller
public class AdminDashboardController {

	@Autowired
	private FestivalService festivalService;
	@Autowired
	private FilmService filmService;
	@Autowired
	private RegistaService registaService;
	@Autowired
	private SalaService salaService;
	@Autowired
	private UtenteService utenteService;
	@Autowired
	private AnalisiPerformanceService analisiPerformanceService;

	@GetMapping("/admin/dashboard")
	public String dashboard(Model model) {
		model.addAttribute("numeroFestival", this.festivalService.getFestival().size());
		model.addAttribute("numeroFilm", this.filmService.getFilm().size());
		model.addAttribute("numeroRegisti", this.registaService.getRegisti().size());
		model.addAttribute("numeroSale", this.salaService.getSale().size());
		model.addAttribute("numeroUtenti", this.utenteService.getUtenti().size());
		return "admin/dashboard";
	}

	@GetMapping("/admin/analisi")
	public String mostraAnalisi(@RequestParam(value = "festivalId", required = false) Long festivalId, Model model) {
		var elencoFestival = this.festivalService.getFestival();
		Long idScelto = (festivalId != null) ? festivalId : (elencoFestival.isEmpty() ? null : elencoFestival.get(0).getId());

		model.addAttribute("elencoFestival", elencoFestival);
		model.addAttribute("festivalId", idScelto);
		if (idScelto != null) {
			model.addAttribute("risultati", this.analisiPerformanceService.confrontaStrategieFetchProgrammaFestival(idScelto));
		}
		return "admin/analisi";
	}
}
