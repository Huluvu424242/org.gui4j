package org.gui4j.component.factory;

import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.LElement;
import org.gui4j.component.Gui4jFlowLayout;
import org.gui4j.component.Gui4jJComponent;
import org.gui4j.core.Gui4jComponentContainer;
import org.gui4j.core.Gui4jQualifiedComponent;
import org.gui4j.core.definition.Attribute;
import org.gui4j.core.definition.AttributeTypeEnumeration;
import org.gui4j.core.definition.AttributeTypeInteger;
import org.gui4j.util.Filter;


public final class Gui4jFlowLayoutFactory extends Gui4jJComponentFactory 
{

    private static final String NAME = "flowLayout";
    private static final String ALIGNMENT = "alignment";
    private static final String HSPACING = "hSpacing";
    private static final String VSPACING = "vSpacing";
    private static final Map mAlignment = new HashMap();

    static {
        mAlignment.put("center", new Integer(FlowLayout.CENTER));
        mAlignment.put("leading", new Integer(FlowLayout.LEADING));
        mAlignment.put("left", new Integer(FlowLayout.LEFT));
        mAlignment.put("right", new Integer(FlowLayout.RIGHT));
        mAlignment.put("trailing", new Integer(FlowLayout.TRAILING));
    }

    /* (non-Javadoc)
     * @see org.gui4j.component.factory.Gui4jJComponentFactory#defineGui4jJComponentBy(org.gui4j.core.Gui4jComponentContainer, java.lang.String, org.dom4j.LElement)
     */
    protected Gui4jJComponent defineGui4jJComponentBy(
        Gui4jComponentContainer gui4jComponentContainer,
        String id,
        LElement e)
    {
        Gui4jFlowLayout gui4jFlowLayout = new Gui4jFlowLayout(gui4jComponentContainer, id);
        List children = e.elements();
        List errorList = new ArrayList();
        for (Iterator it = children.iterator(); it.hasNext();)
        {
            try
            {
            LElement c = (LElement) it.next();
            Gui4jQualifiedComponent gui4jComponentInPath = gui4jComponentContainer.extractGui4jComponent(c);
            gui4jFlowLayout.addPlacement(gui4jComponentInPath);
            }
            catch (Throwable t)
            {
                errorList.add(t);
            }
        }
        checkErrorList(errorList);

        String alignment = e.attributeValue(ALIGNMENT);
        if (alignment != null)
        {
            gui4jFlowLayout.setAlignment(((Integer) mAlignment.get(alignment)).intValue());
        }
        gui4jFlowLayout.definePropertySetter(
            HSPACING,
            getGui4jAccessInstance(Integer.TYPE, gui4jFlowLayout, e, HSPACING));
        gui4jFlowLayout.definePropertySetter(
            VSPACING,
            getGui4jAccessInstance(Integer.TYPE, gui4jFlowLayout, e, VSPACING));
        return gui4jFlowLayout;
    }

    public void addInnerAttributes(String elementName, List list)
    {
    }
    
    public SubElement getSubElement(String elementName)
    {
        if (NAME.equals(elementName))
        {
            return SubElement.star(SubElement.gui4jComponent());
        }
        return null;
    }
    
    public void addToplevelAttributes(List attrList, Filter filter)
    {
        super.addToplevelAttributes(attrList, filter);
        if (filter == null || filter.takeIt(Gui4jFlowLayoutFactory.class))
        {
            attrList.add(new Attribute(ALIGNMENT, new AttributeTypeEnumeration(mAlignment), IMPLIED, false));
            attrList.add(new Attribute(HSPACING, new AttributeTypeInteger(), IMPLIED, false));
            attrList.add(new Attribute(VSPACING, new AttributeTypeInteger(), IMPLIED, false));
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
