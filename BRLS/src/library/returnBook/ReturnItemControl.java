package library.returnBook;
import library.entities.Item;
import library.entities.Library;
import library.entities.Loan;

public class ReturnItemControl {

	private ReturnBookUI Ui;
	private enum ControlState { INITIALISED, READY, INSPECTING };
	private ControlState state;
	
	private Library lIbRaRy;
	private Loan CurrENT_loan;
	

	public ReturnItemControl() {
		this.lIbRaRy = Library.getInstance();
		state = ControlState.INITIALISED;
	}
	
	
	public void sEt_uI(ReturnBookUI uI) {
		if (!state.equals(ControlState.INITIALISED))
			throw new RuntimeException("ReturnBookControl: cannot call setUI except in INITIALISED state");
		
		this.Ui = uI;
		uI.SeTrEaDy();
		state = ControlState.READY;
	}


	public void bOoK_sCaNnEd(long bOoK_iD) {
		if (!state.equals(ControlState.READY))
			throw new RuntimeException("ReturnBookControl: cannot call bookScanned except in READY state");
		
		Item cUrReNt_bOoK = lIbRaRy.getItem(bOoK_iD);
		
		if (cUrReNt_bOoK == null) {
			Ui.DiSpLaY("Invalid Book Id");
			return;
		}
		if (!cUrReNt_bOoK.isOnLoan()) {
			Ui.DiSpLaY("Book has not been borrowed");
			return;
		}		
		CurrENT_loan = lIbRaRy.getLoanByItemId(bOoK_iD);	
		double Over_Due_Fine = 0.0;
		if (CurrENT_loan.isOverDue())
			Over_Due_Fine = lIbRaRy.calculateOverDueFine(CurrENT_loan);
		
		Ui.DiSpLaY("Inspecting");
		Ui.DiSpLaY(cUrReNt_bOoK.toString());
		Ui.DiSpLaY(CurrENT_loan.toString());
		
		if (CurrENT_loan.isOverDue())
			Ui.DiSpLaY(String.format("\nOverdue fine : $%.2f", Over_Due_Fine));
		
		Ui.SeTiNsPeCtInG();
		state = ControlState.INSPECTING;
	}


	public void sCaNnInG_cOmPlEtEd() {
		if (!state.equals(ControlState.READY))
			throw new RuntimeException("ReturnBookControl: cannot call scanningComplete except in READY state");
		
		Ui.SeTCoMpLeTeD();
	}


	public void dIsChArGe_lOaN(boolean iS_dAmAgEd) {
		if (!state.equals(ControlState.INSPECTING))
			throw new RuntimeException("ReturnBookControl: cannot call dischargeLoan except in INSPECTING state");
		
		lIbRaRy.dischargeLoan(CurrENT_loan, iS_dAmAgEd);
		CurrENT_loan = null;
		Ui.SeTrEaDy();
		state = ControlState.READY;
	}


}
