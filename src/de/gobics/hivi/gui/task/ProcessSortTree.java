/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.gobics.hivi.gui.task;

import de.gobics.hivi.Category;
import de.gobics.hivi.comparator.CategoryIdComparator;
import de.gobics.marvis.utils.task.AbstractTask;
import java.util.Comparator;
import java.util.Iterator;
import java.util.logging.Logger;

/**
 *
 * @author manuel
 */
public class ProcessSortTree extends AbstractTask<Category, Void> {

	private final static Logger logger = Logger.getLogger(ProcessSortTree.class.
			getName());
	private final Category root;
	private final Comparator<Category> sorter;

	public ProcessSortTree(Category root_category) {
		this(root_category, new CategoryIdComparator());
	}

	public ProcessSortTree(Category root_category, Comparator<Category> sorter) {
		root = root_category;
		this.sorter = sorter;
	}

	@Override
	public Category doTask() throws Exception {
		logger.finer("Generating list of categories");
		setTaskDescription("Sorting categories");
		Category clone = root.clone();

		// count categories
		int count = 0;
		Iterator<Category> citer = clone.iterator();
		while (citer.hasNext()) {
			citer.next();
			count++;
		}

		// Set status
		setProgressMax(count);
		setProgress(0);

		// Perform calculations
		citer = clone.iterator();
		while (citer.hasNext()) {
			citer.next().sort(sorter);
			incrementProgress();
			if(isCanceled()){
				return null;
			}
		}

		return clone;
	}
}
