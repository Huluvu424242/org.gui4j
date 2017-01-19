package org.gui4j.component.factory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.LElement;
import org.gui4j.component.Gui4jJComponent;
import org.gui4j.core.Gui4jComponentContainer;
import org.gui4j.core.Gui4jQualifiedComponent;

abstract public class Gui4jBoxFactory extends Gui4jJComponentFactory
{

    public SubElement getSubElement(String elementName)
    {
        return SubElement.star(SubElement.gui4jComponent());
    }

    public void addInnerAttributes(String elementName, List list)
    {
    }

    public Gui4jJComponent defineGui4jJComponentBy(Gui4jComponentContainer gui4jComponentContainer, String id,
            LElement e)
    {
        List gui4jComponents = new ArrayList();
        List children = e.elements();
        List errorList = new ArrayList();
        for (Iterator it = children.iterator(); it.hasNext();)
        {
            try
            {
                LElement c = (LElement) it.next();
                Gui4jQualifiedComponent gui4jComponentInPath = gui4jComponentContainer.extractGui4jComponent(c);
                gui4jComponents.add(gui4jComponentInPath);
            }
            catch (Throwable t)
            {
                errorList.add(t);
            }
        }
        checkErrorList(errorList);
        String alignment = getAlignment(gui4jComponentContainer, e);
        Gui4jJComponent gui4jJComponent = createGui4jComponent(id, gui4jComponents, alignment, gui4jComponentContainer);
        return gui4jJComponent;
    }

    abstract protected Gui4jJComponent createGui4jComponent(String id, List gui4jComponents, String alignment,
            Gui4jComponentContainer gui4jComponentContainer);
    
    abstract protected String getAlignment(Gui4jComponentContainer gui4jComponentContainer, LElement e);
}
