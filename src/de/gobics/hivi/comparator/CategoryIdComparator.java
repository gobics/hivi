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
public class CategoryIdComparator extends AbstractCategoryComparator<String> {

	@Override
	public String getScore(Category o1) {
		return o1.getId();
	}

	@Override
	public boolean reverse() {
		return false;
	}

	@Override
	public String getName() {
		return "ID";
	}

}
