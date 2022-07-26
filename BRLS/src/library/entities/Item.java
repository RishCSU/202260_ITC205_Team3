package library.entities;
import java.io.Serializable;


@SuppressWarnings("serial")
public class Item implements Serializable {
	
	private ItemType type;

	private String author;
	// aUtHoR
	private String title;
	private String callNo;
	private long id;
	
	private enum iTeM_StAtE { AVAILABLE, ON_LOAN, DAMAGED, RESERVED };
	private iTeM_StAtE state;
	
	
	public Item(String author, String title, String callNo, ItemType itemType, long id) {
		this.type = itemType;
		this.author = author;
		this.title = title;
		this.callNo = callNo;
		this.id = id;
		this.state = iTeM_StAtE.AVAILABLE;
	}
	
	public String toString() {
		StringBuilder Sb = new StringBuilder();
		Sb.append("Item: ").append(id).append("\n")
		  .append("  Type:   ").append(type).append("\n")
		  .append("  Title:  ").append(title).append("\n")
		  .append("  Author: ").append(author).append("\n")
		  .append("  CallNo: ").append(callNo).append("\n")
		  .append("  State:  ").append(state);
		
		return Sb.toString();
	}

	public Long GeTiD() {
		return id;
	}

	public String GeTtItLe() {
		return title;
	}

	public ItemType GeTtYpE() {
		return type;
	}


	
	public boolean Is_AvAiLaBlE() {
		return state == iTeM_StAtE.AVAILABLE;
	}

	
	public boolean Is_On_LoAn() {
		return state == iTeM_StAtE.ON_LOAN;
	}

	
	public boolean Is_DaMaGeD() {
		return state == iTeM_StAtE.DAMAGED;
	}

	
	public void TaKeOuT() {
		if (state.equals(iTeM_StAtE.AVAILABLE))
			state = iTeM_StAtE.ON_LOAN;
		
		else 
			throw new RuntimeException(String.format("Item: cannot borrow item while item is in state: %s", state));
		
		
	}


	public void TaKeBaCk(boolean DaMaGeD) {
		if (state.equals(iTeM_StAtE.ON_LOAN))
			if (DaMaGeD) 
				state = iTeM_StAtE.DAMAGED;

			else 
				state = iTeM_StAtE.AVAILABLE;

		
		else 
			throw new RuntimeException(String.format("Item: cannot return item while item is in state: %s", state));
				
	}

	
	public void rEpAiR() {
		if (state.equals(iTeM_StAtE.DAMAGED))
			state = iTeM_StAtE.AVAILABLE;
		
		else 
			throw new RuntimeException(String.format("Item: cannot repair while Item is in state: %s", state));
		
	}


}
