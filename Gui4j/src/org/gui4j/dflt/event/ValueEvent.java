package org.gui4j.dflt.event;

import org.gui4j.event.SimpleEvent;

/**
 * Fires an event if the value changes
 */
public class ValueEvent extends SimpleEvent
{
    private Object value;

    public ValueEvent()
    {
    }

    public ValueEvent(Object initalValue)
    {
        value = initalValue;
    }

    public Object getValue()
    {
        return value;
    }
    
    private void valueChange(Object newValue)
    {
        Object oldValue = value;
        value = newValue;
        valueChangeEvent(oldValue);
    }

    protected void valueChangeEvent(Object oldValue)
    {
        fireEvent();
    }

    public void setValue(Object newValue)
    {
        if (value == null)
        {
            if (newValue != null)
            {
                valueChange(newValue);
            }
        }
        else
        {
            if (newValue == null)
            {
                valueChange(null);
            }
            else
            {
                if (!value.equals(newValue))
                {
                    valueChange(newValue);
                }
            }
        }
    }
}
