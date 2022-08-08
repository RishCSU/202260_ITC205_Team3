package library;

import library.borrowitem.BorrowItemUI;
import library.borrowitem.bORROW_IteM_cONTROL;
import library.entities.*;
import library.fixitem.FixItemUI;
import library.fixitem.fIX_iTeM_cONTROL;
import library.payfine.PayFineUI;
import library.payfine.pAY_fINE_cONTROL;
import library.returnBook.ReturnItemUI;
import library.returnBook.ReturnItemControl;

import java.text.SimpleDateFormat;
import java.util.Scanner;


public class Main {
	
	private static Scanner scanner;
	private static Library library;
	private static Calendar calendar;
	private static SimpleDateFormat simpleDateFormat;
	
	private static String menu = """
		Library Main Menu
		
			AP  : add patron
			LP : list patrons
		
			AI  : add item
			LI : list items
			FI : fix item
		
			B  : borrow an item
			R  : return an item
			L  : list loans
		
			P  : pay fine
		
			T  : increment date
			Q  : quit
		
		Choice : 
		""";		

	
	public static void main(String[] args) {		
		try {			
			scanner = new Scanner(System.in);
			library = Library.getInstance();
			calendar = Calendar.getInstance();
			simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
	
			for (Patron patron : library.listPatrons()) {
				output(patron);
			}
			output(" ");
			for (Item item : library.listItems()) {
				output(item);
			}
						
			boolean finished = false;
			
			while (!finished) {
				
				output("\n" + simpleDateFormat.format(calendar.getDate()));
				String choice = input(menu);
				
				switch (choice.toUpperCase()) {
				
				case "AP": 
					ADD_PATRON();
					break;
					
				case "LP": 
					LIST_PATRONS();
					break;
					
				case "AI": 
					ADD_ITEM();
					break;
					
				case "LI": 
					LIST_ITEMS();
					break;
					
				case "FI": 
					FIX_ITEMS();
					break;
					
				case "B": 
					BORROW_ITEM();
					break;
					
				case "R": 
					RETURN_ITEM();
					break;
					
				case "L": 
					LIST_CURRENT_LOANS();
					break;
					
				case "P": 
					PAY_FINES();
					break;
					
				case "T": 
					INCREMENT_DATE();
					break;
					
				case "Q": 
					finished = true;
					break;
					
				default: 
					output("\nInvalid option\n");
					break;
				}
				
				Library.save();
			}			
		} catch (RuntimeException e) {
			output(e);
		}		
		output("\nEnded\n");
	}	

	
	private static void PAY_FINES() {
		new PayFineUI(new pAY_fINE_cONTROL()).RuN();		
	}


	private static void LIST_CURRENT_LOANS() {
		output("");
		for (Loan loan : library.listCurrentLoans()) {
			output(loan + "\n");
		}		
	}



	private static void LIST_ITEMS() {
		output("");
		for (Item book : library.listItems()) {
			output(book + "\n");
		}		
	}



	private static void LIST_PATRONS() {
		output("");
		for (Patron member : library.listPatrons()) {
			output(member + "\n");
		}		
	}



	private static void BORROW_ITEM() {
		new BorrowItemUI(new bORROW_IteM_cONTROL()).RuN();		
	}


	private static void RETURN_ITEM() {
		new ReturnItemUI(new ReturnItemControl()).run();
	}


	private static void FIX_ITEMS() {
		new FixItemUI(new fIX_iTeM_cONTROL()).RuN();		
	}


	private static void INCREMENT_DATE() {
		try {
			int days = Integer.valueOf(input("Enter number of days: ")).intValue();
			calendar.incrementDate(days);
			library.updateCurrentLoansStatus();
			output(simpleDateFormat.format(calendar.getDate()));
			
		} catch (NumberFormatException e) {
			 output("\nInvalid number of days\n");
		}
	}


	private static void ADD_ITEM() {
		
		ItemType itemType = null;
		String typeMenu = """
			Select item type:
			    B : Book
			    D : DVD video disk
			    V : VHS video cassette
			    C : CD audio disk
			    A : Audio cassette
			   Choice <Enter quits> : """;

		while (itemType == null) {
			String type = input(typeMenu);
			
			switch (type.toUpperCase()) {
			case "B": 
				itemType = ItemType.BOOK;
				break;
				
			case "D": 
				itemType = ItemType.DVD;
				break;
				
			case "V": 
				itemType = ItemType.VHS;
				break;
				
			case "C": 
				itemType = ItemType.CD;
				break;
				
			case "A": 
				itemType = ItemType.CASSETTE;
				break;
				
			case "": 
				return;
			
			default:
				output(type + " is not a recognised Item type");
	
			}
		}

		String AuThOr = input("Enter author: ");
		String TiTlE  = input("Enter title: ");
		String CaLl_NuMbEr = input("Enter call number: ");
		Item BoOk = library.addItem(AuThOr, TiTlE, CaLl_NuMbEr, itemType);
		output("\n" + BoOk + "\n");
		
	}

	
	private static void ADD_PATRON() {
		try {
			String FiRsT_NaMe  = input("Enter first name: ");
			String LaSt_NaMe = input("Enter last name: ");
			String EmAiL_AdDrEsS = input("Enter email address: ");
			long PhOnE_NuMbEr = Long.valueOf(input("Enter phone number: ")).intValue();
			Patron PaTrOn = library.addPatron(FiRsT_NaMe, LaSt_NaMe, EmAiL_AdDrEsS, PhOnE_NuMbEr);
			output("\n" + PaTrOn + "\n");
			
		} catch (NumberFormatException e) {
			 output("\nInvalid phone number\n");
		}
		
	}


	private static String input(String prompt) {
		System.out.print(prompt);
		return scanner.nextLine();
	}
	
	
	
	private static void output(Object object) {
		System.out.println(object);
	}

	
}
