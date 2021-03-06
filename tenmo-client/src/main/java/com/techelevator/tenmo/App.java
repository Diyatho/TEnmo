package com.techelevator.tenmo;

import java.math.BigDecimal;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import com.techelevator.tenmo.models.AuthenticatedUser;
import com.techelevator.tenmo.models.TransactionHistory;
import com.techelevator.tenmo.models.TransferFunds;
import com.techelevator.tenmo.models.User;
import com.techelevator.tenmo.models.UserAction;
import com.techelevator.tenmo.models.UserCredentials;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.AuthenticationServiceException;
import com.techelevator.view.ConsoleService;

public class App {

	private static final String API_BASE_URL = "http://localhost:8080/";

	private static final String MENU_OPTION_EXIT = "Exit";
	private static final String LOGIN_MENU_OPTION_REGISTER = "Register";
	private static final String LOGIN_MENU_OPTION_LOGIN = "Login";
	private static final String[] LOGIN_MENU_OPTIONS = { LOGIN_MENU_OPTION_REGISTER, LOGIN_MENU_OPTION_LOGIN,
			MENU_OPTION_EXIT };
	private static final String MAIN_MENU_OPTION_VIEW_BALANCE = "View your current balance";
	private static final String MAIN_MENU_OPTION_SEND_BUCKS = "Send TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS = "View your past transfers";
	private static final String MAIN_MENU_OPTION_REQUEST_BUCKS = "Request TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS = "View your pending requests";
	private static final String MAIN_MENU_OPTION_LOGIN = "Login as different user";
	private static final String[] MAIN_MENU_OPTIONS = { MAIN_MENU_OPTION_VIEW_BALANCE, MAIN_MENU_OPTION_SEND_BUCKS,
			MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS, MAIN_MENU_OPTION_REQUEST_BUCKS,
			MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS, MAIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT };

	private AuthenticatedUser currentUser;
	private ConsoleService console;
	private AuthenticationService authenticationService;
	public RestTemplate restTemplate = new RestTemplate();
	private BigDecimal currentBalance;

	public static void main(String[] args) {
		App app = new App(new ConsoleService(System.in, System.out), new AuthenticationService(API_BASE_URL));
		app.run();
	}

	public App(ConsoleService console, AuthenticationService authenticationService) {
		this.console = console;
		this.authenticationService = authenticationService;
	}

	public void run() {
		System.out.println("*********************");
		System.out.println("* Welcome to TEnmo! *");
		System.out.println("*********************");

		registerAndLogin();
		mainMenu();
	}

	private void mainMenu() {
		while (true) {
			String choice = (String) console.getChoiceFromOptions(MAIN_MENU_OPTIONS);
			if (MAIN_MENU_OPTION_VIEW_BALANCE.equals(choice)) {
				viewCurrentBalance();
			} else if (MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS.equals(choice)) {
				viewTransferHistory();
			} else if (MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS.equals(choice)) {
				viewPendingRequests();
			} else if (MAIN_MENU_OPTION_SEND_BUCKS.equals(choice)) {
				sendBucks();
			} else if (MAIN_MENU_OPTION_REQUEST_BUCKS.equals(choice)) {
				requestBucks();
			} else if (MAIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else {
				// the only other option on the main menu is to exit
				exitProgram();
			}
		}
	}

	private void viewCurrentBalance() {
		try {
			currentBalance = restTemplate
					.exchange(API_BASE_URL + "/tenmo/" + currentUser.getUser().getId() + "/balance", HttpMethod.GET,
							makeAuthEntity(), BigDecimal.class)
					.getBody();
			System.out.println("The Current Balance is " + currentBalance);
		} catch (RestClientResponseException e) {
			console.printError(e.getRawStatusCode() + " " + e.getStatusText());
		} catch (ResourceAccessException e) {
			console.printError(e.getMessage());
		}

	}

	private void viewTransferHistory() {
		try {

			TransactionHistory[] history = restTemplate
					.exchange(API_BASE_URL + "/tenmo/" + currentUser.getUser().getId() + "/history", HttpMethod.GET,
							makeAuthEntity(), TransactionHistory[].class)
					.getBody();
			console.printTransactions(history, currentUser);
			int transferID = console.getUserInputInteger("Please enter transfer ID to view details (0 to cancel): ");
			console.printTransactionDetails(transferID, history);
		} catch (RestClientResponseException e) {
			console.printError(e.getRawStatusCode() + " " + e.getStatusText());
		} catch (ResourceAccessException e) {
			console.printError(e.getMessage());
		}

	}

	private void viewPendingRequests() {
		try {
			boolean actionResponse = false;
			TransactionHistory[] requests = restTemplate.exchange(API_BASE_URL + "/tenmo/" + currentUser.getUser().getId() + "/requests", HttpMethod.GET, makeAuthEntity(), TransactionHistory[].class).getBody();
		console.printPendingRequests(requests);
		int transferId = console.getUserInputInteger("Please enter transfer ID to Approve/Reject (0 to cancel): ");
		int action = console.getUserInputInteger("1: Approve\n2: Reject\n0: Don't approve or reject\nPlease choose an option");
		if (action == 1){
			approve(requests, transferId);
		}
		if(action == 1 || action == 2) {
			UserAction userAction = new UserAction(transferId, action, currentUser.getUser().getId());
			actionResponse = restTemplate.exchange(API_BASE_URL + "/tenmo/action", HttpMethod.POST, makeActionEntity(userAction), Boolean.class).getBody();
		}
		if (actionResponse) {
			if(action == 1) {
				System.out.println("Request Approved");
			}
			if(action == 2) {
				System.out.println("Request Rejected");
			}
		}
		else {
			System.out.println("Unable to execute");
		}
		
		} catch (RestClientResponseException e) {
			console.printError(e.getRawStatusCode() + " " + e.getStatusText());
		} catch (ResourceAccessException e) {
			console.printError(e.getMessage());
		}

	}
	private boolean approve (TransactionHistory[] requests, int transferId) {
		viewCurrentBalance();
		boolean actionResponse = false;
		for (TransactionHistory request : requests) {
			if(request.getTransferId() == transferId) {
				if(request.getAmount().compareTo(currentBalance) > 0) {
					System.out.println("Insufficient balance to approve request");
					return false;
				}
				else {
					UserAction userAction = new UserAction(transferId, 1, currentUser.getUser().getId());
					actionResponse = restTemplate.exchange(API_BASE_URL + "/tenmo/action", HttpMethod.POST, makeActionEntity(userAction), Boolean.class).getBody();
				}
				
			}
		}
		return actionResponse;
	}

	private void sendBucks() {

		try {
			User[] users = restTemplate
					.exchange(API_BASE_URL + "/tenmo/" + currentUser.getUser().getId() + "/getOtherUsers",
							HttpMethod.GET, makeAuthEntity(), User[].class)
					.getBody();
			System.out.println("Registered Users");
			console.printUsers(users);
			int otherUserId = console.getUserInputInteger("Enter ID of user you are sending to (0 to cancel): ");
			BigDecimal amountToBeSent = new BigDecimal(console.getUserInput("Enter amount of money to be sent "));
			TransferFunds transferFunds = new TransferFunds(currentUser.getUser().getId(), otherUserId, amountToBeSent);
			Boolean isTransfered = restTemplate.exchange(API_BASE_URL + "/tenmo/transfer", HttpMethod.POST,
					makeTransferEntity(transferFunds), Boolean.class).getBody();
			if (isTransfered == true) {
				System.out.println("Money sent successfully");
				viewCurrentBalance();
			}
		} catch (RestClientResponseException e) {
			console.printError(e.getRawStatusCode() + " " + e.getStatusText());
		} catch (ResourceAccessException e) {
			console.printError(e.getMessage());
		}

		// return users;
	}

	private void requestBucks() {
		try {
			User[] users = restTemplate
					.exchange(API_BASE_URL + "/tenmo/" + currentUser.getUser().getId() + "/getOtherUsers",
							HttpMethod.GET, makeAuthEntity(), User[].class)
					.getBody();
			System.out.println("Registered Users");
			console.printUsers(users);
			int otherUserId = console.getUserInputInteger("Enter ID of user you are requesting from (0 to cancel): ");
			BigDecimal amountRequested = new BigDecimal(console.getUserInput("Enter amount of money to be sent "));
			TransferFunds transferFunds = new TransferFunds(otherUserId, currentUser.getUser().getId(),
					amountRequested);
			Boolean isRequested = restTemplate.exchange(API_BASE_URL + "/tenmo/request", HttpMethod.POST,
					makeTransferEntity(transferFunds), Boolean.class).getBody();
			if (isRequested == true) {
				System.out.println("Money requested successfully");
				// viewCurrentBalance();
			}
		} catch (RestClientResponseException e) {
			console.printError(e.getRawStatusCode() + " " + e.getStatusText());
		} catch (ResourceAccessException e) {
			console.printError(e.getMessage());
		}

	}

	private void exitProgram() {
		System.exit(0);
	}

	private void registerAndLogin() {
		while (!isAuthenticated()) {
			String choice = (String) console.getChoiceFromOptions(LOGIN_MENU_OPTIONS);
			if (LOGIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else if (LOGIN_MENU_OPTION_REGISTER.equals(choice)) {
				register();
			} else {
				// the only other option on the login menu is to exit
				exitProgram();
			}
		}
	}

	private boolean isAuthenticated() {
		return currentUser != null;
	}

	private void register() {
		System.out.println("Please register a new user account");
		boolean isRegistered = false;
		while (!isRegistered) // will keep looping until user is registered
		{
			UserCredentials credentials = collectUserCredentials();
			try {
				authenticationService.register(credentials);
				isRegistered = true;
				System.out.println("Registration successful. You can now login.");
			} catch (AuthenticationServiceException e) {
				System.out.println("REGISTRATION ERROR: " + e.getMessage());
				System.out.println("Please attempt to register again.");
			}
		}
	}

	private void login() {
		System.out.println("Please log in");
		currentUser = null;
		while (currentUser == null) // will keep looping until user is logged in
		{
			UserCredentials credentials = collectUserCredentials();
			try {
				currentUser = authenticationService.login(credentials);
			} catch (AuthenticationServiceException e) {
				System.out.println("LOGIN ERROR: " + e.getMessage());
				System.out.println("Please attempt to login again.");
			}
		}
	}

	private UserCredentials collectUserCredentials() {
		String username = console.getUserInput("Username");
		String password = console.getUserInput("Password");
		return new UserCredentials(username, password);
	}

	private HttpEntity makeAuthEntity() {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(currentUser.getToken());
		HttpEntity entity = new HttpEntity<>(headers);
		return entity;
	}

	private HttpEntity<TransferFunds> makeTransferEntity(TransferFunds transferFunds) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(currentUser.getToken());
		HttpEntity<TransferFunds> entity = new HttpEntity<>(transferFunds, headers);
		return entity;
	}

	private HttpEntity<UserAction> makeActionEntity(UserAction userAction) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(currentUser.getToken());
		HttpEntity<UserAction> entity = new HttpEntity<>(userAction, headers);
		return entity;
	}
}
