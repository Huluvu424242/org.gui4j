package org.gui4j.component.factory;

import org.dom4j.LElement;
import org.gui4j.component.Gui4jButton;
import org.gui4j.component.Gui4jJComponent;
import org.gui4j.core.Gui4jComponentContainer;

public final class Gui4jButtonFactory extends Gui4jAbstractButtonFactory
{

    private final String NAME = "button";

    /*
     * (non-Javadoc)
     * 
     * @see org.gui4j.component.factory.Gui4jJComponentFactory#defineGui4jJComponentBy(org.gui4j.core.Gui4jComponentContainer,
     *      java.lang.String, org.dom4j.LElement)
     */
    protected Gui4jJComponent defineGui4jJComponentBy(Gui4jComponentContainer gui4jComponentContainer, String id,
            LElement e)
    {

        return new Gui4jButton(gui4jComponentContainer, id);
    }

    public String getName()
    {
        return NAME;
    }

}