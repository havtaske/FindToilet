package dk.miracleas.findtoilet;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public abstract class BaseFeedParser implements FeedParser {

	// names of the XML tags
	static final String ITEM = "node";
	static final String ID = "nid";
	static final String STREET = "Street";
	static final String POSTAL = "Postalcode";
	static final String CITY = "City";
	static final String LAT = "Latitude";
	static final String LON = "Longitude";
	static final String TYPE = "type";
	static final String PAY = "Betaling";
	static final String MANED = "Bemanding";
	static final String CANISTER = "Kanylebeholder";
	static final String BABY = "Puslebord";
	static final String WATER = "Vandhane";
	static final String ROOM = "FriGulvplads";
	static final String DESC = "Body";
	static final String AREA = "Term";
	static final String PIC = "Billeder";

	final URL feedUrl;

	protected BaseFeedParser(String feedUrl) {
		try {
			this.feedUrl = new URL(feedUrl);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	protected InputStream getInputStream() {
		try {
			return feedUrl.openConnection().getInputStream();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
