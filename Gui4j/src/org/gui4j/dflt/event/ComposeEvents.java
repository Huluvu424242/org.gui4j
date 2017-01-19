package org.gui4j.dflt.event;

import org.gui4j.event.Gui4jEvent;
import org.gui4j.event.Gui4jEventListener;
import org.gui4j.event.SimpleEvent;

/**
 * Verbindet die Abhaengigkeit von zwei Events
 */
public class ComposeEvents extends SimpleEvent implements Gui4jEvent, Gui4jEventListener
{

    public ComposeEvents(Gui4jEvent e1, Gui4jEvent e2)
    {
    	e1.addEventListener(this);
    	e2.addEventListener(this);
    }


    /* (non-Javadoc)
     * @see de.bea.event.EventListener#eventOccured()
     */
    public void eventOccured()
    {
    	fireEvent();
    }

}
