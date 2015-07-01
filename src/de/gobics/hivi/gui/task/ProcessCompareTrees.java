package de.gobics.hivi.gui.task;

import de.gobics.hivi.Category;
import de.gobics.hivi.TreeComparissonResult;
import de.gobics.hivi.comparator.AbstractCategoryComparator;
import de.gobics.marvis.utils.task.AbstractTask;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Given two categories, this tasks compares the scores of the underlying tree.
 *
 * @author manuel
 */
public class ProcessCompareTrees extends AbstractTask<TreeComparissonResult, Void> {

	private final List<Category> roots;
	private final List<AbstractCategoryComparator> sorter;

	public ProcessCompareTrees(List<Category> roots, List<AbstractCategoryComparator> comparator) {
		setTaskTitle("Compare trees");
		this.sorter = comparator;

		// Check the roots to be of the same ID
		for (Category r1 : roots) {
			for (Category r2 : roots) {
				if (!r1.getId().equals(r2.getId())) {
					throw new RuntimeException("Category roots do not have the same ID");
				}
			}
		}

		this.roots = new ArrayList<Category>(roots);
	}

	@Override
	protected TreeComparissonResult doTask() throws Exception {
		setTaskDescription("Building list of all categories");
		Map<String, Category[]> id_to_cats = createMap();
		int size = roots.size();

		setTaskDescription("Calculating all scores");
		setProgressMax(id_to_cats.size());
		setProgress(0);

		TreeComparissonResult result = new TreeComparissonResult(size);

		for (String cid : id_to_cats.keySet()) {
			Category[] cats = id_to_cats.get(cid);
			Number[] scores = new Number[size];
			for (int i = 0; i < scores.length; i++) {
				Comparable score = cats[i] != null
						? sorter.get(i).getScore(cats[i])
						: Double.NaN;
				scores[i] = score instanceof Number ? (Number) score : Double.NaN;
			}

			result.addResult(cats, scores);

			incrementProgress();
			if (isCanceled()) {
				return null;
			}
		}


		return result;
	}

	/**
	 * Create a map containing the IDs to the categories.
	 *
	 * @return
	 */
	private Map<String, Category[]> createMap() {
		Map<String, Category[]> id_to_cats = new HashMap<String, Category[]>();
		int size = roots.size();

		for (int idx = 0; idx < size; idx++) {
			Iterator<Category> iter = roots.get(idx).iterator();

			while (iter.hasNext()) {
				Category c = iter.next();
				String cid = c.getId();

				if (!id_to_cats.containsKey(cid)) {
					id_to_cats.put(cid, new Category[size]);
				}
				id_to_cats.get(cid)[idx] = c;
			}
		}
		return id_to_cats;

	}
}
