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
				String PaT_Str = getInput("Swipe patron card (press <enter> to cancel): ");
				if (PaT_Str.length() == 0) {
					control.CaNcEl();
					break;
				}
				try {
					long PAtroN_ID = Long.valueOf(PaT_Str).longValue();
					control.CaRd_sWiPeD(PAtroN_ID);
				}
				catch (NumberFormatException e) {
					displayOutput("Invalid patronID");
				}
				break;
				
			case PAYING:
				double AmouNT = 0;
				String Amt_Str = getInput("Enter amount (<Enter> cancels) : ");
				if (Amt_Str.length() == 0) {
					control.CaNcEl();
					break;
				}
				try {
					AmouNT = Double.valueOf(Amt_Str).doubleValue();
				}
				catch (NumberFormatException e) {}
				if (AmouNT <= 0) {
					displayOutput("Amount must be positive");
					break;
				}
				control.PaY_FiNe(AmouNT);
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


	public void SeTcOmPlEtEd() {
		uiState = PayFineUIState.COMPLETED;
		
	}


	public void SeTpAyInG() {
		uiState = PayFineUIState.PAYING;
		
	}


	public void SeTcAnCeLlEd() {
		uiState = PayFineUIState.CANCELLED;
		
	}


	public void SeTrEaDy() {
		uiState = PayFineUIState.READY;
		
	}


}
