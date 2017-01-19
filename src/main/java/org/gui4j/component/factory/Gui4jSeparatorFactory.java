package org.gui4j.component.factory;

import org.dom4j.LElement;
import org.gui4j.component.Gui4jJComponent;
import org.gui4j.component.Gui4jSeparator;
import org.gui4j.core.Gui4jComponentContainer;


public class Gui4jSeparatorFactory extends Gui4jJComponentFactory
{
    public static final String NAME = "separator";

    /* (non-Javadoc)
     * @see org.gui4j.component.factory.Gui4jJComponentFactory#defineGui4jJComponentBy(org.gui4j.core.Gui4jComponentContainer, java.lang.String, org.dom4j.LElement)
     */
    protected Gui4jJComponent defineGui4jJComponentBy(
        Gui4jComponentContainer gui4jComponentContainer,
        String id,
        LElement e)
    {
        return new Gui4jSeparator(gui4jComponentContainer, id);
        
    }

    /**
     * @see org.gui4j.core.Gui4jComponentFactory#getName()
     */
    public String getName()
    {
        return NAME;
    }

}
