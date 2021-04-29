package it.polito.tdp.poweroutages.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.polito.tdp.poweroutages.DAO.PowerOutageDAO;

public class Model {
	
	PowerOutageDAO podao;
	
	private List<Nerc> nercList;
	
	private List<PowerOutages> eventList;
	private List<PowerOutages> solution;
	
	private int maxAffectedPeople;
	
	public Model() {
		podao = new PowerOutageDAO();
		nercList = podao.getNercList();
	}
	

	public List<PowerOutages> getWorstCase(int maxNumberOfYears, int maxHoursOfOutage, Nerc nerc) {

		// fase di inizializzazione
		solution = new ArrayList<>();
		maxAffectedPeople = 0;

		// ottengo gli eventi del NERC selezionato
		eventList = podao.getPowerOutagesByNerc(nerc);

		// ordino la lista di eventi per data
		Collections.sort(eventList);

		// chiamo la procedura ricorsiva
		search(new ArrayList<PowerOutages>(), maxNumberOfYears, maxHoursOfOutage);

		return solution;
	}
	
	private void search(List<PowerOutages> partial, int maxNumberOfYears, int maxHoursOfOutage) {

		// in questo punto la soluzione è sicuramente valida
		// 		-> tengo traccia della soluzione migliore, se necessario. 
		if (sumAffectedPeople(partial) > maxAffectedPeople) {
			maxAffectedPeople = sumAffectedPeople(partial);
			solution = new ArrayList<PowerOutages>(partial);
		}

		//scorro gli eventi (ordinati per data)
		for (PowerOutages event : eventList) {

			// parziale non può contenere eventi duplicati
			if (!partial.contains(event)) {

				//aggiungo l'evento considerato
				partial.add(event);

				// costruisco solo soluzioni valide; se la nuova aggiunta rende parziale non più valida -> non vado avanti con la ricorsione
				// 		-> questo filtro agisce come condizione di terminazione, le funzioni "ritorneranno" senza il bisogno di una return esplicita
				// 		-> alternativamente, il controllo si può inserire all'inzio della funzione ricorsiva, con una return esplicita
				if (checkMaxYears(partial, maxNumberOfYears) && checkMaxHoursOfOutage(partial, maxHoursOfOutage)) {
					search(partial, maxNumberOfYears, maxHoursOfOutage);
				}

				//backtracking
				partial.remove(event);
			}
		}
	}

	public int sumAffectedPeople(List<PowerOutages> partial) {
		int sum = 0;
		for (PowerOutages event : partial) {
			sum += event.getAffectedPeople();
		}
		return sum;
	}

	private boolean checkMaxYears(List<PowerOutages> partial, int maxNumberOfYears) {
		//sfrutto il fatto che gli eventi sono ordinati per data
		//		-> prendo il primo e l'ultimo
		if (partial.size() >=2 ) {
			int y1 = partial.get(0).getYear();
			int y2 = partial.get(partial.size() - 1).getYear();
			if ((y2 - y1) > maxNumberOfYears) {
				return false;
			}
		}
		return true;
	}

	public int sumOutageHours(List<PowerOutages> partial) {
		int sum = 0;
		for (PowerOutages event : partial) {
			sum += event.getOutageDuration();
		}
		return sum;
	}
		
	private boolean checkMaxHoursOfOutage(List<PowerOutages> partial, int maxHoursOfOutage) {
		int sum = sumOutageHours(partial);
		if (sum > maxHoursOfOutage) {
			return false;
		}
		return true;
	}
		

	public List<Nerc> getNercList() {
		return this.nercList;
	}
	
	
	
	
}
