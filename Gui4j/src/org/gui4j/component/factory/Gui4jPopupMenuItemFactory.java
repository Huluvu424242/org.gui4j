package org.gui4j.component.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JMenuItem;

import org.dom4j.LElement;
import org.gui4j.component.Gui4jJComponent;
import org.gui4j.component.Gui4jMenuItem;
import org.gui4j.component.Gui4jPopupMenuItem;
import org.gui4j.constants.Const;
import org.gui4j.core.Gui4jAbstractComponent;
import org.gui4j.core.Gui4jCall;
import org.gui4j.core.Gui4jComponentContainer;
import org.gui4j.core.Gui4jComponentContainerManager;
import org.gui4j.core.definition.Attribute;
import org.gui4j.core.definition.AttributeTypeAlias;
import org.gui4j.core.definition.Param;
import org.gui4j.exception.Gui4jUncheckedException;
import org.gui4j.util.Filter;


public class Gui4jPopupMenuItemFactory extends Gui4jMenuItemFactory
{
    public static final String POPUP_MENUITEM_NAME = "popupMenuItem";
    
    private static final String CONTEXT_TYPE = "contextType";

    /* (non-Javadoc)
     * @see org.gui4j.component.factory.Gui4jJComponentFactory#defineGui4jJComponentBy(org.gui4j.core.Gui4jComponentContainer, java.lang.String, org.dom4j.LElement)
     */
    protected Gui4jJComponent defineGui4jJComponentBy(
        Gui4jComponentContainer gui4jComponentContainer,
        String id,
        LElement e)
    {
        Class contextType = extractContextType(gui4jComponentContainer, e);
        return new Gui4jPopupMenuItem(gui4jComponentContainer, JMenuItem.class, contextType, id);
    }

    protected Class extractContextType(Gui4jComponentContainer gui4jComponentContainer, LElement e)
    {
        Class contextType = null;
        String contextTypeAliasName = gui4jComponentContainer.getAttrValue(e, CONTEXT_TYPE);
        if (contextTypeAliasName != null)
        {
            contextType = gui4jComponentContainer.getClassForAliasName(contextTypeAliasName);
            if (contextType == null)
            {
                Object[] args = { contextType, contextTypeAliasName };
                throw new Gui4jUncheckedException.ResourceError(
                    gui4jComponentContainer.getConfigurationName(),
                    Gui4jComponentContainerManager.getLineNumber(e.attribute(CONTEXT_TYPE)),
                    RESOURCE_ERROR_alias_not_defined_in_path,
                    args);

            }
        }
        return contextType;
    }

    public void addToplevelAttributes(List attrList, Filter filter)
    {
        super.addToplevelAttributes(attrList, filter);
        if (filter == null || filter.takeIt(Gui4jPopupMenuItemFactory.class))
        {
            attrList.add(new Attribute(CONTEXT_TYPE, new AttributeTypeAlias(), IMPLIED, false));
        }
    }

    /**
     * @see org.gui4j.core.Gui4jComponentFactory#getName()
     */
    public String getName()
    {
        return POPUP_MENUITEM_NAME;
    }

    protected Map getParameterMapForActionCommand(Gui4jAbstractComponent gui4jAbstractComponent)
    {
        Map map = new HashMap();
        assert gui4jAbstractComponent instanceof Gui4jMenuItem;
        Class contextType = ((Gui4jPopupMenuItem) gui4jAbstractComponent).getContextType();
        map.put(Const.PARAM_CONTEXT, contextType == null ? Object.class : contextType);
        return map;
    }

    protected List getParameterListForActionCommand() {
        List list = new ArrayList(1);
        list.add(new Param(Const.PARAM_CONTEXT, CONTEXT_TYPE));
        return list;
    }
    
    protected void definePropertyEnabled(Gui4jAbstractComponent gui4jComponent, LElement e)
    {
        Gui4jPopupMenuItem popupMenuItem = (Gui4jPopupMenuItem) gui4jComponent;
        Gui4jCall enabled =
            getGui4jAccessInstance(Boolean.TYPE, getParameterMapForActionCommand(gui4jComponent), gui4jComponent, e, ENABLED);
        popupMenuItem.setEnabled(enabled);
    }

    protected void definePropertyVisible(Gui4jAbstractComponent gui4jComponent, LElement e)
    {
        Gui4jPopupMenuItem popupMenuItem = (Gui4jPopupMenuItem) gui4jComponent;
        Gui4jCall visible =
            getGui4jAccessInstance(Boolean.TYPE, getParameterMapForActionCommand(gui4jComponent), gui4jComponent, e, VISIBLE);
        popupMenuItem.setVisible(visible);
    }

    /* (non-Javadoc)
     * @see org.gui4j.component.factory.Gui4jAbstractButtonFactory#definePropertyText(org.gui4j.core.Gui4jAbstractComponent, org.dom4j.LElement)
     */
    protected void definePropertyText(Gui4jAbstractComponent gui4jComponent, LElement e)
    {
        Gui4jPopupMenuItem popupMenuItem = (Gui4jPopupMenuItem) gui4jComponent;
        Gui4jCall text = getGui4jAccessInstance(null, getParameterMapForActionCommand(gui4jComponent), gui4jComponent, e, TEXT);
        popupMenuItem.setText(text);
    }

}
