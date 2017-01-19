package org.gui4j.component;

import javax.swing.JToggleButton;

import org.gui4j.core.Gui4jComponentContainer;

public class Gui4jToggleButton extends Gui4jAbstractButton
{

    /**
     * Constructor.
     * @param gui4jComponentContainer
     * @param id
     */
    public Gui4jToggleButton(Gui4jComponentContainer gui4jComponentContainer, String id)
    {
        super(gui4jComponentContainer, JToggleButton.class, id);
    }

}
