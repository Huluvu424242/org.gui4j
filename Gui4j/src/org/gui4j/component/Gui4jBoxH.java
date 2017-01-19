package org.gui4j.component;

import java.util.List;

import javax.swing.Box;
import javax.swing.JComponent;

import org.gui4j.core.Gui4jComponentContainer;

/**
 * Horizontal composition of components
 */
public final class Gui4jBoxH extends Gui4jBox
{

    /**
     * Constructor for Gui4jHBox.
     * 
     * @param gui4jComponentContainer
     * @param id
     * @param gui4jComponents
     */
    public Gui4jBoxH(Gui4jComponentContainer gui4jComponentContainer, String id, List gui4jComponents, String alignment)
    {
        super(gui4jComponentContainer, id, gui4jComponents, alignment);
    }

    protected Box createLayoutContainer()
    {
        return Box.createHorizontalBox();
    }

    protected void setAlignment(JComponent component)
    {
        component.setAlignmentY(0f);
    }

}
