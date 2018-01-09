import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import com.mysql.jdbc.ResultSetMetaData;

public class DBUpdate {

	public void deleter(Statement stmt, String[] symbols){
		for(String s : symbols){
			String updateStmt = String.format("UPDATE rates SET %s = null WHERE date > %d", s, getDate()-1);
			try {
				stmt.executeUpdate(updateStmt);
			} catch (SQLException e){
				System.err.println(e);
			}
		}
	}
	
	public void updater(Statement stmt, HashMap<String, String> cR, String s) {

		// Query for empty cells in the database table
		
		//String selectStmt = String.format("SELECT date, %s FROM rates WHERE %s is null AND date < %d", s, s, getDate()+1);
		String selectStmt = String.format("SELECT date, %s FROM rates WHERE date < %d", s, getDate()+1);
		
		try (ResultSet rs = stmt.executeQuery(selectStmt)) {
			System.out.println(String.format("Starting update of %s", s));
			boolean isStarted = false;
			float value = 0;

			/*
			 * The program :
			 * 		first checks if the ResultSet has a date later than today (=> continue)
			 * 		then checks if the ResultSet reached entered cells (vast number of empty cells starting from year 2006) 
			 *	 	then checks if the csv file holds data for the empty dates (=> continue)
			 * 		then checks if there are missing cells between cells (=> fills with previous day's closing price)
			 * 		then checks if the data in the ResultSet already exists in the database ( => continue)
			 * 		finally fills the recent empty cells with latest data 
			 */
			
			while (rs.next()) {
				String date = Integer.toString(rs.getInt("date"));

				if (!cR.containsKey(date)){
					if(isStarted == false){
						continue;
					} else {
						rs.updateFloat(s, value);
						rs.updateRow();
					}
				} else {
					
				//	if(rs.getFloat(s) != 0)
				//		continue;
				//	else {
						isStarted = true;
						value = Float.parseFloat(cR.get(date));
						rs.updateFloat(s, value);
						rs.updateRow();
				//	}

				}
				
				
				/*
				else {
					
					//Allows to display the data to the console while updating
					//System.out.println(date + " / " + Double.parseDouble(cR.get(date)));
					isStarted = true;
						value = Float.parseFloat(cR.get(date));
						rs.updateFloat(s, value);
						rs.updateRow();
						cR.remove(date);
									}

				if (cR.containsKey(date))
					isStarted = true;


				else if (isStarted == false && !cR.containsKey(date))
					continue;
				else if (isStarted == true && !cR.containsKey(date)) {
					while(rs.getInt("date") < getDate()){
						rs.updateFloat(s, value);
						rs.updateRow();
					}

				} else if (cR.containsKey(date) && rs.getDouble(s) != 0)
					continue;
				else if (rs.getInt("date") > getDate())
					continue;

				else {
					
					//Allows to display the data to the console while updating
					System.out.println(date + " / " + Double.parseDouble(cR.get(date)));
						value = Float.parseFloat(cR.get(date));
						rs.updateFloat(s, value);
						rs.updateRow();
						cR.remove(date);
						*/
				}


		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
		System.out.println(String.format("Finished update of %s", s));
	}

	public void DBexport(Statement stmt) {

		File fd = null;

		try {
			String pathStr = "D:/Documents/Work/FD";
			Path path = Paths.get(pathStr);
			if (Files.notExists(path))
				Files.createDirectories(path);

			String filename = "financialdata" + getDate() + ".csv";
			Path filePath = Paths.get(pathStr, filename);
			if (Files.notExists(filePath))
				Files.createFile(filePath);
			fd = filePath.toFile();
		} catch (IOException e) {
			System.err.println(e);
		}

		try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fd)))) {

			String query = "SELECT * FROM rates WHERE date < " + (getDate()+1) + ";";

			// Retrieve financial data and column names. Then write the header
			// to the file

			ResultSet rs = null;
			java.sql.ResultSetMetaData rsmd = null;
			int colQty = 0;

			try {

				rs = stmt.executeQuery(query);
				rsmd = rs.getMetaData();
				colQty = rsmd.getColumnCount();

				// Write column names as the first row

				for (int i = 1; i <= colQty; i++) {
					out.print(rsmd.getColumnName(i));
					out.print(",");
				}
				out.print("\n");

				// Write financial data, seperated by commas

				while (rs.next()) {

					out.print(rs.getInt(1));

					for (int i = 2; i <= colQty; i++) {
						out.print(",");
						out.print(rs.getFloat(i));
					}

					out.print("\n");
				}

			} catch (SQLException e) {
				System.err.println(e);
			}

		} catch (IOException e) {
			System.err.println(e);
		}

		System.out.println("CSV File is created successfully.");

	}

	public int getDate() {
		Date cal = Calendar.getInstance().getTime();

		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		int date = Integer.parseInt(df.format(cal));

		return date;
	}

}
