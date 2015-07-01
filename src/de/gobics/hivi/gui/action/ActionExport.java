/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.gobics.hivi.gui.action;

import de.gobics.hivi.Category;
import de.gobics.hivi.gui.HiviMainWindow;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

/**
 *
 * @author manuel
 */
public class ActionExport extends AbstractAction implements TreeSelectionListener {

	private final HiviMainWindow main;

	public ActionExport(HiviMainWindow aThis) {
		super("Export", "Export the selected category to a file", KeyEvent.VK_E);
		this.main = aThis;
		setEnabled(false);
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		main.exportResult();
	}

	@Override
	public void valueChanged(TreeSelectionEvent tse) {
		setEnabled(main.getSelectedCategory() != null);
	}
}
