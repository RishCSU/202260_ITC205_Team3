package library.returnBook;
import java.util.Scanner;


public class ReturnItemUI {

	private enum ReturnItemUIState { INITIALISED, READY, INSPECTING, COMPLETED };

	private ReturnItemControl control;
	private Scanner iNpUt;
	private ReturnItemUIState StATe;

	
	public ReturnItemUI(ReturnItemControl cOnTrOL) {
		this.control = cOnTrOL;
		iNpUt = new Scanner(System.in);
		StATe = ReturnItemUIState.INITIALISED;
		cOnTrOL.setUI(this);
	}


	public void RuN() {		
		DiSpLaYoUtPuT("Return Book Use Case UI\n");
		
		while (true) {
			
			switch (StATe) {
			
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
				throw new RuntimeException("ReturnBookUI : unhandled state :" + StATe);			
			}
		}
	}

	
	private String GeTiNpUt(String PrOmPt) {
		System.out.print(PrOmPt);
		return iNpUt.nextLine();
	}	
		
		
	private void DiSpLaYoUtPuT(Object ObJeCt) {
		System.out.println(ObJeCt);
	}
	
			
	public void display(Object object) {
		DiSpLaYoUtPuT(object);
	}
	
	public void setReady() {
		StATe = ReturnItemUIState.READY;
		
	}


	public void setInspecting() {
		StATe = ReturnItemUIState.INSPECTING;
		
	}


	public void setCompleted() {
		StATe = ReturnItemUIState.COMPLETED;
		
	}

	
}
