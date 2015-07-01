package de.gobics.hivi;

/**
 * A single mapping maps that maps a gene to a category.
 *
 * @author manuel
 */
public class Mapping implements Comparable<Mapping> {

	private final String id;
	private final float quality;
	private final String category;
	private final String message;
	private boolean annotated = false;

	public Mapping(String id, String category, float quality) {
		this(id, category, quality, null);
	}

	public Mapping(String id, String category, float quality, String message) {
		this.id = id;
		this.category = category;
		this.quality = quality;
		this.message = message;
	}

	public String getGeneId() {
		return id;
	}

	public String getCategoryId() {
		return category;
	}

	public String getMessage() {
		return message;
	}

	public double getQuality() {
		return quality;
	}

	public int compareTo(Mapping o) {
		int c = category.compareTo(o.category);
		if (c != 0) {
			return c;
		}
		return id.compareTo(o.id);
	}

	public boolean isAnnotated() {
		return annotated;
	}

	public void setAnnotated(boolean annotated) {
		this.annotated = annotated;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(getClass().getSimpleName());
		sb.append("[").append(getGeneId()).append(";");
		sb.append(getCategoryId()).append(";");
		sb.append(getMessage()).append("]");
		return sb.toString();
	}
}
