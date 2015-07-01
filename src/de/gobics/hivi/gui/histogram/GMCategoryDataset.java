/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.gobics.hivi.gui.histogram;

import de.gobics.hivi.Mapping;
import de.gobics.hivi.GeneMappingQualitySorter;
import de.gobics.marvis.utils.ArrayUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.general.DatasetGroup;

/**
 *
 * @author manuel
 */
public class GMCategoryDataset implements CategoryDataset {

	private static final List<Comparable> series_objs = new ArrayList<Comparable>(Arrays.
			asList(new String[]{"Data"}));
	private final String[] row_keys;
	private final Mapping[] mappings;
	private DatasetGroup group;

	public GMCategoryDataset(Mapping[] mappings) {
		this.mappings = mappings;
		Arrays.sort(mappings, new GeneMappingQualitySorter());
		
		row_keys = new String[mappings.length];
		for (int idx = 0; idx < mappings.length; idx++) {
			row_keys[idx] = mappings[idx].getGeneId();
		}
	}

	@Override
	public Comparable getRowKey(int i) {
		return (Comparable) getRowKeys().get(i);
	}

	@Override
	public int getRowIndex(Comparable cmprbl) {
		return getRowKeys().indexOf(cmprbl);
	}

	@Override
	public List getRowKeys() {
		return series_objs;
	}

	@Override
	public Comparable getColumnKey(int i) {
		return (Comparable) getColumnKeys().get(i);
	}

	@Override
	public int getColumnIndex(Comparable cmprbl) {
		return getColumnKeys().indexOf(cmprbl);
	}

	@Override
	public List getColumnKeys() {
		return Arrays.asList(row_keys);
	}

	@Override
	public Number getValue(Comparable row, Comparable col) {
		int idx = ArrayUtils.indexOf(row_keys, col);
		return getValue(1, idx);
	}

	@Override
	public int getRowCount() {
		return getRowKeys().size();
	}

	@Override
	public int getColumnCount() {
		return getColumnKeys().size();
	}

	@Override
	public Number getValue(int i, int i1) {
		return mappings[i1].getQuality();
	}

	@Override
	public void addChangeListener(DatasetChangeListener dl) {
		//Ignore
	}

	@Override
	public void removeChangeListener(DatasetChangeListener dl) {
		//Ignore
	}

	@Override
	public DatasetGroup getGroup() {
		return group;
	}

	@Override
	public void setGroup(DatasetGroup dg) {
		group = dg;
	}
}
