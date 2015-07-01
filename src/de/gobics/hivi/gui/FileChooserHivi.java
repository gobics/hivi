package de.gobics.hivi.gui;

import de.gobics.marvis.utils.swing.filechooser.ChooserAbstract;
import de.gobics.marvis.utils.swing.filechooser.FileFilterAbstract;
import java.io.File;
import java.io.FileFilter;
import java.util.logging.Logger;

/**
 * The awesome new FileChooserHivi
 *
 * @author Manuel Landesfeind &lt;manuel@gobics.de&gt;
 */
public class FileChooserHivi extends ChooserAbstract {

	private static final Logger logger = Logger.getLogger(FileChooserHivi.class.
			getName());

	public FileChooserHivi() {
		this(new File("."));
	}

	public FileChooserHivi(File preselect) {
		super(preselect);

		FileFilterAbstract hividb = new FileFilterAbstract() {

			@Override
			public String getDescriptionName() {
				return "HiVi Database v1.0";
			}

			@Override
			public String[] getDefaultExtensions() {
				return new String[]{"hividb"};
			}
		};

		setFileFilter(hividb);
	}
}
