package org.gui4j.component.factory;

import org.gui4j.component.Gui4jEdit;
import org.gui4j.component.Gui4jPasswordField;
import org.gui4j.core.Gui4jComponentContainer;


public class Gui4jPasswordFieldFactory extends Gui4jEditFactory
{
    private final String NAME = "password";

    /**
     * @see org.gui4j.core.Gui4jComponentFactory#getName()
     */
    public String getName()
    {
        return NAME;
    }

    public Gui4jEdit createTextFieldComponent(Gui4jComponentContainer gui4jComponentContainer, String id)
    {
        return new Gui4jPasswordField(gui4jComponentContainer, id);
    }

}
