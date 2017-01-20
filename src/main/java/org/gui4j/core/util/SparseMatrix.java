package org.gui4j.core.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.gui4j.util.Pair;

/**
 * Generic Class for sparsely populated matrices. The size of the matrix is dynamic, i.e.
 * you can specify arbitrary row and column parameters.
 * 
 * Another way to look at this class is as a "two-dimensional" map. Instead of a single key
 * it uses a row and a column element to store and retrieve data elements.
 * 
 * The public Interface Traverser allows to traverse the matrix generically. The Traverser object is passed
 * into <code>traverse(Traverser)</code> and is given the opportunity to work with each element of the matrix.
 */
public final class SparseMatrix implements Serializable
{
    /**
     * Allows objects to traverse the matrix.
     * @see SparseMatrix#traverse(SparseMatrix.Traverser)
     */
    public static interface Traverser
    {
        /**
         * Called once for each element that is visited during traversal.
         * There is no defined traversal sequence.
         * If the Traverser manipulates any one of row, col or value the effects on the matrix are undefined.
         * @param row the "row" of the traversal's current matrix cell
         * @param col the "column" of the traversal's current matrix cell
         * @param value the value of the traversal's current matrix cell
         */
        public void work(Object row, Object col, Object value);
    }

    // The map where all matrix elements are stored.
    private final Map matrixMap = new HashMap();

    /**
     * Returns the value found at the specified row and column of the matrix.
     * Returns null if there's no value for the specified row and column.
     * @param row Object
     * @param col Object
     * @return Object
     */
    public Object get(Object row, Object col)
    {
        return matrixMap.get(new Pair(row, col));
    }

    /**
     * Sets the value of the matrix at the specified row and column.
     * @param row Object
     * @param col Object
     * @param value
     */
    public void set(Object row, Object col, Object value)
    {
        if (row == null || col == null)
        {
            throw new IllegalArgumentException("row or column may not be null.");
        }

        matrixMap.put(new Pair(row, col), value);
    }

    public void remove(Object row, Object col)
    {
    	matrixMap.remove(new Pair(row, col));
    }

    /**
     * Returns a Set of all used "columns" in the matrix.
     * @return Set
     */
    public Set colSet()
    {
        Set colSet = new HashSet();

        for (Iterator iterator = matrixMap.keySet().iterator(); iterator.hasNext();)
        {
            Pair pair = (Pair) iterator.next();
            colSet.add(pair.getSecond());
        }

        return colSet;
    }

    /**
     * Returns a Set of all used "rows" in the matrix.
     * @return Set
     */
    public Set rowSet()
    {
        Set rowSet = new HashSet();

        for (Iterator iterator = matrixMap.keySet().iterator(); iterator.hasNext();)
        {
            Pair pair = (Pair) iterator.next();
            rowSet.add(pair.getFirst());
        }

        return rowSet;
    }

    /**
     * Traverses the matrix and calls <code>work()</code> on the supplied
     * Traverser object for each matrix element.
     * The traversal sequence is undefined.
     * @param traverser Traverser
     */
    public void traverse(Traverser traverser)
    {
        Iterator iter = matrixMap.entrySet().iterator();
        while (iter.hasNext())
        {
            Map.Entry entry = (Map.Entry) iter.next();
            Pair key = (Pair) entry.getKey();
            Object row = key.getFirst();
            Object col = key.getSecond();
            traverser.work(row, col, entry.getValue());
        }
    }
    
    /**
     * Löscht alle Elemente
     */
    public void clear()
    {
        matrixMap.clear();
    }

    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (!(obj instanceof SparseMatrix))
        {
            return false;
        }

        SparseMatrix other = (SparseMatrix) obj;

        return this.matrixMap.equals(other.matrixMap);
    }

    public int hashCode()
    {
        return matrixMap.hashCode();
    }

    public String toString()
    {
        return "SparseMatrix (" + matrixMap.size() + " elements)";
    }

}
