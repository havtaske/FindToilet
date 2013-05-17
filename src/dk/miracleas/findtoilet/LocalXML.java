package dk.miracleas.findtoilet;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.util.Log;

public class LocalXML {

	private Context context;
	private Toilet t;
	
	public LocalXML(Context context) {

		this.context = context;
		
	}

	public List<Toilet> getData() {

		List<Toilet> toiletList = new ArrayList<Toilet>();
		try {
			
			InputStream is = context.getAssets().open("toilet_skov_natur.gpx");

			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.parse(is, null);

			// get the nodes from tag
			NodeList nodeList = doc.getElementsByTagName("wpt");
			
			for (int j = 0; j < nodeList.getLength(); j++) {
				
				Node nodee = nodeList.item(j);
				t = new Toilet();
				t.setLat(Double.parseDouble(nodee.getAttributes().getNamedItem("lat").getTextContent()));
				t.setLon(Double.parseDouble(nodee.getAttributes().getNamedItem("lon").getTextContent()));
				
				for (int i = 0; i < nodee.getChildNodes().getLength(); i++) {
					
					Node node = nodee.getChildNodes().item(i);
					
					if (node.getNodeName().equalsIgnoreCase("name")) {
						
						t.setStreet(node.getTextContent());
						
					} else if (node.getNodeName().equalsIgnoreCase("Country_str_name")) {
						Log.i("TAG", "to node::" + node.getTextContent());
					}
				}
				
				toiletList.add(t);
			}
			is.close();
		} catch (Exception e) {
			System.out.println("ERROR while parsing xml:----" + e.getMessage());
		}
		return toiletList;
	}
}
