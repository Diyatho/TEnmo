package com.techelevator.view;


import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

import com.techelevator.tenmo.models.AuthenticatedUser;
import com.techelevator.tenmo.models.TransactionHistory;
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
		 System.out.println("Users");
		 System.out.println("ID" + String.format("%1$18s", "Name"));
		 System.out.println("---------------------------------------");
		 for(User user : users) {
			 System.out.println(user.getId() + String.format("%1$18s", user.getUsername()));
			 //System.out.println(user.getId() + ". " + user.getUsername());
		 }
		 
	 }
	 public void printTransactions(TransactionHistory[] history, AuthenticatedUser currentUser) {
		 if(history == null) {
			 System.out.println("There are no transactions to display");
			 return;
		 }
		 if(history.length == 0) {
			 System.out.println("There are no transactions to display");
			 return;
		 }
		 String direction = null, personName = null;
		 System.out.println("---------------------------------------");
		 System.out.println("Transfers");
		 System.out.println("ID" + String.format("%1$18s", "From/To") + String.format("%1$18s", "Amount"));
		 System.out.println("---------------------------------------");
		 for(TransactionHistory transaction : history) {
			 if(transaction.getTransfer_type_id() == 2) {
				 if(transaction.getSenderName().equals(currentUser.getUser().getUsername()))  {
				 direction = "To:  ";
				 personName = transaction.getReceiverName();
				 } 
				 if(transaction.getReceiverName().equals(currentUser.getUser().getUsername())) {
					direction = "From:";
					personName = transaction.getSenderName();
				 }
			 }
			 else if(transaction.getTransfer_type_id() == 1) {
				 
				 if(transaction.getSenderName().equals(currentUser.getUser().getUsername()))  {
					 direction = "To:  ";
					 personName = transaction.getReceiverName();
					 } 
					 if(transaction.getReceiverName().equals(currentUser.getUser().getUsername())) {
						direction = "From:";
						personName = transaction.getSenderName();
					 }
			 }
			 
			 
			 //System.out.println(transaction.getTransferId() + String.format("%1$15s", direction) + String.format("%1$5s", personName) + String.format("%1$12s", "$") + String.format("%1$5s", transaction.getAmount()));
			 System.out.println(transaction.getTransferId() + addSpace(Integer.toString(transaction.getTransferId()).length(), 11) + direction + " " + personName + addSpace((direction + " " + personName).length(), 18) + "$" + String.format("%1$5s", transaction.getAmount()));
		 }
		 
	 }
	 
	
	 public void printPendingRequests(TransactionHistory[] requests) {
		 if(requests == null) {
			 System.out.println("There are no pending requests");
			 return;
		 }
		 if(requests.length == 0) {
			 System.out.println("There are no pending requests");
			 return;
		 }
		 System.out.println("---------------------------------------");
		 System.out.println("Pending Transfers");
		 System.out.println("ID" + String.format("%1$12s", "To") + String.format("%1$18s", "Amount"));
		 System.out.println("---------------------------------------");
		 
		 for(TransactionHistory request : requests) {
			 System.out.println(request.getTransferId() + addSpace(Integer.toString(request.getTransferId()).length(), 12) + request.getReceiverName() + addSpace(request.getReceiverName().length(), 12) + "$"+ String.format("%1$5s",request.getAmount()));
		 }
		 
	 }
	 
	 
	 public String addSpace(int lengthOfWord, int totalLength)
		{
			String space ="";
			for(int i = lengthOfWord; i <= totalLength; i++) {
				space += " ";
			}
			return space;
		}

	public void printTransactionDetails(int transferId, TransactionHistory[] transactions) {
		TransactionHistory history = new TransactionHistory();
		for (TransactionHistory transaction : transactions) {
			if(transaction.getTransferId() == transferId) {
				history = transaction;
			}
		}
		System.out.println("---------------------------------------");
		System.out.println("Transfer Details");
		System.out.println("---------------------------------------");
		System.out.println("Id: " + history.getTransferId());
		System.out.println("From: " + history.getSenderName());
		System.out.println("To: " + history.getReceiverName());
		System.out.println("Type: " + history.getTransfer_type_id());
		System.out.println("Status: " + history.getTransfer_status_desc());
		System.out.println("Amount: " + history.getAmount());
		
	}
}
