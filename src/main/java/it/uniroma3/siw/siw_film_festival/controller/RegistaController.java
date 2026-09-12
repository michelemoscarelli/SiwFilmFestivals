package it.uniroma3.siw.siw_film_festival.controller;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import it.uniroma3.siw.siw_film_festival.model.Regista;
import it.uniroma3.siw.siw_film_festival.service.RegistaService;

@Controller
public class RegistaController {

	@Autowired
	private RegistaService registaService;

	// Caso d'uso pubblico: dati regista (con i film diretti).
	@GetMapping("/registi/{id}")
	public String dettaglioRegista(@PathVariable("id") Long id, Model model) {
		Regista regista = this.registaService.getRegista(id)
			.orElseThrow(() -> new NoSuchElementException("Regista non trovato: id " + id));
		model.addAttribute("regista", regista);
		return "registi/dettaglio";
	}
}
