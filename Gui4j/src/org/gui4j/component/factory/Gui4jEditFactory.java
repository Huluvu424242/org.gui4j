package org.gui4j.component.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.LElement;
import org.gui4j.component.Gui4jEdit;
import org.gui4j.component.Gui4jJComponent;
import org.gui4j.core.Gui4jCall;
import org.gui4j.core.Gui4jComponentContainer;
import org.gui4j.core.Gui4jMap1;
import org.gui4j.core.definition.Attribute;
import org.gui4j.core.definition.AttributeTypeEnumeration;
import org.gui4j.core.definition.AttributeTypeInteger;
import org.gui4j.core.definition.AttributeTypeMethodCall;
import org.gui4j.core.definition.Param;
import org.gui4j.util.Filter;


public class Gui4jEditFactory extends Gui4jJComponentFactory
{
    private final String NAME = "edit";
    private final String VALUE = "value";
    private final String SETVALUE = "setValue";
    private final String WIDTH = "width";
    private final String MAXLENGTH = "maxLength";
    private final String ACTIONCOMMAND = "actionCommand";
    private final String EDITABLE = "editable";
    private final String HALIGNMENT = "alignment";

    private static final Log mLogger = LogFactory.getLog(Gui4jEditFactory.class);

    public final static Map mHorizontalAlign = new HashMap();

    static {
        mHorizontalAlign.put("left", new Integer(SwingConstants.LEFT));
        mHorizontalAlign.put("center", new Integer(SwingConstants.CENTER));
        mHorizontalAlign.put("right", new Integer(SwingConstants.RIGHT));
        mHorizontalAlign.put("leading", new Integer(SwingConstants.LEADING));
        mHorizontalAlign.put("trailing", new Integer(SwingConstants.TRAILING));
    }

    public void addToplevelAttributes(List attrList, Filter filter)
    {
        super.addToplevelAttributes(attrList, filter);
        if (filter == null || filter.takeIt(Gui4jEditFactory.class))
        {
            attrList.add(new Attribute(VALUE, new AttributeTypeMethodCall(String.class, true), IMPLIED, false));
            attrList.add(new Attribute(WIDTH, new AttributeTypeMethodCall(Integer.TYPE), IMPLIED, false));
            attrList.add(new Attribute(MAXLENGTH, new AttributeTypeInteger(), IMPLIED, false));
            List valueParam = new ArrayList();
            valueParam.add(new Param(Gui4jEdit.PARAM_VALUE, String.class));
            attrList.add(new Attribute(ACTIONCOMMAND, new AttributeTypeMethodCall(null, valueParam), IMPLIED, false));
            attrList.add(new Attribute(SETVALUE, new AttributeTypeMethodCall(null, valueParam), IMPLIED, false));
            attrList.add(new Attribute(EDITABLE, new AttributeTypeMethodCall(Boolean.TYPE, true), IMPLIED, false));
            attrList.add(new Attribute(HALIGNMENT, new AttributeTypeEnumeration(mHorizontalAlign), IMPLIED, false));
        }
    }

    /* (non-Javadoc)
     * @see org.gui4j.component.factory.Gui4jJComponentFactory#defineGui4jJComponentBy(org.gui4j.core.Gui4jComponentContainer, java.lang.String, org.dom4j.LElement)
     */
    public Gui4jJComponent defineGui4jJComponentBy(
        Gui4jComponentContainer gui4jComponentContainer,
        String id,
        LElement e)
    {
        Gui4jEdit gui4jEdit = createTextFieldComponent(gui4jComponentContainer, id);
        Gui4jCall value = getGui4jAccessInstance(String.class, gui4jEdit, e, VALUE);
        gui4jEdit.definePropertySetter(VALUE, value);
        gui4jEdit.setValue(value);
        gui4jEdit.definePropertySetter(WIDTH, getGui4jAccessInstance(Integer.TYPE, gui4jEdit, e, WIDTH));
        gui4jEdit.definePropertySetter(EDITABLE, getGui4jAccessInstance(Boolean.TYPE, gui4jEdit, e, EDITABLE));

        {
            int maxLength = getIntValue(gui4jComponentContainer, e, MAXLENGTH, -1);
            if (maxLength >= 0)
            {
                gui4jEdit.setMaxLength(maxLength);
            }
        }

        {
            String hAlign = gui4jComponentContainer.getAttrValue(e, HALIGNMENT);
            if (hAlign != null)
            {
                Integer val = (Integer) mHorizontalAlign.get(hAlign);
                if (val != null)
                {
                    gui4jEdit.setHAlignment(val.intValue());
                }
                else
                {
                    mLogger.warn("hAlignment " + hAlign + " nicht definiert");
                }
            }
        }

        {
            Map m = new Gui4jMap1(Gui4jEdit.PARAM_VALUE, String.class);
            gui4jEdit.setActionCommand(getGui4jAccessInstance(null, m, gui4jEdit, e, ACTIONCOMMAND));
            gui4jEdit.setSetValue(getGui4jAccessInstance(null, m, gui4jEdit, e, SETVALUE));
        }

        return gui4jEdit;
    }

    protected Gui4jEdit createTextFieldComponent(Gui4jComponentContainer gui4jComponentContainer, String id)
    {
        return new Gui4jEdit(gui4jComponentContainer, id);
    }

    /**
     * @see org.gui4j.core.Gui4jComponentFactory#getName()
     */
    public String getName()
    {
        return NAME;
    }

}
