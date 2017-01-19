package org.gui4j.component;

import java.awt.Component;

import javax.swing.JSplitPane;

import org.gui4j.Gui4jCallBase;
import org.gui4j.core.Gui4jComponentContainer;
import org.gui4j.core.Gui4jComponentInstance;
import org.gui4j.core.Gui4jQualifiedComponent;
import org.gui4j.core.Gui4jSwingContainer;
import org.gui4j.core.swing.JSplitPaneExtended;

public final class Gui4jSplitPane extends Gui4jJComponent
{
    private final Gui4jQualifiedComponent mGui4jComponentInPath1;
    private final Gui4jQualifiedComponent mGui4jComponentInPath2;
    private final boolean mShowDivider;
    private int mOrientation;

    /**
     * Constructor for Gui4jSplitPane.
     * 
     * @param gui4jComponentContainer
     * @param id
     * @param orientation
     * @param showDivider
     * @param gui4jComponentInPath1
     * @param gui4jComponentInPath2
     */
    public Gui4jSplitPane(Gui4jComponentContainer gui4jComponentContainer, String id, int orientation,
            boolean showDivider, Gui4jQualifiedComponent gui4jComponentInPath1,
            Gui4jQualifiedComponent gui4jComponentInPath2)
    {
        super(gui4jComponentContainer, JSplitPaneExtended.class, id);
        mOrientation = orientation;
        mGui4jComponentInPath1 = gui4jComponentInPath1;
        mGui4jComponentInPath2 = gui4jComponentInPath2;
        mShowDivider = showDivider;
    }

    protected Gui4jComponentInstance createComponentInstance(Gui4jSwingContainer gui4jSwingContainer,
            Gui4jCallBase gui4jCallBase, Gui4jQualifiedComponent gui4jComponentInPath)
    {
        Gui4jComponentInstance subInstance1 = gui4jSwingContainer.getGui4jComponentInstance(gui4jComponentInPath
                .getGui4jComponentPath(), mGui4jComponentInPath1);
        Gui4jComponentInstance subInstance2 = gui4jSwingContainer.getGui4jComponentInstance(gui4jComponentInPath
                .getGui4jComponentPath(), mGui4jComponentInPath2);

        Component component1 = subInstance1.getComponent();
        Component component2 = subInstance2.getComponent();
        JSplitPaneExtended splitPane = new JSplitPaneExtended(mOrientation, component1, component2);
        splitPane.setContinuousLayout(true); // TODO KKB make available as attribute
        splitPane.setDividerBorderVisible(mShowDivider);
        double h1, h2, size;
        if (mOrientation == JSplitPane.VERTICAL_SPLIT)
        {
            h1 = component1.getPreferredSize().getHeight();
            h2 = component2.getPreferredSize().getHeight();
            size = splitPane.getPreferredSize().getHeight();
        }
        else
        {
            h1 = component1.getPreferredSize().getWidth();
            h2 = component2.getPreferredSize().getWidth();
            size = splitPane.getPreferredSize().getWidth();
        }

        splitPane.setDividerLocation((int) (h1 / (h1 + h2) * size));
        Gui4jComponentInstance gui4jComponentInstance = new Gui4jComponentInstance(gui4jSwingContainer, splitPane,
                gui4jComponentInPath);

        return gui4jComponentInstance;
    }

    public void setDividerSize(JSplitPaneExtended splitPane, int size)
    {
        splitPane.setDividerSize(size);
    }

    /**
     * @see org.gui4j.core.Gui4jAbstractComponent#setProperties(Gui4jComponentInstance)
     */
    protected void setProperties(Gui4jComponentInstance gui4jComponentInstance)
    {
        super.setProperties(gui4jComponentInstance);
    }

    public void setLocation(JSplitPaneExtended splitPane, double location)
    {
        if (location != -1.0)
        {
            if (splitPane.getOrientation() == JSplitPane.HORIZONTAL_SPLIT)
            {
                int pos = (int) (splitPane.getPreferredSize().getWidth() * location);
                splitPane.setDividerLocation(pos);
            }
            else
            {
                int pos = (int) (splitPane.getPreferredSize().getHeight() * location);
                splitPane.setDividerLocation(pos);
            }
        }
        else
        {
            double h1, h2, size;
            Component component1 = splitPane.getLeftComponent();
            Component component2 = splitPane.getRightComponent();
            if (mOrientation == JSplitPane.VERTICAL_SPLIT)
            {
                h1 = component1.getPreferredSize().getHeight();
                h2 = component2.getPreferredSize().getHeight();
                size = splitPane.getPreferredSize().getHeight();
            }
            else
            {
                h1 = component1.getPreferredSize().getWidth();
                h2 = component2.getPreferredSize().getWidth();
                size = splitPane.getPreferredSize().getWidth();
            }

            splitPane.setDividerLocation((int) (h1 / (h1 + h2) * size));
        }
    }

    public void setResizeWeight(JSplitPaneExtended splitPane, double weight)
    {
        splitPane.setResizeWeight(weight);
    }

}