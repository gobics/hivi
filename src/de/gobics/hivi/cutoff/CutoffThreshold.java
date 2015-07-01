package de.gobics.hivi.cutoff;

import de.gobics.hivi.Mapping;

/**
 * The awesome new CutoffThreshold
 *
 * @author Manuel Landesfeind &lt;manuel@gobics.de&gt;
 */
public interface CutoffThreshold {

	public static CutoffThreshold NoCutoff = new CutoffThreshold() {
		@Override
		public boolean keep(Mapping mapping) {
			return true;
		}

		@Override
		public String getShortDescription() {
			return "no cutoff";
		}
	};

	public boolean keep(Mapping mapping);

	public String getShortDescription();
}
