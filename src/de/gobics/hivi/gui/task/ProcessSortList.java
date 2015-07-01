package de.gobics.hivi.gui.task;

import de.gobics.hivi.Category;
import de.gobics.hivi.comparator.AbstractCategoryComparator;
import de.gobics.hivi.comparator.CategoryIdComparator;
import de.gobics.marvis.utils.ArrayUtils;
import de.gobics.marvis.utils.task.AbstractTask;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Logger;

/**
 *
 * @author manuel
 */
public class ProcessSortList extends AbstractTask<Category[], Void> {

	private final static Logger logger = Logger.getLogger(ProcessSortList.class.
			getName());
	private final Category[] categories;
	private final AbstractCategoryComparator sorter;

	public ProcessSortList(Category[] categories) {
		this(categories, new CategoryIdComparator());
	}

	public ProcessSortList(Collection<Category> categories) {
		this(categories, new CategoryIdComparator());
	}

	public ProcessSortList(Collection<Category> categories, AbstractCategoryComparator sorter) {
		this(categories.toArray(new Category[categories.size()]), sorter);
	}

	public ProcessSortList(Category[] categories, AbstractCategoryComparator sorter) {
		this.categories = categories;
		this.sorter = sorter;
	}

	@Override
	public Category[] doTask() throws Exception {
		logger.finer("Generating list of categories");

		setTaskDescription("Calculating scores categories");
		ScoreCache[] scores = new ScoreCache[categories.length];
		setProgressMax(scores.length);
		setProgress(0);

		for (int idx = 0; idx < scores.length; idx++) {
			scores[idx] = new ScoreCache(categories[idx], sorter.getScore(categories[idx]));
			incrementProgress();
		}
		Arrays.sort(scores);

		for (int idx = 0; idx < scores.length; idx++) {
			categories[idx] = scores[idx].category;
		}
		
		if( sorter.reverse() ){
			ArrayUtils.reverseInplace(categories);
		}
		
		return categories;

	}

	private static class ScoreCache implements Comparable<ScoreCache> {

		private final Category category;
		private final Comparable score;

		public ScoreCache(Category c, Comparable s) {
			category = c;
			score = s;
		}

		@Override
		public int compareTo(ScoreCache t) {
			int c = this.score.compareTo(t.score);
			return c != 0 ? c : this.category.compareTo(t.category);
		}
	}
}
