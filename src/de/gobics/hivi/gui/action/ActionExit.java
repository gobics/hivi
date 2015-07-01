/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.gobics.hivi.gui.action;

import de.gobics.hivi.gui.HiviMainWindow;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.Action;

/**
 *
 * @author manuel
 */
public class ActionExit extends AbstractAction {
	private final HiviMainWindow main;

	public ActionExit(HiviMainWindow main) {
		super("Exit", "Close this application", KeyEvent.VK_Q);
		this.main = main;
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		main.dispose();
	}
	
}
