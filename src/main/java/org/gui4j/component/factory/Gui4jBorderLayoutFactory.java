package org.gui4j.component.factory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.dom4j.LElement;
import org.gui4j.component.Gui4jBorderLayout;
import org.gui4j.component.Gui4jJComponent;
import org.gui4j.core.Gui4jComponentContainer;
import org.gui4j.core.Gui4jComponentContainerManager;
import org.gui4j.core.Gui4jQualifiedComponent;
import org.gui4j.core.definition.Attribute;
import org.gui4j.core.definition.AttributeTypeEnumeration;
import org.gui4j.core.definition.AttributeTypeID;
import org.gui4j.core.definition.AttributeTypeMethodCall;
import org.gui4j.core.definition.Param;
import org.gui4j.exception.Gui4jUncheckedException;
import org.gui4j.util.Filter;


public final class Gui4jBorderLayoutFactory extends Gui4jJComponentFactory 
{
    private static final String NAME = "borderLayout";
    private static final String ANCHOR = "anchor";
    private static final String HSPACING = "hSpacing";
    private static final String VSPACING = "vSpacing";
    private static final String PLACEMENT = "placeBl";
    private static final String ID = "id";

    /* (non-Javadoc)
     * @see org.gui4j.component.factory.Gui4jJComponentFactory#defineGui4jJComponentBy(org.gui4j.core.Gui4jComponentContainer, java.lang.String, org.dom4j.LElement)
     */
    protected Gui4jJComponent defineGui4jJComponentBy(
        Gui4jComponentContainer gui4jComponentContainer,
        String id,
        LElement e)
    {
        Gui4jBorderLayout gui4jBorderLayout = new Gui4jBorderLayout(gui4jComponentContainer, id);
        gui4jBorderLayout.definePropertySetter(
            HSPACING,
            getGui4jAccessInstance(Integer.TYPE, gui4jBorderLayout, e, HSPACING));
        gui4jBorderLayout.definePropertySetter(
            VSPACING,
            getGui4jAccessInstance(Integer.TYPE, gui4jBorderLayout, e, VSPACING));

        List children = e.elements();
        List errorList = new ArrayList();
        for (Iterator it = children.iterator(); it.hasNext();)
        {
            try
            {
            LElement child = (LElement) it.next();
            gui4jComponentContainer.autoExtend(child);
            String direction = child.attributeValue(ANCHOR);
            if (direction != null)
            {
                direction = direction.substring(0, 1).toUpperCase() + direction.substring(1);
            }
            String gui4jId = child.attributeValue(ID);
            Gui4jQualifiedComponent gui4jComponentInPath;
            if (gui4jId == null)
            {
                if (child.elements().isEmpty())
                {
                    Object[] args = { PLACEMENT };
                    throw new Gui4jUncheckedException.ResourceError(
                        gui4jComponentContainer.getConfigurationName(),
                        Gui4jComponentContainerManager.getLineNumber(child),
                        RESOURCE_ERROR_element_must_contain_gui4jComponent,
                        args);
                }
                LElement gui4jElement = (LElement) child.elementIterator().next();
                gui4jComponentInPath = gui4jComponentContainer.extractGui4jComponent(gui4jElement);
            }
            else
            {
                gui4jComponentInPath = gui4jComponentContainer.getGui4jQualifiedComponent(gui4jId);
            }

            gui4jBorderLayout.addPlacement(direction, gui4jComponentInPath);
            }
            catch (Throwable t)
            {
                errorList.add(t);
            }
        }
        checkErrorList(errorList);
        return gui4jBorderLayout;
    }

    public SubElement getSubElement(String elementName)
    {
        if (NAME.equals(elementName))
        {
            SubElement place = SubElement.getInstance(PLACEMENT);
            return SubElement.star(place);
        }
        if (PLACEMENT.equals(elementName))
        {
            return SubElement.optional(SubElement.gui4jComponent());
        }
        return null;
    }

    public void addInnerAttributes(String elementName, List list)
    {
        if (PLACEMENT.equals(elementName))
        {
            Set s = new HashSet();
            s.add(new Param("south"));
            s.add(new Param("north"));
            s.add(new Param("west"));
            s.add(new Param("east"));
            s.add(new Param("center"));

            Attribute[] attributesSubElement =
                {
                    new Attribute(ANCHOR, new AttributeTypeEnumeration(s), REQUIRED, false),
                    new Attribute(ID, new AttributeTypeID(), IMPLIED, false)};
            list.addAll(Arrays.asList(attributesSubElement));
        }
    }

    public String[] getInnerElements()
    {
        String[] elems = { PLACEMENT };
        return elems;
    }

    /**
     * @see org.gui4j.core.Gui4jComponentFactory#getName()
     */
    public String getName()
    {
        return NAME;
    }

    public void addToplevelAttributes(List attrList, Filter filter)
    {
        super.addToplevelAttributes(attrList, filter);
        if (filter == null || filter.takeIt(Gui4jBorderLayoutFactory.class))
        {
            attrList.add(new Attribute(HSPACING, new AttributeTypeMethodCall(Integer.TYPE, EVENT_AWARE), IMPLIED, false));
            attrList.add(new Attribute(VSPACING, new AttributeTypeMethodCall(Integer.TYPE, EVENT_AWARE), IMPLIED, false));
        }
    }

}
