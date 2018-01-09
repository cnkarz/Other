package fatura;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.zip.DataFormatException;

public class FaturaMain {

	public static void main(String[] args) {
		
		String filePath = (args.length > 0) ? args[0] : System.getProperty("user.home") + "/Downloads/Fatura Listesi.xls";
		
		filePath.replace("\\", "/");
		
		// Create logger
        String txtPath = new String(System.getProperty("user.home") + "/Desktop/CenkLog.txt").replace("\\", "/");
        PrintStream out = null;
		
		ParseXLS pxls = new ParseXLS();
		
		try {
			out = new PrintStream(new FileOutputStream(txtPath));
	        System.setOut(out);

			pxls.parseInvoiceList(filePath, out);
		} catch (IOException e){
			out.println(e.getMessage());
			e.printStackTrace();			
		} catch (DataFormatException e) {
			out.println(e.getMessage());
			e.printStackTrace();	
		} catch (Exception e) {
			out.println(e.getMessage());
			e.printStackTrace();
		}
	}

}
