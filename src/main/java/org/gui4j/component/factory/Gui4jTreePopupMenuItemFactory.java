package org.gui4j.component.factory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JMenuItem;

import org.dom4j.LElement;
import org.gui4j.component.Gui4jJComponent;
import org.gui4j.component.Gui4jTree;
import org.gui4j.component.Gui4jTreePopupMenuItem;
import org.gui4j.core.Gui4jComponentContainer;
import org.gui4j.core.definition.Attribute;
import org.gui4j.core.definition.AttributeTypeEnumeration;
import org.gui4j.util.Filter;


public class Gui4jTreePopupMenuItemFactory extends Gui4jPopupMenuItemFactory
{
    public static final String TREE_POPUP_MENUITEM_NAME = "treePopupMenuItem";
    private static final String COMMAND = "command";

    private static final Map mCommand;

    static {
        mCommand = new HashMap();
        mCommand.put("collapse", new Integer(Gui4jTree.COMMAND_COLLAPSE));
        mCommand.put("expand", new Integer(Gui4jTree.COMMAND_EXPAND));
        mCommand.put("setroot", new Integer(Gui4jTree.COMMAND_SETROOT));
        mCommand.put("resetroot", new Integer(Gui4jTree.COMMAND_RESETROOT));
        mCommand.put("collapseall", new Integer(Gui4jTree.COMMAND_COLLAPSE_ALL));
        mCommand.put("expandall", new Integer(Gui4jTree.COMMAND_EXPAND_ALL));
    }

    /* (non-Javadoc)
     * @see org.gui4j.component.factory.Gui4jJComponentFactory#defineGui4jJComponentBy(org.gui4j.core.Gui4jComponentContainer, java.lang.String, org.dom4j.LElement)
     */
    protected Gui4jJComponent defineGui4jJComponentBy(
        Gui4jComponentContainer gui4jComponentContainer,
        String id,
        LElement e)
    {
        Class contextType = extractContextType(gui4jComponentContainer, e);
        int command = getMapValue(gui4jComponentContainer, e, COMMAND, mCommand, Gui4jTree.COMMAND_EXPAND);
        return new Gui4jTreePopupMenuItem(gui4jComponentContainer, JMenuItem.class, contextType, command, id);
    }

    public void addToplevelAttributes(List attrList, Filter filter)
    {
        super.addToplevelAttributes(attrList, filter);
        if (filter == null || filter.takeIt(Gui4jTreePopupMenuItemFactory.class))
        {
            for (Iterator it = attrList.iterator(); it.hasNext();)
            {
                Attribute attribute = (Attribute) it.next();
                if (attribute.getName().equals(ACTIONCOMMAND))
                {
                    it.remove();
                }
            }

            attrList.add(new Attribute(COMMAND, new AttributeTypeEnumeration(mCommand), REQUIRED, false));
        }
    }

    /**
     * @see org.gui4j.core.Gui4jComponentFactory#getName()
     */
    public String getName()
    {
        return TREE_POPUP_MENUITEM_NAME;
    }

}
