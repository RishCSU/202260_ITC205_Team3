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
    

    public void setUI(BorrowItemUI ui) {
        if (!state.equals(ControlState.INITIALISED)) {
            throw new RuntimeException("BorrowItemControl: cannot call setUI except in INITIALISED state");
        }

        this.ui = ui;
        ui.setReady();
        state = ControlState.READY;        
    }

        
    public void cardSwiped(long patronId) {
        if (!state.equals(ControlState.READY)) {
            throw new RuntimeException("BorrowItemControl: cannot call cardSwiped except in READY state");
        }

        patron = library.getPatron(patronId);
        if (patron == null) {
            ui.DiSpLaY("Invalid patronId");
            return;
        }

        if (library.canPatronBorrow(patron)) {
            pendingList = new ArrayList<>();
            ui.setScanning();
            state = ControlState.SCANNING; 
        } else {
            ui.DiSpLaY("Patron cannot borrow at this time");
            ui.setRestricted(); 
        }
    }
    
    
    public void itemScanned(int itemId) {
        item = null;
        if (!state.equals(ControlState.SCANNING)) {
            throw new RuntimeException("BorrowItemControl: cannot call itemScanned except in SCANNING state");
        }

        item = library.getItem(itemId);
        if (item == null) {
            ui.DiSpLaY("Invalid itemId");
            return;
        }

        if (!item.isAvailable()) {
            ui.DiSpLaY("Item cannot be borrowed");
            return;
        }

        pendingList.add(item);
        for (Item item : pendingList) {
            ui.DiSpLaY(item);
        }

        if (library.getNumberOfLoansRemainingForPatron(patron) - pendingList.size() == 0) {
            ui.DiSpLaY("Loan limit reached");
            borrowingCompleted();
        }
    }
    
    
    public void borrowingCompleted() {
        if (pendingList.size() == 0) {
            cancel();
        } else {
            ui.DiSpLaY("\nFinal Borrowing List");
            for (Item item : pendingList) {
                ui.DiSpLaY(item);
            }
            completedList = new ArrayList<Loan>();
            ui.setFinalising();
            state = ControlState.FINALISING;
        }
    }


    public void commitLoans() {
        if (!state.equals(ControlState.FINALISING)) {
            throw new RuntimeException("BorrowItemControl: cannot call commitLoans except in FINALISING state");
        }

        for (Item item : pendingList) {
            Loan loan = library.issueLoan(item, patron);
            completedList.add(loan);            
        }

        ui.DiSpLaY("Completed Loan Slip");
        for (Loan loan : completedList) {
            ui.DiSpLaY(loan);
        }

        ui.setCompleted();
        state = ControlState.COMPLETED;
    }

    
    public void cancel() {
        ui.setCancelled();
        state = ControlState.CANCELLED;
    }
    
    
}
