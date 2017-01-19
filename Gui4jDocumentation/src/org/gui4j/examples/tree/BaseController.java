package org.gui4j.examples.tree;

import org.gui4j.Gui4j;
import org.gui4j.Gui4jCallBase;
import org.gui4j.Gui4jController;
import org.gui4j.exception.Gui4jExceptionHandler;

public abstract class BaseController implements Gui4jController, Gui4jCallBase, Gui4jExceptionHandler
{
    private Gui4j gui4j;

    public BaseController(Gui4j gui4j)
    {
        this.gui4j = gui4j;
    }
    
    public void windowClosed()
    {
    }

    public Gui4jExceptionHandler getExceptionHandler()
    {
        return this;
    }

    public Gui4j getGui4j()
    {
        return gui4j;
    }

    public final Gui4jExceptionHandler getDelegationExceptionHandler()
    {
        return null;
    }

    
}
