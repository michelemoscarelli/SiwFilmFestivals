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

import it.uniroma3.siw.siw_film_festival.dto.admin.RegistaFormDto;
import it.uniroma3.siw.siw_film_festival.model.Regista;
import it.uniroma3.siw.siw_film_festival.service.RegistaService;
import jakarta.validation.Valid;

// Casi d'uso admin sui registi: creazione/modifica/eliminazione.
@Controller
public class AdminRegistaController {

	@Autowired
	private RegistaService registaService;

	@GetMapping("/admin/registi")
	public String elencoRegisti(@RequestParam(value = "pagina", defaultValue = "1") int pagina, Model model) {
		Page<Regista> paginaRegisti = this.registaService.getRegistiPagina(pagina);
		model.addAttribute("elencoRegisti", paginaRegisti.getContent());
		model.addAttribute("paginaCorrente", pagina);
		model.addAttribute("totalePagine", Math.max(paginaRegisti.getTotalPages(), 1));
		return "admin/registi";
	}

	@GetMapping("/admin/registi/aggiungi")
	public String formAggiungiRegista(Model model) {
		if (!model.containsAttribute("registaFormDto")) {
			model.addAttribute("registaFormDto", new RegistaFormDto());
		}
		return "admin/aggiungi_regista";
	}

	@PostMapping("/admin/registi/aggiungi")
	public String aggiungiRegista(@Valid @ModelAttribute("registaFormDto") RegistaFormDto dto, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "admin/aggiungi_regista";
		}
		Regista regista = new Regista();
		this.copiaCampi(dto, regista);
		this.registaService.salvaRegista(regista);
		return "redirect:/admin/registi";
	}

	@GetMapping("/admin/registi/{id}/modifica")
	public String formModificaRegista(@PathVariable("id") Long id, Model model) {
		Regista regista = this.registaService.getRegista(id)
			.orElseThrow(() -> new NoSuchElementException("Regista non trovato: id " + id));
		if (!model.containsAttribute("registaFormDto")) {
			RegistaFormDto dto = new RegistaFormDto();
			dto.setNome(regista.getNome());
			dto.setCognome(regista.getCognome());
			dto.setDataNascita(regista.getDataNascita());
			dto.setNazionalita(regista.getNazionalita());
			model.addAttribute("registaFormDto", dto);
		}
		model.addAttribute("regista", regista);
		return "admin/modifica_regista";
	}

	@PostMapping("/admin/registi/{id}/modifica")
	public String modificaRegista(@PathVariable("id") Long id,
			@Valid @ModelAttribute("registaFormDto") RegistaFormDto dto, BindingResult bindingResult, Model model) {
		Regista regista = this.registaService.getRegista(id)
			.orElseThrow(() -> new NoSuchElementException("Regista non trovato: id " + id));
		if (bindingResult.hasErrors()) {
			model.addAttribute("regista", regista);
			return "admin/modifica_regista";
		}
		this.copiaCampi(dto, regista);
		this.registaService.salvaRegista(regista);
		return "redirect:/admin/registi";
	}

	@PostMapping("/admin/registi/{id}/elimina")
	public String eliminaRegista(@PathVariable("id") Long id) {
		this.registaService.eliminaRegista(id);
		return "redirect:/admin/registi";
	}

	private void copiaCampi(RegistaFormDto dto, Regista regista) {
		regista.setNome(dto.getNome());
		regista.setCognome(dto.getCognome());
		regista.setDataNascita(dto.getDataNascita());
		regista.setNazionalita(dto.getNazionalita());
	}
}
