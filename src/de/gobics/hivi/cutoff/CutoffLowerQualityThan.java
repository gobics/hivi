package de.gobics.hivi.cutoff;

import de.gobics.hivi.Mapping;

/**
 * Keep only {@code Mapping}s that have a quality below this threshold.
 *
 * @author Manuel Landesfeind &lt;manuel@gobics.de&gt;
 */
public class CutoffLowerQualityThan implements CutoffThreshold {

	private final double quality;

	public CutoffLowerQualityThan(double thres) {
		this.quality = thres;
	}

	public boolean keep(Mapping mapping) {
		return mapping.getQuality() <= quality;
	}
	
	
	public String getShortDescription() {
		return "qual <= "+quality;
	}
}
