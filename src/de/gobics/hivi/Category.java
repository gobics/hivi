package de.gobics.hivi;

import de.gobics.hivi.iterator.*;
import java.util.*;
import java.util.logging.Logger;

/**
 * A category is a node in the functional hierarchy. Mostly, it is a container
 * for the {@link Mapping}s and and children {@link Category}s.
 *
 *
 * @author manuel
 */
public class Category implements Comparable<Category>, Iterable<Category> {

	private static final Logger logger = Logger.getLogger(Category.class.getName());
	private final String id;
	private String name;
	private List<Category> children = new ArrayList<Category>();
	private final TreeSet<Mapping> mappings = new TreeSet<Mapping>();

	/**
	 * Creates a new category with the given ID and name. The ID should be
	 * unique for the whole hierarchy (this will not be checked).
	 *
	 * @param id (not null)
	 * @param name some name or description
	 * @throws NullPointerException if the given ID is null
	 */
	public Category(String id, String name) {
		if (id == null) {
			throw new NullPointerException("ID can not be null");
		}
		this.id = id;
		this.name = name;

	}

	/**
	 * Returns the ID of this category. This ID can be used to refer to this
	 * category in the remote database where this functional hierarchy is
	 * extracted from.
	 *
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Returns the name/description of this category.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (id != null) {
			sb.append(id).append(" - ");
		}

		//sb.append(name).append(" (").append(countGenesRecursive()).append(";unique: ").append(getGenesRecursive().size()).append(")");
		sb.append(name).append(" (").append(getAnnotatedMappings().size()).
				append("/").append(getAnnotatedMappingsRecursive().size()).
				append(")");
		return sb.toString();
	}

	/**
	 * Change the name of this category to the new name.
	 *
	 * @param new_name
	 */
	public void setName(String new_name) {
		this.name = new_name;
	}

	/**
	 * Implements required method: categories are compared based on their ID.
	 *
	 * @param o
	 * @return
	 */
	public int compareTo(Category o) {
		return getId().compareTo(o.getId());
	}

	/**
	 * Adds a new child this this category. If the
	 *
	 * @param c the child to add
	 */
	public void addChildCategory(Category c) {
		if (!children.contains(c)) {
			children.add(c);
		}
	}

	/**
	 * Adds a new mapping directly to this tree. Because the mapping knows it
	 * destiny (the category it belongs to), it is ensured, that it is put to
	 * the right location.
	 *
	 * @param g the mapping to add
	 * @return true if the desired category can be found
	 */
	public boolean addMapping(Mapping g) {
		if (id.equals(g.getCategoryId())) {
			mappings.add(g);
		}
		Category target = findCategory(g.getCategoryId());
		if (target == null) {
			return false;
		}
		target.mappings.add(g);
		return true;
	}

	/**
	 * Returns the mappings that are located directly at this node.
	 *
	 * @return a (unique) set of the mappings
	 */
	public Set<Mapping> getMappings() {
		return mappings;
	}

	/**
	 * Returns the number of mappings at this category.
	 *
	 * @return number of mappings
	 */
	public int countGeneMappings() {
		return mappings.size();
	}

	/**
	 * Returns the number of annotated mappings at this category.
	 *
	 * @return number of mappings
	 */
	public int countAnnotatedMappings() {
		int count = 0;
		for (Mapping m : mappings) {
			if (m.isAnnotated()) {
				count++;
			}
		}
		return count;
	}

	/**
	 * Returns the number of annotated mappings at this category.
	 *
	 * @return number of mappings
	 */
	public int countAnnotatedMappingsRecursive() {
		int count = 0;
		Iterator<Mapping> iter = iteratorMapping(true);
		while (iter.hasNext()) {
			iter.next();
			count++;
		}
		return count;
	}

	/**
	 * Returns an iterator iterating over all categories, that are contained by
	 * this category. This call is equal to:
	 * <code>getChildrenRecursive().iterator()</code> but implemented in a
	 * memory-saving manner.
	 *
	 * @return
	 */
	public Iterator<Category> iterator() {
		return new CategoryIterator(this);
	}

	/**
	 * Creates and returns an iterator to iterate over all mappings located
	 * somewhere under this category (including own mappings).
	 *
	 * @param annotated_only set to true if only annotated mappings should be
	 * iterated.
	 * @return the iterator
	 */
	public Iterator<Mapping> iteratorMapping(boolean annotated_only) {
		return new MappingIterator(this, annotated_only);
	}

	/**
	 * Creates and returns an iterator to iterate over (distinct) gene ids
	 * located under this category.
	 *
	 * @param annotated_only set to true if only annotated mappings should be
	 * iterated
	 * @return the iterator
	 */
	public Iterator<String> iteratorGeneId(boolean annotated_only) {
		return new GeneIdIterator(this, annotated_only);
	}

	/**
	 * Tries to find a category by the given ID. If the current category is the
	 * searched category, it is returned. Otherwise, the method is invoked on
	 * all child categories.
	 *
	 * @param id the ID to search for
	 * @return a category with the given ID or {@code null} if no such category
	 * exists under this node
	 */
	public Category findCategory(String id) {
		Iterator<Category> iter = new CategoryIterator(this);

		while (iter.hasNext()) {
			Category next = iter.next();
			if (next.getId().equals(id)) {
				return next;
			}
		}

		return null;
	}

	/**
	 * Returns a string containing representing the tree under this node (for
	 * debugging purpose only).
	 *
	 * @return
	 */
	public String dump() {
		return dump(0);
	}

	/**
	 * Helper for {@link #dump} including the depth for indentation.
	 *
	 * @param depth
	 * @return
	 */
	protected String dump(int depth) {
		StringBuilder sb = new StringBuilder("");
		for (int i = 0; i < depth; i++) {
			sb.append(" ");
		}

		sb.append(id).append("-").append(getName()).append(" (").append(mappings.
				size()).append(") {");
		for (Mapping gm : mappings) {
			sb.append(gm.getGeneId()).append(" (").append(gm.getQuality()).
					append(")").append(", ");
		}
		sb.append("}\n");
		for (Category c : children) {
			sb.append(c.dump(depth + 1));
		}
		return sb.toString();
	}

	/**
	 * Returns an array containing the children.
	 *
	 * @return
	 */
	public List<Category> getChildren() {
		return new ArrayList<Category>(children);
	}

	/**
	 * Returns a list of all children that are located under this category
	 * (including their children).
	 *
	 * <B>Note:</B> internally uses the {@link CategoryIterator}.
	 *
	 * @return all categories located in the tree starting at the current
	 * category.
	 */
	public List<Category> getChildrenRecursive() {
		List<Category> list = new LinkedList<Category>();
		Iterator<Category> iter = iterator();
		while (iter.hasNext()) {
			list.add(iter.next());
		}

		//logger.finer(this+" returns list of "+list.size()+" entries");
		return list;
	}

	/**
	 * Returns a set of all mappings in this category that are annotated.
	 *
	 * @return
	 */
	public Set<Mapping> getAnnotatedMappings() {
		Set<Mapping> result = new TreeSet<Mapping>();
		for (Mapping m : getMappings()) {
			if (m.isAnnotated()) {
				result.add(m);
			}
		}

		return result;
	}

	/**
	 * Returns true if this or one of its children has a mapping that has been
	 * annotated during.
	 *
	 * @return true if there is an annotated mapping in this (or child)
	 * category.
	 */
	public boolean hasAnnotatedMappingsRecursive() {
		Iterator<Mapping> iter = iteratorMapping(true);
		while (iter.hasNext()) {
			if (iter.next().isAnnotated()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns a list of mappings that are located somewhere under this category
	 * and are annotated.
	 *
	 * @return a set containing all annotated mappings
	 */
	public Set<Mapping> getAnnotatedMappingsRecursive() {
		return toSet(iteratorMapping(true));
	}

	/**
	 * Returns a list of mappings that are located under this category.
	 *
	 * @return a set containing all mappings
	 */
	public Set<Mapping> getMappingsRecursive() {
		return toSet(iteratorMapping(false));
	}

	/**
	 * Returns a set of (distinct) gene IDs located somewhere under this
	 * category (inclusive).
	 *
	 * @return
	 */
	public Set<String> getGeneIdsRecursive() {
		return toSet(iteratorGeneId(false));
	}

	
	public Set<String> getAnnotatedGeneIds() {
		Set<String> ids = new TreeSet<String>();
		for(Mapping gm : mappings){
			if(gm.isAnnotated()){
				ids.add(gm.getGeneId());
			}
		}
		return ids;
	}
	/**
	 * Returns a set of (distinct) gene IDs located somewhere under this
	 * category (inclusive) that are in an annotated mapping.
	 *
	 * @return
	 */
	public Set<String> getAnnotatedGeneIdsRecursive() {
		TreeSet<String> ids = new TreeSet<String>();
		for (Mapping gm : getAnnotatedMappingsRecursive()) {
			ids.add(gm.getGeneId());
		}
		return ids;
	}

	/**
	 * Sorts the children according to this comparator.
	 *
	 * @param sorter the comparator to sort to
	 */
	public void sort(Comparator<Category> sorter) {
		Category[] cats = children.toArray(new Category[children.size()]);
		Arrays.sort(cats, sorter);
		children.clear();
		for (Category cat : cats) {
			children.add(cat);
		}
	}

	/**
	 * Sorts the children according to this comparator and and then iterates
	 * over the children to sort them.
	 *
	 * <B>Note:</B> if this category contains a big subtree, this may take a lot
	 * of time. If you build a GUI application consider the usage of a
	 * {@link SwingWorker} or equivalent procedure:
	 * <code>
	 * Iterator&lt;Category&gt; citer = iterator();
	 *	while (citer.hasNext()) {
	 *	citer.next().sort(sorter);
	 * }
	 * </code> Actually, this is what happens inside and therefore its time
	 * consume is equivalent.
	 *
	 * @param sorter the comparator to sort to
	 */
	public void sortRecursive(Comparator<Category> sorter) {
		Iterator<Category> citer = iterator();
		while (citer.hasNext()) {
			citer.next().sort(sorter);
		}
	}

	/**
	 * Performs a deep-copy of this category. All mappings will be added and
	 * also the children will be deep-copied.
	 *
	 * @return
	 */
	@Override
	public Category clone() {
		Category clone = new Category(getId(), getName());
		for (Mapping gm : getMappings()) {
			clone.addMapping(gm);
		}
		for (Category child : getChildren()) {
			clone.addChildCategory(child.clone());
		}
		return clone;
	}

	/**
	 * Iterates over the iterator and creates a set of
	 *
	 * @param <T>
	 * @param iter
	 * @return
	 */
	private static <T> Set<T> toSet(Iterator<T> iter) {
		Set<T> found = new TreeSet<T>();
		while (iter.hasNext()) {
			found.add(iter.next());
		}
		return found;
	}

}