package org.gui4j.component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JToolBar;

import org.gui4j.Gui4jCallBase;
import org.gui4j.core.Gui4jComponentContainer;
import org.gui4j.core.Gui4jComponentInstance;
import org.gui4j.core.Gui4jQualifiedComponent;
import org.gui4j.core.Gui4jSwingContainer;

public class Gui4jToolbar extends Gui4jJComponent
{

    private final List mSubcomponents;
    private final int mOrientation;
    private final float mHAlignment;
    private final float mVAlignment;

    public Gui4jToolbar(Gui4jComponentContainer gui4jComponentContainer, String id, int orientation, float vAlignment,
            float hAlignment)
    {
        super(gui4jComponentContainer, JToolBar.class, id);

        mSubcomponents = new ArrayList();
        mOrientation = orientation;
        mHAlignment = hAlignment;
        mVAlignment = vAlignment;
    }

    protected Gui4jComponentInstance createComponentInstance(Gui4jSwingContainer gui4jSwingContainer,
            Gui4jCallBase gui4jCallBase, Gui4jQualifiedComponent gui4jComponentInPath)
    {

        JToolBar toolbar = new JToolBar(gui4jComponentInPath.getQualifiedId(), mOrientation);
        toolbar.setFloatable(false);
        toolbar.setRollover(true);

        Gui4jComponentInstance gui4jComponentInstance = new Gui4jComponentInstance(gui4jSwingContainer, toolbar,
                gui4jComponentInPath);
        return gui4jComponentInstance;
    }

    public void addButton(Gui4jQualifiedComponent gui4jSubComponent)
    {
        mSubcomponents.add(gui4jSubComponent);
    }

    public void addSeparator()
    {
        mSubcomponents.add(null);
    }

    protected void setProperties(Gui4jComponentInstance gui4jComponentInstance)
    {
        super.setProperties(gui4jComponentInstance);

        JToolBar toolbar = (JToolBar) gui4jComponentInstance.getComponent();
        Gui4jSwingContainer gui4jSwingContainer = gui4jComponentInstance.getGui4jSwingContainer();
        for (Iterator it = mSubcomponents.iterator(); it.hasNext();)
        {
            Gui4jQualifiedComponent gui4jSubComponent = (Gui4jQualifiedComponent) it.next();
            JComponent component;
            if (gui4jSubComponent == null)
            {
                toolbar.addSeparator();
                // need to get hold of separator component to set alignment
                // properties
                component = (JComponent) toolbar.getComponent(toolbar.getComponentCount() - 1);
            }
            else
            {
                Gui4jComponentInstance subInstance = gui4jSwingContainer.getGui4jComponentInstance(
                        gui4jComponentInstance.getGui4jComponentInPath().getGui4jComponentPath(), gui4jSubComponent);
                component = subInstance.getSwingComponent();
                toolbar.add(component);
            }
            component.setAlignmentX(mHAlignment);
            component.setAlignmentY(mVAlignment);
        }

    }

}