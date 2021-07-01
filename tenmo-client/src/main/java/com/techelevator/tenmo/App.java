package com.techelevator.tenmo;

import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.AuthenticationServiceException;
import com.techelevator.tenmo.services.BankService;
import com.techelevator.view.ConsoleService;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class App {

	private final Scanner userInput = new Scanner(System.in);

    private static final String API_BASE_URL = "http://localhost:8080/";
    
    private static final String MENU_OPTION_EXIT = "Exit";
    private static final String LOGIN_MENU_OPTION_REGISTER = "Register";
	private static final String LOGIN_MENU_OPTION_LOGIN = "Login";
	private static final String[] LOGIN_MENU_OPTIONS = { LOGIN_MENU_OPTION_REGISTER, LOGIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT };
	private static final String MAIN_MENU_OPTION_VIEW_BALANCE = "View your current balance";
	private static final String MAIN_MENU_OPTION_SEND_BUCKS = "Send TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS = "View your past transfers";
	private static final String MAIN_MENU_OPTION_REQUEST_BUCKS = "Request TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS = "View your pending requests";
	private static final String MAIN_MENU_OPTION_LOGIN = "Login as different user";
	private static final String[] MAIN_MENU_OPTIONS = { MAIN_MENU_OPTION_VIEW_BALANCE, MAIN_MENU_OPTION_SEND_BUCKS, MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS, MAIN_MENU_OPTION_REQUEST_BUCKS, MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS, MAIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT };
	
    private AuthenticatedUser currentUser;
    private ConsoleService console;
    private AuthenticationService authenticationService;
    private BankService bankService = new BankService(API_BASE_URL);
    private static RestTemplate restTemplate;

    public static void main(String[] args) {
    	restTemplate = new RestTemplate();
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
		while(true) {
			String choice = (String)console.getChoiceFromOptions(MAIN_MENU_OPTIONS);
			if(MAIN_MENU_OPTION_VIEW_BALANCE.equals(choice)) {
				viewCurrentBalance();
			} else if(MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS.equals(choice)) {
				viewTransferHistory();
			} else if(MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS.equals(choice)) {
				viewPendingRequests();
			} else if(MAIN_MENU_OPTION_SEND_BUCKS.equals(choice)) {
				sendBucks();
			} else if(MAIN_MENU_OPTION_REQUEST_BUCKS.equals(choice)) {
				requestBucks();
			} else if(MAIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else {
				// the only other option on the main menu is to exit
				exitProgram();
			}
		}
	}

	private void viewCurrentBalance() {
		User user = currentUser.getUser();
		int id = user.getId();
		System.out.println("Your current account balance is: $" + bankService.getAccount(id).getBalance());
	}

	private void viewTransferHistory() {
		// TODO Auto-generated method stub
		
	}

	private void viewPendingRequests() {
		// TODO Auto-generated method stub
		
	}

	private void sendBucks() {
		System.out.println("-------------------------------------------");
		System.out.println("Users");
		System.out.format("%-20s%-20s\n", "ID", "Name");
		System.out.println("-------------------------------------------");

		User user = currentUser.getUser();
		int id = user.getId();
		User[] userList = bankService.displaySendList(id);

		for (User u : userList) {
			System.out.format("%-20s%-20s\n", u.getId(), u.getUsername());
		}
		System.out.println("-------------------------------------------");

		int userInputId = 0;
		BigDecimal userInputMoney;
		System.out.print("Enter ID of user you are sending to (0 to cancel): ");

		String selection = userInput.nextLine();
		if (selection.equals("0")) {
			mainMenu();
		} else {
			userInputId = Integer.valueOf(selection);
		}

		handleTransferInput(user, id, userInputId);
	}

	private void handleTransferInput(User user, int id, int userInputId) {
		String selection;
		BigDecimal userInputMoney;
		boolean loop = true;
		while (loop) {
			System.out.print("Enter amount: ");
			selection = userInput.nextLine();
			double doubleMoney = Double.valueOf(selection);
			userInputMoney = BigDecimal.valueOf(doubleMoney);
			if (userInputMoney.compareTo(bankService.getAccount(id).getBalance()) > -1) {
				System.out.println("Transfer Money greater than the balance. Please input again: ");
			} else {
				Transfer transfer = getTransfer(user, id, userInputId, userInputMoney);
				bankService.sendBucks(transfer);
				System.out.println("Send Successful!");
				loop = false;
			}
		}
	}

	private Transfer getTransfer(User user, int id, int userInputId, BigDecimal userInputMoney) {
		Transfer transfer = new Transfer();
		transfer.setTransferTypeId(Long.valueOf(2));
		transfer.setTransferStatusId(Long.valueOf(2));
		transfer.setFromAccountId(bankService.getAccount(id).getAccount_id());
		transfer.setToAccountId(bankService.getAccount(userInputId).getAccount_id());
		transfer.setAmount(userInputMoney);
		transfer.setFromUserId(Long.valueOf(user.getId()));
		transfer.setToUserId(Long.valueOf(userInputId));
		return transfer;
	}

	private void requestBucks() {
		// TODO Auto-generated method stub
		
	}
	
	private void exitProgram() {
		System.exit(0);
	}

	private void registerAndLogin() {
		while(!isAuthenticated()) {
			String choice = (String)console.getChoiceFromOptions(LOGIN_MENU_OPTIONS);
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
        while (!isRegistered) //will keep looping until user is registered
        {
            UserCredentials credentials = collectUserCredentials();
            try {
            	authenticationService.register(credentials);
            	isRegistered = true;
            	System.out.println("Registration successful. You can now login.");
            } catch(AuthenticationServiceException e) {
            	System.out.println("REGISTRATION ERROR: "+e.getMessage());
				System.out.println("Please attempt to register again.");
            }
        }
	}

	private void login() {
		System.out.println("Please log in");
		currentUser = null;
		while (currentUser == null) //will keep looping until user is logged in
		{
			UserCredentials credentials = collectUserCredentials();
		    try {
				currentUser = authenticationService.login(credentials);
				bankService.AUTH_TOKEN = currentUser.getToken();
			} catch (AuthenticationServiceException e) {
				System.out.println("LOGIN ERROR: "+e.getMessage());
				System.out.println("Please attempt to login again.");
			}
		}
	}
	
	private UserCredentials collectUserCredentials() {
		String username = console.getUserInput("Username");
		String password = console.getUserInput("Password");
		return new UserCredentials(username, password);
	}
}
