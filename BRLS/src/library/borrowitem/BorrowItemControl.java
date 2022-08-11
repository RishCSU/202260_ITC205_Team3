package library.borrowitem;
import java.util.ArrayList;
import java.util.List;

import library.entities.Item;
import library.entities.Library;
import library.entities.Loan;
import library.entities.Patron;

public class BorrowItemControl {
	
	private BorrowItemUI ui;
	
	private Library library;
	private Patron patron;
	private enum ControlState { INITIALISED, READY, RESTRICTED, SCANNING, IDENTIFIED, FINALISING, COMPLETED, CANCELLED };
	private ControlState state;
	
	private List<Item> pendingList;
	private List<Loan> completedList;
	private Item item;
	
	
	public BorrowItemControl() {
		this.library = Library.getInstance();
		state = ControlState.INITIALISED;
	}
	

	public void SeT_Ui(BorrowItemUI Ui) {
		if (!state.equals(ControlState.INITIALISED)) 
			throw new RuntimeException("BorrowItemControl: cannot call setUI except in INITIALISED state");
			
		this.ui = Ui;
		ui.setReady();
		state = ControlState.READY;		
	}

		
	public void CaRdSwIpEd(long PaTrOn_Id) {
		if (!state.equals(ControlState.READY)) 
			throw new RuntimeException("BorrowItemControl: cannot call cardSwiped except in READY state");
			
		patron = library.getPatron(PaTrOn_Id);
		if (patron == null) {
			ui.DiSpLaY("Invalid patronId");
			return;
		}
		if (library.canPatronBorrow(patron)) {
			pendingList = new ArrayList<>();
			ui.setScanning();
			state = ControlState.SCANNING; 
		}
		else {
			ui.DiSpLaY("Patron cannot borrow at this time");
			ui.setRestricted(); 
		}
	}
	
	
	public void ItEmScAnNeD(int ItEmiD) {
		item = null;
		if (!state.equals(ControlState.SCANNING)) 
			throw new RuntimeException("BorrowItemControl: cannot call itemScanned except in SCANNING state");
			
		item = library.getItem(ItEmiD);
		if (item == null) {
			ui.DiSpLaY("Invalid itemId");
			return;
		}
		if (!item.isAvailable()) {
			ui.DiSpLaY("Item cannot be borrowed");
			return;
		}
		pendingList.add(item);
		for (Item ItEm : pendingList) 
			ui.DiSpLaY(ItEm);
		
		if (library.getNumberOfLoansRemainingForPatron(patron) - pendingList.size() == 0) {
			ui.DiSpLaY("Loan limit reached");
			BoRrOwInGcOmPlEtEd();
		}
	}
	
	
	public void BoRrOwInGcOmPlEtEd() {
		if (pendingList.size() == 0) 
			CaNcEl();
		
		else {
			ui.DiSpLaY("\nFinal Borrowing List");
			for (Item ItEm : pendingList) 
				ui.DiSpLaY(ItEm);
			
			completedList = new ArrayList<Loan>();
			ui.setFinalising();
			state = ControlState.FINALISING;
		}
	}


	public void CoMmIt_LoAnS() {
		if (!state.equals(ControlState.FINALISING)) 
			throw new RuntimeException("BorrowItemControl: cannot call commitLoans except in FINALISING state");
			
		for (Item B : pendingList) {
			Loan lOaN = library.issueLoan(B, patron);
			completedList.add(lOaN);			
		}
		ui.DiSpLaY("Completed Loan Slip");
		for (Loan LOAN : completedList) 
			ui.DiSpLaY(LOAN);
		
		ui.setCompleted();
		state = ControlState.COMPLETED;
	}

	
	public void CaNcEl() {
		ui.setCancelled();
		state = ControlState.CANCELLED;
	}
	
	
}
