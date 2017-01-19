package org.gui4j.component.factory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.LElement;
import org.gui4j.component.Gui4jJComponent;
import org.gui4j.component.Gui4jMenu;
import org.gui4j.core.Gui4jComponentContainer;
import org.gui4j.core.Gui4jQualifiedComponent;
import org.gui4j.core.definition.Attribute;
import org.gui4j.core.definition.AttributeTypeEnumeration;
import org.gui4j.util.Filter;

public final class Gui4jMenuFactory extends Gui4jMenuItemFactory 
{
    public final static String MENU_NAME = "menu";

    private final static String WINDOWLIST = "windowList";

    /* (non-Javadoc)
     * @see org.gui4j.component.factory.Gui4jJComponentFactory#defineGui4jJComponentBy(org.gui4j.core.Gui4jComponentContainer, java.lang.String, org.dom4j.LElement)
     */
    protected Gui4jJComponent defineGui4jJComponentBy(
        Gui4jComponentContainer gui4jComponentContainer,
        String id,
        LElement e)
    {
        boolean windowList = gui4jComponentContainer.getBooleanAttrValue(e, WINDOWLIST, false);
        Gui4jMenu menu = new Gui4jMenu(gui4jComponentContainer, windowList, id);
        List children = e.elements();
        List errorList = new ArrayList();
        for (Iterator it = children.iterator(); it.hasNext();)
        {
            try
            {
                LElement child = (LElement) it.next();
                if (child.getName().equals(Gui4jSeparatorFactory.NAME))
                {
                    menu.addSeparator();
                }
                else
                {
                    Gui4jQualifiedComponent gui4jSubComponent = gui4jComponentContainer.extractGui4jComponent(child);
                    menu.addMenuItem(gui4jSubComponent);
                }
            }
            catch (Throwable t)
            {
                errorList.add(t);
            }
        }
        checkErrorList(errorList);
        return menu;
    }

    public SubElement getSubElement(String elementName)
    {
        if (MENU_NAME.equals(elementName))
        {
            SubElement[] subElements = new SubElement[] { SubElement.gui4jRef(), SubElement.getInstance(MENU_NAME),
                SubElement.getInstance(Gui4jMenuItemFactory.NAME),
                SubElement.getInstance(Gui4jRadioButtonMenuItemFactory.RADIOBUTTON_MENUITEM_NAME),
                SubElement.getInstance(Gui4jCheckBoxMenuItemFactory.CHECKBOX_MENUITEM_NAME),
                SubElement.getInstance(Gui4jSeparatorFactory.NAME) };
            return SubElement.star(SubElement.or(subElements));
        }
        if (Gui4jSeparatorFactory.NAME.equals(elementName))
        {
            return SubElement.empty();
        }
        return null;
    }

    public void addInnerAttributes(String elementName, List list)
    {
    }

    /**
     * @see org.gui4j.core.Gui4jComponentFactory#getName()
     */
    public String getName()
    {
        return MENU_NAME;
    }

    /*
     * (non-Javadoc)
     * @see de.bea.gui4j.Gui4jComponentFactory#addToplevelAttributes(java.util.List, de.bea.util.Filter)
     */
    public void addToplevelAttributes(List attrList, Filter filter)
    {
        super.addToplevelAttributes(attrList, filter);
        if (filter == null || filter.takeIt(Gui4jListFactory.class))
        {
            attrList.add(new Attribute(
                WINDOWLIST,
                AttributeTypeEnumeration.getBooleanInstance(false),
                IMPLIED,
                false));
        }
    }

}