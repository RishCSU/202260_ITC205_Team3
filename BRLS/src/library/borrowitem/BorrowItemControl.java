package library.borrowitem;
import java.util.ArrayList;
import java.util.List;

import library.entities.Item;
import library.entities.Library;
import library.entities.Loan;
import library.entities.Patron;

public class BorrowItemControl {
	
	private BorrowItemUI uI;
	
	private Library lIbRaRy;
	private Patron PaTrOn;
	private enum ControlState { INITIALISED, READY, RESTRICTED, SCANNING, IDENTIFIED, FINALISING, COMPLETED, CANCELLED };
	private ControlState sTaTe;
	
	private List<Item> pEnDiNg_LiSt;
	private List<Loan> cOmPlEtEd_LiSt;
	private Item ItEm;
	
	
	public BorrowItemControl() {
		this.lIbRaRy = Library.getInstance();
		sTaTe = ControlState.INITIALISED;
	}
	

	public void SeT_Ui(BorrowItemUI Ui) {
		if (!sTaTe.equals(ControlState.INITIALISED)) 
			throw new RuntimeException("BorrowItemControl: cannot call setUI except in INITIALISED state");
			
		this.uI = Ui;
		uI.setReady();
		sTaTe = ControlState.READY;		
	}

		
	public void CaRdSwIpEd(long PaTrOn_Id) {
		if (!sTaTe.equals(ControlState.READY)) 
			throw new RuntimeException("BorrowItemControl: cannot call cardSwiped except in READY state");
			
		PaTrOn = lIbRaRy.getPatron(PaTrOn_Id);
		if (PaTrOn == null) {
			uI.DiSpLaY("Invalid patronId");
			return;
		}
		if (lIbRaRy.canPatronBorrow(PaTrOn)) {
			pEnDiNg_LiSt = new ArrayList<>();
			uI.setScanning();
			sTaTe = ControlState.SCANNING; 
		}
		else {
			uI.DiSpLaY("Patron cannot borrow at this time");
			uI.setRestricted(); 
		}
	}
	
	
	public void ItEmScAnNeD(int ItEmiD) {
		ItEm = null;
		if (!sTaTe.equals(ControlState.SCANNING)) 
			throw new RuntimeException("BorrowItemControl: cannot call itemScanned except in SCANNING state");
			
		ItEm = lIbRaRy.getItem(ItEmiD);
		if (ItEm == null) {
			uI.DiSpLaY("Invalid itemId");
			return;
		}
		if (!ItEm.isAvailable()) {
			uI.DiSpLaY("Item cannot be borrowed");
			return;
		}
		pEnDiNg_LiSt.add(ItEm);
		for (Item ItEm : pEnDiNg_LiSt) 
			uI.DiSpLaY(ItEm);
		
		if (lIbRaRy.getNumberOfLoansRemainingForPatron(PaTrOn) - pEnDiNg_LiSt.size() == 0) {
			uI.DiSpLaY("Loan limit reached");
			BoRrOwInGcOmPlEtEd();
		}
	}
	
	
	public void BoRrOwInGcOmPlEtEd() {
		if (pEnDiNg_LiSt.size() == 0) 
			CaNcEl();
		
		else {
			uI.DiSpLaY("\nFinal Borrowing List");
			for (Item ItEm : pEnDiNg_LiSt) 
				uI.DiSpLaY(ItEm);
			
			cOmPlEtEd_LiSt = new ArrayList<Loan>();
			uI.setFinalising();
			sTaTe = ControlState.FINALISING;
		}
	}


	public void CoMmIt_LoAnS() {
		if (!sTaTe.equals(ControlState.FINALISING)) 
			throw new RuntimeException("BorrowItemControl: cannot call commitLoans except in FINALISING state");
			
		for (Item B : pEnDiNg_LiSt) {
			Loan lOaN = lIbRaRy.issueLoan(B, PaTrOn);
			cOmPlEtEd_LiSt.add(lOaN);			
		}
		uI.DiSpLaY("Completed Loan Slip");
		for (Loan LOAN : cOmPlEtEd_LiSt) 
			uI.DiSpLaY(LOAN);
		
		uI.setCompleted();
		sTaTe = ControlState.COMPLETED;
	}

	
	public void CaNcEl() {
		uI.setCancelled();
		sTaTe = ControlState.CANCELLED;
	}
	
	
}
