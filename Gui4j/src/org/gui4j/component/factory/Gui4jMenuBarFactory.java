package org.gui4j.component.factory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.LElement;
import org.gui4j.component.Gui4jJComponent;
import org.gui4j.component.Gui4jMenuBar;
import org.gui4j.core.Gui4jComponentContainer;
import org.gui4j.core.Gui4jQualifiedComponent;

public final class Gui4jMenuBarFactory extends Gui4jJComponentFactory 
{
    private static final String NAME = "menuBar";

    /* (non-Javadoc)
     * @see org.gui4j.component.factory.Gui4jJComponentFactory#defineGui4jJComponentBy(org.gui4j.core.Gui4jComponentContainer, java.lang.String, org.dom4j.LElement)
     */
    protected Gui4jJComponent defineGui4jJComponentBy(
        Gui4jComponentContainer gui4jComponentContainer,
        String id,
        LElement e)
    {
        Gui4jMenuBar menuBar = new Gui4jMenuBar(gui4jComponentContainer, id);
        List children = e.elements();
        List errorList = new ArrayList();
        for (Iterator it = children.iterator(); it.hasNext();)
        {
            try
            {
                LElement child = (LElement) it.next();
                Gui4jQualifiedComponent gui4jSubComponent = gui4jComponentContainer.extractGui4jComponent(child);
                menuBar.addMenu(gui4jSubComponent);
            }
            catch (Throwable t)
            {
                errorList.add(t);
            }
        }
        checkErrorList(errorList);
        return menuBar;
    }

    public SubElement getSubElement(String elementName)
    {
        if (NAME.equals(elementName))
        {
            SubElement[] subElements = new SubElement[] { SubElement.gui4jRef(),
                SubElement.getInstance(Gui4jMenuFactory.MENU_NAME) };
            return SubElement.star(SubElement.or(subElements));
        }
        return SubElement.empty();
    }

    public void addInnerAttributes(String elementName, List list)
    {
    }

    /**
     * @see org.gui4j.core.Gui4jComponentFactory#getName()
     */
    public String getName()
    {
        return NAME;
    }

}