package org.gui4j.component.factory;

import java.util.List;

import javax.swing.Icon;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.LElement;
import org.gui4j.component.Gui4jJComponent;
import org.gui4j.component.Gui4jLabel;
import org.gui4j.component.util.Gui4jAlignment;
import org.gui4j.core.Gui4jCall;
import org.gui4j.core.Gui4jComponentContainer;
import org.gui4j.core.definition.Attribute;
import org.gui4j.core.definition.AttributeTypeEnumeration;
import org.gui4j.core.definition.AttributeTypeID;
import org.gui4j.core.definition.AttributeTypeMethodCall;
import org.gui4j.util.Filter;

public final class Gui4jLabelFactory extends Gui4jJComponentFactory
{
    private final String TEXT = "text";
    private final String HALIGNMENT = "hAlignment";
    private final String VALIGNMENT = "vAlignment";
    private final String VERTICALTEXTPOSITION = "vTextPosition";
    private final String HORIZONTALTEXTPOSITION = "hTextPosition";
    private final String NAME = "label";
    private final String MNEMONIC = "mnemonic";
    private final String LABELFOR = "labelFor";
    private final String ICON = "icon";
    private final String SUFFIX = "suffix";

    private static final Log mLogger = LogFactory.getLog(Gui4jLabelFactory.class);

    public void addToplevelAttributes(List attrList, Filter filter)
    {
        super.addToplevelAttributes(attrList, filter);
        if (filter == null || filter.takeIt(Gui4jLabelFactory.class))
        {
            attrList.add(new Attribute(TEXT, new AttributeTypeMethodCall(String.class, true), false, false));
            attrList.add(new Attribute(HALIGNMENT, new AttributeTypeEnumeration(Gui4jAlignment.mHorizontalAlign, Gui4jAlignment.LEADING),
                    false, false));
            attrList.add(new Attribute(VALIGNMENT, new AttributeTypeEnumeration(Gui4jAlignment.mVerticalAlign, Gui4jAlignment.CENTER), false,
                    false));
            attrList.add(new Attribute(VERTICALTEXTPOSITION,
                    new AttributeTypeEnumeration(Gui4jAlignment.mVerticalAlign, Gui4jAlignment.CENTER), false, false));
            attrList.add(new Attribute(HORIZONTALTEXTPOSITION, new AttributeTypeEnumeration(
                    Gui4jAlignment.mHorizontalAlign, Gui4jAlignment.TRAILING), false, false));
            attrList.add(new Attribute(MNEMONIC, new AttributeTypeMethodCall(Character.TYPE), false, false));
            attrList.add(new Attribute(LABELFOR, new AttributeTypeID(), false, false));
            attrList.add(new Attribute(ICON, new AttributeTypeMethodCall(Icon.class), false, false));
            attrList.add(new Attribute(SUFFIX, new AttributeTypeMethodCall(String.class), false, false));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.gui4j.component.factory.Gui4jJComponentFactory#defineGui4jJComponentBy(org.gui4j.core.Gui4jComponentContainer,
     *      java.lang.String, org.dom4j.LElement)
     */
    public Gui4jJComponent defineGui4jJComponentBy(Gui4jComponentContainer gui4jComponentContainer, String id,
            LElement e)
    {
        Gui4jLabel gui4jLabel = new Gui4jLabel(gui4jComponentContainer, id);
        gui4jLabel.definePropertySetter(TEXT, getGui4jAccessInstance(String.class, gui4jLabel, e, TEXT));
        gui4jLabel.definePropertySetter(MNEMONIC, getGui4jAccessInstance(Character.TYPE, gui4jLabel, e, MNEMONIC));
        gui4jLabel.definePropertySetter(ICON, getGui4jAccessInstance(Icon.class, gui4jLabel, e, ICON));
        gui4jLabel.setLabelForId(e.attributeValue(LABELFOR));
        {
            String hAlign = gui4jComponentContainer.getAttrValue(e, HALIGNMENT);
            if (hAlign != null)
            {
                Integer val = (Integer) Gui4jAlignment.mHorizontalAlign.get(hAlign);
                if (val != null)
                {
                    gui4jLabel.setHAlignment(val.intValue());
                }
                else
                {
                    mLogger.warn("hAlignment " + hAlign + " nicht definiert");
                }
            }
        }
        {
            String vAlign = gui4jComponentContainer.getAttrValue(e, VALIGNMENT);
            if (vAlign != null)
            {
                Integer val = (Integer) Gui4jAlignment.mVerticalAlign.get(vAlign);
                if (val != null)
                {
                    gui4jLabel.setVAlignment(val.intValue());
                }
                else
                {
                    mLogger.warn("vAlignment " + vAlign + " nicht definiert");
                }
            }
        }
        {
            String hTextPosition = gui4jComponentContainer.getAttrValue(e, HORIZONTALTEXTPOSITION);
            if (hTextPosition != null)
            {
                Integer val = (Integer) Gui4jAlignment.mHorizontalAlign.get(hTextPosition);
                if (val != null)
                {
                    gui4jLabel.setHTextPosition(val.intValue());
                }
                else
                {
                    mLogger.warn("hTextPosition " + hTextPosition + " nicht definiert");
                }
            }
        }
        {
            String vTextPosition = gui4jComponentContainer.getAttrValue(e, VERTICALTEXTPOSITION);
            if (vTextPosition != null)
            {
                Integer val = (Integer) Gui4jAlignment.mVerticalAlign.get(vTextPosition);
                if (val != null)
                {
                    gui4jLabel.setVTextPosition(val.intValue());
                }
                else
                {
                    mLogger.warn("vTextPosition " + vTextPosition + " nicht definiert");
                }
            }
        }

        {
            Gui4jCall suffixCall = getGui4jAccessInstance(String.class, gui4jLabel, e, SUFFIX);
            gui4jLabel.setSuffixCall(suffixCall);
        }

        return gui4jLabel;
    }

    /**
     * @see org.gui4j.core.Gui4jComponentFactory#getName()
     */
    public String getName()
    {
        return NAME;
    }

}
