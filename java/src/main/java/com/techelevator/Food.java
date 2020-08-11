package com.techelevator;

public abstract class Food implements vendable {

	private String name;

	private String type;

	private double price;

	private String slot;
	
	public String getSlot() {
		return slot;
	}
	public double getPrice() {
		return price;
	}
	public String getName() {
		return name;
	}
	public String getType() {
		return type;
	}
	public String toString() {
		return name + "  " + price;
	}
	public Food(String undelimitedString) {
		String[] stringArray = undelimitedString.split("\\|");
		name = stringArray[1];
		type = stringArray[3];
		price = Double.parseDouble(stringArray[2]);
		slot = stringArray[0];
	}
}
