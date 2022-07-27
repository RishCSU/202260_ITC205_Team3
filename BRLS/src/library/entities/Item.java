package library.entities;
import java.io.Serializable;


@SuppressWarnings("serial")
public class Item implements Serializable {
	
	private ItemType type;
	//TyPe
	private String author;
	// aUtHoR
	private String title;
	private String callNo;
	private long id;
	

	private enum iTeM_StAtE { AVAILABLE, ON_LOAN, DAMAGED, RESERVED };
	private iTeM_StAtE sTaTe;
	
	

	public Item(String AuThOr, String tItLe, String cAlLnO, ItemType ItEmTyPe, long iD) {
		this.type = ItEmTyPe;
		this.author = AuThOr;
		this.title = tItLe;
		this.callNo = cAlLnO;
		this.id = iD;
		this.sTaTe = iTeM_StAtE.AVAILABLE;
	}
	
	public String toString() {
		StringBuilder Sb = new StringBuilder();
		Sb.append("Item: ").append(id).append("\n")
		  .append("  Type:   ").append(type).append("\n")
		  .append("  Title:  ").append(title).append("\n")
		  .append("  Author: ").append(author).append("\n")
		  .append("  CallNo: ").append(callNo).append("\n")
		  .append("  State:  ").append(sTaTe);
		
		return Sb.toString();
	}


	public Long getId() {
		return id;
	}

	public String GeTtItLe() {
		return title;
	}

	public ItemType getType() {
		return type;
	}


	public boolean Is_AvAiLaBlE() {
		return sTaTe == iTeM_StAtE.AVAILABLE;
	}

	
	public boolean Is_On_LoAn() {
		return sTaTe == iTeM_StAtE.ON_LOAN;
	}

	
	public boolean isDamaged() {
		return sTaTe == iTeM_StAtE.DAMAGED;
	}

	
	public void TaKeOuT() {

		if (sTaTe.equals(iTeM_StAtE.AVAILABLE)) 
			sTaTe = iTeM_StAtE.ON_LOAN;
		
		else 
			throw new RuntimeException(String.format("Item: cannot borrow item while item is in state: %s", sTaTe));
		
		
	}


	public void TaKeBaCk(boolean DaMaGeD) {
		if (sTaTe.equals(iTeM_StAtE.ON_LOAN)) 
			if (DaMaGeD) 
				sTaTe = iTeM_StAtE.DAMAGED;			

			else 

				sTaTe = iTeM_StAtE.AVAILABLE;		


		else 
			throw new RuntimeException(String.format("Item: cannot return item while item is in state: %s", sTaTe));
				
	}

	

	public void repair() {

		if (sTaTe.equals(iTeM_StAtE.DAMAGED)) 
			sTaTe = iTeM_StAtE.AVAILABLE;
		
		else
			throw new RuntimeException(String.format("Item: cannot repair while Item is in state: %s", sTaTe));
		
	}


}
