package org.gui4j.component;

import javax.swing.JRadioButton;

import org.gui4j.core.Gui4jComponentContainer;

public final class Gui4jRadioButton extends Gui4jAbstractButton
{

    /**
     * Constructor for Gui4jRadioButton.
     * 
     * @param gui4jComponentContainer
     * @param id
     */
    public Gui4jRadioButton(Gui4jComponentContainer gui4jComponentContainer, String id)
    {
        super(gui4jComponentContainer, JRadioButton.class, id);
    }

}
