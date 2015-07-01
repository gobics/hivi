/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.gobics.hivi.gui;

import de.gobics.hivi.Mapping;
import de.gobics.marvis.utils.ArrayUtils;
import java.util.List;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 *
 * @author manuel
 */
class GeneMappingTableModel implements TableModel {

	private final static int col_id = 0;
	private final static int col_category = 1;
	private final static int col_quality = 2;
	private final static int col_message = 3;
	private final Mapping[] mappings;

	public GeneMappingTableModel(Mapping[] mappings) {
		
		this.mappings = mappings;
	}

	public int getRowCount() {
		return mappings.length;
	}

	public int getColumnCount() {
		return col_message + 1;
	}

	public String getColumnName(int i) {
		if (i == col_id) {
			return "Gene ID";
		}
		if (i == col_category) {
			return "Category";
		}
		if (i == col_quality) {
			return "Mapping quality";
		}
		if (i == col_message) {
			return "Additional message";
		}
		throw new RuntimeException("Column out of range: " + i);
	}

	public Class<?> getColumnClass(int i) {
		if (i == col_quality) {
			return Double.class;
		}
		return String.class;
	}

	public boolean isCellEditable(int i, int i1) {
		return true;
	}

	public Object getValueAt(int row, int column) {
		Mapping gm = mappings[row];
		if (gm == null) {
			return null;
		}
		if (column == col_id) {
			return gm.getGeneId();
		}
		if (column == col_category) {
			return gm.getCategoryId();
		}
		if (column == col_quality) {
			return (Double) gm.getQuality();
		}
		if (column == col_message) {
			return gm.getMessage();
		}
		throw new RuntimeException("No such column: " + column);
	}

	public void setValueAt(Object o, int i, int i1) {
		//ignore - cells are only ediable to copy the IDs
	}

	public void addTableModelListener(TableModelListener tl) {
		//throw new UnsupportedOperationException("Not supported yet.");
	}

	public void removeTableModelListener(TableModelListener tl) {
		//throw new UnsupportedOperationException("Not supported yet.");
	}
}
