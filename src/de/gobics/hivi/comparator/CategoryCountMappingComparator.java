/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.gobics.hivi.comparator;

import de.gobics.hivi.Category;
import java.util.Comparator;

/**
 *
 * @author manuel
 */
public class CategoryCountMappingComparator extends AbstractCategoryComparator<Integer> {

	@Override
	public Integer getScore(Category o1) {
		return (Integer) o1.getAnnotatedMappingsRecursive().size();
	}

	@Override
	public boolean reverse() {
		return true;
	}

	@Override
	public String getName() {
		return "Mapping-Count (recursive)";
	}
}
