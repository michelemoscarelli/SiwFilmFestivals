package it.uniroma3.siw.siw_film_festival.controller.admin;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.uniroma3.siw.siw_film_festival.dto.admin.UtenteFormDto;
import it.uniroma3.siw.siw_film_festival.model.Utente;
import it.uniroma3.siw.siw_film_festival.service.CredenzialiService;
import it.uniroma3.siw.siw_film_festival.service.UtenteService;
import jakarta.validation.Valid;

// Gestione admin degli utenti: modifica dati/ruolo ed eliminazione (con blocco sull'auto-eliminazione).
@Controller
public class AdminUtenteController {

	@Autowired
	private UtenteService utenteService;
	@Autowired
	private CredenzialiService credenzialiService;

	@GetMapping("/admin/utenti")
	public String elencoUtenti(@RequestParam(value = "pagina", defaultValue = "1") int pagina, Model model) {
		Page<Utente> paginaUtenti = this.utenteService.getUtentiPagina(pagina);
		model.addAttribute("elencoUtenti", paginaUtenti.getContent());
		model.addAttribute("paginaCorrente", pagina);
		model.addAttribute("totalePagine", Math.max(paginaUtenti.getTotalPages(), 1));
		return "admin/utenti";
	}

	@GetMapping("/admin/utenti/{id}/modifica")
	public String formModificaUtente(@PathVariable("id") Long id, Model model) {
		Utente utente = this.utenteService.getUtente(id)
			.orElseThrow(() -> new NoSuchElementException("Utente non trovato: id " + id));
		if (!model.containsAttribute("utenteFormDto")) {
			UtenteFormDto dto = new UtenteFormDto();
			dto.setNome(utente.getNome());
			dto.setCognome(utente.getCognome());
			dto.setEmail(utente.getEmail());
			dto.setRuolo(utente.getCredenziali().getRuolo());
			model.addAttribute("utenteFormDto", dto);
		}
		model.addAttribute("utenteModificato", utente);
		return "admin/modifica_utente";
	}

	@PostMapping("/admin/utenti/{id}/modifica")
	public String modificaUtente(@PathVariable("id") Long id,
			@Valid @ModelAttribute("utenteFormDto") UtenteFormDto dto, BindingResult bindingResult, Model model) {
		Utente utente = this.utenteService.getUtente(id)
			.orElseThrow(() -> new NoSuchElementException("Utente non trovato: id " + id));
		if (bindingResult.hasErrors()) {
			model.addAttribute("utenteModificato", utente);
			return "admin/modifica_utente";
		}
		utente.setNome(dto.getNome());
		utente.setCognome(dto.getCognome());
		utente.setEmail(dto.getEmail());
		this.utenteService.salvaUtente(utente);

		utente.getCredenziali().setRuolo(dto.getRuolo());
		this.credenzialiService.saveCredenziali(utente.getCredenziali());

		return "redirect:/admin/utenti";
	}

	// L'admin non puo' eliminare il proprio account da questa pagina (si bloccherebbe fuori da
	// solo, oltre al rischio di restare senza nessun amministratore).
	@PostMapping("/admin/utenti/{id}/elimina")
	public String eliminaUtente(@PathVariable("id") Long id, Authentication authentication,
			RedirectAttributes redirectAttributes) {
		Utente utenteDaEliminare = this.utenteService.getUtente(id)
			.orElseThrow(() -> new NoSuchElementException("Utente non trovato: id " + id));
		Utente utenteCorrente = this.utenteService.getUtenteAutenticato(authentication);
		if (utenteCorrente != null && utenteCorrente.getId().equals(utenteDaEliminare.getId())) {
			redirectAttributes.addFlashAttribute("erroreUtenti", "Non puoi eliminare il tuo stesso account.");
			return "redirect:/admin/utenti";
		}
		this.utenteService.eliminaUtente(id);
		return "redirect:/admin/utenti";
	}
}
