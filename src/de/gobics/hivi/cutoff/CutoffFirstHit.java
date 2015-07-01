package de.gobics.hivi.cutoff;

import de.gobics.hivi.Mapping;
import java.util.TreeSet;

/**
 * Keep only {@code Mapping}s that have a quality above this threshold.
 *
 * @author Manuel Landesfeind &lt;manuel@gobics.de&gt;
 */
public class CutoffFirstHit implements CutoffThreshold {

	private final TreeSet<String> gene_ids = new TreeSet<String>();

	public CutoffFirstHit() {
	}

	public void reset() {
		gene_ids.clear();
	}

	public boolean keep(Mapping mapping) {
		if (gene_ids.contains(mapping.getGeneId())) {
			return false;
		}
		gene_ids.add(mapping.getGeneId());
		return true;
	}

	public String getShortDescription() {
		return "First hit";
	}
}
