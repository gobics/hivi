package de.gobics.hivi.gui;

import de.gobics.hivi.Category;
import de.gobics.hivi.comparator.AbstractCategoryComparator;
import de.gobics.hivi.comparator.CategoryIdComparator;
import java.text.DecimalFormat;
import java.util.TreeMap;
import java.util.logging.Logger;
import javax.swing.JTree;
import javax.swing.tree.TreeModel;

/**
 * The awesome new CategoryTree
 *
 * @author Manuel Landesfeind &lt;manuel@gobics.de&gt;
 */
public class CategoryTree extends JTree {

	private static final Logger logger = Logger.getLogger(CategoryTree.class.
			getName());
	private AbstractCategoryComparator sorter = null;
	private final TreeMap<String, Comparable> scores = new TreeMap<String, Comparable>();

	public CategoryTree(CategoryTreeModel model) {
		scores.clear();
		setModel(model);
		setSorter(sorter);
	}

	public void setSorter(AbstractCategoryComparator sorter) {
		this.sorter = sorter;
		updateUI();
	}

	@Override
	public CategoryTreeModel getModel() {
		return (CategoryTreeModel) super.getModel();
	}

	@Override
	public void setModel(TreeModel model) {
		super.setModel(model);
		if (scores != null) {
			scores.clear();
		}
	}

	@Override
	public String convertValueToText(Object value,
			boolean selected,
			boolean expanded,
			boolean leaf,
			int row,
			boolean hasFocus) {
		if (!(value instanceof Category)) {
			return value.toString();
		}
		if (value == null) {
			return "null";
		}
		Category c = (Category) value;
		return c.getId() + " - " + c.getName() + " (" + formatScore(getScore(c)) + ")";
	}

	private Comparable getScore(Category c) {
		if (!scores.containsKey(c.getId() + "")) {
			if (sorter == null || sorter instanceof CategoryIdComparator) {
				scores.put(c.getId(), c.getAnnotatedGeneIdsRecursive().size());
			}
			else {
				scores.put(c.getId(), sorter.getScore(c));
			}
		}
		return scores.get(c.getId());
	}
	
	private String formatScore(Comparable n){
		if(n instanceof Number){
			float value = ((Number)n).floatValue();
			if( value == 0)
				return "0";
			if( value < 0.001)
				return new DecimalFormat("0.00E00").format(value);
			else if( value > 1)
				return new DecimalFormat("#").format(value);
			return new DecimalFormat("#.###").format(value);
		}
		return n.toString();
	}
}
