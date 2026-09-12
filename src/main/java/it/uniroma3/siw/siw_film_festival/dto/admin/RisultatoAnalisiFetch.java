package it.uniroma3.siw.siw_film_festival.dto.admin;

public class RisultatoAnalisiFetch {

	private final String strategia;
	private final String descrizione;
	private final long numeroQuery;
	private final double tempoMs;
	private final int numeroRisultati;

	public RisultatoAnalisiFetch(String strategia, String descrizione, long numeroQuery, double tempoMs, int numeroRisultati) {
		this.strategia = strategia;
		this.descrizione = descrizione;
		this.numeroQuery = numeroQuery;
		this.tempoMs = tempoMs;
		this.numeroRisultati = numeroRisultati;
	}

	public String getStrategia() {
		return strategia;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public long getNumeroQuery() {
		return numeroQuery;
	}

	public double getTempoMs() {
		return tempoMs;
	}

	public int getNumeroRisultati() {
		return numeroRisultati;
	}
}
