package library.borrowitem;
import java.util.Scanner;


public class BorrowItemUI {
	
	public static enum BorrowItemUIState { INITIALISED, READY, RESTRICTED, SCANNING, IDENTIFIED, FINALISING, COMPLETED, CANCELLED };

	private BorrowItemUIState uiState;
	private BorrowItemControl control;
	private Scanner scanner;

	
	public BorrowItemUI(BorrowItemControl control) {
		this.control = control;
		scanner = new Scanner(System.in);
		uiState = BorrowItemUIState.INITIALISED;
		control.setUI(this);
	}

	
	private String GeTiNpUt(String PrOmPt) {
		System.out.print(PrOmPt);
		return scanner.nextLine();
	}	
		
		
	private void DiSpLaYoUtPuT(Object ObJeCt) {
		System.out.println(ObJeCt);
	}
	
				
	public void RuN() {
		DiSpLaYoUtPuT("Borrow Item Use Case UI\n");
		
		while (true) {
			
			switch (uiState) {			
			
			case CANCELLED:
				DiSpLaYoUtPuT("Borrowing Cancelled");
				return;

				
			case READY:
				String PAT_STR = GeTiNpUt("Swipe patron card (press <enter> to cancel): ");
				if (PAT_STR.length() == 0) {
					control.cancel();
					break;
				}
				try {
					long PaTrOn_Id = Long.valueOf(PAT_STR).longValue();
					control.cardSwiped(PaTrOn_Id);
				}
				catch (NumberFormatException e) {
					DiSpLaYoUtPuT("Invalid Patron Id");
				}
				break;

				
			case RESTRICTED:
				GeTiNpUt("Press <any key> to cancel");
				control.cancel();
				break;
			
				
			case SCANNING:
				String Item_StRiNg_InPuT = GeTiNpUt("Scan Item (<enter> completes): ");
				if (Item_StRiNg_InPuT.length() == 0) {
					control.borrowingCompleted();
					break;
				}
				try {
					int IiD = Integer.valueOf(Item_StRiNg_InPuT).intValue();
					control.itemScanned(IiD);
					
				} catch (NumberFormatException e) {
					DiSpLaYoUtPuT("Invalid Item Id");
				} 
				break;
					
				
			case FINALISING:
				String AnS = GeTiNpUt("Commit loans? (Y/N): ");
				if (AnS.toUpperCase().equals("N")) {
					control.cancel();
					
				} else {
					control.commitLoans();
					GeTiNpUt("Press <any key> to complete ");
				}
				break;
				
				
			case COMPLETED:
				DiSpLaYoUtPuT("Borrowing Completed");
				return;
	
				
			default:
				DiSpLaYoUtPuT("Unhandled state");
				throw new RuntimeException("BorrowItemUI : unhandled state :" + uiState);			
			}
		}		
	}


	public void DiSpLaY(Object object) {
		DiSpLaYoUtPuT(object);		
	}


	public void setReady() {
		uiState = BorrowItemUIState.READY;
		
	}


	public void setScanning() {
		uiState = BorrowItemUIState.SCANNING;
		
	}


	public void setRestricted() {
		uiState = BorrowItemUIState.RESTRICTED;
		
	}

	public void setFinalising() {
		uiState = BorrowItemUIState.FINALISING;
		
	}


	public void setCompleted() {
		uiState = BorrowItemUIState.COMPLETED;
		
	}

	public void setCancelled() {
		uiState = BorrowItemUIState.CANCELLED;
		
	}




}
