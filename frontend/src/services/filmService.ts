import api from './api'
import { FilmPagina } from '../types'

export async function getFilm(titolo?: string, pagina: number = 1): Promise<FilmPagina> {
	const { data } = await api.get<FilmPagina>('/rest/film', {
		params: { titolo: titolo || undefined, pagina },
	})
	return data
}
