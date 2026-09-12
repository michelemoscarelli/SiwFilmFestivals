package it.uniroma3.siw.siw_film_festival.controller;

import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	// Sollevata quando un utente registrato tenta di modificare/eliminare una recensione che non e' sua.
	@ExceptionHandler(AccessDeniedException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public String handleAccessDenied(AccessDeniedException e, Model model) {
		logger.warn("Accesso negato: {}", e.getMessage());
		model.addAttribute("errorMessage", "Non hai i permessi per compiere questa operazione.");
		return "error/403";
	}

	@ExceptionHandler(NoSuchElementException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String handleNoSuchElement(NoSuchElementException e, Model model) {
		logger.warn("Risorsa non trovata: {}", e.getMessage());
		model.addAttribute("errorMessage", "La risorsa richiesta non esiste o e' stata rimossa.");
		return "error/404";
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String handleTypeMismatch(MethodArgumentTypeMismatchException e, Model model) {
		logger.warn("Parametro non valido nell'URL: {}", e.getMessage());
		model.addAttribute("errorMessage", "La risorsa richiesta non esiste o e' stata rimossa.");
		return "error/404";
	}

	// Sollevata quando il file caricato per la locandina supera spring.servlet.multipart.max-file-size.
	@ExceptionHandler(MaxUploadSizeExceededException.class)
	@ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
	public String handleMaxUploadSize(MaxUploadSizeExceededException e, Model model) {
		logger.warn("File caricato troppo grande: {}", e.getMessage());
		model.addAttribute("errorMessage", "Il file caricato e' troppo grande. La dimensione massima consentita e' 10MB.");
		return "error/500";
	}

	@ExceptionHandler(NoResourceFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String handleNoResourceFound(NoResourceFoundException e, Model model) {
		logger.warn("Pagina non trovata: {}", e.getResourcePath());
		model.addAttribute("errorMessage", "La pagina richiesta non esiste.");
		return "error/404";
	}

	// Sollevata da ProiezioneService/RecensioneService quando una regola di business viene violata
	// (es. sovrapposizione sala, doppia recensione).
	@ExceptionHandler(IllegalStateException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public String handleIllegalState(IllegalStateException e, Model model) {
		logger.warn("Operazione non consentita: {}", e.getMessage());
		model.addAttribute("errorMessage", e.getMessage());
		return "error/500";
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public String handleGenericException(Exception e, Model model) {
		logger.error("Errore interno non gestito", e);
		model.addAttribute("errorMessage", "Si e' verificato un errore imprevisto. Riprova piu' tardi.");
		return "error/500";
	}
}
