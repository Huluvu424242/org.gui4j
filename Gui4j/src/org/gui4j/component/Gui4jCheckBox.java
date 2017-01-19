package org.gui4j.component;

import javax.swing.JCheckBox;

import org.gui4j.core.Gui4jComponentContainer;

public class Gui4jCheckBox extends Gui4jAbstractToggleButton
{

    /**
     * Constructor for Gui4jCheckBox.
     * @param gui4jComponentContainer
     * @param id
     */
    public Gui4jCheckBox(Gui4jComponentContainer gui4jComponentContainer, String id)
    {
        super(gui4jComponentContainer, JCheckBox.class, id);
    }

}
