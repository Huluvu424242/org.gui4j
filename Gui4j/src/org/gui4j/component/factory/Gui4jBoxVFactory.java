package org.gui4j.component.factory;

import java.util.List;

import org.dom4j.LElement;

import org.gui4j.component.Gui4jBoxV;
import org.gui4j.component.Gui4jJComponent;
import org.gui4j.component.util.Gui4jAlignment;
import org.gui4j.core.Gui4jComponentContainer;
import org.gui4j.core.definition.Attribute;
import org.gui4j.core.definition.AttributeTypeEnumeration;
import org.gui4j.util.Filter;

public final class Gui4jBoxVFactory extends Gui4jBoxFactory
{

    private static final String VALIGNMENT = "vAlignment";

    /**
     * @see org.gui4j.core.Gui4jComponentFactory#getName()
     */
    public String getName()
    {
        return "vbox";
    }

    protected Gui4jJComponent createGui4jComponent(String id, List gui4jComponents, String alignment,
            Gui4jComponentContainer gui4jComponentContainer)
    {
        return new Gui4jBoxV(gui4jComponentContainer, id, gui4jComponents, alignment);
    }

    protected String getAlignment(Gui4jComponentContainer gui4jComponentContainer, LElement e)
    {
        String vAlign = gui4jComponentContainer.getAttrValue(e, VALIGNMENT);
        if (vAlign == null || "".equals(vAlign))
        {
            return null;
        }
        return vAlign;
    }

    public void addToplevelAttributes(List attrList, Filter filter)
    {
        super.addToplevelAttributes(attrList, filter);
        if (filter == null || filter.takeIt(Gui4jBoxHFactory.class))
        {
            attrList.add(new Attribute(VALIGNMENT, new AttributeTypeEnumeration(Gui4jAlignment.mVerticalAlign,
                    Gui4jAlignment.LEFT), false, false));
        }
    }

}
