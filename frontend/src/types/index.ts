// Rispecchia esattamente i campi restituiti da FilmDto (backend Spring Boot).
// Deliberatamente senza il regista (scelta esplicita, vedi brief).
export interface Film {
	id: number
	titolo: string
	anno: number
	durata: number
	genere: string
	paeseProduzione: string
}

// Rispecchia FilmPaginaDto: risposta paginata di GET /rest/film (10 film per pagina).
export interface FilmPagina {
	contenuto: Film[]
	paginaCorrente: number
	totalePagine: number
	totaleElementi: number
}
