package de.gobics.hivi.comparator;

import de.gobics.hivi.Category;
import java.util.Comparator;

/**
 * The awesome new AbstractCategoryComparator
 *
 * @author Manuel Landesfeind &lt;manuel@gobics.de&gt;
 */
public abstract class AbstractCategoryComparator<T extends Comparable> implements Comparator<Category> {

	@SuppressWarnings("unchecked")
	public final int compare(Category o1, Category o2) {
		Comparable c1 = getScore(o1);
		Comparable c2 = getScore(o2);
		return c1.compareTo(c2) * (reverse() ? -1 : 1);
	}

	public abstract boolean reverse();

	public abstract T getScore(Category o1);
	
	public abstract String getName();
}
