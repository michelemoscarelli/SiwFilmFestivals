package it.uniroma3.siw.siw_film_festival.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import it.uniroma3.siw.siw_film_festival.service.FestivalService;

@Controller
public class HomeController {

	@Autowired
	private FestivalService festivalService;

	@GetMapping("/")
	public String home(Model model) {
		model.addAttribute("festivalInPrimoPiano", this.festivalService.getFestivalInPrimoPiano());
		return "home";
	}

	@GetMapping("/login")
	public String login() {
		return "auth/login";
	}
}
