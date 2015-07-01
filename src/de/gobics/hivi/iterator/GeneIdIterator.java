package de.gobics.hivi.iterator;

import de.gobics.hivi.Category;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * An iterator to iterate over the (distinct) gene IDs in a (sub-) tree.
 *
 * @author Manuel Landesfeind &lt;manuel@gobics.de&gt;
 */
public class GeneIdIterator implements Iterator<String> {

	private final MappingIterator miter;
	private final TreeSet<String> returned = new TreeSet<String>();
	private final TreeSet<String> found = new TreeSet<String>();

	/**
	 * Equivalent to:
	 * <code>new GeneIdIterato(start_category, false)</code>
	 *
	 * @param start_category
	 */
	public GeneIdIterator(Category start_category) {
		this(start_category, false);
	}

	/**
	 * Creates a new iterator starting at the given {@code start_category}. If
	 * the
	 * {@code only_annotated} is true, then only gene IDs will be returned, that
	 * are in a mapping that has been annotated.
	 *
	 * @param start_category
	 * @param only_annotated
	 */
	public GeneIdIterator(Category start_category, boolean only_annotated) {
		miter = new MappingIterator(start_category, only_annotated);
		fill_mappings();
	}

	public boolean hasNext() {
		return !found.isEmpty();
	}

	public String next() {
		if (found.size() <= 1) {
			fill_mappings();
		}
		String next = found.pollFirst();
		returned.add(next);
		return next;
	}

	public void remove() {
		throw new UnsupportedOperationException("Not supported EVER.");
	}

	private void fill_mappings() {
		while (miter.hasNext() && found.size() <= 1) {
			String id = miter.next().getGeneId();
			if (!returned.contains(id) && !found.contains(id)) {
				found.add(id);
			}
		}
	}
}
