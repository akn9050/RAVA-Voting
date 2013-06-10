package com.rau.evoting.models;

public enum ElectionState {
	ZERO("Not Opened"), ONE("Opened"), TWO("Closed");
	
	private final String stateName;

	private ElectionState(String stateName) {
		this.stateName = stateName;
	}
	
	public String getStateName() {
		return stateName;
	}
	
	public static ElectionState getStateById(int id) {
		return ElectionState.values()[id];
	}

}
