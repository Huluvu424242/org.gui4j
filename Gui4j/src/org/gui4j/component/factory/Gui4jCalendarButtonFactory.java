package org.gui4j.component.factory;

import java.util.List;

import org.dom4j.LElement;
import org.gui4j.component.Gui4jCalendarButton;
import org.gui4j.component.Gui4jJComponent;
import org.gui4j.core.Gui4jComponentContainer;
import org.gui4j.core.Gui4jQualifiedComponent;
import org.gui4j.core.definition.Attribute;
import org.gui4j.core.definition.AttributeTypeID;
import org.gui4j.util.Filter;


public final class Gui4jCalendarButtonFactory extends Gui4jAbstractButtonFactory
{

    private final String NAME = "calendarButton";
    private static final String EDITID = "editId";

    /* (non-Javadoc)
     * @see org.gui4j.component.factory.Gui4jJComponentFactory#defineGui4jJComponentBy(org.gui4j.core.Gui4jComponentContainer, java.lang.String, org.dom4j.LElement)
     */
    protected Gui4jJComponent defineGui4jJComponentBy(
        Gui4jComponentContainer gui4jComponentContainer,
        String id,
        LElement e)
    {
        String editId = e.attributeValue(EDITID);
        assert editId != null;
        Gui4jQualifiedComponent gui4jComponentInPath = gui4jComponentContainer.getGui4jQualifiedComponent(editId);
        return new Gui4jCalendarButton(gui4jComponentContainer, id, gui4jComponentInPath);
    }

    public String getName()
    {
        return NAME;
    }

    /* (non-Javadoc)
     * @see de.bea.gui4j.Gui4jComponentFactory#addToplevelAttributes(java.util.List, de.bea.util.Filter)
     */
    public void addToplevelAttributes(List attrList, Filter filter)
    {
        super.addToplevelAttributes(attrList, filter);
        if (filter == null || filter.takeIt(Gui4jCalendarButtonFactory.class))
        {
            attrList.add(new Attribute(EDITID, new AttributeTypeID(), REQUIRED, false));
        }
    }

}
