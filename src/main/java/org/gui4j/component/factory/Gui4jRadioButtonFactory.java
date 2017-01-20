package org.gui4j.component.factory;

import org.dom4j.LElement;

import org.gui4j.component.Gui4jJComponent;
import org.gui4j.component.Gui4jRadioButton;
import org.gui4j.core.Gui4jComponentContainer;

public final class Gui4jRadioButtonFactory extends Gui4jAbstractButtonFactory
{
    private static final String NAME = "radioButton";

    protected Gui4jJComponent defineGui4jJComponentBy(Gui4jComponentContainer gui4jComponentContainer, String id, LElement e)
    {
        return new Gui4jRadioButton(gui4jComponentContainer, id);
    }

    /**
     * @see org.gui4j.core.Gui4jComponentFactory#getName()
     */
    public String getName()
    {
        return NAME;
    }
}
