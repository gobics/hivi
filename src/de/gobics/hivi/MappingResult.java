package de.gobics.hivi;

import de.gobics.hivi.cutoff.CutoffThreshold;

/**
 * The awesome new MappingResult
 *
 * @author Manuel Landesfeind &lt;manuel@gobics.de&gt;
 */
public class MappingResult {

	public final Category root;
	public final Database database;
	public final CutoffThreshold cutoff;
	private String name;

	public MappingResult(Category root, Database database, CutoffThreshold cutoff) {
		this.root = root;
		this.database = database;
		this.cutoff = cutoff;

		name = database.getFile().getName() + " (" + cutoff.getShortDescription() + ")";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public MappingResult getSubResult(Category c) {
		if (root.findCategory(c.getId()) == null) {
			return null;
		}
		MappingResult r = new MappingResult(c, database, cutoff);
		r.setName(database.getFile().getName() + " (" + cutoff.
				getShortDescription() + "): " + c.getId() + " - " + c.getName());
		return r;
	}
}