package library.payfine;
import library.entities.Library;
import library.entities.Patron;

public class pAY_fINE_cONTROL {
	
	private PayFineUI Ui;
	private enum cOnTrOl_sTaTe { INITIALISED, READY, PAYING, COMPLETED, CANCELLED };
	private cOnTrOl_sTaTe StAtE;
	
	private Library LiBrArY;
	private Patron paTRon;


	public pAY_fINE_cONTROL() {
		this.LiBrArY = Library.getInstance();
		StAtE = cOnTrOl_sTaTe.INITIALISED;
	}
	
	
	public void SeT_uI(PayFineUI uI) {
		if (!StAtE.equals(cOnTrOl_sTaTe.INITIALISED)) {
			throw new RuntimeException("PayFineControl: cannot call setUI except in INITIALISED state");
		}	
		this.Ui = uI;
		Ui.setReady();
		StAtE = cOnTrOl_sTaTe.READY;		
	}


	public void CaRd_sWiPeD(long PatROn_Id) {
		if (!StAtE.equals(cOnTrOl_sTaTe.READY)) 
			throw new RuntimeException("PayFineControl: cannot call cardSwiped except in READY state");
			
		paTRon = LiBrArY.getPatron(PatROn_Id);
		
		if (paTRon == null) {
			Ui.display(LiBrArY);
			return;
		}
		Ui.display(paTRon);
		Ui.setPaying();
		StAtE = cOnTrOl_sTaTe.PAYING;
	}
	
	
	public double PaY_FiNe(double AmOuNt) {
		if (!StAtE.equals(cOnTrOl_sTaTe.PAYING)) 
			throw new RuntimeException("PayFineControl: cannot call payFine except in PAYING state");
			
		double ChAnGe = paTRon.payFine(AmOuNt);
		if (ChAnGe > 0) 
			Ui.display(LiBrArY);
		
		Ui.display(paTRon);
		Ui.setCompleted();
		StAtE = cOnTrOl_sTaTe.COMPLETED;
		return ChAnGe;
	}
	
	public void CaNcEl() {
		Ui.setCancelled();
		StAtE = cOnTrOl_sTaTe.CANCELLED;
	}




}
