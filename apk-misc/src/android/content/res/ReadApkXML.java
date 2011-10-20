package android.content.res;

import org.w3c.dom.Document;
import org.w3c.dom.*;

public class ReadApkXML {

	public ReadApkXML(Document doc) {
		try {
			// normalize text representation
			doc.getDocumentElement().normalize();

			NodeList listOfEntry = doc.getElementsByTagName("ENTRY");
			int totalEntry = listOfEntry.getLength();
			System.out.println("Total no of entry : " + totalEntry);

			for (int i = 0; i < listOfEntry.getLength(); i++) {
				Node node = listOfEntry.item(i);
				System.out.println("<Entry>");
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					NamedNodeMap attrMap=node.getAttributes();
					
					for(int j=0;j<attrMap.getLength();j++){
						System.out.println("<"+attrMap.item(j).getNodeName()+","+attrMap.item(j).getNodeValue()+">");
					}
				}// end of if clause

			}// end of for loop with s var
		} catch (Throwable t) {
			t.printStackTrace();
		}
		// System.exit (0);

	}// end of main
}