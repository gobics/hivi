package de.gobics.hivi.gui.task;

import de.gobics.hivi.Category;
import de.gobics.hivi.Mapping;
import de.gobics.hivi.comparator.AbstractCategoryComparator;
import de.gobics.hivi.comparator.CategoryIdComparator;
import de.gobics.marvis.utils.task.AbstractTask;
import de.gobics.marvis.utils.task.AbstractTaskListener;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Export of the Hivi results into an Microsoft Excel 2003 (TM) file.
 *
 * @author manuel
 */
public class ProcessWriteOutput extends AbstractTask<java.lang.Boolean, Void> {

    private final static Logger logger = Logger.getLogger(ProcessWriteOutput.class.
            getName());
    private final Category root;
    private final File out;
    private boolean include_parent_categories = false;
    private final AbstractCategoryComparator sorter;

    /**
     * Create a new export task.
     *
     * @param root_category the root category to be exported
     * @param out the destination file
     * @param include_parent_categories true if gene counts have to be
     * calculated recursive
     */
    public ProcessWriteOutput(Category root_category, File out, boolean include_parent_categories) {
        this(root_category, out, include_parent_categories, new CategoryIdComparator());
    }

    /**
     * Create a new export task.
     *
     * @param root_category the root category to be exported
     * @param out the destination file
     * @param include_parent_categories true if gene counts have to be
     * calculated recursive
     * @param sorter the algorithm to sort the categories with
     */
    public ProcessWriteOutput(Category root_category, File out, boolean include_parent_categories, AbstractCategoryComparator sorter) {
        root = root_category;
        this.out = out;
        this.include_parent_categories = include_parent_categories;
        this.sorter = sorter;
    }

    public void includeParentCategories(boolean include) {
        this.include_parent_categories = include;
    }

    @Override
    public java.lang.Boolean doTask() throws Exception {
        logger.log(Level.FINE, "Writing subtree from node ''{0}'' to file: {1}", new Object[]{root.getId(), out.getAbsolutePath()});

        logger.finer("Generating list of categories");
        List<Category> cats_list = new LinkedList<>();
        cats_list.add(root);
        cats_list.addAll(root.getChildrenRecursive());

        setTaskDescription("Sort categories");
        setProgressMax(100);
        ProcessSortList sorter_process = new ProcessSortList(cats_list, sorter);
        sorter_process.addTaskListener(new AbstractTaskListener<Void>() {
            @Override
            public void setTaskProgress(int percentage) {
                setProgressMax(percentage);
            }
        });
        Category[] cats = sorter_process.perform();

        int row = 0, col = 0;

        setTaskDescription("Export categories to file");
        setProgressMax(cats_list.size());
        setProgress(0);

        logger.finer("Creating writeable notebook");
        System.err.println("DEBUG1");
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Hivi result");
        Row current_row = sheet.createRow(row++);
        current_row.createCell(col++).setCellValue("matched in number of categories:");
        current_row.createCell(col++).setCellValue(cats_list.size());

        row = 3;
        col = 0;
        current_row = sheet.createRow(row++);
        current_row.createCell(col++).setCellValue("category id");
        current_row.createCell(col++).setCellValue("category name");
        current_row.createCell(col++).setCellValue("mappings in this category");
        current_row.createCell(col++).setCellValue("mappings in this category and below");
        current_row.createCell(col++).setCellValue("genes in this category");
        current_row.createCell(col++).setCellValue("genes in this category and below");
        current_row.createCell(col++).setCellValue("score by " + sorter.getName());
        current_row.createCell(col++).setCellValue("orf (quality, message; ...)");

        for (Category c : cats) {
            incrementProgress();
            col = 0;

            // Check if the current category has some genes ANNOTATED
            if (include_parent_categories ? c.countAnnotatedMappingsRecursive() < 1 : c.countAnnotatedMappings() < 1) {
                continue;
            }

            current_row = sheet.createRow(row++);
            Cell cell = current_row.createCell(col++);
            cell.setCellType(Cell.CELL_TYPE_STRING);
            cell.setCellValue(c.getId());
            cell.setCellType(Cell.CELL_TYPE_STRING);
            current_row.createCell(col++).setCellValue(c.getName());

            // Output mapping counts
            current_row.createCell(col++).setCellValue(c.getAnnotatedMappings().size());
            current_row.createCell(col++).setCellValue(c.getAnnotatedMappingsRecursive().size());
            current_row.createCell(col++).setCellValue(c.getAnnotatedGeneIds().size());
            current_row.createCell(col++).setCellValue(c.getAnnotatedGeneIdsRecursive().size());

            // Output scores
            Comparable score = sorter.getScore(c);
            if (score instanceof Number) {
                current_row.createCell(col++).setCellValue(((Number) score).doubleValue());
            } else {
                current_row.createCell(col++).setCellValue(score.toString());
            }

            // List of genes
            Collection<Mapping> genes = include_parent_categories
                    ? c.getAnnotatedMappingsRecursive()
                    : c.getAnnotatedMappings();

            for (Mapping gm : genes) {
                current_row.createCell(col++).setCellValue(gm.getGeneId() + " ("
                        + gm.getQuality() + ", "
                        + gm.getMessage() + ")");
            }

            if (isCanceled()) {
                return null;
            }
        }
        logger.log(Level.FINE, "Wrote {0} resulting entries", cats_list.size());
        try (FileOutputStream fout = new FileOutputStream(out)) {
            workbook.write(fout);
        } catch (Throwable ex) {
            Logger.getLogger(ProcessWriteOutput.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }
}
