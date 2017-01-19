package org.gui4j.event;

import java.util.ArrayList;
import java.util.List;


/**
 * An event is fired only if the <code>fireEvent</code> method is called
 */
public class SimpleEvent implements Gui4jEvent
{
    private final List listeners;
    private boolean enabled = true;

    public SimpleEvent()
    {
        listeners = new ArrayList();
    }

    public synchronized void addEventListener(Gui4jEventListener listener)
    {
        listeners.add(listener);
    }

    public synchronized void removeEventListener(Gui4jEventListener listener)
    {
        listeners.remove(listener);
    }

    public final void fireEvent()
    {
        if (enabled)
        {
            // We take a snapshot of the listeners in a synchronized
            // block but notify the listeners outside of it.
            // Otherwise we have a Concurrent Modification problem if
            // the notification code tries to add or remove a listener.
            Object[] listenersSnapshot;
            synchronized (this)
            {
                listenersSnapshot = listeners.toArray();
            }
            for (int i = listenersSnapshot.length - 1; i >= 0; i--) {
                ((Gui4jEventListener)listenersSnapshot[i]).eventOccured();
            }
        }
    }

    /**
     * Returns the enabled.
     * @return boolean
     */
    public boolean isEnabled()
    {
        return enabled;
    }

    /**
     * Sets the enabled.
     * @param enabled The enabled to set
     */
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

}
