package org.gui4j.core;

import java.awt.event.MouseEvent;

import org.gui4j.Gui4jCallBase;

public final class Gui4jComponentArg implements Gui4jComponent
{
    private final String id;
    private final Gui4jInternal gui4j;

    public Gui4jComponentArg(Gui4jInternal gui4j, String id)
    {
        this.gui4j = gui4j;
        this.id = id;
    }

    /**
     * @see org.gui4j.core.Gui4jComponent#createGui4jComponentInstance(Gui4jSwingContainer, Gui4jCallBase, Gui4jQualifiedComponent)
     */
    public Gui4jComponentInstance createGui4jComponentInstance(
        Gui4jSwingContainer gui4jSwingContainer,
        Gui4jCallBase gui4jCallBase,
        Gui4jQualifiedComponent gui4jComponentInPath)
    {
        return gui4jSwingContainer.getGui4jComponentInstance(gui4jComponentInPath);
    }

    /**
     * @see org.gui4j.core.Gui4jComponent#getId()
     */
    public String getId()
    {
        return id;
    }

    /**
     * @see org.gui4j.core.Gui4jComponent#getGui4j()
     */
    public Gui4jInternal getGui4j()
    {
        return gui4j;
    }

    /**
     * @see org.gui4j.core.Gui4jComponent#getGui4jComponentContainer()
     */
    public Gui4jComponentContainer getGui4jComponentContainer()
    {
        assert false;
        return null;
    }

    /**
     * @see org.gui4j.core.Gui4jComponent#getGui4jComponentProperties()
     */
    public Gui4jComponentProperty[] getGui4jComponentProperties()
    {
        assert false;
        return null;
    }

    /**
     * @see org.gui4j.core.Gui4jComponent#refreshComponent(Gui4jComponentInstance)
     */
    public void refreshComponent(Gui4jComponentInstance gui4jComponentInstance)
    {
        assert false;
    }

    /**
     * @see org.gui4j.core.Gui4jComponent#dispose(Gui4jComponentInstance)
     */
    public void dispose(Gui4jComponentInstance gui4jComponentInstance)
    {
        assert false;
    }

    public void showPopupMenu(Gui4jComponentInstance mGui4jComponentInstance, MouseEvent mouseEvent)
    {
        assert false;
    }


    /* (non-Javadoc)
     * @see org.gui4j.core.Gui4jComponent#handleException(org.gui4j.core.Gui4jComponentInstance, java.lang.Throwable)
     */
    public void handleException(Gui4jComponentInstance gui4jComponentInstance, Throwable t)
    {
        assert false;
    }

    /* (non-Javadoc)
     * @see org.gui4j.core.Gui4jComponent#handleSuccess(org.gui4j.core.Gui4jComponentInstance)
     */
    public void handleSuccess(Gui4jComponentInstance gui4jComponentInstance)
    {
        assert false;
    }

    /* (non-Javadoc)
     * @see org.gui4j.core.Gui4jComponent#evaluateContext(org.gui4j.Gui4jCallBase)
     */
    public Object evaluateContext(Gui4jCallBase gui4jCallBase)
    {
        assert false;
        return null;
    }

}
