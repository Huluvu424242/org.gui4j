package org.gui4j.component.factory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.LElement;
import org.gui4j.component.Gui4jCardLayout;
import org.gui4j.component.Gui4jJComponent;
import org.gui4j.core.Gui4jCall;
import org.gui4j.core.Gui4jComponentContainer;
import org.gui4j.core.Gui4jComponentContainerManager;
import org.gui4j.core.Gui4jQualifiedComponent;
import org.gui4j.core.definition.Attribute;
import org.gui4j.core.definition.AttributeTypeID;
import org.gui4j.core.definition.AttributeTypeMethodCall;
import org.gui4j.exception.Gui4jUncheckedException;
import org.gui4j.util.Filter;


public final class Gui4jCardLayoutFactory extends Gui4jJComponentFactory 
{
    private static final String NAME = "cardLayout";
    private static final String PLACEMENT = "placeCl";
    private static final String REFRESH = "refresh";
    private static final String ID = "id";
    private static final String CONDITION = "condition";
    private static final String DEFAULT = "default";

    private static final Log mLogger = LogFactory.getLog(Gui4jCardLayoutFactory.class);

    /* (non-Javadoc)
     * @see org.gui4j.component.factory.Gui4jJComponentFactory#defineGui4jJComponentBy(org.gui4j.core.Gui4jComponentContainer, java.lang.String, org.dom4j.LElement)
     */
    protected Gui4jJComponent defineGui4jJComponentBy(
        Gui4jComponentContainer gui4jComponentContainer,
        String id,
        LElement e)
    {
        String gui4jIdDefault = e.attributeValue(DEFAULT);
        assert gui4jIdDefault != null;
        Gui4jQualifiedComponent gui4jComponentDefault =
            gui4jComponentContainer.getGui4jQualifiedComponent(gui4jIdDefault);
        assert gui4jComponentDefault != null;

        Gui4jCardLayout gui4jCardLayout = new Gui4jCardLayout(gui4jComponentContainer, id, gui4jComponentDefault);
        List children = e.elements();
        List errorList = new ArrayList();
        for (Iterator it = children.iterator(); it.hasNext();)
        {
            try
            {
                LElement child = (LElement) it.next();
                gui4jComponentContainer.autoExtend(child);
                Gui4jCall condition = getGui4jAccessInstance(Boolean.TYPE, gui4jCardLayout, child, CONDITION);

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
                    LElement gui4jElement = (LElement) child.elements().iterator().next();
                    gui4jComponentInPath = gui4jComponentContainer.extractGui4jComponent(gui4jElement);
                }
                else
                {
                    gui4jComponentInPath = gui4jComponentContainer.getGui4jQualifiedComponent(gui4jId);
                }

                gui4jCardLayout.addPlacement(condition, gui4jComponentInPath);
            }
            catch (Throwable t)
            {
                errorList.add(t);
            }
        }

        checkErrorList(errorList);

        {
            Map m = null;
            Gui4jCall refresh = getGui4jAccessInstance(null, m, gui4jCardLayout, e, REFRESH);
            if (refresh != null)
            {
                Gui4jCall[] dependantProperties = refresh.getDependantProperties();
                if (dependantProperties == null || dependantProperties.length == 0)
                {
                    mLogger.warn("Set of dependant events is empty");
                }
                gui4jCardLayout.setRefresh(dependantProperties);
            }
        }

        return gui4jCardLayout;
    }

    public void addToplevelAttributes(List attrList, Filter filter)
    {
        super.addToplevelAttributes(attrList, filter);
        if (filter == null || filter.takeIt(Gui4jCardLayoutFactory.class))
        {
            attrList.add(new Attribute(REFRESH, new AttributeTypeMethodCall(null, EVENT_AWARE), IMPLIED, false));
            attrList.add(new Attribute(DEFAULT, new AttributeTypeID(), REQUIRED, false));
        }
    }

    public void addInnerAttributes(String elementName, List list)
    {
        if (PLACEMENT.equals(elementName))
        {
            Attribute[] attributesSubElement =
            {
                new Attribute(ID, new AttributeTypeID(), IMPLIED, false),
                new Attribute(CONDITION, new AttributeTypeMethodCall(Boolean.TYPE), REQUIRED, false)};
            list.addAll(Arrays.asList(attributesSubElement));
        }
    }
    
    public SubElement getSubElement(String elementName)
    {
        SubElement place = SubElement.getInstance(PLACEMENT);
        if (NAME.equals(elementName))
        {
            return SubElement.star(place);
        }
        if (PLACEMENT.equals(elementName))
        {
            return SubElement.optional(SubElement.gui4jComponent());
        }
        return null;
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
}