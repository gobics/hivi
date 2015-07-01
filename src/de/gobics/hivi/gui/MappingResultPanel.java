package de.gobics.hivi.gui;

import de.gobics.hivi.*;
import de.gobics.hivi.comparator.AbstractCategoryComparator;
import de.gobics.hivi.gui.histogram.GMCategoryDataset;
import de.gobics.hivi.gui.task.ProcessSortTree;
import de.gobics.marvis.utils.task.TaskResultListener;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;

/**
 * The awesome new MappingResultPanel
 *
 * @author Manuel Landesfeind &lt;manuel@gobics.de&gt;
 */
public class MappingResultPanel extends JPanel {

	private static final Logger logger = Logger.getLogger(MappingResultPanel.class.
			getName());
	private final HiviMainWindow main;
	private final CategoryTree tree;
	private final SelectSortAlgorithm cb_sort_size = new SelectSortAlgorithm();
	/**
	 * This part specifies the database to read the data from.
	 */
	private final JTable table_genemappings = new JTable();
	private final ChartPanel chart_panel = new ChartPanel(new JFreeChart(new CategoryPlot()));
	private final PieChartPanel piechart_panel;
	public final MappingResult mapping_result;
	private final JPanel stat_panel = new JPanel();
	private final JCheckBox cb_display_empty = new JCheckBox("display empty subtrees", true);

	public MappingResultPanel(HiviMainWindow main, MappingResult mr) {
		super(new BorderLayout());
		this.main = main;
		piechart_panel = new PieChartPanel(main);
		this.mapping_result = mr;
		tree = new CategoryTree(new CategoryTreeModel(mr.root, cb_display_empty.
				isSelected()));

		// Left preview side
		JPanel preview_left = new JPanel(new BorderLayout());
		preview_left.add(cb_sort_size, BorderLayout.PAGE_START);
		JScrollPane spane = new JScrollPane(tree);
		preview_left.add(spane, BorderLayout.CENTER);
		preview_left.add(cb_display_empty, BorderLayout.PAGE_END);

		spane = new JScrollPane(table_genemappings);
		spane.setPreferredSize(new Dimension(10, 10));

		// Right side

		// - top chart panels
		JSplitPane split_charts = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, chart_panel, piechart_panel);

		JPanel panel_bottom = new JPanel(new BorderLayout());
		panel_bottom.add(stat_panel, BorderLayout.PAGE_START);
		panel_bottom.add(spane, BorderLayout.CENTER);
		JSplitPane split_second = new JSplitPane(JSplitPane.VERTICAL_SPLIT, split_charts, panel_bottom);

		JSplitPane split_first = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, preview_left, split_second);
		add(split_first, BorderLayout.CENTER);

		table_genemappings.setAutoCreateRowSorter(true);
		table_genemappings.setDefaultRenderer(Double.class, new TableCellRendereDouble());

		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent tse) {
				displaySelectedCategory();
			}
		});
		cb_sort_size.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				sortPreview();
			}
		});
		cb_display_empty.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				TreeModel model = tree.getModel();
				if (model instanceof CategoryTreeModel) {
					((CategoryTreeModel) model).setDisplayEmpty(cb_display_empty.isSelected());
				}
			}
		});

		tree.addTreeSelectionListener(main.action_export);
		tree.addTreeSelectionListener(main.action_use_as_new_root);
	}

	public MappingResult getMappingResult() {
		return mapping_result;
	}

	/**
	 * Returns the currently selected category in the tree. If no category is
	 * selected, this method might return null.
	 *
	 * @return
	 */
	public Category getSelectedCategory() {
		TreePath path = tree.getSelectionPath();
		if (path == null) {
			return null;
		}
		Object o = path.getLastPathComponent();
		if (o == null || !(o instanceof Category)) {
			return null;
		}

		return (Category) o;
	}

	public void displaySelectedCategory() {
		Category cat = getSelectedCategory();
		if (cat == null) {
			return;
		}
		Collection<Mapping> mappings = cat.getAnnotatedMappingsRecursive();

		// Display in table
		GeneMappingTableModel table_model = new GeneMappingTableModel(mappings.
				toArray(new Mapping[mappings.size()]));
		table_genemappings.setModel(table_model);


		// Update histogram
		Mapping[] maparray = mappings.toArray(new Mapping[mappings.size()]);
		CategoryDataset dataset = new GMCategoryDataset(maparray);

		JFreeChart chart = ChartFactory.createBarChart(null, null, null, dataset, PlotOrientation.VERTICAL, false, false, false);

		chart_panel.setChart(chart);
		piechart_panel.setRoot(cat);
	}

	/**
	 * Sort the result tree according to the selection state of the
	 * {@code cb_sort_size} check box.
	 */
	private void sortPreview() {
		Category result = getResultRoot();
		if (result == null) {
			JOptionPane.showMessageDialog(this, "No result available", "Warning", JOptionPane.WARNING_MESSAGE);
			return;
		}

		final AbstractCategoryComparator sorter = cb_sort_size.
				getSelectedSortAlgorithm(result);
		final ProcessSortTree process = new ProcessSortTree(result, sorter);
		process.addTaskListener(new TaskResultListener<Void>() {
			@Override
			public void taskDone() {
				Category new_root = process.getTaskResult();
				if (new_root != null) {
					tree.setModel(new CategoryTreeModel(new_root, cb_display_empty.
							isSelected()));
					tree.setSorter(sorter);
				}
			}
		});
		main.executeTask(process);
	}

	/**
	 * Returns the root of the results. This is extracted from the tree model.
	 *
	 * @return
	 */
	public Category getResultRoot() {
		Object node = ((CategoryTreeModel) tree.getModel()).getRoot();
		if (!(node instanceof Category)) {
			return null;
		}
		return (Category) node;
	}
}
