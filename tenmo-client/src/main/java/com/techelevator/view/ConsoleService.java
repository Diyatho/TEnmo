package com.techelevator.view;


import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

import com.techelevator.tenmo.models.TransferFunds;
import com.techelevator.tenmo.models.User;

public class ConsoleService {

	private PrintWriter out;
	private Scanner in;

	public ConsoleService(InputStream input, OutputStream output) {
		this.out = new PrintWriter(output, true);
		this.in = new Scanner(input);
	}

	public Object getChoiceFromOptions(Object[] options) {
		Object choice = null;
		while (choice == null) {
			displayMenuOptions(options);
			choice = getChoiceFromUserInput(options);
		}
		out.println();
		return choice;
	}

	private Object getChoiceFromUserInput(Object[] options) {
		Object choice = null;
		String userInput = in.nextLine();
		try {
			int selectedOption = Integer.valueOf(userInput);
			if (selectedOption > 0 && selectedOption <= options.length) {
				choice = options[selectedOption - 1];
			}
		} catch (NumberFormatException e) {
			// eat the exception, an error message will be displayed below since choice will be null
		}
		if (choice == null) {
			out.println(System.lineSeparator() + "*** " + userInput + " is not a valid option ***" + System.lineSeparator());
		}
		return choice;
	}

	private void displayMenuOptions(Object[] options) {
		out.println();
		for (int i = 0; i < options.length; i++) {
			int optionNum = i + 1;
			out.println(optionNum + ") " + options[i]);
		}
		out.print(System.lineSeparator() + "Please choose an option >>> ");
		out.flush();
	}

	public String getUserInput(String prompt) {
		out.print(prompt+": ");
		out.flush();
		return in.nextLine();
	}

	public Integer getUserInputInteger(String prompt) {
		Integer result = null;
		do {
			out.print(prompt+": ");
			out.flush();
			String userInput = in.nextLine();
			try {
				result = Integer.parseInt(userInput);
			} catch(NumberFormatException e) {
				out.println(System.lineSeparator() + "*** " + userInput + " is not valid ***" + System.lineSeparator());
			}
		} while(result == null);
		return result;
	}
	 public void printError(String errorMessage) {
		    System.err.println(errorMessage);
	 }
	 public void printUsers(User[] users) {
		 System.out.println("---------------------------------------");
		 System.out.println("ID" + String.format("%1$18s", "Name"));
		 System.out.println("---------------------------------------");
		 for(User user : users) {
			 System.out.println(user.getId() + String.format("%1$18s", user.getUsername()));
			 //System.out.println(user.getId() + ". " + user.getUsername());
		 }
		 
	 }
	 public void printTransactions(TransferFunds[] transferFunds) {
		 System.out.println("---------------------------------------");
		 System.out.println("ID" + String.format("%1$18s", "Sender") + String.format("%1$18s", "Receiever") + String.format("%1$18s", "Amount"));
		 System.out.println("---------------------------------------");
		 for(TransferFunds transferFund : transferFunds) {
			 System.out.println(transferFund.getTransferId() + String.format("%1$18s", transferFund.getSenderId()) + String.format("%1$18s", transferFund.getReceiverId()) + String.format("%1$18s", "$") + String.format("%1$4s", transferFund.getAmount()));
		 }
	 }
}
