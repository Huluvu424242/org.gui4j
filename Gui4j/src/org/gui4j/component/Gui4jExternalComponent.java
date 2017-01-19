package org.gui4j.component;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JPanel;

import org.gui4j.Gui4jCallBase;
import org.gui4j.core.Gui4jComponentContainer;
import org.gui4j.core.Gui4jComponentInstance;
import org.gui4j.core.Gui4jQualifiedComponent;
import org.gui4j.core.Gui4jSwingContainer;


public final class Gui4jExternalComponent extends Gui4jAbstractPopupComponent
{

    /**
     * Constructor for Gui4jExternalComponent.
     * @param gui4jComponentContainer
     * @param componentClass
     * @param id
     */
    public Gui4jExternalComponent(Gui4jComponentContainer gui4jComponentContainer, Class componentClass, String id)
    {
        super(gui4jComponentContainer, componentClass, id);
    }

    /* (non-Javadoc)
     * @see org.gui4j.core.Gui4jAbstractComponent#createComponentInstance(org.gui4j.core.Gui4jSwingContainer, org.gui4j.Gui4jCallBase, org.gui4j.core.Gui4jQualifiedComponent)
     */
    protected Gui4jComponentInstance createComponentInstance(
        Gui4jSwingContainer gui4jSwingContainer,
        Gui4jCallBase gui4jCallBase,
        Gui4jQualifiedComponent gui4jComponentInPath)
    {
    	/*
        Component component =
            (Component) mComponent.getValue(gui4jCallBase, null, null);
            */
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        Gui4jComponentInstance gui4jComponentInstance =
            new Gui4jComponentInstance(gui4jSwingContainer, panel, gui4jComponentInPath);
        return gui4jComponentInstance;
    }

    public void setComponent(Gui4jComponentInstance gui4jComponentInstance, Component component)
    {
    	JPanel panel = (JPanel)gui4jComponentInstance.getSwingComponent();
    	// panel.removeAll();
    	panel.add(component,"Center");
    }
        
}
