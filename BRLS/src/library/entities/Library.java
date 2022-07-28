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
				try (ObjectInputStream libraryFile = new ObjectInputStream(new FileInputStream(LIBRARY_FILE));) {
					self = (Library) libraryFile.readObject();
					Calendar.getInstance().setDate(self.currentDate);
					libraryFile.close();
				}
				catch (Exception e) {
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
			try (ObjectOutputStream libraryFile = new ObjectOutputStream(new FileOutputStream(LIBRARY_FILE));) {
				libraryFile.writeObject(self);
				libraryFile.flush();
				libraryFile.close();	
			}
			catch (Exception e) {
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
		Patron newPatron = new Patron(firstName, lastName, emailAddress, phoneNumber, getNextPatronId());
		patrons.put(newPatron.getId(), newPatron);		
		return newPatron;
	}

	
	public Item addItem(String author, String title, String callNumber, ItemType itemType) {		
		Item newItem = new Item(author, title, callNumber, itemType, getNextItemId());
		catalog.put(newItem.getId(), newItem);
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
		if (patron.getNumberOfCurrentLoans() == LOAN_LIMIT ) { 
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
		Date dueDate = Calendar.getInstance().getDueDate(LOAN_PERIOD);
		Loan loan = new Loan(getNextLoanId(), book, patron, dueDate);
		patron.takeOutLoan(loan);
		book.takeOut();
		loans.put(loan.getId(), loan);
		currentLoans.put(book.getId(), loan);
		return loan;
	}
	
	
	public Loan getLoanByItemId(long itemId) {
		if (currentLoans.containsKey(itemId)) { 
			return currentLoans.get(itemId);
		}
		return null;
	}

	
	public double calculateOverDueFine(Loan loan) {
		if (loan.isOverDue()) {
			long daysOverDue = Calendar.getInstance().getDaysDifference(loan.getDueDate());
			double fine = daysOverDue * FINE_PER_DAY;
			return fine;
		}
		return 0.0;		
	}


	public void dischargeLoan(Loan currentLoan, boolean isDamaged) {
		Patron patron = currentLoan.getPatron();
		Item item  = currentLoan.getItem();
		
		double overDueFine = calculateOverDueFine(currentLoan);
		patron.addFine(overDueFine);	
		
		patron.dischargeLoan(currentLoan);
		item.takeBack(isDamaged);
		if (isDamaged) {
			patron.addFine(DAMAGE_FEE);
			damagedItems.put(item.getId(), item);
		}
		currentLoan.discharge();
		currentLoans.remove(item.getId());
	}


	public void updateCurrentLoansStatus() {
		for (Loan loan : currentLoans.values()) {
			loan.updateStatus();
		}		
	}


	public void repairItem(Item currentItem) {
		if (damagedItems.containsKey(currentItem.getId())) {
			currentItem.repair();
			damagedItems.remove(currentItem.getId());
		} else {
			throw new RuntimeException("Library: repairItem: item is not damaged");
		}
	}
}
