package com.techelevator;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class VendingMachine {

	public Map <String, Integer> inventoryHashMap = new HashMap<String, Integer>();
	public Map <String, Food> vendingMachineSlot = new TreeMap<String, Food>();
	private double currentlyDepositedMoney = 0;
	
	public double getCurrentlyDepositedMoney() {
		return currentlyDepositedMoney;
	}
	public void feedMoney(int dollarsInserted) {
		currentlyDepositedMoney+= dollarsInserted;
	}
	public void chargeMoney(double costOfProduct) {
		currentlyDepositedMoney-= costOfProduct;
	}
	
	
	
	
}
