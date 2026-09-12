package it.uniroma3.siw.siw_film_festival.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.siw_film_festival.dto.RegistrazioneDto;
import it.uniroma3.siw.siw_film_festival.model.Credenziali;
import it.uniroma3.siw.siw_film_festival.model.Utente;
import it.uniroma3.siw.siw_film_festival.security.Ruolo;
import it.uniroma3.siw.siw_film_festival.service.CredenzialiService;
import it.uniroma3.siw.siw_film_festival.service.UtenteService;
import jakarta.validation.Valid;

@Controller
public class AuthController {

	@Autowired
	private UtenteService utenteService;

	@Autowired
	private CredenzialiService credenzialiService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@GetMapping("/registrazione")
	public String mostraFormRegistrazione(Model model) {
		if (!model.containsAttribute("registrazioneDto")) {
			model.addAttribute("registrazioneDto", new RegistrazioneDto());
		}
		return "auth/registrazione";
	}

	@PostMapping("/registrazione")
	public String registraUtente(@Valid @ModelAttribute("registrazioneDto") RegistrazioneDto dto,
			BindingResult bindingResult, Model model) {

		if (bindingResult.hasErrors()) {
			return "auth/registrazione";
		}
		if (this.credenzialiService.getCredenzialiByUsername(dto.getUsername()) != null) {
			model.addAttribute("erroreUsername", "Username gia' in uso.");
			return "auth/registrazione";
		}
		if (this.utenteService.getUtenteByEmail(dto.getEmail()) != null) {
			model.addAttribute("erroreEmail", "Email gia' registrata.");
			return "auth/registrazione";
		}

		Credenziali credenziali = new Credenziali();
		credenziali.setUsername(dto.getUsername());
		credenziali.setPassword(this.passwordEncoder.encode(dto.getPassword()));
		credenziali.setRuolo(Ruolo.USER_ROLE);
		this.credenzialiService.saveCredenziali(credenziali);

		Utente utente = new Utente();
		utente.setNome(dto.getNome());
		utente.setCognome(dto.getCognome());
		utente.setEmail(dto.getEmail());
		utente.setCredenziali(credenziali);
		this.utenteService.salvaUtente(utente);

		return "redirect:/login?registrato=true";
	}
}
