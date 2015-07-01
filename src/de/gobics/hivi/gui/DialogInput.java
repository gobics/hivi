package de.gobics.hivi.gui;

import de.gobics.hivi.Database;
import de.gobics.hivi.gui.task.ProcessCreateMapping;
import java.io.File;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 * The awesome new DialogInput
 *
 * @author Manuel Landesfeind &lt;manuel@gobics.de&gt;
 */
public class DialogInput {

	private static final Logger logger = Logger.getLogger(DialogInput.class.
			getName());
	private final static InputOptionsPanel options_panel = new InputOptionsPanel();

	public static ProcessCreateMapping showDialog(HiviMainWindow main) {
		int res = JOptionPane.showConfirmDialog(main, options_panel, "New Mapping", JOptionPane.OK_CANCEL_OPTION);
		if (res != JOptionPane.OK_OPTION) {
			return null;
		}

		// Get the database
		File db_file = options_panel.getDatabaseFile();
		if (db_file == null) {
			main.error("Please select a database");
			return null;
		}

		Database db = null;
		try {
			db = new Database(db_file);
		}
		catch (Exception ex) {
			main.error("Can not load database: \n" + ex.getMessage());
			return null;
		}

		// Get the gene list
		File in_file = options_panel.getInputFile();
		String in_list = options_panel.getGeneList();
		if (in_file == null && in_list == null) {
			main.error("Please select a text file containing gene-ids or provide it directly");
			return null;
		}

		ProcessCreateMapping process = in_file != null 
				? new ProcessCreateMapping(db, in_file) 
				: new ProcessCreateMapping(db, in_list);
		process.setQualityCutoff(options_panel.getQualityThreshold());

		return process;
	}
}
