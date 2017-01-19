package org.gui4j.component.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.LElement;

import org.gui4j.component.Gui4jAbstractToggleButton;
import org.gui4j.component.Gui4jJComponent;
import org.gui4j.constants.Const;
import org.gui4j.core.Gui4jComponentContainer;
import org.gui4j.core.definition.Attribute;
import org.gui4j.core.definition.AttributeTypeMethodCall;
import org.gui4j.core.definition.Param;
import org.gui4j.util.Filter;

// KKB, 27.7.2005: As soon as the onSelect attribute is removed there is no need any longer
// for this class. Factory for checkBox can then extend
// Gui4jAbstractButtonFactory directly. Instead of onSelect, one should use actionCommand
// which also allows the '?value' parameter to enquire about the selected status. This
// is implemented for all buttons, i.e. also for checkBoxMenuItem that is missing the
// onSelect attribute, which is not consistent.
// => onSelect should be regarded deprecated in favor of actionCommand
public abstract class Gui4jAbstractToggleButtonFactory extends Gui4jAbstractButtonFactory
{
    private static final String ONSELECT = "onSelect";

    protected Gui4jJComponent defineGui4jJComponentBy(Gui4jComponentContainer gui4jComponentContainer, String id,
            LElement e)
    {
        Gui4jAbstractToggleButton button = createGui4jToggleButton(gui4jComponentContainer, id);

        {
            Map paramMap = new HashMap();
            paramMap.put(Const.PARAM_VALUE, Boolean.TYPE);
            button.setOnSelect(getGui4jAccessInstance(null, paramMap, button, e, ONSELECT));
        }

        return button;
    }

    protected abstract Gui4jAbstractToggleButton createGui4jToggleButton(
            Gui4jComponentContainer gui4jComponentContainer, String id);

    public void addToplevelAttributes(List attrList, Filter filter)
    {
        super.addToplevelAttributes(attrList, filter);
        if (filter == null || filter.takeIt(Gui4jCheckBoxFactory.class))
        {
            List params = new ArrayList();
            params.add(new Param(Const.PARAM_VALUE, boolean.class));
            attrList.add(new Attribute(ONSELECT, new AttributeTypeMethodCall(null, params), IMPLIED, false));
        }
    }

}
