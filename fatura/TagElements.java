package fatura;

import java.util.ArrayList;

public class TagElements {
	public TagElements() {	}
	
	// GL_VOUCHER header tags
	public ArrayList<String> getHeaderTags(){
		ArrayList<String> headers = new ArrayList<>();
		
		headers.add("TYPE");				// Invoice category
//		headers.add("NUMBER");				// Record id assigned by the program
		headers.add("DATE");				// Invoice date
		headers.add("TOTAL_DEBIT");			// Invoice amount
		headers.add("TOTAL_CREDIT");		// Total sale amount 
		headers.add("PRINT_COUNTER");		// Counts number of modifications
		headers.add("CREATED_BY");			// Constant
		headers.add("DATE_CREATED");		
		headers.add("HOUR_CREATED");		
		headers.add("MIN_CREATED");
		headers.add("SEC_CREATED");
//		headers.add("MODIFIED_BY");			// Assigned by the program
//		headers.add("DATE_MODIFIED");		// Assigned by the program
//		headers.add("HOUR_MODIFIED");		// Assigned by the program
//		headers.add("MIN_MODIFIED");		// Assigned by the program
//		headers.add("SEC_MODIFIED");		// Assigned by the program
		headers.add("CURRSEL_TOTALS");
//		headers.add("DATA_REFERENCE");		// Assigned by the program
		headers.add("RC_TOTAL_DEBIT");
		headers.add("RC_TOTAL_CREDIT");		
//		headers.add("TRANSACTIONS");		// Added manually for XML tree growing
		headers.add("ORGLOGOID");
		headers.add("DEFNFLDSLIST");
		headers.add("DOC_DATE");

		return headers;
	}
		
	// 153 and 191 GL_CODE TRANSACTION tags
	public ArrayList<String> getDebitTags(){
		ArrayList<String> te = new ArrayList<>();
		
		te.add("GL_CODE");
		te.add("PARENT_GLCODE");
		te.add("DEBIT");			
		te.add("DESCRIPTION");
		te.add("RC_XRATE");			// Constant
		te.add("RC_AMOUNT");
		te.add("TC_XRATE");			// Constant
		te.add("TC_AMOUNT");
		te.add("QUANTITY");			// Constant	
//		te.add("DATA_REFERENCE");	// Assigned by the program
		te.add("DETLIST");			// Constant
		te.add("DEFNFLDLIST");		// Constant
		te.add("MONTH");
		te.add("YEAR");
		te.add("DOC_DATE");

		return te;
	}
	
	// 320 GL_CODE TRANSACTION tags
	public ArrayList<String> getCreditTags(){
		ArrayList<String> te = new ArrayList<>();
		
		te.add("SIGN");				// Constant
		te.add("GL_CODE");
		te.add("PARENT_GLCODE");
		te.add("CREDIT");			
		te.add("DESCRIPTION");
		te.add("RC_XRATE");			// Constant
		te.add("RC_AMOUNT");
		te.add("TC_XRATE");			// Constant
		te.add("TC_AMOUNT");
		te.add("QUANTITY");			// Constant	
//		te.add("DATA_REFERENCE");	// Assigned by the program
		te.add("DETLIST");			// Constant
		te.add("DEFNFLDLIST");		// Constant
		te.add("MONTH");
		te.add("YEAR");
		te.add("DOC_DATE");

		return te;
	}
}
