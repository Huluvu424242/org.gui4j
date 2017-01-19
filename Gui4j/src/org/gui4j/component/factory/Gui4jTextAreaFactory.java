package org.gui4j.component.factory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.dom4j.LElement;
import org.gui4j.component.Gui4jJComponent;
import org.gui4j.component.Gui4jTextArea;
import org.gui4j.constants.Const;
import org.gui4j.core.Gui4jComponentContainer;
import org.gui4j.core.Gui4jMap1;
import org.gui4j.core.definition.Attribute;
import org.gui4j.core.definition.AttributeTypeEnumeration;
import org.gui4j.core.definition.AttributeTypeInteger;
import org.gui4j.core.definition.AttributeTypeMethodCall;
import org.gui4j.core.definition.Param;
import org.gui4j.util.Filter;


public final class Gui4jTextAreaFactory extends Gui4jJComponentFactory
{
    private static final String NAME = "textArea";
    private final String TEXT = "text";
    private final String SETTEXT = "setText";
    private final String FOCUSLOST = "focusLost";
    private final String VISIBLEROWS = "visibleRows";
    private final String EDITABLE = "editable";
    private final String WIDTH = "width";
    private final String MAXLENGTH = "maxLength";
    private final String LINEWRAP = "lineWrap";
    private final String WRAPSTYLEWORD = "wrapStyleWord";

    /* (non-Javadoc)
     * @see org.gui4j.component.factory.Gui4jJComponentFactory#defineGui4jJComponentBy(org.gui4j.core.Gui4jComponentContainer, java.lang.String, org.dom4j.LElement)
     */
    protected Gui4jJComponent defineGui4jJComponentBy(
        Gui4jComponentContainer gui4jComponentContainer,
        String id,
        LElement e)
    {
        Gui4jTextArea gui4jTextArea = new Gui4jTextArea(gui4jComponentContainer, id);
        gui4jTextArea.definePropertySetter(TEXT, getGui4jAccessInstance(String.class, gui4jTextArea, e, TEXT));
        gui4jTextArea.definePropertySetter(EDITABLE, getGui4jAccessInstance(
            Boolean.TYPE,
            gui4jTextArea,
            e,
            EDITABLE));
        gui4jTextArea.definePropertySetter(WIDTH, getGui4jAccessInstance(Integer.TYPE, gui4jTextArea, e, WIDTH));
        gui4jTextArea.setVisibleRows(getIntValue(gui4jComponentContainer, e, VISIBLEROWS, 3));
        
        gui4jTextArea.setLineWrap(gui4jComponentContainer.getBooleanAttrValue(e, LINEWRAP, false));
        gui4jTextArea.setWrapStyleWord(gui4jComponentContainer.getBooleanAttrValue(e, WRAPSTYLEWORD, true));
        
        {
            Map m = new Gui4jMap1(Gui4jTextArea.PARAM_VALUE, String.class);
            gui4jTextArea.setFocusLost(getGui4jAccessInstance(null, m, gui4jTextArea, e, FOCUSLOST));
            gui4jTextArea.setSetText(getGui4jAccessInstance(null, m, gui4jTextArea, e, SETTEXT));
        }

        {
            int maxLength = getIntValue(gui4jComponentContainer, e, MAXLENGTH, -1);
            if (maxLength >= 0)
            {
                gui4jTextArea.setMaxLength(maxLength);
            }
        }
        
        
        return gui4jTextArea;
    }

    public void addToplevelAttributes(List attrList, Filter filter)
    {
        Collection paramsForSetText = new ArrayList();
        paramsForSetText.add(new Param(Const.PARAM_VALUE, String.class));
        
        super.addToplevelAttributes(attrList, filter);
        if (filter == null || filter.takeIt(Gui4jTextAreaFactory.class))
        {
            attrList.add(new Attribute(TEXT, new AttributeTypeMethodCall(String.class), IMPLIED, false));
            attrList.add(new Attribute(SETTEXT, new AttributeTypeMethodCall(null, paramsForSetText), IMPLIED, false));
            attrList.add(new Attribute(FOCUSLOST, new AttributeTypeMethodCall(null), IMPLIED, false));
            attrList.add(new Attribute(VISIBLEROWS, new AttributeTypeInteger(), IMPLIED, false));
            attrList.add(new Attribute(EDITABLE, new AttributeTypeMethodCall(Boolean.TYPE), IMPLIED, false));
            attrList.add(new Attribute(WIDTH, new AttributeTypeInteger(), IMPLIED, false));
            attrList.add(new Attribute(MAXLENGTH, new AttributeTypeInteger(), IMPLIED, false));
            attrList.add(new Attribute(LINEWRAP, AttributeTypeEnumeration.getBooleanInstance(false), IMPLIED, false));
            attrList.add(new Attribute(WRAPSTYLEWORD, AttributeTypeEnumeration.getBooleanInstance(true), IMPLIED, false));
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