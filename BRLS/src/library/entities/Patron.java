package library.entities;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class Patron implements Serializable {

    private long patronId;
    private String firstName;
    private String lastName;
    private String emailAdress;
    private long phoneNumber;
    private double finesOwing;
    private Map<Long, Loan> currentLoans;

    
    public Patron(String firstName, String lastName, String emailAdress, long phoneNumber, long memberId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAdress = emailAdress;
        this.phoneNumber = phoneNumber;
        this.patronId = memberId;
        this.currentLoans = new HashMap<>();
    }

    
    public String toString() {
        String strFormatFines = String.format("$%.2f", finesOwing);

        StringBuilder sb = new StringBuilder();
        sb.append("Patron:  ").append(patronId).append("\n")
          .append("  Name:  ").append(firstName).append(" ").append(lastName).append("\n")
          .append("  Email: ").append(emailAdress).append("\n")
          .append("  Phone: ").append(phoneNumber).append("\n")
          .append("  Fines Owed: ").append(strFormatFines).append("\n");
        
        for (Loan loan : currentLoans.values()) {
            sb.append(loan).append("\n");
        }          
        return sb.toString();
    }

    
    public Long getId() {
        return patronId;
    }

    
    public List<Loan> getLoans() {
        return new ArrayList<Loan>(currentLoans.values());
    }

    
    public int getNumberOfCurrentLoans() {
        return currentLoans.size();
    }

    
    public double getFinesOwed() {
        return finesOwing;
    }

    
    public void takeOutLoan(Loan loan) {
        long loanId = loan.getId();
        if (!currentLoans.containsKey(loanId)) {
            currentLoans.put(loanId, loan);
        } else {
            throw new RuntimeException("Duplicate loan added to member");
        }        
    }

    public void dischargeLoan(Loan loan) {
        long loanId = loan.getId();
        if (currentLoans.containsKey(loanId)) {
            currentLoans.remove(loanId);
        } else {
            throw new RuntimeException("No such loan held by member");
        }        
    }
    
    public String getLastName() {
        return lastName;
    }

    
    public String getFirstName() {
        return firstName;
    }


    public void addFine(double fineAmount) {
        finesOwing += fineAmount;
    }
    
    public double payFine(double paymentAmount) {
        if (paymentAmount < 0) {
            throw new RuntimeException("Member.payFine: amount must be positive");
        }

        double change = 0;
        if (paymentAmount > finesOwing) {
            change = paymentAmount - finesOwing;
            finesOwing = 0;
        } else { 
            finesOwing -= paymentAmount;
        }
        return change;
    }



}
