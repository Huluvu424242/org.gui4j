package org.gui4j.component.factory;

import org.gui4j.component.Gui4jAbstractToggleButton;
import org.gui4j.component.Gui4jCheckBox;
import org.gui4j.core.Gui4jComponentContainer;

public class Gui4jCheckBoxFactory extends Gui4jAbstractToggleButtonFactory
{
    private static final String NAME = "checkBox";

    protected Gui4jAbstractToggleButton createGui4jToggleButton(Gui4jComponentContainer gui4jComponentContainer,
            String id)
    {
        return new Gui4jCheckBox(gui4jComponentContainer, id);
    }

    public String getName()
    {
        return NAME;
    }

}
