package org.gui4j.component.factory;

import java.util.Iterator;
import java.util.List;

import org.dom4j.LElement;
import org.gui4j.component.Gui4jJComponent;
import org.gui4j.component.Gui4jPopupMenu;
import org.gui4j.core.Gui4jComponentContainer;
import org.gui4j.core.Gui4jQualifiedComponent;

public class Gui4jPopupMenuFactory extends Gui4jJComponentFactory 
{
    private final static String NAME = "popupMenu";

    protected Gui4jJComponent defineGui4jJComponentBy(
        Gui4jComponentContainer gui4jComponentContainer,
        String id,
        LElement e)
    {
        Gui4jPopupMenu popup = new Gui4jPopupMenu(gui4jComponentContainer, id);
        List children = e.elements();
        for (Iterator it = children.iterator(); it.hasNext();)
        {
            LElement child = (LElement) it.next();
            if (child.getName().equals(Gui4jSeparatorFactory.NAME))
            {
                popup.addSeparator();
            }
            else
            {
                Gui4jQualifiedComponent gui4jSubComponent = gui4jComponentContainer.extractGui4jComponent(child);
                popup.addMenuItem(gui4jSubComponent);
            }
        }
        return popup;
    }

    public SubElement getSubElement(String elementName)
    {
        if (NAME.equals(elementName))
        {
            SubElement[] subElements = new SubElement[] { SubElement.gui4jRef(),
                SubElement.getInstance(Gui4jPopupMenuItemFactory.POPUP_MENUITEM_NAME),
                SubElement.getInstance(Gui4jTreePopupMenuItemFactory.TREE_POPUP_MENUITEM_NAME),
                SubElement.getInstance(Gui4jSeparatorFactory.NAME) };
            return SubElement.star(SubElement.or(subElements));
        }
        return null;
    }

    public void addInnerAttributes(String elementName, List list)
    {
    }

    public String getName()
    {
        return NAME;
    }

}