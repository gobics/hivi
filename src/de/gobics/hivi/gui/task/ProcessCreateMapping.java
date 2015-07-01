/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.gobics.hivi.gui.task;

import de.gobics.hivi.*;
import de.gobics.hivi.cutoff.CutoffThreshold;
import de.gobics.marvis.utils.task.AbstractTask;
import java.io.*;
import java.util.*;
import java.util.logging.Logger;

public class ProcessCreateMapping extends AbstractTask<MappingResult, Void> {

	private final static Logger logger = Logger.getLogger(ProcessCreateMapping.class.
			getName());
	private CutoffThreshold cutoff = CutoffThreshold.NoCutoff;
	private final Database database;
	private final File input_file_genes;
	private final String input_list;

	public ProcessCreateMapping(Database database, File in_list) {
		if (database == null) {
			throw new RuntimeException("No geneset given");
		}
		if (in_list == null) {
			throw new RuntimeException("No input file given");
		}
		this.database = database;
		this.input_file_genes = in_list;
		input_list = null;
	}

	public ProcessCreateMapping(Database database, String in_list) {
		if (database == null) {
			throw new RuntimeException("No geneset given");
		}
		if (in_list == null) {
			throw new RuntimeException("No input file given");
		}
		this.database = database;
		this.input_file_genes = null;
		input_list = in_list;
	}

	/**
	 * Returns all gene IDs that have been added in the input file.
	 *
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private TreeSet<String> readGeneIds() throws FileNotFoundException, IOException {
		TreeSet<String> ids = new TreeSet<String>();

		BufferedReader reader = new BufferedReader(input_file_genes != null ? new FileReader(input_file_genes) : new StringReader(input_list));
		String id;
		while ((id = reader.readLine()) != null) {
			ids.add(id.trim());
		}

		return ids;
	}

	public void setQualityCutoff(CutoffThreshold cutoff) {
		this.cutoff = cutoff;
	}

	@Override
	protected MappingResult doTask() throws Exception {
		int fail_counter = 0;
		logger.fine("Try to load schema");
		Category root_category = database.getSchemaLoader().load();
		TreeMap<String, TreeSet<Category>> fast_category_access = new TreeMap<String, TreeSet<Category>>();
		Iterator<Category> citer = root_category.iterator();
		while (citer.hasNext()) {
			Category c = citer.next();
			if (!fast_category_access.containsKey(c.getId())) {
				fast_category_access.put(c.getId(), new TreeSet<Category>());
			}
			fast_category_access.get(c.getId()).add(c);
		}

		logger.finer("Schema loaded");


		logger.fine("Loading gene ids from input");
		TreeSet<String> gene_ids_sorted = readGeneIds();
		logger.finer(gene_ids_sorted.size() + " input gene ids loaded: "+gene_ids_sorted);

		setTaskDescription("Mapping input to schema");
		MappingReadIterator iter = database.getMappingIterator();
		setProgressMax(database.countMappings());
		while (iter.hasNext()) {
			incrementProgress();
			if (isCanceled()) {
				return null;
			}
			Mapping mapping = iter.next();

			if (cutoff.keep(mapping) && gene_ids_sorted.contains(mapping.
					getGeneId())) {
				mapping.setAnnotated(true);
			}

			TreeSet<Category> cs = fast_category_access.get(mapping.
					getCategoryId());
			if (cs == null) {
				//logger.warning("Can not find category in schema: " + mapping.getCategoryId());
				fail_counter++;
			}
			else {
				for (Category c : cs) {
					c.addMapping(mapping);
				}
			}
		}

		logger.warning("Could not find category for " + fail_counter + " mappings");


		return new MappingResult(root_category, database, cutoff);
	}
}
