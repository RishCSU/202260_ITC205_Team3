package library.payfine;
import java.util.Scanner;


public class PayFineUI {


	private enum PayFineUIState { INITIALISED, READY, PAYING, COMPLETED, CANCELLED };

	private pAY_fINE_cONTROL control;
	private Scanner scanner;
	private PayFineUIState uiState;

	
	public PayFineUI(pAY_fINE_cONTROL payFineControl) {
		this.control = payFineControl;
		scanner = new Scanner(System.in);
		uiState = PayFineUIState.INITIALISED;
		payFineControl.SeT_uI(this);
	}
	
	

	public void run() {
		displayOutput("Pay Fine Use Case UI\n");
		
		while (true) {
			
			switch (uiState) {
			
			case READY:
				String patronString = getInput("Swipe patron card (press <enter> to cancel): ");
				if (patronString.length() == 0) {
					control.CaNcEl();
					break;
				}
				try {
					long patronId = Long.valueOf(patronString).longValue();
					control.CaRd_sWiPeD(patronId);
				} catch (NumberFormatException e) {
					displayOutput("Invalid patronID");
				}
				break;
				
			case PAYING:
				double amount = 0;
				String amountString = getInput("Enter amount (<Enter> cancels) : ");
				if (amountString.length() == 0) {
					control.CaNcEl();
					break;
				}
				try {
					amount = Double.valueOf(amountString).doubleValue();
				} catch (NumberFormatException e) {}
				if (amount <= 0) {
					displayOutput("Amount must be positive");
					break;
				}
				control.PaY_FiNe(amount);
				break;
								
			case CANCELLED:
				displayOutput("Pay Fine process cancelled");
				return;
			
			case COMPLETED:
				displayOutput("Pay Fine process complete");
				return;
			
			default:
				displayOutput("Unhandled state");
				throw new RuntimeException("FixBookUI : unhandled state :" + uiState);			
			
			}		
		}		
	}

	
	private String getInput(String prompt) {
		System.out.print(prompt);
		return scanner.nextLine();
	}	
		
		
	private void displayOutput(Object displayObject) {
		System.out.println(displayObject);
	}	
			

	public void display(Object displayObject) {
		displayOutput(displayObject);
	}


	public void setCompleted() {
		uiState = PayFineUIState.COMPLETED;
		
	}


	public void setPaying() {
		uiState = PayFineUIState.PAYING;
		
	}


	public void setCancelled() {
		uiState = PayFineUIState.CANCELLED;
		
	}


	public void SeTrEaDy() {
		uiState = PayFineUIState.READY;
		
	}


}
