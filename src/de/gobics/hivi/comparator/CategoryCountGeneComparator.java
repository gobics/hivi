/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.gobics.hivi.comparator;

import de.gobics.hivi.Category;
import java.util.logging.Logger;

/**
 *
 * @author manuel
 */
public class CategoryCountGeneComparator extends AbstractCategoryComparator<Integer> {

	private static final Logger logger = Logger.getLogger(CategoryCountGeneComparator.class.getName());

	@Override
	public Integer getScore(Category o1) {
		return o1.getAnnotatedGeneIdsRecursive().size();
	}

	@Override
	public boolean reverse() {
		return true;
	}

	@Override
	public String getName() {
		return "Gene-count (recursive)";
	}
}
