package org.gui4j.component.factory;

import java.awt.Component;
import java.util.List;
import java.util.Map;

import org.dom4j.LElement;
import org.gui4j.component.Gui4jExternalComponent;
import org.gui4j.core.Gui4jCall;
import org.gui4j.core.Gui4jComponent;
import org.gui4j.core.Gui4jComponentContainer;
import org.gui4j.core.Gui4jComponentFactory;
import org.gui4j.core.definition.Attribute;
import org.gui4j.core.definition.AttributeTypeAlias;
import org.gui4j.core.definition.AttributeTypeMethodCall;
import org.gui4j.util.Filter;


public final class Gui4jExternalComponentFactory extends Gui4jComponentFactory
{
    private final static String NAME = "external";
    private final static String COMPONENT = "component";
    private final static String COMPONENTTYPE = "componentType";

    public void addToplevelAttributes(List attrList, Filter filter)
    {
        super.addToplevelAttributes(attrList, filter);
        if (filter == null || filter.takeIt(Gui4jExternalComponentFactory.class))
        {
            attrList.add(new Attribute(COMPONENT, new AttributeTypeMethodCall(Component.class), REQUIRED, false));
            attrList.add(new Attribute(COMPONENTTYPE, new AttributeTypeAlias(), IMPLIED, false));
        }
    }

    /* (non-Javadoc)
     * @see org.gui4j.core.Gui4jComponentFactory#defineBy(org.gui4j.core.Gui4jComponentContainer, java.lang.String, org.dom4j.LElement)
     */
    public Gui4jComponent defineBy(Gui4jComponentContainer gui4jComponentContainer, String id, LElement e)
    {
        String componentTypeAlias = gui4jComponentContainer.getAttrValue(e, COMPONENTTYPE);
        Class componentType = Component.class;
        if (componentTypeAlias != null)
        {
            componentType = gui4jComponentContainer.getClassForAliasName(componentTypeAlias);
        }

        Gui4jExternalComponent gui4jExternalComponent =
            new Gui4jExternalComponent(gui4jComponentContainer, componentType, id);
        Map m = null;
		Gui4jCall component = getGui4jAccessInstance(Component.class, m, gui4jExternalComponent, e, COMPONENT);
        gui4jExternalComponent.definePropertySetter(COMPONENT,component);
        defineProperties(gui4jExternalComponent, e);
        // gui4jExternalComponent.setComponent(component);
        return gui4jExternalComponent;
    }

    /**
     * @see org.gui4j.core.Gui4jComponentFactory#getName()
     */
    public String getName()
    {
        return NAME;
    }

}
