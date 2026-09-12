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

import it.uniroma3.siw.siw_film_festival.dto.admin.SalaFormDto;
import it.uniroma3.siw.siw_film_festival.model.Sala;
import it.uniroma3.siw.siw_film_festival.service.SalaService;
import jakarta.validation.Valid;

// Casi d'uso admin sulle sale: creazione/modifica/eliminazione.
@Controller
public class AdminSalaController {

	@Autowired
	private SalaService salaService;

	@GetMapping("/admin/sale")
	public String elencoSale(@RequestParam(value = "pagina", defaultValue = "1") int pagina, Model model) {
		Page<Sala> paginaSale = this.salaService.getSalePagina(pagina);
		model.addAttribute("elencoSale", paginaSale.getContent());
		model.addAttribute("paginaCorrente", pagina);
		model.addAttribute("totalePagine", Math.max(paginaSale.getTotalPages(), 1));
		return "admin/sale";
	}

	@GetMapping("/admin/sale/aggiungi")
	public String formAggiungiSala(Model model) {
		if (!model.containsAttribute("salaFormDto")) {
			model.addAttribute("salaFormDto", new SalaFormDto());
		}
		return "admin/aggiungi_sala";
	}

	@PostMapping("/admin/sale/aggiungi")
	public String aggiungiSala(@Valid @ModelAttribute("salaFormDto") SalaFormDto dto, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "admin/aggiungi_sala";
		}
		Sala sala = new Sala();
		this.copiaCampi(dto, sala);
		this.salaService.salvaSala(sala);
		return "redirect:/admin/sale";
	}

	@GetMapping("/admin/sale/{id}/modifica")
	public String formModificaSala(@PathVariable("id") Long id, Model model) {
		Sala sala = this.salaService.getSala(id)
			.orElseThrow(() -> new NoSuchElementException("Sala non trovata: id " + id));
		if (!model.containsAttribute("salaFormDto")) {
			SalaFormDto dto = new SalaFormDto();
			dto.setNome(sala.getNome());
			dto.setIndirizzo(sala.getIndirizzo());
			dto.setCapienza(sala.getCapienza());
			model.addAttribute("salaFormDto", dto);
		}
		model.addAttribute("sala", sala);
		return "admin/modifica_sala";
	}

	@PostMapping("/admin/sale/{id}/modifica")
	public String modificaSala(@PathVariable("id") Long id,
			@Valid @ModelAttribute("salaFormDto") SalaFormDto dto, BindingResult bindingResult, Model model) {
		Sala sala = this.salaService.getSala(id)
			.orElseThrow(() -> new NoSuchElementException("Sala non trovata: id " + id));
		if (bindingResult.hasErrors()) {
			model.addAttribute("sala", sala);
			return "admin/modifica_sala";
		}
		this.copiaCampi(dto, sala);
		this.salaService.salvaSala(sala);
		return "redirect:/admin/sale";
	}

	@PostMapping("/admin/sale/{id}/elimina")
	public String eliminaSala(@PathVariable("id") Long id) {
		this.salaService.eliminaSala(id);
		return "redirect:/admin/sale";
	}

	private void copiaCampi(SalaFormDto dto, Sala sala) {
		sala.setNome(dto.getNome());
		sala.setIndirizzo(dto.getIndirizzo());
		sala.setCapienza(dto.getCapienza());
	}
}
