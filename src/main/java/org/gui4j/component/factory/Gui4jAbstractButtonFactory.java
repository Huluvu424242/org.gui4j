package org.gui4j.component.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;

import org.dom4j.LElement;

import org.gui4j.component.Gui4jAbstractButton;
import org.gui4j.component.Gui4jJComponent;
import org.gui4j.component.util.Gui4jAlignment;
import org.gui4j.constants.Const;
import org.gui4j.core.Gui4jAbstractComponent;
import org.gui4j.core.Gui4jMap1;
import org.gui4j.core.definition.Attribute;
import org.gui4j.core.definition.AttributeTypeEnumeration;
import org.gui4j.core.definition.AttributeTypeMethodCall;
import org.gui4j.core.definition.Param;
import org.gui4j.util.Filter;

/**
 * Class to represent Swing Gui4jAbstractButtonFactory
 */
public abstract class Gui4jAbstractButtonFactory extends Gui4jJComponentFactory
{
    protected static final String TEXT = "text";
    protected static final String ICON = "icon";
    protected static final String ICON_TEXT_GAP = "iconTextGap";
    protected static final String ACTIONCOMMAND = "actionCommand";
    private final String VERTICALTEXTPOSITION = "vTextPosition";
    private final String HORIZONTALTEXTPOSITION = "hTextPosition";
    private static final String GROUP = "group";
    private static final String MNEMONIC = "mnemonic";
    private static final String SELECTED = "selected";

    public void addToplevelAttributes(List attrList, Filter filter)
    {
        super.addToplevelAttributes(attrList, filter);
        if (filter == null || filter.takeIt(Gui4jAbstractButtonFactory.class))
        {
            attrList.add(new Attribute(TEXT, new AttributeTypeMethodCall(String.class, EVENT_AWARE), IMPLIED, false));
            attrList.add(new Attribute(ACTIONCOMMAND, new AttributeTypeMethodCall(null,
                    getParameterListForActionCommand()), IMPLIED, false));
            attrList.add(new Attribute(GROUP, new AttributeTypeMethodCall(String.class), IMPLIED, false));
            attrList.add(new Attribute(MNEMONIC, new AttributeTypeMethodCall(Character.TYPE), IMPLIED, false));
            attrList.add(new Attribute(VERTICALTEXTPOSITION, new AttributeTypeEnumeration(
                    Gui4jAlignment.mVerticalAlign, Gui4jAlignment.CENTER), false, false));
            attrList.add(new Attribute(HORIZONTALTEXTPOSITION, new AttributeTypeEnumeration(
                    Gui4jAlignment.mHorizontalAlign, Gui4jAlignment.TRAILING), false, false));
            attrList.add(new Attribute(ICON, new AttributeTypeMethodCall(Icon.class), IMPLIED, false));
            attrList.add(new Attribute(ICON_TEXT_GAP, new AttributeTypeMethodCall(Integer.TYPE), IMPLIED, false));
            attrList
                    .add(new Attribute(SELECTED, new AttributeTypeMethodCall(Boolean.TYPE, EVENT_AWARE), IMPLIED, false));
        }
    }

    protected void definePropertyText(Gui4jAbstractComponent gui4jJComponent, LElement e)
    {
        gui4jJComponent.definePropertySetter(TEXT, getGui4jAccessInstance(String.class,
                getParameterMapForActionCommand(gui4jJComponent), gui4jJComponent, e, TEXT));
    }

    protected void defineProperties(Gui4jJComponent gui4jJComponent, LElement e)
    {
        super.defineProperties(gui4jJComponent, e);
        Gui4jAbstractButton button = (Gui4jAbstractButton) gui4jJComponent;
        definePropertyText(gui4jJComponent, e);
        gui4jJComponent.definePropertySetter(GROUP, getGui4jAccessInstance(String.class, gui4jJComponent, e, GROUP));
        button.setActionCommand(getGui4jAccessInstance(null, getParameterMapForActionCommand(gui4jJComponent),
                gui4jJComponent, e, ACTIONCOMMAND));
        button.definePropertySetter(MNEMONIC, getGui4jAccessInstance(Character.TYPE, gui4jJComponent, e, MNEMONIC));
        gui4jJComponent.definePropertySetter(ICON, getGui4jAccessInstance(Icon.class, gui4jJComponent, e, ICON));
        gui4jJComponent.definePropertySetter(ICON_TEXT_GAP, getGui4jAccessInstance(Integer.TYPE, gui4jJComponent, e,
                ICON_TEXT_GAP));
        gui4jJComponent.definePropertySetter(SELECTED, getGui4jAccessInstance(Boolean.TYPE, gui4jJComponent, e,
                SELECTED));
        
        {
            String hTextPosition = gui4jJComponent.getGui4jComponentContainer().getAttrValue(e, HORIZONTALTEXTPOSITION);
            if (hTextPosition != null)
            {
                Integer val = (Integer) Gui4jAlignment.mHorizontalAlign.get(hTextPosition);
                if (val != null)
                {
                    button.setHTextPosition(val.intValue());
                }                
            }
        }
        {
            String vTextPosition = gui4jJComponent.getGui4jComponentContainer().getAttrValue(e, VERTICALTEXTPOSITION);
            if (vTextPosition != null)
            {
                Integer val = (Integer) Gui4jAlignment.mVerticalAlign.get(vTextPosition);
                if (val != null)
                {
                    button.setVTextPosition(val.intValue());
                }                
            }
        }

    }

    protected Map getParameterMapForActionCommand(Gui4jAbstractComponent gui4jAbstractComponent)
    {
        return new Gui4jMap1(Const.PARAM_VALUE, Boolean.TYPE);
    }

    protected List getParameterListForActionCommand()
    {
        List params = new ArrayList(1);
        params.add(new Param(Const.PARAM_VALUE, boolean.class));
        return params;
    }

}
