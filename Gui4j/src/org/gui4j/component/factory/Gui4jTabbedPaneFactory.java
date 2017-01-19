package org.gui4j.component.factory;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.LElement;
import org.gui4j.component.Gui4jJComponent;
import org.gui4j.component.Gui4jTabbedPane;
import org.gui4j.core.Gui4jCall;
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

public final class Gui4jTabbedPaneFactory extends Gui4jJComponentFactory 
{
    private static final String NAME = "tabbedPane";
    private static final String PLACEMENT = "placeTp";
    private static final String TITLE = "title";
    private static final String FOREGROUND = "foreground";
    private static final String BACKGROUND = "background";
    private static final String TOOLTIP = "tooltip";
    private static final String ID = "id";
    private static final String TABPOLICY = "tabPolicy";
    private static final String ONCHANGE = "onChange";
    private static final String TABSELECTION = "tabSelection";
    private static final String CONDITION = "condition";
    private static final String ONSELECT = "onSelect";
    private static final String TABPLACEMENT = "tabPlacement";
    private static final String EMBEDDED = "embedded";

    private static final Log mLogger = LogFactory.getLog(Gui4jTabbedPaneFactory.class);

    /*
     * (non-Javadoc)
     * 
     * @see org.gui4j.component.factory.Gui4jJComponentFactory#defineGui4jJComponentBy(org.gui4j.core.Gui4jComponentContainer,
     *      java.lang.String, org.dom4j.LElement)
     */
    protected Gui4jJComponent defineGui4jJComponentBy(Gui4jComponentContainer gui4jComponentContainer, String id,
            LElement e)
    {

        String tabPolicyStr = gui4jComponentContainer.getAttrValue(e, TABPOLICY);
        int tabPolicy = JTabbedPane.SCROLL_TAB_LAYOUT;
        if (tabPolicyStr != null)
        {
            if (tabPolicyStr.equalsIgnoreCase("wrap"))
            {
                tabPolicy = JTabbedPane.WRAP_TAB_LAYOUT;
            }
            if (tabPolicyStr.equalsIgnoreCase("scroll"))
            {
                tabPolicy = JTabbedPane.SCROLL_TAB_LAYOUT;
            }
        }

        String tabPlacementStr = gui4jComponentContainer.getAttrValue(e, TABPLACEMENT);
        int tabPlacement = SwingConstants.TOP;
        if (tabPlacementStr != null)
        {
            if (tabPlacementStr.equalsIgnoreCase("top"))
            {
                tabPlacement = SwingConstants.TOP;
            }
            if (tabPlacementStr.equalsIgnoreCase("bottom"))
            {
                tabPlacement = SwingConstants.BOTTOM;
            }
            if (tabPlacementStr.equalsIgnoreCase("left"))
            {
                tabPlacement = SwingConstants.LEFT;
            }
            if (tabPlacementStr.equalsIgnoreCase("right"))
            {
                tabPlacement = SwingConstants.RIGHT;
            }
        }

        boolean embedded = gui4jComponentContainer.getBooleanAttrValue(e, EMBEDDED, false);

        Gui4jTabbedPane gui4jTabbedPane = new Gui4jTabbedPane(gui4jComponentContainer, id, tabPolicy, tabPlacement,
                embedded);

        {
            Map m = null;
            Gui4jCall refresh = getGui4jAccessInstance(null, m, gui4jTabbedPane, e, TABSELECTION);
            if (refresh != null)
            {
                Gui4jCall[] dependantProperties = refresh.getDependantProperties();
                if (dependantProperties == null || dependantProperties.length == 0)
                {
                    mLogger.warn("Set of dependant events is empty");
                }
                gui4jTabbedPane.setTabSelection(dependantProperties);
            }
        }

        gui4jTabbedPane.setOnChange(getGui4jAccessInstance(null, gui4jTabbedPane, e, ONCHANGE));

        List children = e.elements();
        List errorList = new ArrayList();
        for (Iterator it = children.iterator(); it.hasNext();)
        {
            try
            {
                LElement child = (LElement) it.next();
                Gui4jCall title = getGui4jAccessInstance(String.class, gui4jTabbedPane, child, TITLE);
                Gui4jCall foreground = getGui4jAccessInstance(Color.class, gui4jTabbedPane, child, FOREGROUND);
                Gui4jCall background = getGui4jAccessInstance(Color.class, gui4jTabbedPane, child, BACKGROUND);
                Gui4jCall enabled = getGui4jAccessInstance(Boolean.TYPE, gui4jTabbedPane, child, ENABLED);
                Gui4jCall visible = getGui4jAccessInstance(Boolean.TYPE, gui4jTabbedPane, child, VISIBLE);
                Gui4jCall tooltip = getGui4jAccessInstance(String.class, gui4jTabbedPane, child, TOOLTIP);
                Gui4jCall condition = getGui4jAccessInstance(Boolean.TYPE, gui4jTabbedPane, child, CONDITION);
                Gui4jCall onSelect = getGui4jAccessInstance(null, gui4jTabbedPane, child, ONSELECT);
                String gui4jId = child.attributeValue(ID);
                Gui4jQualifiedComponent gui4jComponentInPath;
                if (gui4jId == null)
                {
                    if (child.elements().isEmpty())
                    {
                        Object[] args = { PLACEMENT };
                        throw new Gui4jUncheckedException.ResourceError(gui4jComponentContainer
                                .getConfigurationName(), Gui4jComponentContainerManager.getLineNumber(child),
                                RESOURCE_ERROR_element_must_contain_gui4jComponent, args);
                    }
                    LElement gui4jElement = (LElement) child.elements().iterator().next();
                    gui4jComponentInPath = gui4jComponentContainer.extractGui4jComponent(gui4jElement);
                }
                else
                {
                    gui4jComponentInPath = gui4jComponentContainer.getGui4jQualifiedComponent(gui4jId);
                }

                // is automatically added by the constructor
                Gui4jTabbedPane.Entry entry = gui4jTabbedPane.new Entry(title, gui4jComponentInPath);
                entry.setForeground(foreground);
                entry.setBackground(background);
                entry.setTooltip(tooltip);
                entry.setEnabled(enabled);
                entry.setVisible(visible);
                entry.setCondition(condition);
                entry.setOnSelect(onSelect);
            }
            catch (Throwable t)
            {
                errorList.add(t);
            }
        }
        checkErrorList(errorList);
        return gui4jTabbedPane;
    }

    public void addToplevelAttributes(List attrList, Filter filter)
    {
        //      TODO: use constants
        Set enumTabPolicy = new HashSet();
        enumTabPolicy.add(new Param("wrap"));
        enumTabPolicy.add(new Param("scroll"));

        Set enumTabPlacement = new HashSet();
        enumTabPlacement.add(new Param("top"));
        enumTabPlacement.add(new Param("bottom"));
        enumTabPlacement.add(new Param("left"));
        enumTabPlacement.add(new Param("right"));

        super.addToplevelAttributes(attrList, filter);
        if (filter == null || filter.takeIt(Gui4jTabbedPaneFactory.class))
        {
            attrList
                    .add(new Attribute(TABPOLICY, new AttributeTypeEnumeration(enumTabPolicy, "scroll"), IMPLIED, false));
            attrList.add(new Attribute(TABPLACEMENT, new AttributeTypeEnumeration(enumTabPlacement, "top"), IMPLIED,
                    false));
            attrList.add(new Attribute(ONCHANGE, new AttributeTypeMethodCall(null), IMPLIED, false));
            attrList.add(new Attribute(TABSELECTION, new AttributeTypeMethodCall(null), IMPLIED, false));
            attrList.add(new Attribute(EMBEDDED, AttributeTypeEnumeration.getBooleanInstance(false), IMPLIED, false));
        }
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
            Attribute[] attributesSubElement = {
                    new Attribute(TITLE, new AttributeTypeMethodCall(String.class), REQUIRED, false),
                    new Attribute(ID, new AttributeTypeID(), IMPLIED, false),
                    new Attribute(FOREGROUND, new AttributeTypeMethodCall(Color.class), IMPLIED, false),
                    new Attribute(BACKGROUND, new AttributeTypeMethodCall(Color.class), IMPLIED, false),
                    new Attribute(TOOLTIP, new AttributeTypeMethodCall(String.class), IMPLIED, false),
                    new Attribute(ENABLED, new AttributeTypeMethodCall(Boolean.TYPE), IMPLIED, false),
                    new Attribute(VISIBLE, new AttributeTypeMethodCall(Boolean.TYPE), IMPLIED, false),
                    new Attribute(CONDITION, new AttributeTypeMethodCall(Boolean.TYPE), IMPLIED, false),
                    new Attribute(ONSELECT, new AttributeTypeMethodCall(null), IMPLIED, false), };
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

}