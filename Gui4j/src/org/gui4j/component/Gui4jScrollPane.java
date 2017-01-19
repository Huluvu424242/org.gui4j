package org.gui4j.component;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JScrollPane;
import javax.swing.border.Border;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gui4j.core.Gui4jComponentContainer;
import org.gui4j.core.Gui4jComponentInstance;
import org.gui4j.core.Gui4jQualifiedComponent;


public final class Gui4jScrollPane extends Gui4jJComponent
{
    private static final Log mLogger = LogFactory.getLog(Gui4jScrollPane.class);

    private final Gui4jQualifiedComponent mGui4jComponentInPath;
    private final int mHScrollPolicy;
    private final int mVScrollPolicy;

    /**
     * Constructor for Gui4jScrollPane.
     * @param gui4jComponentContainer
     * @param id
     * @param gui4jComponentInPath
     * @param hScrollPolicy
     * @param vScrollPolicy
     */
    public Gui4jScrollPane(
        Gui4jComponentContainer gui4jComponentContainer,
        String id,
        Gui4jQualifiedComponent gui4jComponentInPath,
        int hScrollPolicy,
        int vScrollPolicy)
    {
        super(gui4jComponentContainer, JScrollPane.class, id);
        mGui4jComponentInPath = gui4jComponentInPath;
        mHScrollPolicy = hScrollPolicy;
        mVScrollPolicy = vScrollPolicy;
    }

    protected void setProperties(Gui4jComponentInstance gui4jComponentInstance)
    {
        super.setProperties(gui4jComponentInstance);
        JScrollPane scrollPane = (JScrollPane) gui4jComponentInstance.getComponent();
        Gui4jComponentInstance subInstance =
            gui4jComponentInstance.getGui4jSwingContainer().getGui4jComponentInstance(
                gui4jComponentInstance.getGui4jComponentInPath().getGui4jComponentPath(),
                mGui4jComponentInPath);
        scrollPane.setViewportView(subInstance.getComponent());
        scrollPane.setHorizontalScrollBarPolicy(mHScrollPolicy);
        scrollPane.setVerticalScrollBarPolicy(mVScrollPolicy);
        
        scrollPane.getVerticalScrollBar().setFocusable(false);
        scrollPane.getHorizontalScrollBar().setFocusable(false);
        
        if (false || getGui4j().traceMode())
        {
            mLogger.debug(
                "Preferred size of scrollPane with id " + getId() + " is " + scrollPane.getPreferredSize());
            mLogger.debug(
                "Preferred size of subComponent with id "
                    + subInstance.getGui4jComponent().getId()
                    + " is "
                    + subInstance.getSwingComponent().getPreferredSize());
        }
    }

    public void setViewportBorder(JScrollPane scrollPane, Border border) {
        scrollPane.setViewportBorder(border);
    }
    
    /**
     * @see org.gui4j.core.Gui4jAbstractComponent#setBackground(java.awt.Component, java.awt.Color)
     */
    public void setBackground(Component component, Color background)
    {
        super.setBackground(component, background);
        JScrollPane scrollPane = (JScrollPane) component;
        scrollPane.getViewport().setBackground(background);
    }

}
