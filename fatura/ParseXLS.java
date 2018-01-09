package fatura;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.zip.DataFormatException;

import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class ParseXLS {
	private CreateXML tree;
	private InputStream wrappedStream;
	private Workbook workbook;
	
	public ParseXLS() {	
		tree = new CreateXML();
		tree.plantTree();
	}
	
	public void parseInvoiceList(String filePath, PrintStream out) throws DataFormatException, IOException {
		
		// Check whether older Fatura Listesi exists in the directory
		File file1 = new File (filePath.split("\\.")[0] + "(1).xls");
		File file2 = new File (filePath.split("\\.")[0] + "(1).xls");
		String msg = "Birden fazla Fatura Listesi dosyasi var. Mevcut dosyalari silip tekrar programi calistirin";
		if (file1.exists() || file2.exists()) throw new IOException(msg);
		
		try {

			// Access the xls file
			wrappedStream = POIFSFileSystem.createNonClosingInputStream(new FileInputStream(new File(filePath)));
			
			// Check whether it is an xls file
			Path path = FileSystems.getDefault().getPath(filePath);
			if (!Files.probeContentType(path).equals("application/vnd.ms-excel")) {
				throw new DataFormatException("Secilen dosya, bu programa uygun degil.");
			}
			
			workbook = WorkbookFactory.create(wrappedStream);
			Sheet firstSheet = workbook.getSheetAt(0);
			Iterator<Row> rowIterator = firstSheet.iterator();
			
			// Iterate through rows
			int rowCounter = 0;
			double sumOfInvoices = 0;
			String date = "";
			String newDate = "";

			ArrayList<HashMap<String, String>> transactions = new ArrayList<>();
			
			// Make iterator point to xls table header
			Row row = rowIterator.next();
			
			while (rowIterator.hasNext()) {
				row = rowIterator.next();

				newDate = getDate(row);
				
				// Parse total
			    NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
			    DataFormatter formatter = new DataFormatter();
			    Double invoiceSum = format.parse(formatter.formatCellValue(row.getCell(2))).doubleValue();
				
				// Skip reverse invoices
				if (invoiceSum < 0) {
					if (!transactions.isEmpty()) {
						tree.growTree(getHeader(date, sumOfInvoices), transactions);
						rowCounter = 0;
						sumOfInvoices = 0;
						transactions = new ArrayList<>();
					} else {
						continue;
					}
				} 
				
				// Grow tree if date changes or counter hits 3
				if (!transactions.isEmpty() && (rowCounter == 3 || (!date.equals(newDate) && !date.equals(""))) ) {
					tree.growTree(getHeader(date, sumOfInvoices), transactions);
					rowCounter = 0;
					sumOfInvoices = 0;
					transactions = new ArrayList<>();
				} 
				
				date = newDate;

				for (HashMap<String, String> hm : getTransactions(row)) {
					transactions.add(hm);
				}
				
				rowCounter++;
				newDate = date;
				sumOfInvoices += invoiceSum;

			}
		} catch (FileNotFoundException e) {
			out.println(filePath + " adresinde dosya bulunamadi!");
		} catch (Exception e) {
			out.println(e.getMessage());
			e.printStackTrace();
		} finally {
			workbook.close();
			wrappedStream.close();
		}
		
		tree.writeXmlFile();
	}	
	
	private HashMap<String, String> getHeader(String date, double total){
		HashMap<String, String> header = new HashMap<>();
		header.put("TYPE", "4");
		header.put("PRINT_COUNTER", "1");
		header.put("CREATED_BY", "5");
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy"); 

		header.put("DATE_CREATED", String.valueOf(now.format(formatter)));		
		header.put("HOUR_CREATED", String.valueOf(now.getHour()));		
		header.put("MIN_CREATED", String.valueOf(now.getMinute()));
		header.put("SEC_CREATED", String.valueOf(now.getSecond()));
		
		header.put("CURRSEL_TOTALS", "3");	
		header.put("ORGLOGOID", " ");
		header.put("DEFNFLDSLIST", " ");
		
//		String totalStr = String.valueOf(total);
//		if (totalStr.split("\\.").length > 1 && totalStr.split("\\.")[1].length() > 2) {
//			totalStr = totalStr.split("\\.")[0] + "." + totalStr.split("\\.")[1].substring(0,2); 
//		}
		
		String totalStr =String.valueOf((double) Math.round(total*100) / 100);
		
		header.put("DATE", date);
		header.put("DOC_DATE", date);
		header.put("TOTAL_DEBIT", totalStr);
		header.put("TOTAL_CREDIT", totalStr);
		header.put("RC_TOTAL_DEBIT", totalStr);
		header.put("RC_TOTAL_CREDIT", totalStr);

		return header;
	}
	
	private ArrayList<HashMap<String, String>> getTransactions(Row row) throws ParseException {
		
		ArrayList<HashMap<String, String>> txs = new ArrayList<>();
		
		DataFormatter formatter = new DataFormatter();
		String invoiceNo = formatter.formatCellValue(row.getCell(8));
	    NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
	    
		Double total = format.parse(formatter.formatCellValue(row.getCell(2))).doubleValue();
		Double vat08 = format.parse(formatter.formatCellValue(row.getCell(3))).doubleValue();
		Double vat18 = format.parse(formatter.formatCellValue(row.getCell(4))).doubleValue();
		String date = getDate(row);
		
		int[] transactionType = {153, 191};
		
		if (vat08 > 0) {
			for (int t : transactionType) {
				txs.add(getTransactionEntry(total, vat08, "08", invoiceNo, date, t));
			}
		}
		
		if (vat18 > 0) {
			for (int t : transactionType) {
				txs.add(getTransactionEntry(total, vat18, "18", invoiceNo, date, t));
			}
		}
		
		txs.add(getTransactionEntry(total, 0, "", invoiceNo, date, 320));

		return txs;
	}	
	
	private HashMap<String, String> getTransactionEntry(
			double total, double tax, String vatType, String invoiceNo, String date, int trCode) throws ParseException{
		HashMap<String, String> entry = new HashMap<>();
		
		Calendar calendar = Calendar.getInstance();
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd.MM.yyyy");
		calendar.setTime(sdf.parse(date));
			
		switch (trCode) {
			case 153:
				double netAmount = (double) Math.round((tax/(Double.parseDouble(vatType)/100))*100)/100;
				
				String net = String.valueOf((Math.abs((total - tax) - netAmount) < 0.03) ? (total - tax) : netAmount);

//				if (net.split("\\.").length > 1 && net.split("\\.")[1].length() > 2) {
//					net = net.split("\\.")[0] + "." + net.split("\\.")[1].substring(0,2); 
//				}
				
				entry.put("GL_CODE", ("153.010.0" + vatType));
				entry.put("PARENT_GLCODE", "153");
				entry.put("DEBIT", net);
				entry.put("RC_AMOUNT", net);
				entry.put("TC_AMOUNT", net);
				break;
			case 191:
				entry.put("GL_CODE", ("191.010.0" + vatType));
				entry.put("PARENT_GLCODE", "191");
				entry.put("DEBIT", String.valueOf(tax));
				entry.put("RC_AMOUNT", String.valueOf(tax));
				entry.put("TC_AMOUNT", String.valueOf(tax));
				break;
			case 320:
				entry.put("SIGN", "1");
				entry.put("GL_CODE", "320.010.002");
				entry.put("PARENT_GLCODE", "320");
				entry.put("CREDIT", String.valueOf(total));
				entry.put("RC_AMOUNT", String.valueOf(total));
				entry.put("TC_AMOUNT", String.valueOf(total));
				break;
		}
		
		entry.put("QUANTITY", "0");
		entry.put("DETLIST", " ");
		entry.put("DEFNFLDLIST", " ");
		entry.put("RC_XRATE", "1");
		entry.put("TC_XRATE", "1");
		entry.put("DESCRIPTION", invoiceNo);
		entry.put("MONTH", String.valueOf(calendar.get(Calendar.MONTH) + 1));
		entry.put("YEAR", String.valueOf(calendar.get(Calendar.YEAR)));
		entry.put("DOC_DATE", date);
		
		return entry;
	}
	
	private String getDate(Row row) throws ParseException {
		DataFormatter df = new DataFormatter();		
		java.text.SimpleDateFormat dateParser = new java.text.SimpleDateFormat("MM/d/yy");
		java.text.SimpleDateFormat dateFormatter = new java.text.SimpleDateFormat("dd.MM.yyyy");
		String rawDate = df.formatCellValue(row.getCell(0)).split(" ")[0];
		return dateFormatter.format(dateParser.parse(rawDate));
	}
}
