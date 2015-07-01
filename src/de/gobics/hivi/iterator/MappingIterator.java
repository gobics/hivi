package de.gobics.hivi.iterator;

import de.gobics.hivi.Category;
import de.gobics.hivi.Mapping;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Logger;

/**
 * An iterator to iterate over the mappings of a (sub-) tree.
 *
 * @author Manuel Landesfeind &lt;manuel@gobics.de&gt;
 */
public class MappingIterator implements Iterator<Mapping> {

		private final CategoryIterator citer;
		private final LinkedList<Mapping> found_mappings = new LinkedList<Mapping>();
		private final boolean only_annotated;

		/**
		 * Equivalent to: <code> new MappingIterator(start_category, false)</code>
		 * @param start_category 
		 */
		public MappingIterator(Category start_category) {
			this(start_category, false);
		}

		/**
		 * Creates a new mapping iterator starting at the given start category
		 * @param start_category category to start with (inclusive)
		 * @param only_annotated if true, only mappings will be returned if they have been annotated
		 */
		public MappingIterator(Category start_category, boolean only_annotated) {
			citer = new CategoryIterator(start_category);
			this.only_annotated = only_annotated;
			fill_mappings();
		}

		public boolean hasNext() {
			return !found_mappings.isEmpty();
		}

		public Mapping next() {
			if (found_mappings.size() <= 1) {
				fill_mappings();
			}
			return found_mappings.pollFirst();
		}

		public void remove() {
			throw new UnsupportedOperationException("Not supported EVER.");
		}

		private void fill_mappings() {
			while (citer.hasNext() && found_mappings.size() <= 1) {
				Category next_category = citer.next();

				if (only_annotated) {
					for (Mapping m : next_category.getMappings()) {
						if (m.isAnnotated()) {
							found_mappings.add(m);
						}
					}
				}
				else {
					found_mappings.addAll(next_category.getMappings());
				}
			}
		}
	}
