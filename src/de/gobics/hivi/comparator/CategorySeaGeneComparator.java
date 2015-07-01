package de.gobics.hivi.comparator;

import de.gobics.hivi.Category;
import de.gobics.hivi.HypergeometricDistribution;
import java.util.Iterator;

/**
 * Sorts categories based on a variant of the Set Enrichment Analysis utilizing
 * the hypergeometric distribution.
 *
 * @author manuel
 */
public class CategorySeaGeneComparator extends AbstractCategoryComparator<Double> {

	private final HypergeometricDistribution dist;

	public CategorySeaGeneComparator(Category root) {
		int overall = count(root.iteratorGeneId(false));
		int good = count(root.iteratorGeneId(true));
		this.dist = new HypergeometricDistribution(overall, good);
	}

	@Override
	public Double getScore(Category o1) {
		int test_size = count(o1.iteratorGeneId(false));
		int i = count(o1.iteratorGeneId(true));
		if( i == 0){
			return 1d;
		}
		Double pvalue = (Double) dist.hypergeom(test_size, i);
		return pvalue;
	}

	private int count(Iterator iter) {
		int num = 0;
		while (iter.hasNext()) {
			num++;
			iter.next();
		}
		return num;
	}

	@Override
	public boolean reverse() {
		return false;
	}

	@Override
	public String getName() {
		return "SEA p-Value (gene-based)";
	}
}
