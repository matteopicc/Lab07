package it.polito.tdp.poweroutages.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.polito.tdp.poweroutages.DAO.PowerOutageDAO;

public class Model {
	
	PowerOutageDAO podao;
	private NercIdMap nercIdMap;
	private List<Nerc> nercList;
	
	private List<PowerOutages> eventList;
	private List<PowerOutages> eventListFiltered;
	private List<PowerOutages> solution;
	
	private int maxAffectedPeople;
	
	
	public Model() {
		podao = new PowerOutageDAO();
		
		nercIdMap = new NercIdMap();
		nercList = podao.getNercList(nercIdMap);
		eventList = podao.getPowerOutageList(nercIdMap);	 
	}
	
	public List<Nerc> getNercList() {
		return podao.getNercList(nercIdMap);
	}
	
	public List<PowerOutages> getWorstCase(int maxNumberOfYears, int maxHoursOfStage, Nerc nerc){
		solution = new ArrayList<PowerOutages>();
		maxAffectedPeople = 0;
		eventListFiltered = new ArrayList<PowerOutages>();
		
		for(PowerOutages event : eventList) {
			if(event.getNerc().equals(nerc)) {
				eventListFiltered.add(event);
			}
		}
		
		Collections.sort(eventListFiltered);
		recursive(new ArrayList<PowerOutages>(),maxNumberOfYears,maxHoursOfStage);
		
		return solution;
	}
	
	public int sumAffectedPeople(List<PowerOutages> partial) {
		int sum = 0;
		for(PowerOutages event : partial) {
			sum += event.getAffectedPeople();
		}
		return sum;
	}

	private boolean checkMaxHoursOfStage(List<PowerOutages> partial, int maxHoursOfStage) {
		int sum = sumOutageHours(partial);
		if(sum>maxHoursOfStage) {
			return false;
		}
		return true;
	}

	public int sumOutageHours(List<PowerOutages> partial) {
		int sum = 0;
		for(PowerOutages event : partial) {
			sum += event.getOutageDuration();
		}
		return sum;
	}

	private boolean checkMaxYears(List<PowerOutages> partial, int maxNumberOfYears) {
		if(partial.size()>=2) {
			int y1 = partial.get(0).getYear();
			int y2 = partial.get(partial.size()-1).getYear();
			if((y2-y1+1)>maxNumberOfYears) {
				return false;
			}
		}
		return true;
	}
	private void recursive(List<PowerOutages> partial,int maxNumberOfYears,int maxHoursOfStage) {
		if(sumAffectedPeople(partial)>maxAffectedPeople) {
			maxAffectedPeople = sumAffectedPeople(partial);
			solution = partial;
		}
		
		for(PowerOutages event : eventListFiltered) {
			if(!partial.contains(event)) {
				partial.add(event);
			if(checkMaxYears(partial,maxNumberOfYears) && checkMaxHoursOfStage(partial,maxHoursOfStage)) {
			   recursive(partial,maxNumberOfYears,maxHoursOfStage);
			   partial.remove(event);	
			}
			
			}
			
		}
	}

	public List<Integer> getYearList() {
		Set<Integer>yearSet = new HashSet<Integer>();
		for(PowerOutages event : eventList) {
			yearSet.add(event.getYear());
		}
		List<Integer>yearList = new ArrayList<Integer>(yearSet);
		yearList.sort(new Comparator<Integer>(){
			@Override
			public int compare(Integer o1, Integer o2) {
				return o2.compareTo(o1);
			}
		});
	
	
      return yearList;
	}
}
