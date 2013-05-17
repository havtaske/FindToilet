package dk.miracleas.findtoilet;

import java.util.ArrayList;
import java.util.List;

import android.sax.Element;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.util.Xml;

public class XmlParser extends BaseFeedParser {

	protected XmlParser(String feedUrl) {
		super(feedUrl);
	}

	public List<Toilet> parse() {

		final Toilet currentMessage = new Toilet();
		RootElement root = new RootElement("xml");
		final List<Toilet> messages = new ArrayList<Toilet>();
		Element item = root.getChild("node");
		item.setEndElementListener(new EndElementListener() {
			public void end() {
				try {
					messages.add(currentMessage.clone());
				} catch (CloneNotSupportedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		item.getChild(ID).setEndTextElementListener(new EndTextElementListener() {
			public void end(String body) {
				currentMessage.setId(Integer.parseInt(body));
			}
		});
		
		item.getChild(STREET).setEndTextElementListener(new EndTextElementListener() {
			public void end(String body) {
				currentMessage.setStreet(body);
			}
		});

		item.getChild(POSTAL).setEndTextElementListener(new EndTextElementListener() {
			public void end(String body) {
				currentMessage.setPostal(Integer.parseInt(body));
			}
		});
		
		item.getChild(CITY).setEndTextElementListener(new EndTextElementListener() {
			public void end(String body) {
				currentMessage.setCity(body);
			}
		});
		
		item.getChild(LAT).setEndTextElementListener(new EndTextElementListener() {
			public void end(String body) {
				currentMessage.setLat(Double.parseDouble(body));
			}
		});
		
		item.getChild(LON).setEndTextElementListener(new EndTextElementListener() {
			public void end(String body) {
				currentMessage.setLon(Double.parseDouble(body));
			}
		});
		
		item.getChild(TYPE).setEndTextElementListener(new EndTextElementListener() {
			public void end(String body) {
				currentMessage.setType(body);
			}
		});
		
		item.getChild(PAY).setEndTextElementListener(new EndTextElementListener() {
			public void end(String body) {
				currentMessage.setPaid(body);
			}
		});
		
		item.getChild(MANED).setEndTextElementListener(new EndTextElementListener() {
			public void end(String body) {
				currentMessage.setManed(body);
			}
		});
		
		item.getChild(CANISTER).setEndTextElementListener(new EndTextElementListener() {
			public void end(String body) {
				currentMessage.setCanister(body);
			}
		});
		
		item.getChild(BABY).setEndTextElementListener(new EndTextElementListener() {
			public void end(String body) {
				currentMessage.setBaby(body);
			}
		});
		
		item.getChild(WATER).setEndTextElementListener(new EndTextElementListener() {
			public void end(String body) {
				currentMessage.setWater(body);
			}
		});
		
		item.getChild(ROOM).setEndTextElementListener(new EndTextElementListener() {
			public void end(String body) {
				currentMessage.setRoom(body);
			}
		});
		
		item.getChild(DESC).setEndTextElementListener(new EndTextElementListener() {
			public void end(String body) {
				currentMessage.setDesc(body);
			}
		});
		
		item.getChild(AREA).setEndTextElementListener(new EndTextElementListener() {
			public void end(String body) {
				currentMessage.setArea(body);
			}
		});
		
		item.getChild(PIC).setEndTextElementListener(new EndTextElementListener() {
			public void end(String body) {
				currentMessage.setPicture(body);
			}
		});
		
		try {
			Xml.parse(this.getInputStream(), Xml.Encoding.UTF_8, root.getContentHandler());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return messages;
	}
}
