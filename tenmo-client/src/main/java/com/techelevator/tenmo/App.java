package com.techelevator.tenmo;

import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.AuthenticationServiceException;
import com.techelevator.tenmo.services.BankService;
import com.techelevator.view.ConsoleService;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
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

    public App(ConsoleService console, AuthenticationService authenticationService) {
		this.console = console;
		this.authenticationService = authenticationService;
	}

	public static void main(String[] args) {
		restTemplate = new RestTemplate();
		App app = new App(new ConsoleService(System.in, System.out), new AuthenticationService(API_BASE_URL));
		app.run();
	}

	public void run() {
		System.out.println("*********************");
		System.out.println("* Welcome to TEnmo! *");
		System.out.println("*********************");
		
		registerAndLogin();
		mainMenu();
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

	private void login() {
		System.out.println("Please log in");
		currentUser = null;
		while (currentUser == null) //will keep looping until user is logged in
		{
			UserCredentials credentials = collectUserCredentials();
			try {
				currentUser = authenticationService.login(credentials);
				bankService.AUTH_TOKEN = currentUser.getToken();//set token
			} catch (AuthenticationServiceException e) {
				System.out.println("LOGIN ERROR: "+e.getMessage());
				System.out.println("Please attempt to login again.");
			}
		}
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

	private void exitProgram() {
		System.exit(0);
	}

	private void viewCurrentBalance() {
		User user = currentUser.getUser();
		int id = user.getId();
		System.out.println("Your current account balance is: $" + bankService.getAccount(id).getBalance());
	}

	private void viewTransferHistory() {
		displayTransferMenu();

		User user = currentUser.getUser();
		int id = user.getId();
		TransferView[] transferViews = bankService.displayTransfer(id);;

		for (TransferView tv : transferViews) {
			System.out.format("%-15s%-20s$%-30s\n", tv.getTransferId(), tv.getUserType() + ": " + tv.getUsername(), tv.getAmount());
		}

		boolean loop = true;
		while (loop) { loop = viewTransferDetails(user, id); }
	}

	private boolean viewTransferDetails(User user, int id) {
		System.out.println("-----------------");
		System.out.print("Please enter transfer ID to view details (0 to cancel): ");

		String selection = userInput.nextLine();
		if (selection.equals("0")) {
			mainMenu();
		} else {
			if (!isId(selection)) {
				System.out.println("Not ID");
			} else {
				int userInputTransferId = Integer.valueOf(selection);
				TransferView[] transferDetails = bankService.displayTransferDetails(id, userInputTransferId);

				if (transferDetails.length == 0) {
					System.out.println("Wrong TransferId.");
				} else {
					showTransferDetails(user, transferDetails);
					return false;
				}
			}
		}
		return true;
	}

	private void showTransferDetails(User user, TransferView[] transferDetails) {
		System.out.println("--------------------------------------------");
		System.out.println("Transfer Details");
		System.out.println("--------------------------------------------");
		for (TransferView detail : transferDetails) {
			if (detail.getUserType().equals("From")) {
				displayFromTransferDetails(user, detail);
			}
			if (detail.getUserType().equals("To")) {
				displayToTransferDetails(user, detail);
			}
		}
	}

	private void viewPendingRequests() {
		displayPendingTransferMenu();

		User user = currentUser.getUser();
		int id = user.getId();
		TransferView[] transferViews = bankService.getPendingList(id);;

		if (transferViews.length == 0) {
			System.out.println("There is no pending transfers");
			mainMenu();
		}
		List<Integer> ids = getTransferIds(transferViews);
		boolean loop = true;
		while (loop) { loop = viewPendingDetails(ids, id); }
	}

	private boolean viewPendingDetails(List<Integer> ids, int id) {
		System.out.print("Please enter transfer ID to approve/reject (0 to cancel): ");
		String selection = userInput.nextLine();
		if (selection.equals("0")) {
			mainMenu();
		} else {
			if (!isId(selection)) {
				System.out.println("Not ID");
			} else {
				int userInputTransferId = Integer.valueOf(selection);
				boolean isExist = idIdExist(userInputTransferId, ids);
				if (isExist) {
					displayPendingMenu();
					boolean inLoop = true;
					while (inLoop) {
						System.out.println("Please choose an option:");
						selection = userInput.nextLine();
						if (selection.equals("0")) {
							System.out.println("Pending transfer Canceled.");
							mainMenu();
						} else if (!selection.equals("1") && !selection.equals("2")) {
							System.out.println("Please input right choice");
						} else if (selection.equals("2")) {
							return reject(userInputTransferId);
						} else {
							return approve(userInputTransferId, id);
						}
					}
				} else {
					System.out.println("ID not exist.");
				}
			}
		}
		return true;
	}

	private boolean reject(int userInputTransferId) {
		bankService.pendingReject(userInputTransferId);
		System.out.println("Reject paying!");
		return false;
	}

	private boolean approve(int userInputTransferId, int userId) {
		Transfer transfer = bankService.getTranserByTransferId(userInputTransferId);
    	BigDecimal balance = bankService.getAccount(userId).getBalance();
    	if (transfer.getAmount().doubleValue() > balance.doubleValue()) {
			System.out.println("Not enough balance. Approved failed");
			mainMenu();
		} else {
    		bankService.pendingApprove(transfer);
			System.out.println("Approve successful.");
		}
    	return false;
	}

	private void displayTransferMenu() {
		System.out.println("-------------------------------------------");
		System.out.println("Transfers");
		System.out.format("%-15s%-20s%-30s\n", "ID", "From/To","Amount");
		System.out.println("-------------------------------------------");
	}

	private void displayPendingTransferMenu() {
		System.out.println("-------------------------------------------");
		System.out.println("Pending Transfers");
		System.out.format("%-15s%-20s%-30s\n", "ID", "To","Amount");
		System.out.println("-------------------------------------------");
	}

	private void displayPendingMenu() {
		System.out.println("1: Approve");
		System.out.println("2: Reject");
		System.out.println("0: Don't approve or reject");
		System.out.println("---------");
	}

	private void displayUserMenu() {
		System.out.println("-------------------------------------------");
		System.out.println("Users");
		System.out.format("%-20s%-20s\n", "ID", "Name");
		System.out.println("-------------------------------------------");
	}

	private List<Integer> getTransferIds(TransferView[] transferViews) {
		List<Integer> ids = new ArrayList<>();
		for (TransferView tv : transferViews) {
			ids.add( tv.getTransferId().intValue());
			System.out.format("%-15s%-20s$%-30s\n", tv.getTransferId(), tv.getUsername(), tv.getAmount());
		}
		System.out.println("-------------------");
		return ids;
	}

	private void sendBucks() {
		displayUserMenu();

		User user = currentUser.getUser();
		int id = user.getId();
		User[] userList = bankService.displaySendList(id);
		List<Integer> ids = getUserIds(userList);
		System.out.println("-------------------------------------------");
		boolean loop = true;
		while (loop) {
			int userInputId = 0;
			System.out.print("Enter ID of user you are sending to (0 to cancel): ");

			String selection = userInput.nextLine();
			if (selection.equals("0")) {
				mainMenu();
			} else {
				boolean b = isId(selection);
				if (b) {
					userInputId = Integer.valueOf(selection);
					if (idIdExist(userInputId, ids)) {
						handleTransferInput(user, id, userInputId);
						loop = false;
					} else {
						System.out.println("ID not exist.");
					}
				} else {
					System.out.println("Not ID");
				}
			}
		}
	}

	private List<Integer> getUserIds(User[] userList) {
		List<Integer> ids = new ArrayList<>();
		for (User u : userList) {
			ids.add(u.getId());
			System.out.format("%-20s%-20s\n", u.getId(), u.getUsername());
		}
		return ids;
	}

	private void requestBucks() {
		displayUserMenu();

		User user = currentUser.getUser();
		int id = user.getId();
		User[] userList = bankService.displaySendList(id);
		List<Integer> ids = getUserIds(userList);

		System.out.println("-------------------------------------------");
		boolean loop = true;
		while (loop) {
			loop = handleUserRequest(user, id, ids);
		}
	}

	private boolean handleUserRequest(User user, int id, List<Integer> ids) {
		System.out.print("Enter ID of user you are requesting from (0 to cancel): ");
		String selection = userInput.nextLine();
		if (selection.equals("0")) {
			mainMenu();
		} else {
			if (isId(selection)) {
				int userInputId = Integer.valueOf(selection);
				if (idIdExist(userInputId, ids)) {
					handleRequestTransferInput(user, id, userInputId);
					return false;
				} else {
					System.out.println("ID not exist.");
				}
			} else {
				System.out.println("Not ID");
			}
		}
		return true;
	}

	private boolean idIdExist(int id, List<Integer> ids) {
    	int indexOfId = -1;
    	indexOfId = ids.indexOf(id);
    	return indexOfId != -1;
	}

	private void handleTransferInput(User user, int id, int userInputId) {
		boolean loop = true;
		while (loop) {
			loop = handleTransferAmount(user, id, userInputId);
		}
	}

	private boolean handleTransferAmount(User user, int id, int userInputId) {
		System.out.print("Enter amount: ");
		String selection = userInput.nextLine();

		if (!isMoney(selection)) {
			System.out.println("Transfer Money just be Numbers.");
		} else {
			double doubleMoney = Double.valueOf(selection);

			if (doubleMoney > bankService.getAccount(id).getBalance().doubleValue()) {
				System.out.println("Transfer Money greater than the balance. Please input again: ");
			} else if (doubleMoney <= 0) {
				System.out.println("Transfer Money must greater than 0.");
			} else {
				BigDecimal userInputMoney = BigDecimal.valueOf(doubleMoney);
				Transfer transfer = getTransferForSend(user, id, userInputId, userInputMoney);
				bankService.sendBucks(transfer);
				System.out.println("Send Successful!");
				return false;
			}
		}
		return true;
	}

	private void handleRequestTransferInput(User user, int id, int userInputId) {
		boolean loop = true;
		while (loop) {
			loop = handleRequestTransferAmount(user, id, userInputId);
		}
	}

	private boolean handleRequestTransferAmount(User user, int id, int userInputId) {
		System.out.print("Enter amount: ");
		String selection = userInput.nextLine();
		if (!isMoney(selection)) {
			System.out.println("Transfer Money just be Numbers.");
		} else {
			double doubleMoney = Double.valueOf(selection);
			BigDecimal userInputMoney = BigDecimal.valueOf(doubleMoney);
			Transfer transfer = getTransferForRequest(user, id, userInputId, userInputMoney);
			bankService.requestBucks(transfer);
			System.out.println("Request Successful!");
			return false;
		}
		return true;
	}

	private void displayFromTransferDetails(User user, TransferView detail) {
		System.out.format("Id: " + detail.getTransferId() + "\n" +
				"From: " + detail.getUsername() + "\n" +
				"To: " + user.getUsername() + " (Me)\n" +
				"Type: " + detail.getTransferType() + "\n" +
				"Status: " + detail.getTransferStatus() + "\n" +
				"Amount: $" + detail.getAmount() + "\n");
	}

	private void displayToTransferDetails(User user, TransferView detail) {
		System.out.format("Id: " + detail.getTransferId() + "\n" +
				"From: " + user.getUsername() + "(Me)\n" +
				"To: " + detail.getUsername() + "\n" +
				"Type: " + detail.getTransferType() + "\n" +
				"Status: " + detail.getTransferStatus() + "\n" +
				"Amount: $" + detail.getAmount() + "\n");
	}

	private boolean isMoney(String s) {
    	try {
    		double number = Double.valueOf(s);
    		return true;
		} catch (Exception e) {
    		return false;
		}
	}

	private boolean isId(String s) {
		try {
			int number = Integer.valueOf(s);
			return number > 0;
		} catch (Exception e) {
			return false;
		}
	}

	private Transfer getTransferForRequest(User user, int id, int userInputId, BigDecimal userInputMoney) {
		Transfer transfer = new Transfer();
		transfer.setTransferTypeId(Long.valueOf(1));
		transfer.setTransferStatusId(Long.valueOf(1));
		transfer.setToAccountId(bankService.getAccount(id).getAccount_id());
		transfer.setFromAccountId(bankService.getAccount(userInputId).getAccount_id());
		transfer.setAmount(userInputMoney);
		transfer.setFromUserId(Long.valueOf(userInputId));
		transfer.setToUserId(Long.valueOf(user.getId()));
		return transfer;
	}

	private Transfer getTransferForSend(User user, int id, int userInputId, BigDecimal userInputMoney) {
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

	private boolean isAuthenticated() {
		return currentUser != null;
	}
	
	private UserCredentials collectUserCredentials() {
		String username = console.getUserInput("Username");
		String password = console.getUserInput("Password");
		return new UserCredentials(username, password);
	}
}
