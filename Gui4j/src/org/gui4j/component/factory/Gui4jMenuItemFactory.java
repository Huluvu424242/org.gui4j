package org.gui4j.component.factory;

import java.util.List;

import javax.swing.JMenuItem;

import org.dom4j.LElement;
import org.gui4j.component.Gui4jJComponent;
import org.gui4j.component.Gui4jMenuItem;
import org.gui4j.core.Gui4jComponentContainer;
import org.gui4j.core.definition.Attribute;
import org.gui4j.core.definition.AttributeTypeMethodCall;
import org.gui4j.util.Filter;


public class Gui4jMenuItemFactory extends Gui4jAbstractButtonFactory
{
    public final static String NAME = "menuItem";
    
    private final String ACCELERATOR = "accelerator";

    /* (non-Javadoc)
     * @see org.gui4j.component.factory.Gui4jJComponentFactory#defineGui4jJComponentBy(org.gui4j.core.Gui4jComponentContainer, java.lang.String, org.jdom.Element)
     */
    protected Gui4jJComponent defineGui4jJComponentBy(
        Gui4jComponentContainer gui4jComponentContainer,
        String id,
        LElement e)
    {
        return new Gui4jMenuItem(gui4jComponentContainer, JMenuItem.class, id);
    }

    public void addToplevelAttributes(List attrList, Filter filter)
    {
        super.addToplevelAttributes(attrList, filter);
        if (filter == null || filter.takeIt(Gui4jMenuItemFactory.class))
        {
            attrList.add(
                new Attribute(ACCELERATOR, new AttributeTypeMethodCall(String.class), IMPLIED, false));
        }
    }

    protected void defineProperties(Gui4jJComponent gui4jJComponent, LElement e)
    {
        super.defineProperties(gui4jJComponent, e);
        gui4jJComponent.definePropertySetter(
            ACCELERATOR,
            getGui4jAccessInstance(String.class, gui4jJComponent, e, ACCELERATOR));
    }

    /**
     * @see org.gui4j.core.Gui4jComponentFactory#getName()
     */
    public String getName()
    {
        return NAME;
    }

}
