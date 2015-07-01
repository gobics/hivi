package de.gobics.hivi.gui;

import de.gobics.hivi.Mapping;
import de.gobics.hivi.cutoff.CutoffHigherQualityThan;
import de.gobics.hivi.cutoff.CutoffLowerQualityThan;
import de.gobics.hivi.cutoff.CutoffThreshold;
import de.gobics.marvis.utils.swing.FilechooserTextField;
import de.gobics.marvis.utils.swing.SpringUtilities;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.*;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;

/**
 * This panel is displayed in an JOptionPane when a new mapping is requested.
 *
 * @author manuel
 */
public class InputOptionsPanel extends JPanel {

	/**
	 * File chooser widget for the database file.
	 */
	private final FilechooserTextField chooser_db = new FilechooserTextField(new FileChooserHivi());
	/**
	 * File chooser widget for the input file.
	 */
	private final FilechooserTextField chooser_in = new FilechooserTextField();
	private final JTextArea gene_list = new JTextArea();
	/**
	 * SpinnerModel to select the quality cutoff.
	 */
	private final SpinnerNumberModel spinner_quality_cutoff = new SpinnerNumberModel(0, 0, Double.MAX_VALUE, 1);
	private final JCheckBox use_file = new JCheckBox("Gene file:");
	private final JCheckBox use_text = new JCheckBox("Gene list:");
	private final JCheckBox use_threshold = new JCheckBox("Use cutoff: Keep mapping with quality", false);
	private final JComboBox cb_cutofftype = new JComboBox(new String[]{"above", "below"});

	/**
	 * Creates a new panel.
	 *
	 * @param hivi
	 */
	public InputOptionsPanel() {
		super(new SpringLayout());

		JPanel quality = new JPanel();
		quality.add(cb_cutofftype);
		JSpinner spinner = new JSpinner(spinner_quality_cutoff);
		spinner.setPreferredSize(new Dimension(200, spinner.getMinimumSize().height + 5));
		add(spinner);
		quality.add(spinner);

		// the database
		add(new JLabel("Database:"));
		add(chooser_db);
		// the input file...
		add(use_file);
		add(chooser_in);
		// ... or use text
		add(use_text);
		JScrollPane spane = new JScrollPane(gene_list);
		spane.setPreferredSize(new Dimension(100, 100));
		add(spane);
		// thresholds
		add(use_threshold);
		add(quality);

		SpringUtilities.makeCompactGrid(this);

		// Setup actions
		use_file.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				use_text.setSelected(!use_file.isSelected());
				usage_action();
			}
		});

		use_text.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				use_file.setSelected(!use_text.isSelected());
				usage_action();
			}
		});

	}

	private void usage_action() {
		if (use_file.isSelected()) {
			use_text.setSelected(false);
			gene_list.setEnabled(false);
			chooser_in.setEnabled(true);
		}
		if (use_text.isSelected()) {
			use_file.setSelected(false);
			gene_list.setEnabled(true);
			chooser_in.setEnabled(false);
		}
	}

	/**
	 * Returns the selected database file. If no file is selected yet, a file
	 * open dialog will pop up.
	 *
	 * @return
	 */
	public File getDatabaseFile() {
		return chooser_db.getSelectedFile();
	}

	/**
	 * Returns the selected input file. If there is no file selected now, the
	 * file open dialog will show up.
	 *
	 * @return
	 */
	public File getInputFile() {
		if (!use_file.isSelected() || chooser_in.getSelectedFile() == null) {
			return null;
		}
		return chooser_in.getSelectedFile();
	}

	public String getGeneList() {
		if (!use_text.isSelected() || gene_list.getText().isEmpty()) {
			return null;
		}
		return gene_list.getText();
	}

	/**
	 * Returns the quality threshold for the mappings.
	 *
	 * @return some double value
	 */
	public CutoffThreshold getQualityThreshold() {
		if (use_threshold.isSelected()) {
			if (cb_cutofftype.getSelectedItem().equals("below")) {
				return new CutoffLowerQualityThan(spinner_quality_cutoff.
						getNumber().doubleValue());
			}
			return new CutoffHigherQualityThan(spinner_quality_cutoff.getNumber().
					doubleValue());
		}
		return new CutoffThreshold() {
			@Override
			public boolean keep(Mapping mapping) {
				return true;
			}

			@Override
			public String getShortDescription() {
				return "no cutoff";
			}
		};
	}
}