package de.gobics.hivi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.TreeMap;
import java.util.logging.Logger;

/**
 *
 * @author manuel
 */
public class SchemaLoader {

	private static final Logger logger = Logger.getLogger(SchemaLoader.class.
			getName());
	private final InputStream stream;
	private final String name;

	public SchemaLoader(String name, InputStream stream) {
		this.name = name;
		this.stream = stream;
	}

	public Category load() throws IOException {
		InputStreamReader instream = new InputStreamReader(stream);
		BufferedReader reader = new BufferedReader(instream);
		String line = null;

		Category root = new Category(name, name);

		// Contains a mapping of IDs to categories for easy access
		TreeMap<String, Category> cats = new TreeMap<String, Category>();

		while ((line = reader.readLine()) != null) {
			String[] tokens = line.split("\t");

			if (!cats.containsKey(tokens[0])) {
				cats.put(tokens[0], new Category(tokens[0], tokens[1]));
			}
			else {
				cats.get(tokens[0]).setName(tokens[1]);
			}

			for (int idx = 2; idx < tokens.length; idx++) {
				if (!cats.containsKey(tokens[idx])) {
					cats.put(tokens[idx], new Category(tokens[idx], null));
				}
				cats.get(tokens[idx]).addChildCategory(cats.get(tokens[0]));
			}

			if (tokens.length == 2) { //this is a ONTOLOGY root category
				root.addChildCategory(cats.get(tokens[0]));
			}
		}
		reader.close();
		return root;
	}
}
