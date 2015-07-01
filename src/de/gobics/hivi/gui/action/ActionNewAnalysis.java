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
public class ActionNewAnalysis extends AbstractAction {

	private final HiviMainWindow main;

	public ActionNewAnalysis(HiviMainWindow main) {
		super("New analysis", "Read the input file and map it to the schema", KeyEvent.VK_N);
		this.main = main;
		
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		main.mapInput();
	}
}
