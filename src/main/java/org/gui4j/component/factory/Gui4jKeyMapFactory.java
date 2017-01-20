package org.gui4j.component.factory;

import java.awt.Component;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;

import org.dom4j.LElement;
import org.gui4j.component.Gui4jKeyMap;
import org.gui4j.core.Gui4jCall;
import org.gui4j.core.Gui4jComponent;
import org.gui4j.core.Gui4jComponentContainer;
import org.gui4j.core.Gui4jComponentFactory;
import org.gui4j.core.definition.Attribute;
import org.gui4j.core.definition.AttributeTypeEnumeration;
import org.gui4j.core.definition.AttributeTypeID;
import org.gui4j.core.definition.AttributeTypeMethodCall;
import org.gui4j.util.Filter;


/**
 * Factory fuer KeyMap. Eine KeyMap enthaelt eine Menge von input und action
 * Elementen. Ein input Element definiert ein Mapping von einem Keystroke auf
 * eine action. Und ein action Element definiert das Mapping von einer Action
 * auf einen ActionCommand. Der Einfachheit halber werden Key-Bindings wie
 * Swing-Instanzen verwendet.
 */
public class Gui4jKeyMapFactory extends Gui4jComponentFactory 
{
    private static final String NAME = "keyMap";
    private static final String ADDKEYMAP = "addKeyMap";
    private static final String INPUT = "input";
    private static final String ACTION = "action";

    private static final String AFOCUSCONDITION = "focusCondition";
    private static final String ASTROKE = "stroke";
    private static final String AACTION = "action";
    private static final String ANAME = "name";
    private static final String AACTIONCOMMAND = "actionCommand";
    private static final String AID = "id";

    private static final Map mFocusCondition;
    private static final int COND_FOCUSED = JComponent.WHEN_FOCUSED;
    private static final int COND_ANCESTOR = JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT;
    private static final int COND_WINDOW = JComponent.WHEN_IN_FOCUSED_WINDOW;

    static {
        mFocusCondition = new HashMap();
        mFocusCondition.put("focused", new Integer(COND_FOCUSED));
        mFocusCondition.put("ancestor", new Integer(COND_ANCESTOR));
        mFocusCondition.put("window", new Integer(COND_WINDOW));
    }

    /* (non-Javadoc)
     * @see de.bea.gui4j.Gui4jComponentFactory#defineBy(de.bea.gui4j.Gui4jComponentContainer, java.lang.String, org.jdom.Element)
     */
    public Gui4jComponent defineBy(Gui4jComponentContainer gui4jComponentContainer, String id, LElement e)
    {
        Gui4jKeyMap keyMap = new Gui4jKeyMap(gui4jComponentContainer, Component.class, id);
        for (Iterator it = e.elements().iterator(); it.hasNext();)
        {
            LElement child = (LElement) it.next();
            if (child.getName().equals(INPUT))
            {
                int focusCondition =
                    getMapValue(gui4jComponentContainer, child, AFOCUSCONDITION, mFocusCondition, COND_FOCUSED);
                Gui4jCall stroke = getGui4jAccessInstance(String.class, keyMap, child, ASTROKE);
                Gui4jCall action = getGui4jAccessInstance(Object.class, keyMap, child, AACTION);
                Gui4jCall actionCommand = getGui4jAccessInstance(null, keyMap, child, AACTIONCOMMAND);
                keyMap.addInput(focusCondition, stroke, action, actionCommand);
            }
            if (child.getName().equals(ACTION))
            {
                Gui4jCall action = getGui4jAccessInstance(Object.class, keyMap, child, ANAME);
                Gui4jCall cmd = getGui4jAccessInstance(null, keyMap, child, AACTIONCOMMAND);
                keyMap.addAction(action, cmd);
            }
            if (child.getName().equals(ADDKEYMAP))
            {
                String keyMapId = child.attributeValue(AID);
                keyMap.addKeyMapWithId(keyMapId);
            }
        }
        return keyMap;
    }

    /* (non-Javadoc)
     * @see de.bea.gui4j.Gui4jComponentFactory#getName()
     */
    public String getName()
    {
        return "keyMap";
    }

    public Gui4jComponentFactory.SubElement getSubElement(String elementName)
    {
        if (INPUT.equals(elementName) || ACTION.equals(elementName) || ADDKEYMAP.equals(elementName))
        {
            return SubElement.empty();
        }
        if (NAME.equals(elementName))
        {
            SubElement[] subElements =
                {
                    SubElement.getInstance(INPUT),
                    SubElement.getInstance(ACTION),
                    SubElement.getInstance(ADDKEYMAP)};

            return SubElement.star(SubElement.or(subElements));
        }
        return null;
    }

    public void addInnerAttributes(String elementName, List list)
    {
        if (INPUT.equals(elementName))
        {
            Attribute[] attrs =
                {
                    new Attribute(
                        AFOCUSCONDITION,
                        new AttributeTypeEnumeration(mFocusCondition),
                        REQUIRED,
                        false),
                    new Attribute(ASTROKE, new AttributeTypeMethodCall(String.class), REQUIRED, false),
                    new Attribute(AACTIONCOMMAND, new AttributeTypeMethodCall(null), IMPLIED, false),
                    new Attribute(AACTION, new AttributeTypeMethodCall(Object.class), IMPLIED, false)};
            list.addAll(Arrays.asList(attrs));
        }
        if (ACTION.equals(elementName))
        {
            Attribute[] attrs =
                {
                    new Attribute(ANAME, new AttributeTypeMethodCall(Object.class), REQUIRED, false),
                    new Attribute(AACTIONCOMMAND, new AttributeTypeMethodCall(null), REQUIRED, false)};
            list.addAll(Arrays.asList(attrs));
        }
        if (ADDKEYMAP.equals(elementName))
        {
            Attribute[] attrs =
                { new Attribute(AID, new AttributeTypeID(), REQUIRED, false)};
            list.addAll(Arrays.asList(attrs));
        }
    }

    public void addToplevelAttributes(List attrList, Filter filter)
    {
        super.addToplevelAttributes(attrList, filter);
        if (filter == null || filter.takeIt(Gui4jKeyMapFactory.class))
        {
            attrList.clear();
        }
    }

    /* (non-Javadoc)
     * @see de.bea.gui4j.Gui4jComponentFactory#getInnerElements()
     */
    public String[] getInnerElements()
    {
        return new String[] { INPUT, ACTION, ADDKEYMAP };
    }

}
