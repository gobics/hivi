package de.gobics.hivi.cutoff;

import de.gobics.hivi.Mapping;

/**
 * Keep only {@code Mapping}s that have a quality above this threshold.
 *
 * @author Manuel Landesfeind &lt;manuel@gobics.de&gt;
 */
public class CutoffHigherQualityThan implements CutoffThreshold {

	private final double quality;

	public CutoffHigherQualityThan(double thres) {
		this.quality = thres;
	}

	public boolean keep(Mapping mapping) {
		return mapping.getQuality() >= quality;
	}

	public String getShortDescription() {
		return "qual >= "+quality;
	}
}
