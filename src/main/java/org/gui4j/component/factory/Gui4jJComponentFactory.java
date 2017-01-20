package org.gui4j.component.factory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.KeyStroke;
import javax.swing.border.Border;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.LElement;
import org.gui4j.component.Gui4jJComponent;
import org.gui4j.core.Gui4jCall;
import org.gui4j.core.Gui4jComponent;
import org.gui4j.core.Gui4jComponentContainer;
import org.gui4j.core.Gui4jComponentContainerManager;
import org.gui4j.core.Gui4jComponentFactory;
import org.gui4j.core.Gui4jMap1;
import org.gui4j.core.Gui4jTypeCheck;
import org.gui4j.core.definition.Attribute;
import org.gui4j.core.definition.AttributeTypeEnumeration;
import org.gui4j.core.definition.AttributeTypeID;
import org.gui4j.core.definition.AttributeTypeMethodCall;
import org.gui4j.core.definition.AttributeTypeString;
import org.gui4j.core.definition.Param;
import org.gui4j.exception.Gui4jUncheckedException;
import org.gui4j.util.Filter;


abstract public class Gui4jJComponentFactory extends Gui4jComponentFactory
{
    private static final String BORDER = "border";
    private static final String TOOLTIP = "tooltip";
    private static final String OPAQUE = "opaque";
    private static final String GRABFOCUS = "grabFocus";
    private static final String HANDLE_READ_ONLY = "handleReadOnly";
    private static final String KEYMAP = "keyMapId";
    private static final String FOCUS_KEYS_FORWARD = "focusKeysForward";
    private static final String FOCUS_KEYS_BACKWARD = "focusKeysBackward";

    private static final Log mLogger = LogFactory.getLog(Gui4jJComponentFactory.class);

    protected void defineProperties(Gui4jJComponent gui4jJComponent, LElement e)
    {
        super.defineProperties(gui4jJComponent, e);
        gui4jJComponent.definePropertySetter(OPAQUE, getGui4jAccessInstance(
            Boolean.TYPE,
            gui4jJComponent,
            e,
            OPAQUE), false);
        gui4jJComponent.definePropertySetter(BORDER, getGui4jAccessInstance(Border.class, new Gui4jMap1(
            CONTEXT,
            Object.class), gui4jJComponent, e, BORDER), true);

        gui4jJComponent.definePropertySetter(TOOLTIP, getGui4jAccessInstance(String.class, new Gui4jMap1(
            CONTEXT,
            Object.class), gui4jJComponent, e, TOOLTIP), true);

        Gui4jCall grabFocus = getGui4jAccessInstance(
            null,
            new Gui4jMap1(CONTEXT, Object.class),
            gui4jJComponent,
            e,
            GRABFOCUS);
        if (grabFocus != null)
        {
            Gui4jCall[] dependantProperties = grabFocus.getDependantProperties();
            if (dependantProperties == null || dependantProperties.length == 0)
            {
                mLogger.warn("Set of dependant events is empty");
            }
            gui4jJComponent.setGrabFocus(grabFocus);
            if (grabFocus.getResultClass() != null)
            {
                Gui4jTypeCheck.ensureType(
                    Boolean.TYPE,
                    grabFocus.getResultClass(),
                    gui4jJComponent.getConfigurationName(),
                    e.attributeValue(GRABFOCUS));
            }
        }

        Set focusKeysForward = extractKeyStrokes(gui4jJComponent, e, FOCUS_KEYS_FORWARD);
        if (focusKeysForward != null)
        {
            gui4jJComponent.setFocusTraversalKeysForward(focusKeysForward);
        }

        Set focusKeysBackward = extractKeyStrokes(gui4jJComponent, e, FOCUS_KEYS_BACKWARD);
        if (focusKeysBackward != null)
        {
            gui4jJComponent.setFocusTraversalKeysBackward(focusKeysBackward);
        }

        gui4jJComponent.setKeyMapId(e.attributeValue(KEYMAP));
        String handleReadOnly = e.attributeValue(HANDLE_READ_ONLY);
        gui4jJComponent.setHandleReadOnly(handleReadOnly != null && handleReadOnly.equalsIgnoreCase("true"));
    }

    /**
     * Returns a Set of KeyStroke objects created from the specified attribute. The attribute value is
     * expected to be a comma separated list of individual KeyStroke definitions. Each definition is converted
     * to a KeyStroke using {@link KeyStroke#getKeyStroke(java.lang.String)}.
     * @param gui4jJComponent
     * @param e element
     * @param attr name of the attribute containing the keystroke defintions
     * @return Set(KeyStroke)
     */
    private Set extractKeyStrokes(Gui4jJComponent gui4jJComponent, LElement e, String attr)
    {
        String value = e.attributeValue(attr);
        if (value == null)
        {
            return null;
        }
        else
        {
            Set keyStrokes = new HashSet();
            String[] keyStrings = value.split(",");
            for (int i = 0; i < keyStrings.length; i++)
            {
                String keyString = keyStrings[i].trim();
                if (keyString.length() > 0)
                {
                    KeyStroke stroke = KeyStroke.getKeyStroke(keyString);
                    if (stroke == null)
                    {
                        throw new Gui4jUncheckedException.ResourceError(
                            gui4jJComponent.getConfigurationName(),
                            Gui4jComponentContainerManager.getLineNumber(e),
                            RESOURCE_ERROR_invalid_keystroke,
                            new Object[] { keyString });
                    }
                    else
                    {
                        keyStrokes.add(stroke);
                    }
                }
            }
            return keyStrokes;
        }
    }

    public void addToplevelAttributes(List attrList, Filter filter)
    {
        super.addToplevelAttributes(attrList, filter);
        if (filter == null || filter.takeIt(Gui4jJComponentFactory.class))
        {
            List l = new ArrayList();
            l.add(new Param(CONTEXT));
            attrList.add(new Attribute(
                BORDER,
                new AttributeTypeMethodCall(Border.class, l, EVENT_AWARE),
                IMPLIED,
                false));
            attrList.add(new Attribute(
                TOOLTIP,
                new AttributeTypeMethodCall(String.class, l, EVENT_AWARE),
                IMPLIED,
                false));
            attrList.add(new Attribute(OPAQUE, new AttributeTypeMethodCall(Boolean.TYPE), IMPLIED, false));
            attrList.add(new Attribute(
                GRABFOCUS,
                new AttributeTypeMethodCall(Boolean.TYPE, l, EVENT_AWARE),
                IMPLIED,
                false));
            attrList.add(new Attribute(FOCUS_KEYS_FORWARD, new AttributeTypeString(), IMPLIED, false));
            attrList.add(new Attribute(FOCUS_KEYS_BACKWARD, new AttributeTypeString(), IMPLIED, false));
            attrList.add(new Attribute(
                HANDLE_READ_ONLY,
                AttributeTypeEnumeration.getBooleanInstance(),
                false,
                false));
            attrList.add(new Attribute(KEYMAP, new AttributeTypeID(), false, false));
        }
    }

    final public Gui4jComponent defineBy(Gui4jComponentContainer gui4jComponentContainer, String id, LElement e)
    {
        Gui4jJComponent gui4jJComponent = defineGui4jJComponentBy(gui4jComponentContainer, id, e);
        defineProperties(gui4jJComponent, e);
        return gui4jJComponent;
    }

    abstract protected Gui4jJComponent defineGui4jJComponentBy(
        Gui4jComponentContainer gui4jComponentContainer,
        String id,
        LElement e);
}