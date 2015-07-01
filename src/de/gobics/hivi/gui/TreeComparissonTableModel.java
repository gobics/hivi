package de.gobics.hivi.gui;

import de.gobics.hivi.TreeComparissonResult;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 *
 * @author manuel
 */
public class TreeComparissonTableModel implements TableModel {

	private enum Type {

		MappingCount, MappingCountRecursive, GeneCount, GeneCountRecursive, Score
	}
	private static final int COLS_PER_TREE = Type.values().length;
	private final TreeComparissonResult result;

	public TreeComparissonTableModel(TreeComparissonResult result) {
		this.result = result;
	}

	@Override
	public int getRowCount() {
		return result.size();
	}

	@Override
	public int getColumnCount() {
		return 4 + (result.treeCount() * COLS_PER_TREE);
	}

	@Override
	public String getColumnName(int i) {
		if (i == 0) {
			return "Category ID";
		}
		if (i == 1) {
			return "Category name";
		}
		if (i ==2) {
			return "Min. difference";
		}
		if (i == 3) {
			return "Max. difference";
		}

		// We are in the tree attribute range
		int tree = getTreeIndex(i) + 1;
		Type type = getType(i);
		switch (type) {
			case MappingCount:
				return "Tree " + tree + " mapping count";
			case MappingCountRecursive:
				return "Tree " + tree + " mapping count (recursive)";
			case GeneCount:
				return "Tree " + tree + " gene count";
			case GeneCountRecursive:
				return "Tree " + tree + " gene count (recursive)";
			case Score:
				return "Tree " + tree + " score";
		}
		throw new RuntimeException("Can not detect column name for column: " + i);
	}

	@Override
	public Class<?> getColumnClass(int i) {

		if (i == 0 || i == 1) {
			return String.class;
		}
		if (i == 2 || i == 3) {
			return Double.class;
		}
		// We are in the tree attribute range
		Type type = getType(i);
		switch (type) {
			case MappingCount:
				return Integer.class;
			case MappingCountRecursive:
				return Integer.class;
			case GeneCount:
				return Integer.class;
			case GeneCountRecursive:
				return Integer.class;
			case Score:
				return Double.class;
		}
		throw new RuntimeException("Can not detect column type for column: " + i);
	}

	@Override
	public boolean isCellEditable(int i, int i1) {
		return true;
	}

	@Override
	public Object getValueAt(int row, int col) {
		if (col == 0) {
			return result.getId(row);
		}
		if (col == 1) {
			return result.getName(row);
		}
		if (col == 2) {
			return result.minDifference(row);
		}
		if (col == 3) {
			return result.maxDifference(row);
		}

		// We are in the tree attribute range
		int tree = getTreeIndex(col);
		Type type = getType(col);

		switch (type) {
			case MappingCount:
				return result.getCategories(row)[tree].countAnnotatedMappings();
			case MappingCountRecursive:
				return result.getCategories(row)[tree].countAnnotatedMappingsRecursive();
			case GeneCount:
				return result.getCategories(row)[tree].getAnnotatedGeneIds().size();
			case GeneCountRecursive:
				return result.getCategories(row)[tree].getAnnotatedGeneIdsRecursive().size();
			case Score:
				return result.getScores(row)[tree];
		}
		throw new RuntimeException("Can not get value for column: " + col);
	}

	@Override
	public void setValueAt(Object o, int i, int i1) {
		// ignore
	}

	@Override
	public void addTableModelListener(TableModelListener tl) {
		// ignore 
	}

	@Override
	public void removeTableModelListener(TableModelListener tl) {
		// ignore
	}

	private int getTreeIndex(int column) {
		column -= 4; // remove columns: id, min, max
		return (int) column / COLS_PER_TREE;
	}

	private Type getType(int column) {
		int tree_index = getTreeIndex(column);
		column -= 4; // remove first columns
		int part_column = column - (tree_index * COLS_PER_TREE);
		if (part_column == 0) {
			return Type.MappingCount;
		}
		if (part_column == 1) {
			return Type.MappingCountRecursive;
		}
		if (part_column == 2) {
			return Type.GeneCount;
		}
		if (part_column == 3) {
			return Type.GeneCountRecursive;
		}
		return Type.Score;
	}
}
