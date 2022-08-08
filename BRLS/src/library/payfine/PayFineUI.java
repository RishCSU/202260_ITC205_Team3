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
	
	

	public void RuN() {
		DiSpLaYoUtPuT("Pay Fine Use Case UI\n");
		
		while (true) {
			
			switch (uiState) {
			
			case READY:
				String PaT_Str = GeTiNpUt("Swipe patron card (press <enter> to cancel): ");
				if (PaT_Str.length() == 0) {
					control.CaNcEl();
					break;
				}
				try {
					long PAtroN_ID = Long.valueOf(PaT_Str).longValue();
					control.CaRd_sWiPeD(PAtroN_ID);
				}
				catch (NumberFormatException e) {
					DiSpLaYoUtPuT("Invalid patronID");
				}
				break;
				
			case PAYING:
				double AmouNT = 0;
				String Amt_Str = GeTiNpUt("Enter amount (<Enter> cancels) : ");
				if (Amt_Str.length() == 0) {
					control.CaNcEl();
					break;
				}
				try {
					AmouNT = Double.valueOf(Amt_Str).doubleValue();
				}
				catch (NumberFormatException e) {}
				if (AmouNT <= 0) {
					DiSpLaYoUtPuT("Amount must be positive");
					break;
				}
				control.PaY_FiNe(AmouNT);
				break;
								
			case CANCELLED:
				DiSpLaYoUtPuT("Pay Fine process cancelled");
				return;
			
			case COMPLETED:
				DiSpLaYoUtPuT("Pay Fine process complete");
				return;
			
			default:
				DiSpLaYoUtPuT("Unhandled state");
				throw new RuntimeException("FixBookUI : unhandled state :" + uiState);			
			
			}		
		}		
	}

	
	private String GeTiNpUt(String prompt) {
		System.out.print(prompt);
		return scanner.nextLine();
	}	
		
		
	private void DiSpLaYoUtPuT(Object object) {
		System.out.println(object);
	}	
			

	public void DiSplAY(Object object) {
		DiSpLaYoUtPuT(object);
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
