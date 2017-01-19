package org.gui4j.component;

import java.awt.KeyboardFocusManager;
import java.util.Map;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.border.Border;

import org.gui4j.Gui4jCallBase;
import org.gui4j.core.Gui4jCall;
import org.gui4j.core.Gui4jComponentContainer;
import org.gui4j.core.Gui4jComponentFactory;
import org.gui4j.core.Gui4jComponentInstance;
import org.gui4j.core.Gui4jMap1;
import org.gui4j.core.Gui4jQualifiedComponent;
import org.gui4j.core.Gui4jSwingContainer;
import org.gui4j.event.Gui4jEventListener;
import org.gui4j.exception.ErrorTags;
import org.gui4j.exception.Gui4jUncheckedException;


abstract public class Gui4jJComponent extends Gui4jAbstractPopupComponent implements ErrorTags
{

    protected Gui4jCall mGrabFocus;
    private boolean mHandleReadOnly;
    private String mKeyMapId;
    private Set mFocusTraversalKeysForward;
    private Set mFocusTraversalKeysBackward;

    public Gui4jJComponent(Gui4jComponentContainer gui4jComponentContainer, Class swingClass, String id)
    {
        super(gui4jComponentContainer, swingClass, id);
    }

    
    protected void setProperties(final Gui4jComponentInstance gui4jComponentInstance)
    {
        super.setProperties(gui4jComponentInstance);

        final JComponent component = gui4jComponentInstance.getSwingComponent();

        if (mKeyMapId != null)
        {
            Gui4jQualifiedComponent gui4jComponentInPath = gui4jComponentInstance.getGui4jComponent()
                .getGui4jComponentContainer()
                .getGui4jQualifiedComponent(mKeyMapId);
            Gui4jComponentInstance keyMapInstance = gui4jComponentInstance.getGui4jComponentInstance(gui4jComponentInPath);
            Gui4jKeyMap.applyDefinitions(keyMapInstance, component);
        }

        if (mGrabFocus != null)
        {
            Gui4jEventListener eventListener = new Gui4jEventListener()
            {
                public void eventOccured()
                {
                    Boolean b = Boolean.TRUE;
                    if (mGrabFocus.getResultClass() != null)
                    {
                        Map m = new Gui4jMap1(Gui4jComponentFactory.CONTEXT, gui4jComponentInstance.getContext());
                        b = (Boolean) mGrabFocus.getValue(gui4jComponentInstance.getGui4jCallBase(), m, null);
                    }
                    if (Boolean.TRUE.equals(b))
                    {
                        JComponent jComponent = gui4jComponentInstance.getSwingComponent();
                        jComponent.grabFocus();
                    }
                }
            };
            registerEvents(
                gui4jComponentInstance.getGui4jSwingContainer(),
                gui4jComponentInstance.getGui4jCallBase(),
                mGrabFocus.getDependantProperties(),
                eventListener);
        }

        if (mFocusTraversalKeysForward != null)
        {
            component.setFocusTraversalKeys(
                KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
                mFocusTraversalKeysForward);
        }

        if (mFocusTraversalKeysBackward != null)
        {
            component.setFocusTraversalKeys(
                KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
                mFocusTraversalKeysBackward);
        }

    }

    protected boolean handleReadOnly()
    {
        return mHandleReadOnly;
    }

    protected Gui4jComponentInstance createComponentInstance(
        Gui4jSwingContainer gui4jSwingContainer,
        Gui4jCallBase gui4jCallBase,
        Gui4jQualifiedComponent gui4jComponentInPath)
    {
        try
        {
            JComponent jComponent = (JComponent) getComponentClass().newInstance();
            Gui4jComponentInstance gui4jComponentInstance = new Gui4jComponentInstance(
                gui4jSwingContainer,
                jComponent,
                gui4jComponentInPath);
            return gui4jComponentInstance;
        }
        catch (IllegalAccessException e)
        {
            throw new Gui4jUncheckedException.ProgrammingError(PROGRAMMING_ERROR_illegal_access_exception, e);
        }
        catch (InstantiationException e)
        {
            throw new Gui4jUncheckedException.ProgrammingError(PROGRAMMING_ERROR_instantiation_exception, e);
        }
    }

    public void setHandleReadOnly(boolean handleReadOnly)
    {
        mHandleReadOnly = handleReadOnly;
    }

    public void setKeyMapId(String id)
    {
        mKeyMapId = id;
    }

    /**
     * Sets the border.
     * @param jComponent
     * @param border The border to set
     */
    public void setBorder(JComponent jComponent, Border border)
    {
        jComponent.setBorder(border);
    }

    /**
     * Sets the tooltip.
     * @param jComponent
     * @param text The tooltip to set
     */
    public void setTooltip(JComponent jComponent, String text)
    {
        jComponent.setToolTipText(text);
    }

    public void setOpaque(JComponent jComponent, boolean opaque)
    {
        jComponent.setOpaque(opaque);
    }

    /**
     * Sets the grabFocus.
     * @param grabFocus The grabFocus to set
     */
    public void setGrabFocus(Gui4jCall grabFocus)
    {
        mGrabFocus = grabFocus;
    }

    public void setFocusTraversalKeysForward(Set traversalKeys)
    {
        mFocusTraversalKeysForward = traversalKeys;
    }

    public void setFocusTraversalKeysBackward(Set traversalKeys)
    {
        mFocusTraversalKeysBackward = traversalKeys;
    }

}