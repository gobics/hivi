package de.gobics.hivi;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The awesome new MappingReadIterator
 *
 * @author Manuel Landesfeind &lt;manuel@gobics.de&gt;
 */
public class MappingReadIterator implements Iterator<Mapping> {

	private static final Logger logger = Logger.getLogger(MappingReadIterator.class.
			getName());
	private final BufferedReader in;
	private int counter = 0;
	private Mapping next = null;

	public MappingReadIterator(BufferedReader in) {
		this.in = in;
		while(! fillNext()){};
	}

	public boolean hasNext() {
		return next != null;
	}

	public Mapping next() {
		Mapping cur = next;
		while(! fillNext()){};
		return cur;
	}

	public void remove() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	/**
	 * Tries to read the next line and produce a Mapping out of it. If it succeded, then
	 * true is returned, otherwise false.
	 * 
	 * @return 
	 */
	private boolean fillNext() {
		counter++;
		String line = null;
		try {
			line = in.readLine();
		}
		catch (IOException ex) {
			logger.log(Level.SEVERE, null, ex);
			try {
				in.close();
			}
			catch (IOException ex1) {
				//Ignore
			}
		}
		if (line == null) {
			next = null;
			return true;
		}
		String[] token = line.split("\\t");
		String id = token[0];
		if (id == null || id.isEmpty()) {
			logger.warning("ID field is empty in line " + counter + ": " + line);
			return false;
		}
		String category = token[1];
		if (category == null || category.isEmpty()) {
			logger.warning("Category field is empty in line " + counter + ": " + line);
			return false;
		}
		float quality = -1;
		try {
			quality = new Double(token[2]).floatValue();
		}
		catch (NumberFormatException ex) {
			logger.log(Level.SEVERE, "Quality in line " + counter + " is not parsable: ", ex);
			return false;
		}
		next = new Mapping(id, category, quality, token.length > 3 ? token[3] : null);
		return true;
	}
}
