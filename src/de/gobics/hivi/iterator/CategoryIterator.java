package de.gobics.hivi.iterator;

import de.gobics.hivi.Category;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * An iterator to iterate over an specific (sub-) tree.
 *
 * @author Manuel Landesfeind &lt;manuel@gobics.de&gt;
 */
public class CategoryIterator implements Iterator<Category> {

		private final LinkedList<Category> to_visit = new LinkedList<Category>();

		public CategoryIterator(Category start_category) {
			to_visit.add(start_category);
		}

		public boolean hasNext() {
			return !to_visit.isEmpty();
		}

		public Category next() {
			Category next = to_visit.pollFirst();
			if (next == null) {
				throw new RuntimeException("Visited all nodes");
			}
			to_visit.addAll(next.getChildren());
			return next;
		}

		public void remove() {
			throw new UnsupportedOperationException("Not supported EVER.");
		}
	}
