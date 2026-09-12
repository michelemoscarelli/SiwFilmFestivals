import { useEffect, useState } from 'react'
import { Film } from '../types'
import { getFilm } from '../services/filmService'
import FilmCard from './FilmCard'

function FilmList() {
	const [film, setFilm] = useState<Film[]>([])
	const [ricerca, setRicerca] = useState<string>('')
	const [pagina, setPagina] = useState<number>(1)
	const [totalePagine, setTotalePagine] = useState<number>(1)
	const [totaleElementi, setTotaleElementi] = useState<number>(0)
	const [caricamento, setCaricamento] = useState<boolean>(true)
	const [errore, setErrore] = useState<string | null>(null)

	// Ogni nuova ricerca riparte dalla pagina 1 (altrimenti si potrebbe restare su una pagina
	// che non esiste piu' per il nuovo filtro).
	useEffect(() => {
		setPagina(1)
	}, [ricerca])

	useEffect(() => {
		let attivo = true
		setCaricamento(true)
		setErrore(null)

		async function caricaFilm() {
			try {
				const risultato = await getFilm(ricerca.trim() || undefined, pagina)
				if (attivo) {
					setFilm(risultato.contenuto)
					setTotalePagine(risultato.totalePagine)
					setTotaleElementi(risultato.totaleElementi)
				}
			} catch (err) {
				if (attivo) {
					setErrore('Impossibile contattare il server. Riprova piu\' tardi.')
				}
				console.error(err)
			} finally {
				if (attivo) {
					setCaricamento(false)
				}
			}
		}

		const timeoutId = window.setTimeout(caricaFilm, 250)
		return () => {
			attivo = false
			window.clearTimeout(timeoutId)
		}
	}, [ricerca, pagina])

	return (
		<div>
			<input
				type="text"
				className="film-search"
				placeholder="Cerca un film per titolo..."
				value={ricerca}
				onChange={(e) => setRicerca(e.target.value)}
			/>

			{!caricamento && !errore && <p className="film-totale">Totale film: {totaleElementi}</p>}

			{caricamento && <p className="stato-messaggio">Caricamento film in corso...</p>}
			{!caricamento && errore && <p className="stato-messaggio stato-errore">{errore}</p>}
			{!caricamento && !errore && film.length === 0 && (
				<p className="stato-messaggio">Nessun film trovato.</p>
			)}
			{!caricamento && !errore && film.length > 0 && (
				<div className="film-grid">
					{film.map((f) => (
						<FilmCard key={f.id} film={f} />
					))}
				</div>
			)}

			<div className="film-paginazione">
				<button
					type="button"
					className="film-pagina-btn"
					disabled={pagina <= 1}
					onClick={() => setPagina((p) => Math.max(1, p - 1))}
				>
					&laquo; Precedente
				</button>
				<span className="film-pagina-info">
					Pagina {pagina} di {totalePagine}
				</span>
				<button
					type="button"
					className="film-pagina-btn"
					disabled={pagina >= totalePagine}
					onClick={() => setPagina((p) => Math.min(totalePagine, p + 1))}
				>
					Successiva &raquo;
				</button>
			</div>
		</div>
	)
}

export default FilmList
