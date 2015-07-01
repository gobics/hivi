package de.gobics.hivi.gui.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;
import javax.swing.KeyStroke;

/**
 * The awesome new AbstractAction
 *
 * @author Manuel Landesfeind &lt;manuel@gobics.de&gt;
 */
public abstract class AbstractAction extends javax.swing.AbstractAction {

	private static final Logger logger = Logger.getLogger(AbstractAction.class.
			getName());

	public AbstractAction(String name, String tooltip) {
		this(name, tooltip, -1);
	}

	public AbstractAction(String name, String tooltip, int accelerator_key) {
		super(name);
		putValue(LONG_DESCRIPTION, tooltip);
		if (accelerator_key >= 0) {
			putValue(MNEMONIC_KEY, accelerator_key);
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(accelerator_key, KeyEvent.CTRL_MASK));
		}
	}
}
