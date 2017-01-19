package org.gui4j.component;

import javax.swing.JButton;

import org.gui4j.core.Gui4jComponentContainer;

public final class Gui4jButton extends Gui4jAbstractButton
{

    /**
     * Constructor for Gui4jButton.
     * @param gui4jComponentContainer
     * @param id
     */
    public Gui4jButton(Gui4jComponentContainer gui4jComponentContainer, String id)
    {
        super(gui4jComponentContainer, JButton.class, id);
    }

}