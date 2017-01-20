package org.gui4j.core;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;

import org.gui4j.Gui4jCallBase;

/**
 * Contains the combination of a Gui4jComponent and a Swing instance. Note that a Gui4jComponent
 * depends only on a given resource name and a given controller class. Additionally,
 * the same instance of a Gui4jComponent might be used for several Swing instances. On the
 * other side, there is always one instance of Gui4jComponentInstance for each Swing instance.
 */
public final class Gui4jComponentInstance
{
    private Component mComponent;
    private Gui4jQualifiedComponent mGui4jComponentInPath;
    private Gui4jSwingContainer mGui4jSwingContainer;
    private Map mStorage;
    private Gui4jInternal mGui4j;
    private Gui4jCallBase mGui4jCallBase;
    private Object mContext = CONTEXT_NOT_INIT;

    private static final Object CONTEXT_NOT_INIT = new Object();

    public Gui4jComponentInstance(
        Gui4jSwingContainer gui4jSwingContainer,
        Component component,
        Gui4jQualifiedComponent gui4jComponentInPath)
    {
        assert gui4jComponentInPath != null;
        assert component != null;
        mComponent = component;
        mGui4jComponentInPath = gui4jComponentInPath;
        mGui4jSwingContainer = gui4jSwingContainer;
        mStorage = new HashMap();
        mGui4j = gui4jComponentInPath.getGui4jComponent().getGui4j();
        mGui4jCallBase = gui4jSwingContainer.getGui4jCallBase(gui4jComponentInPath);
    }

    public Gui4jInternal getGui4j()
    {
        return mGui4j;
    }

    public Object getContext()
    {
        if (mContext == CONTEXT_NOT_INIT)
        {
            mContext = mGui4jComponentInPath.getGui4jComponent().evaluateContext(getGui4jCallBase());
        }
        return mContext;
    }

    public Component getComponent()
    {
        return mComponent;
    }

    public JComponent getSwingComponent()
    {
        return (JComponent) mComponent;
    }

    public Gui4jComponent getGui4jComponent()
    {
        return mGui4jComponentInPath == null ? null : mGui4jComponentInPath.getGui4jComponent();
    }

    public Gui4jQualifiedComponent getGui4jComponentInPath()
    {
        return mGui4jComponentInPath;
    }

    public Gui4jSwingContainer getGui4jSwingContainer()
    {
        return mGui4jSwingContainer;
    }

    public void handleSuccess()
    {
        if (mGui4jComponentInPath != null)
        {
            Gui4jComponent gui4jComponent = mGui4jComponentInPath.getGui4jComponent();
            if (gui4jComponent != null)
            {
                gui4jComponent.handleSuccess(this);
                return;
            }
        }
        // nur ausführen, wenn oben etwas nicht definiert ist (=> Fehler)
        getGui4j().handleSuccess(getGui4jCallBase(), null);
    }

    public void handleException(Throwable t)
    {
        if (mGui4jComponentInPath != null)
        {
            Gui4jComponent gui4jComponent = mGui4jComponentInPath.getGui4jComponent();
            if (gui4jComponent != null)
            {
                gui4jComponent.handleException(this, t);
                return;
            }
        }
        // nur ausführen, wenn oben etwas nicht definiert ist (=> Fehler)
        Gui4jInternal gui4j = getGui4j();
        if (gui4j != null)
        {
            getGui4j().handleException(getGui4jCallBase(), t, null);
        }
    }

    /**
     * Convenience method.
     * @param path
     * @return Gui4jComponentInstance
     */
    public Gui4jComponentInstance getGui4jComponentInstance(Gui4jQualifiedComponent path)
    {
        return mGui4jSwingContainer.getGui4jComponentInstance(mGui4jComponentInPath.getGui4jComponentPath(), path);
    }

    public Gui4jCallBase getGui4jCallBase()
    {
        return mGui4jCallBase;
    }

    /**
     * Returns the storage.
     * @param tag
     * @return Object
     */
    public Object getStorage(Object tag)
    {
        return mStorage.get(tag);
    }

    /**
     * Sets the storage.
     * @param tag
     * @param storage The storage to set
     */
    public void setStorage(Object tag, Object storage)
    {
        mStorage.put(tag, storage);
    }

    public void refreshComponent()
    {
        getGui4jComponent().refreshComponent(this);
    }

    /**
     * Gibt Speicher frei
     */
    public void dispose()
    {
        if (mGui4jComponentInPath != null)
        {
            mGui4jComponentInPath.getGui4jComponent().dispose(this);
        }
        mGui4jSwingContainer = null;
        mComponent = null;
        mGui4jComponentInPath = null;
        mGui4jSwingContainer = null;
        if (mStorage != null)
        {
            mStorage.clear();
        }
        mStorage = null;
        mGui4j = null;
        mGui4jCallBase = null;
        mContext = null;
    }

}
