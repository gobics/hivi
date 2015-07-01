package de.gobics.hivi.gui;

import de.gobics.hivi.Category;
import de.gobics.hivi.gui.action.AbstractAction;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.AttributedString;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

/**
 * Displays a pie chart of the children of a node.
 *
 * @author manuel
 */
public class PieChartPanel extends ChartPanel {

    private HiviMainWindow parent;
    private boolean display_genes = true;
    private final Set<Category> hidden = new TreeSet<Category>();
    private final List<String> name_lookup = new LinkedList<String>();
    private Category root = null;

    public PieChartPanel(HiviMainWindow parent, Category root) {
        this(parent);
        setRoot(root);
    }

    public PieChartPanel(HiviMainWindow parent) {
        super(ChartFactory.createPieChart(
                null, // chart title
                new DefaultPieDataset(), // data
                false, // include legend
                true,
                false), false);
        this.parent = parent;

        PiePlot plot = (PiePlot) getChart().getPlot();
        plot.setNoDataMessage("No data available");
        plot.setCircular(false);
        plot.setLabelGap(0.02);

        plot.setLabelGenerator(new PieSectionLabelGenerator() {
            @Override
            public String generateSectionLabel(PieDataset pd, Comparable cmprbl) {
                if (!(cmprbl instanceof Integer)) {
                    throw new RuntimeException("Not an integer");
                }
                return name_lookup.get(((Integer) cmprbl).intValue()) + " (" + pd.getValue(cmprbl).intValue() + (display_genes ? " genes)" : " mappings)");
            }

            @Override
            public AttributedString generateAttributedSectionLabel(PieDataset pd, Comparable cmprbl) {
                throw new RuntimeException("Unsupported");
            }
        });

        setPopupMenu(new PieMenu(this));
    }

    public void setRoot(Category root) {
        this.root = root;
        hidden.clear();
        updatePlot();
    }

    private void updatePlot() {
        DefaultPieDataset ds = new DefaultPieDataset();
        int other_count = 0;
        int counter = 0;
        name_lookup.clear();

        for (Category child : root.getChildren()) {
            int size = display_genes
                    ? child.getAnnotatedGeneIdsRecursive().size()
                    : child.getAnnotatedMappingsRecursive().size();

            if (!isHidden(child) && size > 0) {
                ds.setValue((Comparable) counter, size);
                name_lookup.add(counter, child.getId() + " - " + child.getName());
                counter++;
            } else {
                other_count += size;
                hidden.add(child);
            }
        }

        if (other_count > 0) {
            ds.setValue((Comparable) counter, other_count);
            name_lookup.add(counter, "Other");
            counter++;
        }

        ((PiePlot) getChart().getPlot()).setDataset(ds);
    }

    private boolean isHidden(Category child) {
        return hidden.contains(child);
    }

    private void setHidden(Category c, boolean hide) {
        if (hide) {
            hidden.add(c);
        } else {
            hidden.remove(c);
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                updatePlot();
            }
        });

    }

    public boolean isDisplayGenes() {
        return display_genes;
    }

    public void setDisplayGenes(boolean display_genes) {
        this.display_genes = display_genes;
        updatePlot();
    }

    private class PieMenu extends JPopupMenu {

        private final PieChartPanel chart_panel;
        private final JMenu submenu_display = new JMenu("Display categories...");

        public PieMenu(PieChartPanel chart) {
            this.chart_panel = chart;

            add(new JMenuItem(new AbstractAction("Export graphic", "Exports the graphic in a graphic format") {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    try {
                        chart_panel.setDoubleBuffered(false);
                        ExportGraphic2D.export(chart_panel);
                        chart_panel.setDoubleBuffered(true);
                    } catch (IOException ex) {
                        Logger.getLogger(PieChartPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }));

            final JCheckBoxMenuItem item_genes = new JCheckBoxMenuItem("Display genes", display_genes);
            item_genes.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    setDisplayGenes(item_genes.isSelected());
                }
            });
            add(item_genes);

            add(submenu_display);

            submenu_display.addMenuListener(new MenuListener() {
                @Override
                public void menuSelected(MenuEvent me) {
                    for (final Category c : root.getChildren()) {
                        final JCheckBoxMenuItem item = new JCheckBoxMenuItem(c.getId() + " - " + c.getName(), !isHidden(c));
                        item.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent ae) {
                                SwingUtilities.invokeLater(new Runnable() {
                                    @Override
                                    public void run() {

                                        setHidden(c, !item.isSelected());
                                    }
                                });
                            }
                        });
                        submenu_display.add(item);
                    }
                }

                @Override
                public void menuDeselected(MenuEvent me) {
                    submenu_display.removeAll();
                }

                @Override
                public void menuCanceled(MenuEvent me) {
                    submenu_display.removeAll();
                }
            });
        }
    }
}
