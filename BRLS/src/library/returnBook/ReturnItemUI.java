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


	public void RuN() {		
		DiSpLaYoUtPuT("Return Book Use Case UI\n");
		
		while (true) {
			
			switch (uiState) {
			
			case INITIALISED:
				break;
				
			case READY:
				String BoOk_InPuT_StRiNg = GeTiNpUt("Scan Book (<enter> completes): ");
				if (BoOk_InPuT_StRiNg.length() == 0) 
					control.scanningCompleted();
				
				else {
					try {
						long Book_Id = Long.valueOf(BoOk_InPuT_StRiNg).longValue();
						control.itemScanned(Book_Id);
					}
					catch (NumberFormatException e) {
						DiSpLaYoUtPuT("Invalid bookId");
					}					
				}
				break;				
				
			case INSPECTING:
				String AnS = GeTiNpUt("Is book damaged? (Y/N): ");
				boolean Is_DAmAgEd = false;
				if (AnS.toUpperCase().equals("Y")) 					
					Is_DAmAgEd = true;
				
				control.dischargeLoan(Is_DAmAgEd);
			
			case COMPLETED:
				DiSpLaYoUtPuT("Return processing complete");
				return;
			
			default:
				DiSpLaYoUtPuT("Unhandled state");
				throw new RuntimeException("ReturnBookUI : unhandled state :" + uiState);
			}
		}
	}

	
	private String GeTiNpUt(String PrOmPt) {
		System.out.print(PrOmPt);
		return scanner.nextLine();
	}	
		
		
	private void DiSpLaYoUtPuT(Object ObJeCt) {
		System.out.println(ObJeCt);
	}
	
			
	public void display(Object object) {
		DiSpLaYoUtPuT(object);
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
