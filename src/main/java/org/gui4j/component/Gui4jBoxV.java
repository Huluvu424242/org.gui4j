package org.gui4j.component;

import java.util.List;

import javax.swing.Box;
import javax.swing.JComponent;

import org.gui4j.core.Gui4jComponentContainer;

/**
 * Vertical composition of components
 */
public final class Gui4jBoxV extends Gui4jBox
{

    /**
     * Constructor for Gui4jBoxV.
     * 
     * @param gui4jComponentContainer
     * @param id
     * @param gui4jComponents
     */
    public Gui4jBoxV(Gui4jComponentContainer gui4jComponentContainer, String id, List gui4jComponents, String alignment)
    {
        super(gui4jComponentContainer, id, gui4jComponents, alignment);
    }

    protected Box createLayoutContainer()
    {
        return Box.createVerticalBox();
    }

    protected void setAlignment(JComponent component)
    {
        component.setAlignmentX(0f);
    }

}
