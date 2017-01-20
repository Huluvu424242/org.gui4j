package org.gui4j.component.factory;

import java.awt.GridBagConstraints;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.LElement;
import org.gui4j.component.Gui4jGridBagLayout;
import org.gui4j.component.Gui4jJComponent;
import org.gui4j.core.Gui4jComponentContainer;
import org.gui4j.core.Gui4jComponentContainerManager;
import org.gui4j.core.Gui4jQualifiedComponent;
import org.gui4j.core.definition.Attribute;
import org.gui4j.core.definition.AttributeTypeEnumeration;
import org.gui4j.core.definition.AttributeTypeFloatingPoint;
import org.gui4j.core.definition.AttributeTypeID;
import org.gui4j.core.definition.AttributeTypeInteger;
import org.gui4j.exception.Gui4jUncheckedException;


public final class Gui4jGridBagLayoutFactory extends Gui4jJComponentFactory 
{
    private static final String NAME = "gridBagLayout";
    private static final String PLACEMENT = "placeGbl";
    private static final String ROW = "row";
    private static final String COL = "col";
    private static final String WIDTH = "width";
    private static final String HEIGHT = "height";
    private static final String WEIGHTX = "weightX";
    private static final String WEIGHTY = "weightY";
    private static final String IPADX = "ipadX";
    private static final String IPADY = "ipadY";
    private static final String TOP = "top";
    private static final String LEFT = "left";
    private static final String BOTTOM = "bottom";
    private static final String RIGHT = "right";
    private static final String FILL = "fill";
    private static final String ANCHOR = "anchor";
    private static final String ID = "id";
    private static final Map mAnchor;
    private static final Map mFill;

    private static final Log mLogger = LogFactory.getLog(Gui4jGridBagLayoutFactory.class);

    static {
        mAnchor = new HashMap();
        mFill = new HashMap();
        mAnchor.put("center", new Integer(GridBagConstraints.CENTER));
        mAnchor.put("east", new Integer(GridBagConstraints.EAST));
        mAnchor.put("north", new Integer(GridBagConstraints.NORTH));
        mAnchor.put("northEast", new Integer(GridBagConstraints.NORTHEAST));
        mAnchor.put("northWest", new Integer(GridBagConstraints.NORTHWEST));
        mAnchor.put("south", new Integer(GridBagConstraints.SOUTH));
        mAnchor.put("southEast", new Integer(GridBagConstraints.SOUTHEAST));
        mAnchor.put("southWest", new Integer(GridBagConstraints.SOUTHWEST));
        mAnchor.put("west", new Integer(GridBagConstraints.WEST));

        mFill.put("none", new Integer(GridBagConstraints.NONE));
        mFill.put("both", new Integer(GridBagConstraints.BOTH));
        mFill.put("horizontal", new Integer(GridBagConstraints.HORIZONTAL));
        mFill.put("vertical", new Integer(GridBagConstraints.VERTICAL));
    }

    /* (non-Javadoc)
     * @see org.gui4j.component.factory.Gui4jJComponentFactory#defineGui4jJComponentBy(org.gui4j.core.Gui4jComponentContainer, java.lang.String, org.dom4j.LElement)
     */
    protected Gui4jJComponent defineGui4jJComponentBy(
        Gui4jComponentContainer gui4jComponentContainer,
        String id,
        LElement e)
    {
        Gui4jGridBagLayout gui4jGridBagLayout = new Gui4jGridBagLayout(gui4jComponentContainer, id);
        List children = e.elements();
        List errorList = new ArrayList();
        for (Iterator it = children.iterator(); it.hasNext();)
        {
            try
            {
                LElement child = (LElement) it.next();
                gui4jComponentContainer.autoExtend(child);
                int gridY = getIntValue(gui4jComponentContainer, child, ROW);
                int gridX = getIntValue(gui4jComponentContainer, child, COL);
                int gridWidth = getIntValue(gui4jComponentContainer, child, WIDTH, 1);
                int gridHeight = getIntValue(gui4jComponentContainer, child, HEIGHT, 1);
                int ipadX = getIntValue(gui4jComponentContainer, child, IPADX, 0);
                int ipadY = getIntValue(gui4jComponentContainer, child, IPADY, 0);
                int top = getIntValue(gui4jComponentContainer, child, TOP, 0);
                int left = getIntValue(gui4jComponentContainer, child, LEFT, 0);
                int bottom = getIntValue(gui4jComponentContainer, child, BOTTOM, 0);
                int right = getIntValue(gui4jComponentContainer, child, RIGHT, 0);
                double weightX = getDoubleValue(gui4jComponentContainer, child, WEIGHTX, 1.0);
                double weightY = getDoubleValue(gui4jComponentContainer, child, WEIGHTY, 1.0);

                String anchorStr = gui4jComponentContainer.getAttrValue(child, ANCHOR);
                int anchor = GridBagConstraints.WEST;
                if (anchorStr != null)
                {
                    Integer val = (Integer) mAnchor.get(anchorStr);
                    if (val != null)
                    {
                        anchor = val.intValue();
                    }
                    else
                    {
                        mLogger.warn("Anchor " + anchorStr + " is not defined");
                    }
                }

                String fillStr = child.attributeValue(FILL);
                int fill = GridBagConstraints.BOTH;
                if (fillStr != null)
                {
                    fill = ((Integer) mFill.get(fillStr)).intValue();
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
                    LElement gui4jElement = (LElement) child.elements().iterator().next();
                    gui4jComponentInPath = gui4jComponentContainer.extractGui4jComponent(gui4jElement);
                }
                else
                {
                    gui4jComponentInPath = gui4jComponentContainer.getGui4jQualifiedComponent(gui4jId);
                }

                gui4jGridBagLayout.addPlacement(
                    gui4jComponentInPath,
                    gridX,
                    gridY,
                    gridWidth,
                    gridHeight,
                    fill,
                    weightX,
                    weightY,
                    anchor,
                    ipadX,
                    ipadY,
                    top,
                    left,
                    bottom,
                    right);
            }
            catch (Throwable t)
            {
                errorList.add(t);
            }
        }
        checkErrorList(errorList);
        return gui4jGridBagLayout;
    }

    public SubElement getSubElement(String elementName)
    {
        if (PLACEMENT.equals(elementName))
        {
            return SubElement.optional(SubElement.gui4jComponent());
        }
        if (NAME.equals(elementName))
        {
            SubElement place = SubElement.getInstance(PLACEMENT);
            return SubElement.star(place);
        }
        return null;
    }

    public void addInnerAttributes(String elementName, List list)
    {
        if (elementName.equals(PLACEMENT))
        {
            Attribute[] attributesSubElement = {
                new Attribute(ROW, new AttributeTypeInteger(), REQUIRED, false),
                    new Attribute(COL, new AttributeTypeInteger(), REQUIRED, false),
                    new Attribute(WIDTH, new AttributeTypeInteger(), IMPLIED, false),
                    new Attribute(HEIGHT, new AttributeTypeInteger(), IMPLIED, false),
                    new Attribute(
                        WEIGHTX,
                        new AttributeTypeFloatingPoint(),
                        IMPLIED,
                        false),
                    new Attribute(
                        WEIGHTY,
                        new AttributeTypeFloatingPoint(),
                        IMPLIED,
                        false),
                    new Attribute(IPADX, new AttributeTypeInteger(), IMPLIED, false),
                    new Attribute(IPADY, new AttributeTypeInteger(), IMPLIED, false),
                    new Attribute(TOP, new AttributeTypeInteger(), IMPLIED, false),
                    new Attribute(LEFT, new AttributeTypeInteger(), IMPLIED, false),
                    new Attribute(BOTTOM, new AttributeTypeInteger(), IMPLIED, false),
                    new Attribute(RIGHT, new AttributeTypeInteger(), IMPLIED, false),
                    new Attribute(FILL, new AttributeTypeEnumeration(mFill), IMPLIED, false),
                    new Attribute(ANCHOR, new AttributeTypeEnumeration(mAnchor), IMPLIED, true),
                    new Attribute(ID, new AttributeTypeID(), IMPLIED, false)};
            list.addAll(Arrays.asList(attributesSubElement));
            return;
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
}