package library.entities;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class Library implements Serializable {
    
    private static final String LIBRARY_FILE = "library.obj";
    private static final int LOAN_LIMIT = 2;
    private static final int LOAN_PERIOD = 2;
    private static final double FINE_PER_DAY = 1.0;
    private static final double MAX_FINES_ALLOWED = 1.0;
    private static final double DAMAGE_FEE = 2.0;
    
    private static Library self;
    private long nextItemId;
    private long nextPatronId;
    private long nextLoanId;
    private Date currentDate;
    
    private Map<Long, Item> catalog;
    private Map<Long, Patron> patrons;
    private Map<Long, Loan> loans;
    private Map<Long, Loan> currentLoans;
    private Map<Long, Item> damagedItems;
    

    private Library() {
        catalog = new HashMap<>();
        patrons = new HashMap<>();
        loans = new HashMap<>();
        currentLoans = new HashMap<>();
        damagedItems = new HashMap<>();
        nextItemId = 1;
        nextPatronId = 1;
        nextLoanId = 1;
    }

    
    public static synchronized Library getInstance() {
        if (self == null) {
            Path PATH = Paths.get(LIBRARY_FILE);
            if (Files.exists(PATH)) {
                try (
                    FileInputStream libraryStream = new FileInputStream(LIBRARY_FILE);
                    ObjectInputStream libraryFile = new ObjectInputStream(libraryStream);
                ) {
                    self = (Library) libraryFile.readObject();
                    Calendar.getInstance().setDate(self.currentDate);
                    libraryFile.close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                self = new Library();
            }
        }
        return self;
    }

    
    public static synchronized void save() {
        if (self != null) {
            self.currentDate = Calendar.getInstance().getDate();
            try (
                FileOutputStream libraryStream = new FileOutputStream(LIBRARY_FILE);
                ObjectOutputStream libraryFile = new ObjectOutputStream(libraryStream);
            ) {
                libraryFile.writeObject(self);
                libraryFile.flush();
                libraryFile.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    
    private long getNextItemId() {
        return nextItemId++;
    }

    
    private long getNextPatronId() {
        return nextPatronId++;
    }

    
    private long getNextLoanId() {
        return nextLoanId++;
    }

    
    public List<Patron> listPatrons() {
        return new ArrayList<Patron>(patrons.values()); 
    }


    public List<Item> listItems() {
        return new ArrayList<Item>(catalog.values()); 
    }


    public List<Loan> listCurrentLoans() {
        return new ArrayList<Loan>(currentLoans.values());
    }


    public Patron addPatron(String firstName, String lastName, String emailAddress, long phoneNumber) {
        long newPatronId = getNextPatronId();
        Patron newPatron = new Patron(firstName, lastName, emailAddress, phoneNumber, newPatronId);
        patrons.put(newPatronId, newPatron);
        return newPatron;
    }

    
    public Item addItem(String author, String title, String callNumber, ItemType itemType) {
        long newItemId = getNextItemId();
        Item newItem = new Item(author, title, callNumber, itemType, newItemId);
        catalog.put(newItemId, newItem);
        return newItem;
    }

    
    public Patron getPatron(long patronId) {
        if (patrons.containsKey(patronId)) {
            return patrons.get(patronId);
        }
        return null;
    }

    
    public Item getItem(long itemId) {
        if (catalog.containsKey(itemId)) {
            return catalog.get(itemId);
        }
        return null;
    }

    
    public int getLoanLimit() {
        return LOAN_LIMIT;
    }

    
    public boolean canPatronBorrow(Patron patron) {
        if (patron.getNumberOfCurrentLoans() == LOAN_LIMIT) { 
            return false;
        }
            
        if (patron.getFinesOwed() >= MAX_FINES_ALLOWED) {
            return false;
        }
            
        for (Loan loan : patron.getLoans()) {
            if (loan.isOverDue()) {
                return false;
            }
        }
        return true;
    }

    
    public int getNumberOfLoansRemainingForPatron(Patron patron) {
        return LOAN_LIMIT - patron.getNumberOfCurrentLoans();
    }
    

    // As per the Class Diagram, the variable name of the first parameter is book.
    public Loan issueLoan(Item book, Patron patron) {
        long newLoanId = getNextLoanId();
        Date dueDate = Calendar.getInstance().getDueDate(LOAN_PERIOD);
        Loan newLoan = new Loan(newLoanId, book, patron, dueDate);
        patron.takeOutLoan(newLoan);
        book.takeOut();
        loans.put(newLoan.getId(), newLoan);
        currentLoans.put(book.getId(), newLoan);
        return newLoan;
    }
    
    
    public Loan getLoanByItemId(long itemId) {
        if (currentLoans.containsKey(itemId)) { 
            return currentLoans.get(itemId);
        }
        return null;
    }

    
    public double calculateOverDueFine(Loan loan) {
        if (loan.isOverDue()) {
            Date loanDueDate = loan.getDueDate();
            long daysOverDue = Calendar.getInstance().getDaysDifference(loanDueDate);
            double fine = daysOverDue * FINE_PER_DAY;
            return fine;
        }
        return 0.0;
    }


    public void dischargeLoan(Loan currentLoan, boolean isDamaged) {
        Patron currentPatron = currentLoan.getPatron();
        Item currentItem  = currentLoan.getItem();
        long currentItemId = currentItem.getId();
        
        double overDueFine = calculateOverDueFine(currentLoan);
        currentPatron.addFine(overDueFine);
        
        currentPatron.dischargeLoan(currentLoan);
        currentItem.takeBack(isDamaged);
        if (isDamaged) {
            currentPatron.addFine(DAMAGE_FEE);
            damagedItems.put(currentItemId, currentItem);
        }
        currentLoan.discharge();
        currentLoans.remove(currentItemId);
    }


    public void updateCurrentLoansStatus() {
        for (Loan loan : currentLoans.values()) {
            loan.updateStatus();
        }
    }


    public void repairItem(Item currentItem) {
        long currentItemId = currentItem.getId();
        if (damagedItems.containsKey(currentItemId)) {
            currentItem.repair();
            damagedItems.remove(currentItemId);
        } else {
            throw new RuntimeException("Library: repairItem: item is not damaged");
        }
    }
}
