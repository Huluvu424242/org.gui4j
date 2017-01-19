package org.gui4j.examples.util;

import org.gui4j.Gui4j;
import org.gui4j.Gui4jCallBase;
import org.gui4j.exception.Gui4jExceptionHandler;

public class LightController implements Gui4jCallBase, Gui4jExceptionHandler
{
    private final Gui4j gui4j;
    private final Converter converter;

    public LightController(Gui4j gui4j)
    {
        this.gui4j = gui4j;
        this.converter = new Converter();
    }
    
    public Converter conv()
    {
        return converter;
    }
    
    public Gui4jExceptionHandler getDelegationExceptionHandler()
    {
        return null;
    }
    
    public Gui4jExceptionHandler getExceptionHandler()
    {
        return this;
    }
    
    public Gui4j getGui4j()
    {
        return gui4j;
    }
}
