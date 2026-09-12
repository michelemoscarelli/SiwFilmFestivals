import { Film } from '../types'

interface FilmCardProps {
	film: Film
}

function FilmCard({ film }: FilmCardProps) {
	return (
		<a className="film-card" href={`http://localhost:8080/film/${film.id}`}>
			<h2>{film.titolo}</h2>
			<p className="film-card-meta">
				{film.genere} &middot; {film.anno} &middot; {film.durata} min
			</p>
			<p className="film-card-paese">{film.paeseProduzione}</p>
		</a>
	)
}

export default FilmCard
