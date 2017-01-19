package org.gui4j.component.factory;

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JToolBar;

import org.dom4j.LElement;
import org.gui4j.component.Gui4jJComponent;
import org.gui4j.component.Gui4jToolbar;
import org.gui4j.core.Gui4jComponentContainer;
import org.gui4j.core.Gui4jQualifiedComponent;
import org.gui4j.core.definition.Attribute;
import org.gui4j.core.definition.AttributeTypeEnumeration;
import org.gui4j.core.definition.Param;
import org.gui4j.util.Filter;

public class Gui4jToolbarFactory extends Gui4jJComponentFactory
{

    public static final String NAME = "toolbar";

    private static final String ORIENTATION = "orientation";
    private static final String ORIENTATION_HORIZONTAL = "horizontal";
    private static final String ORIENTATION_VERTICAL = "vertical";

    private static final String HALIGNMENT = "hAlignment";
    private static final String VALIGNMENT = "vAlignment";

    private static final String CENTER = "center";
    private static final String TOP = "top";
    private static final String BOTTOM = "bottom";
    private static final String LEFT = "left";
    private static final String RIGHT = "right";

    protected Gui4jJComponent defineGui4jJComponentBy(Gui4jComponentContainer gui4jComponentContainer, String id,
            LElement e)
    {

        String orientationStr = e.attributeValue(ORIENTATION);
        int orientation = (orientationStr == null || orientationStr.equalsIgnoreCase(ORIENTATION_HORIZONTAL)) ? JToolBar.HORIZONTAL
                : JToolBar.VERTICAL;

        float hAlignment = Component.CENTER_ALIGNMENT;
        String hAlignmentStr = e.attributeValue(HALIGNMENT);
        if (LEFT.equals(hAlignmentStr))
        {
            hAlignment = Component.LEFT_ALIGNMENT;
        }
        else if (RIGHT.equals(hAlignmentStr))
        {
            hAlignment = Component.RIGHT_ALIGNMENT;
        }

        float vAlignment = Component.CENTER_ALIGNMENT;
        String vAlignmentStr = e.attributeValue(VALIGNMENT);
        if (TOP.equals(vAlignmentStr))
        {
            vAlignment = Component.TOP_ALIGNMENT;
        }
        else if (BOTTOM.equals(vAlignmentStr))
        {
            vAlignment = Component.BOTTOM_ALIGNMENT;
        }

        Gui4jToolbar toolbar = new Gui4jToolbar(gui4jComponentContainer, id, orientation, vAlignment, hAlignment);

        List children = e.elements();
        List errorList = new ArrayList();
        for (Iterator it = children.iterator(); it.hasNext();)
        {
            try
            {
                LElement child = (LElement) it.next();
                if (child.getName().equals(Gui4jSeparatorFactory.NAME))
                {
                    toolbar.addSeparator();
                }
                else
                {
                    Gui4jQualifiedComponent gui4jSubComponent = gui4jComponentContainer.extractGui4jComponent(child);
                    toolbar.addButton(gui4jSubComponent);
                }
            }
            catch (Throwable t)
            {
                errorList.add(t);
            }
        }
        checkErrorList(errorList);
        return toolbar;
    }

    public String getName()
    {
        return NAME;
    }

    public SubElement getSubElement(String elementName)
    {
        if (NAME.equals(elementName))
        {
            return SubElement.star(SubElement.gui4jComponent());
        }
        return null;
    }

    public void addInnerAttributes(String elementName, List list)
    {
    }

    public void addToplevelAttributes(List attrList, Filter filter)
    {
        super.addToplevelAttributes(attrList, filter);

        if (filter == null || filter.takeIt(Gui4jToolbarFactory.class))
        {

            Set orientationValues = new HashSet();
            orientationValues.add(new Param(ORIENTATION_HORIZONTAL));
            orientationValues.add(new Param(ORIENTATION_VERTICAL));

            Set hAlignmentValues = new HashSet();
            hAlignmentValues.add(new Param(CENTER));
            hAlignmentValues.add(new Param(LEFT));
            hAlignmentValues.add(new Param(RIGHT));

            Set vAlignmentValues = new HashSet();
            vAlignmentValues.add(new Param(CENTER));
            vAlignmentValues.add(new Param(TOP));
            vAlignmentValues.add(new Param(BOTTOM));

            attrList.add(new Attribute(ORIENTATION, new AttributeTypeEnumeration(orientationValues,
                    ORIENTATION_HORIZONTAL), IMPLIED, false));
            attrList.add(new Attribute(HALIGNMENT, new AttributeTypeEnumeration(hAlignmentValues, CENTER), IMPLIED,
                    false));
            attrList.add(new Attribute(VALIGNMENT, new AttributeTypeEnumeration(vAlignmentValues, CENTER), IMPLIED,
                    false));
        }
    }
}