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
public class ActionUseAsRoot extends AbstractAction implements TreeSelectionListener {

	private final HiviMainWindow main;

	public ActionUseAsRoot(HiviMainWindow aThis) {
		super("Use selected node as new root", "Creates a new result panel with the selected node being the root category");
		this.main = aThis;
		setEnabled(false);
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		main.useSelectedNodeAsRoot();
	}

	@Override
	public void valueChanged(TreeSelectionEvent tse) {
		setEnabled( main.getSelectedCategory() != null);
	}
	
}
