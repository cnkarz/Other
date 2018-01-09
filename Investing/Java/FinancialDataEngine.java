
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;

public class FinancialDataEngine {
	private static String[] symbols;

	public static void main(String[] args) {

		DataReader dr = new DataReader();
		DBUpdate dbu = new DBUpdate();
		HashMap<String, String> rates = new HashMap<>();
		
		try {
			long startTime = System.currentTimeMillis();
			DriverManager.registerDriver(new com.mysql.jdbc.Driver());			
			Connection conn = DriverManager
					.getConnection("jdbc:mysql://localhost/financialdata?" + "user=root&password=12345678");
			Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			conn.setAutoCommit(false);

			System.out.println("Connection to database established..");
			
			// Updates database with the missing (most recent) values 
			
			String[] symbols = dr.getFileNames();
			Arrays.sort(symbols);
			
			// dbu.deleter(stmt, symbols);
			
			for(int i = 0; i < symbols.length; i++){
				rates = dr.getFileContent(symbols[i]);
				dbu.updater(stmt, rates, symbols[i]);
			}
			
			// Creates a .csv file for the whole database
			
			dbu.DBexport(stmt);
			
			conn.commit();
			conn.close();
			System.out.println("Update completed!");
			long endTime = System.currentTimeMillis();
			double timeElapsed =  (endTime-startTime)/1000;
			System.out.println("Update took " + timeElapsed + " seconds.");
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
	}

}
