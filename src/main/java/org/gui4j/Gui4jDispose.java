package org.gui4j;

/**
 * Interface for cleanup.
 */
public interface Gui4jDispose
{
    /**
     * dispose instance. After this call, the instance must not be used anymore. Used to 
     * make the work easier for the garbage collection.
     */
    void dispose();
}
