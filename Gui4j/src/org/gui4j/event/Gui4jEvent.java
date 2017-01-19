package org.gui4j.event;

/**
 * The interface <code>Event</code> can be used to register and
 * deregister <code>EventListener</code> instances.
 */
public interface Gui4jEvent
{
    /** 
     * Add listener
     * @param listener
     */
    void addEventListener(Gui4jEventListener listener);

    /**
     * Remove listener
     * @param listener
     */
    void removeEventListener(Gui4jEventListener listener);
}
