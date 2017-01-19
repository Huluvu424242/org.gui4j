package org.gui4j.util;

import java.io.Serializable;

/**
 * A Pair is a container for two objects, the "first" and the "second" one.
 * Instances of <code>Pair</code> are immutable.
 */
public class Pair implements Serializable
{
    private final Object first;
    private final Object second;

    /**
     * Constructor for Pair.
     * @param first must be different from null
     * @param second must be different from null
     */
    public Pair(Object first, Object second)
    {
        assert first != null;
        assert second != null;
        this.first = first;
        this.second = second;
    }

    /**
     * Returns the first.
     * @return Object
     */
    public Object getFirst()
    {
        return first;
    }

    /**
     * Returns the second.
     * @return Object
     */
    public Object getSecond()
    {
        return second;
    }

    /**
     * @see java.lang.Object#equals(Object)
     */
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        if (!(obj instanceof Pair))
        {
            return false;
        }
        Pair pair = (Pair) obj;
        return first.equals(pair.first) && second.equals(pair.second);
    }

    /*
     * @see java.lang.Object#hashCode()
     * KKB, 17.3.04: hashCode calculated after "Effective Java", Item 8
     */
    public int hashCode()
    {
        int result = 17;
        result = 37 * result + first.hashCode();
        result = 37 * result + second.hashCode();
        
        return result;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return "(" + first + ", " + second + ")";
    }

}
