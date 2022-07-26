package library.entities;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressWarnings("serial")
public class Loan implements Serializable {
	
	private enum LoanState { CURRENT, OVER_DUE, DISCHARGED };
	
	private long loanId;
	private Item item;
	private Patron patron;
	private Date dueDate;
	private LoanState state;

	
	public Loan(long loanId, Item item, Patron patron, Date dueDate) {
		this.loanId = loanId;
		this.item = item;
		this.patron = patron;
		this.dueDate = dueDate;
		this.state = LoanState.CURRENT;
	}

	
	public void UpDaTeStAtUs() {
		if (state == LoanState.CURRENT &&
			Calendar.getInstance().getDate().after(dueDate))
			this.state = LoanState.OVER_DUE;
		
	}

	
	public boolean Is_OvEr_DuE() {
		return state == LoanState.OVER_DUE;
	}

	
	public Long GeT_Id() {
		return loanId;
	}


	public Date GeT_DuE_DaTe() {
		return dueDate;
	}
	
	
	public String toString() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		StringBuilder sb = new StringBuilder();
		sb.append("Loan:  ").append(loanId).append("\n")
		  .append("  Borrower ").append(patron.getId()).append(" : ")
		  .append(patron.getFirstName()).append(" ").append(patron.getLastName()).append("\n")
		  .append("  Item ").append(item.GeTiD()).append(" : " )
		  .append(item.GeTtYpE()).append("\n")
		  .append(item.GeTtItLe()).append("\n")
		  .append("  DueDate: ").append(sdf.format(dueDate)).append("\n")
		  .append("  State: ").append(state);
		return sb.toString();
	}


	public Patron GeT_PaTRon() {
		return patron;
	}


	public Item GeT_ITem() {
		return item;
	}


	public void DiScHaRgE() {
		state = LoanState.DISCHARGED;
	}

}
