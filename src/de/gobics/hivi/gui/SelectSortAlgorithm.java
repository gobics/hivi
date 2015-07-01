package de.gobics.hivi.gui;

import de.gobics.hivi.comparator.CategoryCountGeneComparator;
import de.gobics.hivi.comparator.AbstractCategoryComparator;
import de.gobics.hivi.comparator.CategoryIdComparator;
import de.gobics.hivi.comparator.CategoryCountMappingComparator;
import de.gobics.hivi.comparator.CategorySeaGeneComparator;
import de.gobics.hivi.comparator.CategorySeaGenemappingComparator;
import de.gobics.hivi.*;
import java.util.Comparator;
import javax.swing.JComboBox;

/**
 * The awesome new SelectSortAlgorithm
 *
 * @author Manuel Landesfeind &lt;manuel@gobics.de&gt;
 */
public class SelectSortAlgorithm extends JComboBox {

	public SelectSortAlgorithm() {
		addItem(("Sort by ID"));
		addItem(("Sort by gene-count (recursive)"));
		addItem(("Sort by mapping-count (recursive)"));
		addItem(("Sort by SEA (gene-based)"));
		addItem(("Sort by SEA (mapping-based)"));
	}

	public AbstractCategoryComparator getSelectedSortAlgorithm(Category root) {
		int sel = getSelectedIndex();
		int idx = 0;
		if (sel == idx++) {
			return new CategoryIdComparator();
		}
		if (sel == idx++) {
			return new CategoryCountGeneComparator();
		}
		if (sel == idx++) {
			return new CategoryCountMappingComparator();
		}
		if (sel == idx++) {
			return new CategorySeaGeneComparator(root);
		}
		if (sel == idx++) {
			return new CategorySeaGenemappingComparator(root);
		}
		throw new RuntimeException("Unkown selection: " + getSelectedItem() + " with index: " + sel);
	}
}
