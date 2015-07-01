/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.gobics.hivi;

import java.util.Comparator;

/**
 *
 * @author manuel
 */
public class GeneMappingQualitySorter implements Comparator<Mapping> {

	public int compare(Mapping first, Mapping second) {
		int c = Double.compare(first.getQuality(), second.getQuality());
		if( c == 0 )
			return first.compareTo(second);
		return c;
	}



}
