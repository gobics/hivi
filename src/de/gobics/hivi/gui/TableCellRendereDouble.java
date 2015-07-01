/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.gobics.hivi.gui;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author manuel
 */
public class TableCellRendereDouble extends DefaultTableCellRenderer {

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		if ( value instanceof Double) {
			value = value.toString();
		}
		return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	}

}
