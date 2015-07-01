package de.gobics.hivi.gui;

import de.gobics.hivi.Category;
import de.gobics.hivi.gui.task.ProcessWriteOutput;
import de.gobics.marvis.utils.swing.FilechooserTextField;
import de.gobics.marvis.utils.swing.filechooser.ChooserExcelX;
import java.io.File;
import javax.swing.*;

/**
 * Dialog to set options for exporting an analysis.
 *
 * @author Manuel Landesfeind &lt;manuel@gobics.de&gt;
 */
public class DialogExport {

	private final static FilechooserTextField chooser = new FilechooserTextField(ChooserExcelX.getInstance(), true);
	private final static JCheckBox cb_include_parents = new JCheckBox("Include parents?");
	private final static SelectSortAlgorithm sorter_select = new SelectSortAlgorithm();
	

	public static ProcessWriteOutput showDialog(HiviMainWindow main) {
		Category root = main.getSelectedCategory();
		if( root == null ){
			main.error("Select a category to export");
			return null;
		}
		
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.add(new JLabel("Exporting category: "+root.getId()+" - "+root.getName()));
		panel.add(chooser);
		panel.add(cb_include_parents);
		panel.add(sorter_select);
		
		int res = JOptionPane.showConfirmDialog(main, panel, "Export", JOptionPane.OK_CANCEL_OPTION);
		if (res != JOptionPane.OK_OPTION) {
			return null;
		}

		File dest = chooser.getSelectedFile();
		if( dest == null){
			return null;
		}
		
		ProcessWriteOutput process = new ProcessWriteOutput(root, dest, cb_include_parents.isSelected(), sorter_select.getSelectedSortAlgorithm(root));
		return process;
	}
}
