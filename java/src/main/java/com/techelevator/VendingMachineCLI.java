package com.techelevator;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore.Entry;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.techelevator.view.Menu;

public class VendingMachineCLI {

	private static final String MAIN_MENU_OPTION_DISPLAY_ITEMS = "Display Vending Machine Items";
	private static final String MAIN_MENU_OPTION_PURCHASE = "Purchase";
	private static final String MAIN_MENU_OPTION_EXIT = "Exit";
	private static final String MAIN_MENU_OPTION_SALES_REPORT = "Sales Report";
	private static final String[] MAIN_MENU_OPTIONS = { MAIN_MENU_OPTION_DISPLAY_ITEMS, MAIN_MENU_OPTION_PURCHASE,
			MAIN_MENU_OPTION_EXIT, MAIN_MENU_OPTION_SALES_REPORT };

	private Menu menu;

	public VendingMachineCLI(Menu menu) {
		this.menu = menu;
	}

	public void run() {
		try {
			FileWriter fileClearer = new FileWriter("log.txt");
			fileClearer.write("");
			fileClearer.close();
			fileClearer = new FileWriter("salesreport.txt");
			fileClearer.write("");
			fileClearer.close();
		} catch (IOException e1) {
		}

		// constant variables
		final double DOLLAR_COST = 1.00;
		final double QUARTER_COST = 0.25;
		final double DIME_COST = 0.10;
		final double NICKEL_COST = 0.05;
		final double PENNY_COST = 0.01;
		final int MAX_STOCK = 5;
		// add a number formatter
		NumberFormat formatter = new DecimalFormat("#0.00");
		// instantiate a vending machine
		VendingMachine vendingMachine = new VendingMachine();
		// hard-code the path to our input file because it would not make sense for the
		// user to be able to control that
		String filePath = "VendingMachine.txt";
		// instantiate an array list that will hold each line from the input file
		// separately
		List<String> configurationList = new ArrayList<String>();
		// instantiate a scanner that reads from our input file
		Scanner fileScanner = new Scanner(filePath);
		// instantiate a second scanner, used to read user input
		Scanner userInputScanner = new Scanner(System.in);
		// get the lines of text from the input file and store them in our array list
		try {
			configurationList = Files.readAllLines(Paths.get(filePath));
		} catch (IOException e) {
		}
		// create
		ArrayList<Food> foodArrayList = new ArrayList<Food>();
		// loop through each line of input from text file
		for (String foodObjectInfo : configurationList) {
			// split the lines of text by the delimiter (|)
			String[] stringArray = foodObjectInfo.split("\\|");
			final int indexOfFoodTypeForStringArray = 3;
			String foodType = stringArray[indexOfFoodTypeForStringArray];
			// depending on what type of food it is, use a different constructor to
			// instantiate the food
			// and store it into an array list
			switch (foodType) {
			case "Chip":
				foodArrayList.add(new Chips(foodObjectInfo));
				break;
			case "Gum":
				foodArrayList.add(new Gum(foodObjectInfo));
				break;
			case "Candy":
				foodArrayList.add(new Candy(foodObjectInfo));
				break;
			case "Drink":
				foodArrayList.add(new Soda(foodObjectInfo));
				break;
			}
			// add the <Food, integer> pair to the inventory map and the <String, Food> pair
			// to the slot map
			final int lastIndexOfFoodArrayList = foodArrayList.size() - 1;
			vendingMachine.inventoryHashMap.put((foodArrayList.get(lastIndexOfFoodArrayList).getSlot()), MAX_STOCK);
			vendingMachine.vendingMachineSlot.put(foodArrayList.get(lastIndexOfFoodArrayList).getSlot(),
					foodArrayList.get(lastIndexOfFoodArrayList));
		}
		// main menu user interface
		while (true) {
			String choice = (String) menu.getChoiceFromOptions(MAIN_MENU_OPTIONS);
			// display the products for sale via console
			if (choice.equals(MAIN_MENU_OPTION_DISPLAY_ITEMS)) {
				vendingMachine.vendingMachineSlot.forEach((key, value) -> System.out
						.println(key + " " + foodArrayList.get(foodArrayList.indexOf(value))));
			} else if (choice.equals(MAIN_MENU_OPTION_PURCHASE)) {
				// purchase menu user interface
				while (true) {
					final String PURCHASE_MENU_OPTION_FEED_MONEY = "Feed Money";
					final String PURCHASE_MENU_OPTION_SELECT_PRODUCT = "Select Product";
					final String PURCHASE_MENU_OPTION_FINISH_TRANSACTION = "Finish Transaction";
					final String[] PURCHASE_MENU_OPTIONS = { PURCHASE_MENU_OPTION_FEED_MONEY,
							PURCHASE_MENU_OPTION_SELECT_PRODUCT, PURCHASE_MENU_OPTION_FINISH_TRANSACTION };
					String purchaseMenuChoice = (String) menu.getChoiceFromOptions(PURCHASE_MENU_OPTIONS);
					if (purchaseMenuChoice.equals(PURCHASE_MENU_OPTION_FEED_MONEY)) {
						// add however much money the user adds to the vending machine balance as an
						// integer (since bills only)
						System.out.println("How much money (bills only) do you want to enter?");
						int amountOfMoneyEntered = (Integer.parseInt(userInputScanner.nextLine()));
						vendingMachine.feedMoney(amountOfMoneyEntered);
						System.out.println(
								"Current balance: $" + formatter.format(vendingMachine.getCurrentlyDepositedMoney()));

						FileWriter fileWriter;
						try {
							fileWriter = new FileWriter("log.txt", true);
							SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
							Date date = new Date();
							fileWriter.append(dateFormatter.format(date) + " FEED MONEY $ "
									+ formatter.format(amountOfMoneyEntered) + " $ "
									+ formatter.format(vendingMachine.getCurrentlyDepositedMoney()) + "\r\n");
							fileWriter.flush();
						} catch (IOException e) {

						}

					} else if (purchaseMenuChoice.equals(PURCHASE_MENU_OPTION_SELECT_PRODUCT)) {
						// if user has a balance of 0, send them an error telling them to load money
						// first
						if (vendingMachine.getCurrentlyDepositedMoney() == 0) {
							System.out.println("Please enter money before attempting to purchase a product.");
						} else {
							// asks user for what slot they would like to purchase from
							String userSelectedSlot;
							// check that the slot that the user asked for exists and exit the loop once we
							// have a valid value
							while (true) {
								System.out.println("Please enter the slot you would like to dispense from.");
								userSelectedSlot = userInputScanner.nextLine();
								if (vendingMachine.inventoryHashMap.containsKey(userSelectedSlot)) {
									break;
								}
							}
							// checks that item is in stock
							int stockOfFood = vendingMachine.inventoryHashMap.get(userSelectedSlot);
							Food selectedFood = vendingMachine.vendingMachineSlot.get(userSelectedSlot);
							if (stockOfFood > 0) {
								if (selectedFood.getPrice() <= vendingMachine.getCurrentlyDepositedMoney()) {
									System.out.println(selectedFood.consumptionCaptionSound());
									vendingMachine.chargeMoney(selectedFood.getPrice());
									stockOfFood--;
									vendingMachine.inventoryHashMap.put(userSelectedSlot, stockOfFood);

									FileWriter fileWriter;
									try {
										fileWriter = new FileWriter("log.txt", true);
										SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
										Date date = new Date();
										fileWriter.append(dateFormatter.format(date) + " " + selectedFood.getName()
												+ " " + selectedFood.getSlot() + "  $  "
												+ formatter.format(selectedFood.getPrice()) + " $ "
												+ formatter.format(vendingMachine.getCurrentlyDepositedMoney())
												+ "\r\n");
										fileWriter.flush();

									} catch (IOException e) {

									}

								}
							} else {
								// item is not in stock
								System.out.println("Item is out of stock");
								break;
							}
						}
					} else if (purchaseMenuChoice.equals(PURCHASE_MENU_OPTION_FINISH_TRANSACTION)) {
						// end transaction process
						// declare counts of each coin type
						int quarterCount = 0;
						int dimeCount = 0;
						int nickelCount = 0;
						int pennyCount = 0;
						int dollarCount = 0;
						// the logic is to make as many of the largest currency type as possible,
						// then move down to the next highest type, subtracting the value of the coins
						// we have created from the current balance,
						// eventually reaching 0
						if (vendingMachine.getCurrentlyDepositedMoney() / 1.00 > 0) {
							dollarCount += (int) (vendingMachine.getCurrentlyDepositedMoney() / .9999);
							vendingMachine.chargeMoney(dollarCount * DOLLAR_COST);
						}
						if (vendingMachine.getCurrentlyDepositedMoney() / 0.25 > 0) {
							quarterCount += (int) (vendingMachine.getCurrentlyDepositedMoney() / 0.2499);
							vendingMachine.chargeMoney(quarterCount * QUARTER_COST);
						}
						if (vendingMachine.getCurrentlyDepositedMoney() / 0.10 > 0) {
							dimeCount += (int) (vendingMachine.getCurrentlyDepositedMoney() / 0.0999);
							vendingMachine.chargeMoney(dimeCount * DIME_COST);
						}
						if (vendingMachine.getCurrentlyDepositedMoney() / 0.05 > 0) {
							nickelCount += (int) (vendingMachine.getCurrentlyDepositedMoney() / 0.0499);
							vendingMachine.chargeMoney(nickelCount * NICKEL_COST);
						}
						if (vendingMachine.getCurrentlyDepositedMoney() / 0.01 > 0) {
							pennyCount += (int) (vendingMachine.getCurrentlyDepositedMoney() / 0.0099);
							vendingMachine.chargeMoney(pennyCount * PENNY_COST);
						}
						// let the user know how many coins they got back
						double amountOfChangeDue = ((PENNY_COST * pennyCount) + (NICKEL_COST * nickelCount) +(DIME_COST * dimeCount)+ (QUARTER_COST * quarterCount)+ (DOLLAR_COST * dollarCount));
						
						System.out.println("Transaction complete.  Your change is " + dollarCount + " dollars, "
								+ quarterCount + " quarters, " + dimeCount + " dimes, " + nickelCount + " nickels, "
								+ pennyCount + " pennies");
						// set balance = 0 just in case, even though the process above should drain
						// balance to 0
						vendingMachine.chargeMoney(vendingMachine.getCurrentlyDepositedMoney());

						FileWriter fileWriter;						
						try {
							fileWriter = new FileWriter("log.txt", true);
							SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
							Date date = new Date();
							fileWriter.append(dateFormatter.format(date) + " GIVE CHANGE $ "
									+ formatter.format(amountOfChangeDue) + " $ "
									+ formatter.format(vendingMachine.getCurrentlyDepositedMoney()) + "\r\n");
							fileWriter.flush();
						} catch (IOException e) {
						}

						break;
					}
				}
			} else if (choice.equals(MAIN_MENU_OPTION_EXIT)) {
				System.exit(0);
			} else if (choice.equals(MAIN_MENU_OPTION_SALES_REPORT)) {
				FileWriter fileWriter;						
				try {
					double totalSales = 0;
					fileWriter = new FileWriter("salesreport.txt", true);
					for( Map.Entry<String, Food> entry : vendingMachine.vendingMachineSlot.entrySet()) {
						fileWriter.append(entry.getValue().getName() + " | " + (MAX_STOCK - vendingMachine.inventoryHashMap.get(entry.getKey())) + "\r\n");
						fileWriter.flush();
						totalSales+= (MAX_STOCK - vendingMachine.inventoryHashMap.get(entry.getKey())) * vendingMachine.vendingMachineSlot.get(entry.getKey()).getPrice();
					}
					fileWriter.append("\r\n\r\n**TOTAL SALES** $" + formatter.format(totalSales));
					fileWriter.flush();
				} catch (IOException e) {
				}
			}
		}
	}

	public static void main(String[] args) {
		Menu menu = new Menu(System.in, System.out);
		VendingMachineCLI cli = new VendingMachineCLI(menu);
		cli.run();
	}
}
