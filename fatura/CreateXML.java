package fatura;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class CreateXML {
	private Document doc;
	private Element root;
	private int counterMain, counterTr;
	private double creditAccAfter;

	private TagElements tagElements;

	public CreateXML() {
		tagElements = new TagElements();
		counterMain = counterTr = 0;
		creditAccAfter = 0;
	}

	public void plantTree() {
		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.getDOMImplementation().createDocument(null, "GL_VOUCHERS", null);

			root = doc.getDocumentElement();
			
		} catch (ParserConfigurationException e) {
			System.err.println(e);
			e.printStackTrace();
		}
	}
	
	// Add new GL_VOUCHER to the tree
	public void growTree (HashMap<String, String> headerValues, ArrayList<HashMap<String, String>> transactionValues) {

		Element rootChild = doc.createElement("GL_VOUCHER");
		Attr rootChildAttr = doc.createAttribute("DBOP");
		rootChildAttr.setValue("INS");
		rootChild.setAttributeNode(rootChildAttr);
		root.appendChild(rootChild);

		List<String> headerTags = tagElements.getHeaderTags();

		// Add header tags for this GL_VOUCHER
		for (String h : headerTags) {
			Element node = doc.createElement(h);
			node.setTextContent(headerValues.get(h));
			rootChild.appendChild(node);	
		}
		
		Element transactions = doc.createElement("TRANSACTIONS");
		rootChild.appendChild(transactions);
		
		// Count how many GL_VOUCHER blocks are added.
		counterMain++;
		
		// Add new TRANSACTION this GL_VOUCHER
		for (Map<String, String> m : transactionValues) {
			Element transaction = doc.createElement("TRANSACTION");
			transactions.appendChild(transaction);
			
			// Count how many transactions are added.
			counterTr++;
						
			List<String> transactionTags = (m.get("PARENT_GLCODE").equals("320")) 
					? tagElements.getCreditTags() : tagElements.getDebitTags();
					
			// Accumulator for calculated values
			if (!m.get("PARENT_GLCODE").equals("320")) 	creditAccAfter += Double.parseDouble(m.get("DEBIT"));

			for (String t : transactionTags) {
				Element node = doc.createElement(t);
				node.setTextContent(m.get(t));
				transaction.appendChild(node);	
			}
		}
		
		// Checks whether values read and values calculated match.
		assert(Double.parseDouble(headerValues.get("TOTAL_DEBIT")) == creditAccAfter) 
		: "Bu transaction'da hata olustu: GL_VOUCHER #"+counterMain + "TRANSACTION #" + counterTr;

		creditAccAfter = 0;
	}	
	
	public void writeXmlFile() {
		try {
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-9"); //Write in LOGO program encoding
			
			String filePath = System.getProperty("user.home") + "/Desktop/LogoImport.xml";
			filePath.replace("\\", "/");
			StreamResult result = new StreamResult(new File(filePath));

			// Output to console for testing
//			StreamResult result = new StreamResult(System.out);

			transformer.transform(new DOMSource(doc), result);
//			System.out.println(counterMain + " bolumde " + counterTr + " transaction.");
//			System.out.println("Dosya kaydedildi!");

		} catch (TransformerException e) {
			System.err.println(e);
			e.printStackTrace();
		}
	}
}