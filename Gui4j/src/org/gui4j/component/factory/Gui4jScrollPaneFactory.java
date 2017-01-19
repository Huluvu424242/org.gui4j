package org.gui4j.component.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;

import org.dom4j.LElement;
import org.gui4j.component.Gui4jJComponent;
import org.gui4j.component.Gui4jScrollPane;
import org.gui4j.core.Gui4jComponentContainer;
import org.gui4j.core.Gui4jComponentContainerManager;
import org.gui4j.core.Gui4jMap1;
import org.gui4j.core.Gui4jQualifiedComponent;
import org.gui4j.core.definition.Attribute;
import org.gui4j.core.definition.AttributeTypeEnumeration;
import org.gui4j.core.definition.AttributeTypeID;
import org.gui4j.core.definition.AttributeTypeMethodCall;
import org.gui4j.core.definition.Param;
import org.gui4j.exception.Gui4jUncheckedException;
import org.gui4j.util.Filter;


public final class Gui4jScrollPaneFactory extends Gui4jJComponentFactory 
{
    private static final String NAME = "scrollPane";
    private static final String ID = "id";
    private static final String HSCROLLPOLICY = "hScrollPolicy";
    private static final String VSCROLLPOLICY = "vScrollPolicy";
    private static final String VIEWPORTBORDER = "viewportBorder";

    private static final String ALWAYS = "always";
    private static final String NEVER = "never";
    private static final String AUTO = "auto";
    
    private static final Map mHScrollPolicy = new HashMap();
    private static final Map mVScrollPolicy = new HashMap();

    static
    {
        mHScrollPolicy.put(ALWAYS, new Integer(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS));
        mHScrollPolicy.put(NEVER, new Integer(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER));
        mHScrollPolicy.put(AUTO, new Integer(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED));

        mVScrollPolicy.put(ALWAYS, new Integer(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS));
        mVScrollPolicy.put(NEVER, new Integer(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER));
        mVScrollPolicy.put(AUTO, new Integer(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED));
    }

    /* (non-Javadoc)
     * @see org.gui4j.component.factory.Gui4jJComponentFactory#defineGui4jJComponentBy(org.gui4j.core.Gui4jComponentContainer, java.lang.String, org.dom4j.LElement)
     */
    protected Gui4jJComponent defineGui4jJComponentBy(
        Gui4jComponentContainer gui4jComponentContainer,
        String id,
        LElement e)
    {
        String gui4jId = e.attributeValue(ID);
        Gui4jQualifiedComponent gui4jComponentInPath;
        if (gui4jId == null)
        {
            if (e.elements().isEmpty())
            {
                Object[] args = { getName() };
                throw new Gui4jUncheckedException.ResourceError(
                    gui4jComponentContainer.getConfigurationName(),
                    Gui4jComponentContainerManager.getLineNumber(e),
                    RESOURCE_ERROR_element_must_contain_gui4jComponent,
                    args);
            }
            LElement gui4jElement = (LElement) e.elements().iterator().next();
            gui4jComponentInPath = gui4jComponentContainer.extractGui4jComponent(gui4jElement);
        }
        else
        {
            gui4jComponentInPath = gui4jComponentContainer.getGui4jQualifiedComponent(gui4jId);
        }

        int hScrollPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED;
        int vScrollPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;
        String hScrollPolicyStr = e.attributeValue(HSCROLLPOLICY);
        String vScrollPolicyStr = e.attributeValue(VSCROLLPOLICY);
        if (hScrollPolicyStr != null)
        {
            hScrollPolicy = ((Integer) mHScrollPolicy.get(hScrollPolicyStr)).intValue();
        }
        if (vScrollPolicyStr != null)
        {
            vScrollPolicy = ((Integer) mVScrollPolicy.get(vScrollPolicyStr)).intValue();
        }

        Gui4jScrollPane gui4jScrollPane = new Gui4jScrollPane(
            gui4jComponentContainer,
            id,
            gui4jComponentInPath,
            hScrollPolicy,
            vScrollPolicy);
        return gui4jScrollPane;
    }

    protected void defineProperties(Gui4jJComponent gui4jJComponent, LElement e)
    {
        super.defineProperties(gui4jJComponent, e);
        gui4jJComponent.definePropertySetter(VIEWPORTBORDER, getGui4jAccessInstance(Border.class, new Gui4jMap1(
            CONTEXT,
            Object.class), gui4jJComponent, e, VIEWPORTBORDER), true);        
    }

    public SubElement getSubElement(String elementName)
    {
        if (NAME.equals(elementName))
        {
            return SubElement.optional(SubElement.gui4jComponent());
        }
        return null;
    }

    public void addInnerAttributes(String elementName, List list)
    {
    }

    public void addToplevelAttributes(List attrList, Filter filter)
    {
        super.addToplevelAttributes(attrList, filter);
        if (filter == null || filter.takeIt(Gui4jScrollPaneFactory.class))
        {
            List l = new ArrayList();
            l.add(new Param(CONTEXT));
            
            attrList.add(new Attribute(ID, new AttributeTypeID(), IMPLIED, false));
            attrList.add(new Attribute(
                HSCROLLPOLICY,
                new AttributeTypeEnumeration(mHScrollPolicy, AUTO),
                IMPLIED,
                false));
            attrList.add(new Attribute(
                VSCROLLPOLICY,
                new AttributeTypeEnumeration(mVScrollPolicy, AUTO),
                IMPLIED,
                false));
            attrList.add(new Attribute(
                VIEWPORTBORDER,
                new AttributeTypeMethodCall(Border.class, l, EVENT_AWARE),
                IMPLIED,
                false));

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