package org.gui4j.component.factory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.Icon;

import org.dom4j.LElement;
import org.gui4j.component.Gui4jJComponent;
import org.gui4j.component.Gui4jSimpleInternalFrame;
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

public class Gui4jSimpleInternalFrameFactory extends Gui4jJComponentFactory
{

    private static final String NAME = "simpleInternalFrame";
    private static final String ID = "id";
    private static final String TOOLBAR = "toolbar";
    private static final String TITLE = "title";
    private static final String INFOTEXT = "infoText";
    private static final String ICON = "icon";
    private static final String TABICON = "tabIcon";
    private static final String TABTEXT = "tabText";
    private static final String SELECTED = "selected";
    private static final String ACTION_COMMAND = "actionCommand";

    protected Gui4jJComponent defineGui4jJComponentBy(Gui4jComponentContainer gui4jComponentContainer, String id,
            LElement e)
    {

        // find required content (toolbar) either as nested element or by "id"
        // ("toolbar") attribute
        String gui4jId = e.attributeValue(ID);
        String toolbarId = e.attributeValue(TOOLBAR);
        Gui4jQualifiedComponent content;
        Gui4jQualifiedComponent toolbar = null;

        if (toolbarId != null)
        {
            // toolbar attribute takes precedence over nested elements
            toolbar = gui4jComponentContainer.getGui4jQualifiedComponent(toolbarId);
        }
        if (gui4jId == null)
        {
            if (e.elements().isEmpty())
            {
                Object[] args = { getName() };
                throw new Gui4jUncheckedException.ResourceError(gui4jComponentContainer.getConfigurationName(),
                        Gui4jComponentContainerManager.getLineNumber(e),
                        RESOURCE_ERROR_element_must_contain_gui4jComponent, args);
            }
            // no "id" property given => we interpret the "last" child as the
            // content
            LElement gui4jElement = (LElement) e.elements().get(e.elements().size() - 1);
            content = gui4jComponentContainer.extractGui4jComponent(gui4jElement);
            if (toolbar == null && e.elements().size() > 1)
            {
                toolbar = gui4jComponentContainer.extractGui4jComponent((LElement) e.elements().get(0));
            }
        }
        else
        {
            content = gui4jComponentContainer.getGui4jQualifiedComponent(gui4jId);
            if (toolbar == null && !e.elements().isEmpty())
            {
                toolbar = gui4jComponentContainer.extractGui4jComponent((LElement) e.elements().get(0));
            }
        }

        String selected = e.attributeValue(SELECTED);
        Gui4jSimpleInternalFrame sif = new Gui4jSimpleInternalFrame(gui4jComponentContainer, id, content, toolbar,
                selected);

        // define text and icon attributes
        sif.definePropertySetter(TITLE, getGui4jAccessInstance(String.class, sif, e, TITLE));
        sif.definePropertySetter(TABTEXT, getGui4jAccessInstance(String.class, sif, e, TABTEXT));
        sif.definePropertySetter(INFOTEXT, getGui4jAccessInstance(String.class, sif, e, INFOTEXT));
        sif.definePropertySetter(ICON, getGui4jAccessInstance(Icon.class, sif, e, ICON));
        sif.definePropertySetter(TABICON, getGui4jAccessInstance(Icon.class, sif, e, TABICON));

        sif.setActionCommand(getGui4jAccessInstance(null, sif, e, ACTION_COMMAND));

        return sif;
    }

    public String getName()
    {
        return NAME;
    }

    public void addInnerAttributes(String elementName, List list)
    {
    }

    public SubElement getSubElement(String elementName)
    {
        if (NAME.equals(elementName))
        {
            return SubElement.seq(new SubElement[] {
                    SubElement.optional(SubElement.getInstance(Gui4jToolbarFactory.NAME)),
                    SubElement.optional(SubElement.gui4jComponent()) });
        }
        return null;
    }

    public void addToplevelAttributes(List attrList, Filter filter)
    {
        super.addToplevelAttributes(attrList, filter);

        Set s = new HashSet();
        s.add(new Param(Gui4jSimpleInternalFrame.SELECTED_ALWAYS));
        s.add(new Param(Gui4jSimpleInternalFrame.SELECTED_NEVER));
        s.add(new Param(Gui4jSimpleInternalFrame.SELECTED_ONFOCUS));

        if (filter == null || filter.takeIt(Gui4jSimpleInternalFrameFactory.class))
        {
            attrList.add(new Attribute(ID, new AttributeTypeID(), IMPLIED, false));
            attrList.add(new Attribute(TOOLBAR, new AttributeTypeID(), IMPLIED, false));
            attrList.add(new Attribute(TITLE, new AttributeTypeMethodCall(String.class, true), false, false));
            attrList.add(new Attribute(TABTEXT, new AttributeTypeMethodCall(String.class, true), false, false));
            attrList.add(new Attribute(INFOTEXT, new AttributeTypeMethodCall(String.class, true), false, false));
            attrList.add(new Attribute(ICON, new AttributeTypeMethodCall(Icon.class, true), false, false));
            attrList.add(new Attribute(TABICON, new AttributeTypeMethodCall(Icon.class, true), false, false));
            attrList.add(new Attribute(SELECTED, new AttributeTypeEnumeration(s,
                    Gui4jSimpleInternalFrame.SELECTED_ALWAYS), false, false));
            attrList.add(new Attribute(ACTION_COMMAND, new AttributeTypeMethodCall(null), IMPLIED, false));
        }
    }

    public String[] getInnerElements()
    {
        return null;
    }
}