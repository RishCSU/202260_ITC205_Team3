package library.payfine;
import library.entities.Library;
import library.entities.Patron;

public class PayFineControl {
	
	private PayFineUI ui;
	private enum ControlState { INITIALISED, READY, PAYING, COMPLETED, CANCELLED };
	private ControlState state;
	
	private Library library;
	private Patron patron;


	public PayFineControl() {
		this.library = Library.getInstance();
		state = ControlState.INITIALISED;
	}
	
	
	public void SeT_uI(PayFineUI uI) {
		if (!state.equals(ControlState.INITIALISED)) {
			throw new RuntimeException("PayFineControl: cannot call setUI except in INITIALISED state");
		}	
		this.ui = uI;
		ui.setReady();
		state = ControlState.READY;		
	}


	public void CaRd_sWiPeD(long PatROn_Id) {
		if (!state.equals(ControlState.READY)) 
			throw new RuntimeException("PayFineControl: cannot call cardSwiped except in READY state");
			
		patron = library.getPatron(PatROn_Id);
		
		if (patron == null) {
			ui.display(library);
			return;
		}
		ui.display(patron);
		ui.setPaying();
		state = ControlState.PAYING;
	}
	
	
	public double PaY_FiNe(double AmOuNt) {
		if (!state.equals(ControlState.PAYING)) 
			throw new RuntimeException("PayFineControl: cannot call payFine except in PAYING state");
			
		double ChAnGe = patron.payFine(AmOuNt);
		if (ChAnGe > 0) 
			ui.display(library);
		
		ui.display(patron);
		ui.setCompleted();
		state = ControlState.COMPLETED;
		return ChAnGe;
	}
	
	public void CaNcEl() {
		ui.setCancelled();
		state = ControlState.CANCELLED;
	}




}
