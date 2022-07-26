package library.returnBook;
import library.entities.Item;
import library.entities.Library;
import library.entities.Loan;

public class ReturnItemControl {

    private ReturnItemUI ui;
    private enum ControlState { INITIALISED, READY, INSPECTING };
    private ControlState state;
    
    private Library library;
    private Loan currentLoan;

    public ReturnItemControl() {
        this.library = Library.getInstance();
        state = ControlState.INITIALISED;
    }

    public void setUI(ReturnItemUI ui) {
        if (!state.equals(ControlState.INITIALISED)) {
            throw new RuntimeException("ReturnBookControl: cannot call setUI except in INITIALISED state");
        }
        
        this.ui = ui;
        ui.setReady();
        state = ControlState.READY;
    }

    public void itemScanned(long bookId) {
        if (!state.equals(ControlState.READY)) {
            throw new RuntimeException("ReturnBookControl: cannot call bookScanned except in READY state");
        }
        
        Item currentBookId = library.getItem(bookId);
        
        if (currentBookId == null) {
            ui.display("Invalid Book Id");
            return;
        }
        if (!currentBookId.isOnLoan()) {
            ui.display("Book has not been borrowed");
            return;
        }        
        currentLoan = library.getLoanByItemId(bookId);
        double overDueFine = 0.0;
        if (currentLoan.isOverDue()) {
            overDueFine = library.calculateOverDueFine(currentLoan);
        }
        
        ui.display("Inspecting");
        String stringCurrentBookId = currentBookId.toString();
        ui.display(stringCurrentBookId);

        String stringCurrentLoan = currentLoan.toString();
        ui.display(stringCurrentLoan);
        
        if (currentLoan.isOverDue()) {
            String stringOverDueFine = String.format("\nOverdue fine : $%.2f", overDueFine);
            ui.display(stringOverDueFine);
        }
        ui.setInspecting();
        state = ControlState.INSPECTING;
    }

    public void scanningCompleted() {
        if (!state.equals(ControlState.READY)) {
            throw new RuntimeException("ReturnBookControl: cannot call scanningComplete except in READY state");
        }
        ui.setCompleted();
    }

    public void dischargeLoan(boolean isDamaged) {
        if (!state.equals(ControlState.INSPECTING)) {
            throw new RuntimeException("ReturnBookControl: cannot call dischargeLoan except in INSPECTING state");
        }
        library.dischargeLoan(currentLoan, isDamaged);
        currentLoan = null;
        ui.setReady();
        state = ControlState.READY;
    }
}
