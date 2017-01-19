package org.gui4j.component;

import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.gui4j.Gui4jCallBase;
import org.gui4j.core.Gui4jComponentContainer;
import org.gui4j.core.Gui4jComponentInstance;
import org.gui4j.core.Gui4jQualifiedComponent;
import org.gui4j.core.Gui4jSwingContainer;

public final class Gui4jFlowLayout extends Gui4jJComponent
{
    private final List mGui4jComponents;
    private int mAlignment = FlowLayout.CENTER;

    /**
     * Constructor for Gui4jFlowLayout.
     * 
     * @param gui4jComponentContainer
     * @param id
     */
    public Gui4jFlowLayout(Gui4jComponentContainer gui4jComponentContainer, String id)
    {
        super(gui4jComponentContainer, JPanel.class, id);
        mGui4jComponents = new ArrayList();
    }

    public void addPlacement(Gui4jQualifiedComponent gui4jComponentInPath)
    {
        mGui4jComponents.add(gui4jComponentInPath);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.gui4j.core.Gui4jAbstractComponent#createComponentInstance(org.gui4j.core.Gui4jSwingContainer,
     *      org.gui4j.Gui4jCallBase, org.gui4j.core.Gui4jQualifiedComponent)
     */
    protected Gui4jComponentInstance createComponentInstance(Gui4jSwingContainer gui4jSwingContainer,
            Gui4jCallBase gui4jCallBase, Gui4jQualifiedComponent gui4jComponentInPath)
    {
        FlowLayout flowLayout = new FlowLayout(mAlignment);
        JPanel panel = new JPanel(flowLayout);
        Gui4jComponentInstance gui4jComponentInstance = new Gui4jComponentInstance(gui4jSwingContainer, panel,
                gui4jComponentInPath);
        for (Iterator it = mGui4jComponents.iterator(); it.hasNext();)
        {
            Gui4jQualifiedComponent subComponent = (Gui4jQualifiedComponent) it.next();
            Gui4jComponentInstance subInstance = gui4jSwingContainer.getGui4jComponentInstance(gui4jComponentInPath
                    .getGui4jComponentPath(), subComponent);
            panel.add(subInstance.getComponent());
        }
        return gui4jComponentInstance;
    }

    public void setAlignment(int alignment)
    {
        mAlignment = alignment;
    }

    public void setHSpacing(JComponent jComponent, int hSpacing)
    {
        ((FlowLayout) jComponent.getLayout()).setHgap(hSpacing);
    }

    public void setVSpacing(JComponent jComponent, int vSpacing)
    {
        ((FlowLayout) jComponent.getLayout()).setVgap(vSpacing);
    }

}