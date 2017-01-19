package org.gui4j.dflt.event;

import org.gui4j.event.Gui4jEventListener;

/**
 * Fires an event if a value changes and is equal to a given reference value
 */
public final class EqualsValueEvent extends ValueEvent implements Gui4jEventListener
{
    private final Object refValue;
    private final ValueEvent valueEvent;

    /**
     * Constructor for EqualsValueEvent.
     * @param refValue
     */
    public EqualsValueEvent(Object refValue)
    {
        super();
        this.refValue = refValue;
        this.valueEvent = null;
    }

    /**
     * Constructor for EqualsValueEvent.
     * @param refValue
     * @param initalValue
     */
    public EqualsValueEvent(Object refValue, Object initalValue)
    {
        super(initalValue);
        this.refValue = refValue;
        this.valueEvent = null;
    }

    /**
     * Constructor for EqualsValueEvent.
     * @param refValue
     * @param valueEvent
     */
    public EqualsValueEvent(Object refValue, ValueEvent valueEvent)
    {
        super(valueEvent.getValue());
        this.refValue = refValue;
        this.valueEvent = valueEvent;
        this.valueEvent.addEventListener(this);
    }

    public void eventOccured()
    {
        setValue(valueEvent.getValue());
    }

    /* (non-Javadoc)
     * @see org.gui4j.dflt.event.ValueEvent#valueChangeEvent(java.lang.Object)
     */
    protected void valueChangeEvent(Object oldValue)
    {
        Object newValue = getValue();
        if (refValue == newValue || refValue != null && refValue.equals(newValue))
        {
            fireEvent();
        }
        else
        {
            // new value is different from refValue
            // check if oldValue was equal to refValue
            
            if (refValue == oldValue || refValue != null && refValue.equals(oldValue))
            {
                fireEvent();
            }
        }
    }

}
