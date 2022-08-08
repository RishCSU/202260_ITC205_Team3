package library.returnBook;
import java.util.Scanner;


public class ReturnItemUI {

	private enum ReturnItemUIState { INITIALISED, READY, INSPECTING, COMPLETED };

	private ReturnItemControl control;
	private Scanner scanner;
	private ReturnItemUIState uiState;

	
	public ReturnItemUI(ReturnItemControl returnItemControl) {
		this.control = returnItemControl;
		scanner = new Scanner(System.in);
		uiState = ReturnItemUIState.INITIALISED;
		returnItemControl.setUI(this);
	}


	public void run() {
		displayOutput("Return Book Use Case UI\n");
		
		while (true) {
			
			switch (uiState) {
			
			case INITIALISED:
				break;
				
			case READY:
				String bookInputString = getInput("Scan Book (<enter> completes): ");
				if (bookInputString.length() == 0) {
					control.scanningCompleted();
				}
				
				else {
					try {
						long bookId = Long.valueOf(bookInputString).longValue();
						control.itemScanned(bookId);
					}
					catch (NumberFormatException e) {
						displayOutput("Invalid bookId");
					}					
				}
				break;				
				
			case INSPECTING:
				String AnS = getInput("Is book damaged? (Y/N): ");
				boolean Is_DAmAgEd = false;
				if (AnS.toUpperCase().equals("Y")) 					
					Is_DAmAgEd = true;
				
				control.dischargeLoan(Is_DAmAgEd);
			
			case COMPLETED:
				displayOutput("Return processing complete");
				return;
			
			default:
				displayOutput("Unhandled state");
				throw new RuntimeException("ReturnBookUI : unhandled state :" + uiState);
			}
		}
	}

	
	private String getInput(String PrOmPt) {
		System.out.print(PrOmPt);
		return scanner.nextLine();
	}	
		
		
	private void displayOutput(Object ObJeCt) {
		System.out.println(ObJeCt);
	}
	
			
	public void display(Object object) {
		displayOutput(object);
	}
	
	public void setReady() {
		uiState = ReturnItemUIState.READY;
		
	}


	public void setInspecting() {
		uiState = ReturnItemUIState.INSPECTING;
		
	}


	public void setCompleted() {
		uiState = ReturnItemUIState.COMPLETED;
		
	}

	
}
