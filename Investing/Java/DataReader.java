/**
 * This class accesses to the folder with the .csv files,
 * which hold the closing rates for each symbol. 
 * It gets the file names and access inside the file 
 * and parse the values and gives back as a HashMap.
 * 
 * @author Cenk Arioz
 * 
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class DataReader {

	private double closingRate;
	private String dirPath;

	/**
	 * This method accesses to the folder containing the .csv files.
	 * It parses filenames and extracts the symbols to an array.
	 * 
	 *@return array holding the symbols inside the folder 
	 */
	
	public DataReader() {}

	public String[] getFileNames() {
		dirPath = "C:/MTX_ASC_DATA";

		Path dir = Paths.get(dirPath);
		int numberOfFiles = new File(dirPath).listFiles().length;
		String[] fileNames = new String[numberOfFiles];
		int index = 0;

		if (Files.isDirectory(dir) && Files.exists(dir)) {

			try (DirectoryStream<Path> allFiles = Files.newDirectoryStream(dir)) {
				for (Path dataFile : allFiles) {
					String symbol = dataFile.getFileName().toString().split("\\.")[0];
					fileNames[index] = symbol;
					index++;
				}
			} catch (DirectoryIteratorException e) {
				System.err.println(e);
			} catch (IOException e) {
				System.err.println(e);
			}
		}
		return fileNames;
	}

	/**
	 * This method receives the symbol and accesses to the .csv file. 
	 * It then parses the file to extract the dates and closing rates
	 * to a hashmap 
	 * 
	 * @param symbol as string
	 * @return a hashmap containing dates and closing rates at these dates 
	 */
	public HashMap<String, String> getFileContent(String fN) {
		String filePath = String.format("C:/MTX_ASC_DATA/%s.csv", fN);
		File csvFile = new File(filePath);
		HashMap<String, String> closingRates = new HashMap<>();

		try (BufferedReader in = new BufferedReader(new FileReader(csvFile))) {
			String line = in.readLine();

			while (line != null) {
				String[] columns = line.split(";");
				closingRates.put(columns[0], columns[4]);
				line = in.readLine();
			}

		} catch (IOException e) {
			System.err.println(e);
		}
		return closingRates;
	}
}
