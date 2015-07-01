/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.gobics.hivi.gui;

import de.gobics.hivi.Category;
import de.gobics.marvis.utils.ArrayUtils;
import java.util.LinkedList;
import java.util.List;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 *
 * @author manuel
 */
public class CategoryTreeModel implements TreeModel {

	private final Category root;
	private final LinkedList<TreeModelListener> listener = new LinkedList<TreeModelListener>();
	private boolean display_empty_trees = true;

	public CategoryTreeModel() {
		this(new Category("null", "Please load a dataset first"), true);
	}

	public CategoryTreeModel(Category root, boolean display_empty) {
		this.root = root;
		display_empty_trees = display_empty;
	}

	public Object getRoot() {
		return root;
	}

	public Object getChild(Object parent, int index) {
		if (!(parent instanceof Category)) {
			throw new RuntimeException("Given node is not a Category");
		}

		return getChildren((Category) parent).get(index);
	}

	public int getChildCount(Object parent) {
		if (!(parent instanceof Category)) {
			throw new RuntimeException("Given node is not a Category");
		}
		return getChildren((Category) parent).size();
	}

	public boolean isLeaf(Object node) {
		if (!(node instanceof Category)) {
			throw new RuntimeException("Given node is not a Category");
		}
		return getChildCount(node) == 0;
	}

	public void valueForPathChanged(TreePath path, Object newValue) {
		//ignore
	}

	public int getIndexOfChild(Object parent, Object child) {
		if (!(parent instanceof Category)) {
			throw new RuntimeException("Given parent node is not a Category");
		}
		if (!(child instanceof Category)) {
			throw new RuntimeException("Given child node is not a Category");
		}

		return getChildren((Category) parent).indexOf(child);
	}

	private List<Category> getChildren(Category parent) {
		List<Category> childs = parent.getChildren();
		if (!display_empty_trees) {
			for (int i = 0; i < childs.size();) {
				if (childs.get(i).hasAnnotatedMappingsRecursive()) {
					i++;
				}
				else {
					childs.remove(i);
				}
			}
		}
		return childs;
	}

	public void addTreeModelListener(TreeModelListener l) {
		if (!listener.contains(l)) {
			listener.add(l);
		}
	}

	public void removeTreeModelListener(TreeModelListener l) {
		listener.remove(l);
	}

	public void setDisplayEmpty(boolean display_empty_subtrees) {
		display_empty_trees = display_empty_subtrees;
		for (TreeModelListener tml : listener) {
			tml.treeStructureChanged(new TreeModelEvent(this, new Object[]{root}));
		}
	}
}
