package it.uniroma3.siw.siw_film_festival.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.uniroma3.siw.siw_film_festival.service.ProiezioneService;

// Caso d'uso pubblico "ricerca delle proiezioni per data" (bonus sez. 13 del PDF): stesso schema
// repository -> service -> controller -> vista degli altri casi d'uso pubblici dell'app.
@Controller
public class ProiezioneController {

	@Autowired
	private ProiezioneService proiezioneService;

	@GetMapping("/proiezioni")
	public String ricercaPerData(
			@RequestParam(value = "data", required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate data,
			Model model) {
		LocalDate dataRicerca = (data != null) ? data : LocalDate.now();
		model.addAttribute("dataRicerca", dataRicerca);
		model.addAttribute("proiezioni", this.proiezioneService.getProiezioniPerData(dataRicerca));
		return "proiezioni/ricerca";
	}
}
