package org.gui4j.component.factory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JSplitPane;

import org.dom4j.LElement;
import org.gui4j.component.Gui4jJComponent;
import org.gui4j.component.Gui4jSplitPane;
import org.gui4j.core.Gui4jComponentContainer;
import org.gui4j.core.Gui4jQualifiedComponent;
import org.gui4j.core.definition.Attribute;
import org.gui4j.core.definition.AttributeTypeEnumeration;
import org.gui4j.core.definition.AttributeTypeFloatingPoint;
import org.gui4j.core.definition.AttributeTypeInteger;
import org.gui4j.core.definition.Param;
import org.gui4j.util.Filter;

public final class Gui4jSplitPaneFactory extends Gui4jJComponentFactory 
{
    private static final String NAME = "splitPane";
    private static final String ORIENTATION = "orientation";
    private static final String ORIENTATION_HORIZONTAL = "horizontal";
    private static final String ORIENTATION_VERTICAL = "vertical";
    private static final String DIVIDERSIZE = "dividerSize";
    private static final String LOCATION = "location";
    private static final String RESIZE_WEIGHT = "resizeWeight";
    private static final String SHOW_DIVIDER = "showDivider";

    /*
     * (non-Javadoc)
     * 
     * @see org.gui4j.component.factory.Gui4jJComponentFactory#defineGui4jJComponentBy(org.gui4j.core.Gui4jComponentContainer,
     *      java.lang.String, org.dom4j.LElement)
     */
    protected Gui4jJComponent defineGui4jJComponentBy(Gui4jComponentContainer gui4jComponentContainer, String id,
            LElement e)
    {
        List children = e.elements();
        LElement element1 = (LElement) children.get(0);
        LElement element2 = (LElement) children.get(1);
        Gui4jQualifiedComponent gui4jComponentInPath1 = gui4jComponentContainer.extractGui4jComponent(element1);
        Gui4jQualifiedComponent gui4jComponentInPath2 = gui4jComponentContainer.extractGui4jComponent(element2);

        String orientationStr = e.attributeValue(ORIENTATION);
        int orientation = (orientationStr == null || orientationStr.equalsIgnoreCase(ORIENTATION_HORIZONTAL)) ? JSplitPane.HORIZONTAL_SPLIT
                : JSplitPane.VERTICAL_SPLIT;
        boolean showDivider = gui4jComponentContainer.getBooleanAttrValue(e, SHOW_DIVIDER, true);        

        Gui4jSplitPane gui4jSplitPane = new Gui4jSplitPane(gui4jComponentContainer, id, orientation, showDivider, 
                gui4jComponentInPath1, gui4jComponentInPath2);

        gui4jSplitPane.definePropertySetter(LOCATION, getGui4jAccessInstance(Double.TYPE, gui4jSplitPane, e, LOCATION));
        gui4jSplitPane.definePropertySetter(RESIZE_WEIGHT, getGui4jAccessInstance(Double.TYPE, gui4jSplitPane, e,
                RESIZE_WEIGHT));

        gui4jSplitPane.definePropertySetter(DIVIDERSIZE, getGui4jAccessInstance(Integer.TYPE, gui4jSplitPane, e,
                DIVIDERSIZE));
        
        return gui4jSplitPane;
    }

    public SubElement getSubElement(String elementName)
    {
        if (NAME.equals(elementName))
        {
            SubElement[] elems = { SubElement.gui4jComponent(), SubElement.gui4jComponent() };
            return SubElement.seq(elems);
        }
        return null;
    }

    public void addInnerAttributes(String elementName, List list)
    {
    }

    public void addToplevelAttributes(List attrList, Filter filter)
    {
        Set s = new HashSet();
        s.add(new Param(ORIENTATION_HORIZONTAL));
        s.add(new Param(ORIENTATION_VERTICAL));

        super.addToplevelAttributes(attrList, filter);
        if (filter == null || filter.takeIt(Gui4jSplitPaneFactory.class))
        {
            attrList.add(new Attribute(ORIENTATION, new AttributeTypeEnumeration(s, ORIENTATION_HORIZONTAL), IMPLIED,
                    false));
            attrList.add(new Attribute(DIVIDERSIZE, new AttributeTypeInteger(), IMPLIED, false));
            attrList.add(new Attribute(LOCATION, new AttributeTypeFloatingPoint(), IMPLIED, false));
            attrList.add(new Attribute(RESIZE_WEIGHT, new AttributeTypeFloatingPoint(), IMPLIED, false));
            attrList.add(new Attribute(SHOW_DIVIDER, AttributeTypeEnumeration.getBooleanInstance(true), IMPLIED, false));
        }
    }

    /**
     * @see org.gui4j.core.Gui4jComponentFactory#getName()
     */
    public String getName()
    {
        return NAME;
    }

}