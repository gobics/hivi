package de.gobics.hivi.gui.action;

import de.gobics.hivi.gui.HiviMainWindow;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

/**
 * The awesome new ActionNewAnalysis
 *
 * @author Manuel Landesfeind &lt;manuel@gobics.de&gt;
 */
public class ActionNewTreeComparisson extends AbstractAction {

	private final HiviMainWindow main;

	public ActionNewTreeComparisson(HiviMainWindow main) {
		super("New tree comparisson", "Compare two or more trees", KeyEvent.VK_T);
		this.main = main;
		
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		main.compareTrees();
	}
}
