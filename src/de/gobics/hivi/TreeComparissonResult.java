/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.gobics.hivi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author manuel
 */
public class TreeComparissonResult {

    private final int num_trees;
    private final List<String> id_to_idx = new LinkedList<String>();
    private final List<Category[]> categories = new LinkedList<Category[]>();
    private final List<Number[]> scores = new LinkedList<Number[]>();

    public TreeComparissonResult(int number_of_trees) {
        this.num_trees = number_of_trees;
    }

    /**
     * Adds a new result of categories and scores to this result set.
     *
     * @param cats the categories
     * @param scrs the scores
     * @return the index of the new result.
     */
    public synchronized int addResult(Category[] cats, Number[] scrs) {
        if (cats.length != num_trees) {
            throw new RuntimeException("Category array does not have required number of " + num_trees + " entries: " + cats.length);
        }
        if (scrs.length != num_trees) {
            throw new RuntimeException("Category array does not have required number of " + num_trees + " entries: " + scrs.length);
        }

        for (int idx1 = 0; idx1 < cats.length; idx1++) {
            for (int idx2 = idx1 + 1; idx2 < cats.length; idx2++) {
                if (cats[idx1] != null && cats[idx2] != null && !cats[idx1].getId().equals(cats[idx2].getId())) {
                    throw new RuntimeException("Categories have different IDs: " + cats[idx1].getId() + " != " + cats[idx2].getId());
                }
            }
        }

        String id = getIdOfCatArray(cats);
        if (id_to_idx.contains(id)) {
            throw new RuntimeException("Already have id: " + id);
        }
        id_to_idx.add(id);
        int idx = getIndex(id);
        categories.add(cats);
        scores.add(scrs);

        return idx;
    }

    public int size() {
        return id_to_idx.size();
    }

    public Collection<String> getIds() {
        return new ArrayList<String>(id_to_idx);
    }

    /**
     * Returns the ID of the given index.
     *
     * @param idx
     * @return the ID of the given result row or {@code null} if no such row
     * exists
     */
    public String getId(int idx) {
        return id_to_idx.get(idx);
    }

    public String getName(int idx) {
        return getNameOfCatArray(categories.get(idx));
    }

    /**
     * Returns the index of the given ID or -1 if this ID is unkown.
     *
     * @param id
     * @return
     */
    public int getIndex(String id) {
        return id_to_idx.indexOf(id);
    }

    public Category[] getCategories(String id) {
        if (!id_to_idx.contains(id)) {
            throw new RuntimeException("No such ID: " + id);
        }
        return getCategories(getIndex(id));
    }

    public Category[] getCategories(int row) {
        return categories.get(row);
    }

    public Number[] getScores(String id) {
        if (!id_to_idx.contains(id)) {
            throw new RuntimeException("No such ID: " + id);
        }
        return getScores(getIndex(id));
    }

    public Number[] getScores(int row) {
        return scores.get(row);
    }

    public double minDifference(int row) {
        Number[] scores = getScores(row);
        double min = Double.MAX_VALUE;
        for (int i1 = 0; i1 < scores.length; i1++) {
            for (int i2 = i1 + 1; i2 < scores.length; i2++) {
                min = Math.min(min, Math.abs(scores[i1].doubleValue() - scores[i2].doubleValue()));
            }
        }
        return min;
    }

    public double maxDifference(int row) {
        Number[] scores = getScores(row);
        double max = 0d;
        for (int i1 = 0; i1 < scores.length; i1++) {
            for (int i2 = i1 + 1; i2 < scores.length; i2++) {
                max = Math.max(max, Math.abs(scores[i1].doubleValue() - scores[i2].doubleValue()));
            }
        }
        return max;
    }

    private String getIdOfCatArray(Category[] cats) {
        for (int i = 0; i < cats.length; i++) {
            if (cats[i] != null) {
                return cats[i].getId();
            }
        }
        throw new RuntimeException("Can not get ID of array in: " + array_to_string(cats));
    }

    private String getNameOfCatArray(Category[] cats) {
        for (int i = 0; i < cats.length; i++) {
            if (cats[i] != null) {
                return cats[i].getName();
            }
        }
        throw new RuntimeException("Can not get name of array in: " + array_to_string(cats));
    }

    public int treeCount() {
        return num_trees;
    }

    private String array_to_string(Category[] cats) {
        if (cats.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder("[").append(cats[0].toString());
        for (int i = 0; i < cats.length; i++) {
            sb.append("; ").append(cats[i].toString());
        }
        return sb.toString();
    }
}
