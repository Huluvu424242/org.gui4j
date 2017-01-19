package org.gui4j.component.factory;

import org.dom4j.LElement;

import org.gui4j.component.Gui4jJComponent;
import org.gui4j.component.Gui4jToggleButton;
import org.gui4j.core.Gui4jComponentContainer;

public class Gui4jToggleButtonFactory extends Gui4jAbstractButtonFactory
{
    private static final String NAME = "toggleButton";

    protected Gui4jJComponent defineGui4jJComponentBy(Gui4jComponentContainer gui4jComponentContainer, String id,
            LElement e)
    {
        return new Gui4jToggleButton(gui4jComponentContainer, id);
    }

    public String getName()
    {
        return NAME;
    }

}
