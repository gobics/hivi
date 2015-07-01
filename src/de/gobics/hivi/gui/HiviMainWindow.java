package de.gobics.hivi.gui;

import de.gobics.hivi.Category;
import de.gobics.hivi.MappingResult;
import de.gobics.hivi.TreeComparissonResult;
import de.gobics.hivi.comparator.AbstractCategoryComparator;
import de.gobics.hivi.gui.action.ActionExit;
import de.gobics.hivi.gui.action.ActionExport;
import de.gobics.hivi.gui.action.ActionNewAnalysis;
import de.gobics.hivi.gui.action.ActionNewTreeComparisson;
import de.gobics.hivi.gui.action.ActionUseAsRoot;
import de.gobics.hivi.gui.task.ProcessCompareTrees;
import de.gobics.hivi.gui.task.ProcessCreateMapping;
import de.gobics.hivi.gui.task.ProcessWriteOutput;
import de.gobics.marvis.utils.swing.Notebook;
import de.gobics.marvis.utils.swing.SpringUtilities;
import de.gobics.marvis.utils.swing.Statusdialog;
import de.gobics.marvis.utils.swing.TaskWrapper;
import de.gobics.marvis.utils.task.AbstractTask;
import de.gobics.marvis.utils.task.TaskResultListener;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

/**
 *
 * @author manuel
 */
public class HiviMainWindow extends JFrame {

    private static final Logger logger = Logger.getLogger(HiviMainWindow.class.
            getName());
    public final Statusdialog statusdialog = new Statusdialog(this);
    public final ActionNewAnalysis action_analyse = new ActionNewAnalysis(this);
    public final ActionNewTreeComparisson action_compare = new ActionNewTreeComparisson(this);
    public final ActionExport action_export = new ActionExport(this);
    public final ActionUseAsRoot action_use_as_new_root = new ActionUseAsRoot(this);
    public final ActionExit action_exit = new ActionExit(this);
    public final Notebook notebook = new Notebook();

    public HiviMainWindow() {
        super("Hivi v1.7");
        JPanel main = new JPanel(new BorderLayout());
        this.add(main);

        JMenuBar menubar = new JMenuBar();
        setJMenuBar(menubar);
        JMenu menu = new JMenu("File");
        menubar.add(menu);
        menu.add(new JMenuItem(action_analyse));
        menu.add(new JMenuItem(action_compare));
        menu.add(new JSeparator());
        menu.add(new JMenuItem(action_exit));

        menu = new JMenu("Selected node");
        menubar.add(menu);
        menu.add(new JMenuItem(action_use_as_new_root));
        menu.add(new JMenuItem(action_export));

        main.add(notebook, BorderLayout.CENTER);

        setMinimumSize(new Dimension(600, 500));
        pack();
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                HiviMainWindow kc = new HiviMainWindow();
                kc.setDefaultCloseOperation(EXIT_ON_CLOSE);
                kc.setVisible(true);
            }
        });
        // Timer to track and log the currently used amount of memory

//		Timer memory = new Timer(5000, new ActionListener() {
//			public void actionPerformed(ActionEvent ae) {
//				Runtime rt = Runtime.getRuntime();
//				logger.fine("Now consuming " + HumanReadable.bytes(rt.
//						totalMemory() - rt.freeMemory()) + " RAM");
//			}
//		});
//		memory.setRepeats(true);
//		memory.start();
    }

    /**
     * Displays the given error message.
     *
     * @param err_msg
     */
    public void error(String err_msg) {
        JOptionPane.showMessageDialog(this,
                err_msg,
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    public void mapInput() {
        final ProcessCreateMapping process = DialogInput.showDialog(this);
        if (process == null) {
            return;
        }
        process.addTaskListener(new TaskResultListener<Void>() {
            @Override
            public void taskDone() {
                if (process.isCanceled()) {
                    return;
                }

                MappingResult result = process.getTaskResult();
                if (result.root.getAnnotatedMappingsRecursive().isEmpty()) {
                    error("No genes have been mapped");
                    return;
                }
                displayResult(result);
            }
        });
        executeTask(process);
    }

    public void displayResult(MappingResult r) {
        notebook.addTab(r.getName(), new MappingResultPanel(this, r));
    }

    public void exportResult() {
        final ProcessWriteOutput process = DialogExport.showDialog(this);
        process.addTaskListener(new TaskResultListener<Void>() {
            @Override
            public void taskDone() {
                if (process.getTaskResult()) {
                    JOptionPane.showMessageDialog(HiviMainWindow.this, "Ouput created");
                }
            }
        });
        executeTask(process);
    }

    public Category getSelectedCategory() {
        Component c = notebook.getSelectedComponent();
        if (!(c instanceof MappingResultPanel)) {
            return null;
        }
        return ((MappingResultPanel) c).getSelectedCategory();
    }

    /**
     * Searches for the currently selected node (in the currently selected tab)
     * and displays this node as root in a new tab.
     */
    public void useSelectedNodeAsRoot() {
        Component c = notebook.getSelectedComponent();
        if (!(c instanceof MappingResultPanel)) {
            return;
        }
        MappingResultPanel mrp = (MappingResultPanel) c;
        Category cat = mrp.getSelectedCategory();
        if (cat == null) {
            return;
        }
        displayResult(mrp.getMappingResult().getSubResult(cat));
    }

    protected void executeTask(AbstractTask task) {
        statusdialog.monitorTask(task);
        new TaskWrapper(task).execute();
    }

    public void compareTrees() {
        List<MappingResult> results = getMappingResults();
        if (results.size() < 2) {
            error("At least two analyses are required first");
            return;
        }

        JCheckBox[] boxes = new JCheckBox[results.size()];
        SelectSortAlgorithm cb_sorter = new SelectSortAlgorithm();

        JPanel selections = new JPanel(new SpringLayout());
        selections.add(cb_sorter);

        for (int i = 0; i < boxes.length; i++) {
            boxes[i] = new JCheckBox(results.get(i).getName(), false);
            selections.add(boxes[i]);
        }
        SpringUtilities.makeCompactGrid(selections, boxes.length + 1, 1);

        JScrollPane spane = new JScrollPane(selections);
        int res = JOptionPane.showConfirmDialog(this, spane, "Select at least two trees", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (res != JOptionPane.OK_OPTION) {
            return;
        }

        List<Category> roots = new LinkedList<Category>();
        List<AbstractCategoryComparator> sorter = new LinkedList<AbstractCategoryComparator>();

        for (int i = 0; i < boxes.length; i++) {
            if (boxes[i].isSelected()) {
                roots.add(results.get(i).root);
                sorter.add(cb_sorter.getSelectedSortAlgorithm(results.get(i).root));
            }
        }
        if (roots.size() < 2) {
            error("At least two analyses need to be selected");
            return;
        }

        final ProcessCompareTrees process = new ProcessCompareTrees(roots, sorter);

        process.addTaskListener(new TaskResultListener<Void>() {
            @Override
            public void taskDone() {
                TreeComparissonResult result = process.getTaskResult();
                if (result == null) {
                    return;
                }
                TreeComparissonTableModel tm = new TreeComparissonTableModel(result);
                JTable table = new JTable(tm);
                table.setAutoCreateRowSorter(true);
                notebook.addTab("Tree comparisson", new JScrollPane(table));
            }
        });

        executeTask(process);
    }

    private List<MappingResult> getMappingResults() {
        List<MappingResult> results = new ArrayList<MappingResult>(notebook.getComponentCount());

        for (Component c : notebook.getComponents()) {
            if (c instanceof MappingResultPanel) {
                results.add(((MappingResultPanel) c).mapping_result);
            }
        }
        return results;
    }
}
