package org.gui4j.core;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.LElement;
import org.gui4j.core.definition.Attribute;
import org.gui4j.core.definition.AttributeTypeID;
import org.gui4j.core.definition.AttributeTypeMethodCall;
import org.gui4j.core.definition.Param;
import org.gui4j.exception.ErrorTags;
import org.gui4j.exception.Gui4jUncheckedException;
import org.gui4j.util.Filter;

/**
 * Base class for all component factories. In order to provide a new component,
 * a sub-class of Gui4jComponentFactory must be defined. During initialization,
 * instances of the factories defined in an initialization property file are
 * created. These instances are used to extract the necessary information from
 * the corresponding part in a given xml resource file. The
 * Gui4jComponentManager is responsible for these factory instance. One instance
 * a Gui4jComponentManager contains always at most one instance of a given
 * factory class and one instance of Gui4j contains exactly one instance of
 * Gui4jComponentManager. Hence, if one instance of Gui4j is used, there is
 * always only one instance of a defined factory class.
 */
public abstract class Gui4jComponentFactory implements ErrorTags, Serializable
{
    private static final Log mLog = LogFactory.getLog(Gui4jComponentFactory.class);

    public final static String FIELD_Id = "guiId";
    public final static String FIELDGui4jRef_Id = "id";
    public final static String FIELD_Style = "style";

    protected final static boolean IMPLIED = false;
    protected final static boolean REQUIRED = true;
    protected final static boolean NOT_EVENT_AWARE = false;
    protected final static boolean EVENT_AWARE = true;
    protected final static String DTD_IMPLIED = "#IMPLIED";
    protected final static String DTD_REQUIRED = "#REQUIRED";
    protected final static String CDATA = "CDATA";

    public static final String ELEMENT_GUI4JREF = "Ref";
    public static final String ELEMENT_COMPONENT = "Component";

    private static final String BACKGROUND = "background";
    private static final String APPLY = "apply";
    private static final String ONINIT = "onInit";
    private static final String CURSOR = "cursor";
    protected static final String ENABLED = "enabled";
    protected static final String VISIBLE = "visible";
    protected static final String FOCUSABLE = "focusable";
    private static final String FONT = "font";
    private static final String FOREGROUND = "foreground";
    private static final String ONCLICK = "onClick";
    private static final String POPUP = "popup";
    public static final String CONTEXT = "context";

    private static final Attribute[] ATTRIBUTE_ARRAY_TEMPLATE = new Attribute[0];

    private Gui4jInternal mGui4j;
    private Gui4jCallFactory mGui4jCallFactory;

    /**
     * Defines the subelements this component allows. This default
     * implementation returns a subelement representing an empty element.
     * Factory classes for components that allow subelements have to override
     * this method.
     * 
     * @param elementName
     *            the element, whose SubElement should be returned.
     * @return the SubElement for the specified element or <code>null</code>
     *         if an unknown elementName was specified.
     */
    public SubElement getSubElement(String elementName)
    {
        return SubElement.empty();
    }

    /**
     * Writes the DTD of this component to the given stream.
     * 
     * @param out
     */
    public final void writeDTD(PrintWriter out)
    {
        writeToplevelElement(out, getSubElement(getName()));

        String[] elements = getInnerElements();
        if (elements != null)
        {
            for (int i = 0; i < elements.length; i++)
            {
                String element = elements[i];
                List attributes = new ArrayList();
                addInnerAttributes(element, attributes);
                writeElement(out, element, getSubElement(element), attrList2Arr(attributes));
            }
        }
    }

    /**
     * Generates a component by a given JDOM-Element
     * 
     * @param gui4jComponentContainer
     * @param id
     * @param e
     * @return Gui4jComponent
     */
    abstract public Gui4jComponent defineBy(Gui4jComponentContainer gui4jComponentContainer, String id, LElement e);

    /**
     * Returns the name of the component
     * 
     * @return String
     */
    abstract public String getName();

    public void setGui4j(Gui4jInternal gui4j)
    {
        mGui4j = gui4j;
    }

    public void setGui4jCallFactory(Gui4jCallFactory gui4jCallFactory)
    {
        mGui4jCallFactory = gui4jCallFactory;
    }

    protected void defineProperties(Gui4jAbstractComponent gui4jComponent, LElement e)
    {
        gui4jComponent.definePropertySetter(BACKGROUND, getGui4jAccessInstance(Color.class, new Gui4jMap1(CONTEXT,
                Object.class), gui4jComponent, e, BACKGROUND), true);
        gui4jComponent.definePropertySetter(CURSOR, getGui4jAccessInstance(Cursor.class, gui4jComponent, e, CURSOR));
        gui4jComponent.definePropertySetter(FONT, getGui4jAccessInstance(Font.class, new Gui4jMap1(CONTEXT,
                Object.class), gui4jComponent, e, FONT), true);
        gui4jComponent.definePropertySetter(FOREGROUND, getGui4jAccessInstance(Color.class, new Gui4jMap1(CONTEXT,
                Object.class), gui4jComponent, e, FOREGROUND), true);
        gui4jComponent.setOnClick(getGui4jAccessInstance(null, MouseEvent.class, gui4jComponent, e, ONCLICK));
        gui4jComponent.setApply(getGui4jAccessInstance(null, gui4jComponent.getComponentClass(), gui4jComponent, e,
                APPLY));
        gui4jComponent.setOnInit(getGui4jAccessInstance(null, gui4jComponent.getComponentClass(), gui4jComponent, e,
                ONINIT));
        definePropertyEnabled(gui4jComponent, e);
        definePropertyVisible(gui4jComponent, e);
        gui4jComponent.definePropertySetter(FOCUSABLE, getGui4jAccessInstance(Boolean.TYPE, gui4jComponent, e,
                FOCUSABLE));
        gui4jComponent.setContext(getGui4jAccessInstance(Object.class, gui4jComponent, e, CONTEXT));

        String popupId = e.attributeValue(POPUP);
        if (popupId != null)
        {
            Gui4jQualifiedComponent popupComponent = gui4jComponent.getGui4jComponentContainer()
                    .getGui4jQualifiedComponent(popupId);
            gui4jComponent.setPopupMenu(popupComponent);
        }
    }

    protected void definePropertyEnabled(Gui4jAbstractComponent gui4jComponent, LElement e)
    {
        gui4jComponent.definePropertySetter(ENABLED, getGui4jAccessInstance(Boolean.TYPE, gui4jComponent, e, ENABLED));
    }

    protected void definePropertyVisible(Gui4jAbstractComponent gui4jComponent, LElement e)
    {
        gui4jComponent.definePropertySetter(VISIBLE, getGui4jAccessInstance(Boolean.TYPE, gui4jComponent, e, VISIBLE));
    }

    protected void writeToplevelElement(PrintWriter out, SubElement subElement)
    {
        List attrList = new ArrayList();
        addToplevelAttributes(attrList, null);
        Attribute[] attrs = attrList2Arr(attrList);
        writeElement(out, getName(), subElement, attrs);
    }

    public void addToplevelAttributes(List attrList, Filter filter)
    {
        if (filter == null || filter.takeIt(Gui4jComponentFactory.class))
        {
            List l = new ArrayList();
            l.add(new Param(CONTEXT));
            attrList.add(new Attribute(BACKGROUND, new AttributeTypeMethodCall(Color.class, l, EVENT_AWARE), IMPLIED,
                    false));
            attrList.add(new Attribute(CURSOR, new AttributeTypeMethodCall(Cursor.class, EVENT_AWARE), IMPLIED, false));
            attrList
                    .add(new Attribute(ENABLED, new AttributeTypeMethodCall(Boolean.TYPE, EVENT_AWARE), IMPLIED, false));
            attrList
                    .add(new Attribute(VISIBLE, new AttributeTypeMethodCall(Boolean.TYPE, EVENT_AWARE), IMPLIED, false));
            attrList.add(new Attribute(FOCUSABLE, new AttributeTypeMethodCall(Boolean.TYPE, EVENT_AWARE), IMPLIED,
                    false));
            attrList.add(new Attribute(FONT, new AttributeTypeMethodCall(Font.class, l, EVENT_AWARE), IMPLIED, false));
            attrList.add(new Attribute(FOREGROUND, new AttributeTypeMethodCall(Color.class, l, EVENT_AWARE), IMPLIED,
                    false));
            attrList.add(new Attribute(ONCLICK, new AttributeTypeMethodCall(null), IMPLIED, false));
            attrList.add(new Attribute(APPLY, new AttributeTypeMethodCall(null), IMPLIED, false));
            attrList.add(new Attribute(ONINIT, new AttributeTypeMethodCall(null, l), IMPLIED, false));
            attrList.add(new Attribute(POPUP, new AttributeTypeID(), IMPLIED, false));
            attrList.add(new Attribute(CONTEXT, new AttributeTypeMethodCall(Object.class), IMPLIED, false));
        }
    }

    /**
     * Adds the allowed attributes for the specified sub element of this
     * component to the given list. Factory classes for components allowing sub
     * elements have to override this method. The default implementation does
     * not alter the list.
     * 
     * @param elementName
     * @param list
     */
    public void addInnerAttributes(String elementName, List list)
    {
    }

    protected void writeElement(PrintWriter out, String elementName, SubElement subElement, Attribute[] attributes)
    {
        out.println("<!ELEMENT " + elementName + " " + subElement + ">");
        boolean topLevelComponent = getName().equals(elementName);
        out.println("<!ATTLIST " + elementName);
        if (topLevelComponent)
        {
            out.println("  " + FIELD_Id + " " + CDATA + " " + DTD_IMPLIED);
        }
        out.println("  " + FIELD_Style + " " + CDATA + " " + DTD_IMPLIED);
        for (int i = 0; i < attributes.length; i++)
        {
            out.println("  " + attributes[i]);
        }
        out.println(">");
        // print DTD part for style component
        out.println("<!ELEMENT " + Gui4jComponentManager.ELEMENT_Gui4jStyle + "_" + elementName + " EMPTY>");
        if (attributes.length > 0)
        {
            out.println("<!ATTLIST " + Gui4jComponentManager.ELEMENT_Gui4jStyle + "_" + elementName);
            for (int i = 0; i < attributes.length; i++)
            {
                out.println("  " + attributes[i].getOptional());
            }
            out.println(">");
        }
    }

    /**
     * Returns an array with all inner elements that are defined by this
     * component. If other top level elements are allowed to be nested inside
     * this component (i.e. are possible sub elements), they must NOT be
     * included in the returned array. Factory classes for components allowing
     * sub elements other than top-level components have to override this class.
     * The default implementation returns <code>null</code>.
     * 
     * @return String[]
     */
    public String[] getInnerElements()
    {
        return null;
    }

    public int getIntValue(Gui4jComponentContainer gui4jComponentContainer, LElement e, String field)
    {
        String value = gui4jComponentContainer.getAttrValue(e, field);
        try
        {
            return Integer.parseInt(value);
        }
        catch (NumberFormatException ex)
        {
            Object[] args = { value };
            throw new Gui4jUncheckedException.ResourceError(gui4jComponentContainer.getConfigurationName(), e
                    .getLineNumber(), RESOURCE_ERROR_int_DataConversionException, args, ex);
        }
    }

    public int getIntValue(Gui4jComponentContainer gui4jComponentContainer, LElement e, String field, int defaultValue)
    {
        String str = gui4jComponentContainer.getAttrValue(e, field);
        if (str == null)
        {
            return defaultValue;
        }
        else
        {
            try
            {
                return Integer.parseInt(str);
            }
            catch (NumberFormatException ex)
            {
                Object[] args = { str };
                throw new Gui4jUncheckedException.ResourceError(gui4jComponentContainer.getConfigurationName(), e
                        .getLineNumber(), RESOURCE_ERROR_int_DataConversionException, args, ex);
            }
        }
    }

    public double getDoubleValue(Gui4jComponentContainer gui4jComponentContainer, LElement e, String field,
            double defaultValue)
    {
        String str = gui4jComponentContainer.getAttrValue(e, field);
        if (str == null)
        {
            return defaultValue;
        }
        else
        {
            try
            {
                return Double.parseDouble(str);
            }
            catch (NumberFormatException ex)
            {
                Object[] args = { str };
                throw new Gui4jUncheckedException.ResourceError(gui4jComponentContainer.getConfigurationName(), e
                        .getLineNumber(), RESOURCE_ERROR_double_DataConversionException, args, ex);
            }
        }
    }

    public String toString()
    {
        return getClass().getName();
    }

    public Gui4jCall getGui4jAccessInstance(Class expectedType, Gui4jComponent gui4jComponent, LElement e, String field)
    {
        String accessPath = gui4jComponent.getGui4jComponentContainer().getAttrValue(e, field);
        Gui4jCall gui4jAccess = mGui4jCallFactory.getInstance(gui4jComponent, e.getLineNumber(), accessPath);
        Gui4jTypeCheck.ensureType(expectedType, gui4jAccess, accessPath);
        return gui4jAccess;
    }

    /**
     * Creates a Gui4jCall from the method call declaration in an XML file. It
     * searches for a method provided by the controller of the gui4jComponent
     * that matches the method call declaration in the XML file.
     * 
     * @param expectedType
     *            The expected return type of the method call. Specify
     *            <code>null</code> if the return type doesn't matter, i.e.
     *            all return types are acceptable.
     * @param valueClassMap
     *            A map of parameter names to class objects. This is needed to
     *            search for the correct method of the controller if the
     *            declared method call contains parameters.
     * @param gui4jComponent
     *            The current Gui4jComponent
     * @param e
     *            The current XML element.
     * @param field
     *            The name of the XML attribute containing the method call
     *            declaration.
     * @return Gui4jCall
     */
    public Gui4jCall getGui4jAccessInstance(Class expectedType, Map valueClassMap, Gui4jComponent gui4jComponent,
            LElement e, String field)
    {
        String accessPath = gui4jComponent.getGui4jComponentContainer().getAttrValue(e, field);
        Gui4jCall gui4jAccess = mGui4jCallFactory.getInstance(gui4jComponent, e.getLineNumber(), valueClassMap,
                accessPath);
        Gui4jTypeCheck.ensureType(expectedType, gui4jAccess, accessPath);
        return gui4jAccess;
    }

    public Gui4jCall getGui4jAccessInstance(Class expectedType, Class valueType, Gui4jComponent gui4jComponent,
            LElement e, String field)
    {
        String accessPath = gui4jComponent.getGui4jComponentContainer().getAttrValue(e, field);
        Gui4jCall gui4jAccess = mGui4jCallFactory.getInstance(gui4jComponent, e.getLineNumber(), valueType, accessPath);
        Gui4jTypeCheck.ensureType(expectedType, gui4jAccess, accessPath);
        return gui4jAccess;
    }

    protected Gui4jInternal getGui4j()
    {
        return mGui4j;
    }

    protected int getMapValue(Gui4jComponentContainer gui4jComponentContainer, LElement e, String attrName,
            Map valueMap, int defaultValue)
    {
        String valueStr = gui4jComponentContainer.getAttrValue(e, attrName);
        if (valueStr != null)
        {
            Integer val = (Integer) valueMap.get(valueStr);
            return val != null ? val.intValue() : defaultValue;
        }
        else
        {
            return defaultValue;
        }
    }

    protected static Attribute[] attrList2Arr(List attrList)
    {
        return (Attribute[]) attrList.toArray(ATTRIBUTE_ARRAY_TEMPLATE);
    }

    protected static String constructAttributeType(Collection collection)
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append("(");
        for (Iterator iter = collection.iterator(); iter.hasNext();)
        {
            String type = (String) iter.next();
            buffer.append(type);
            buffer.append("|");
        }
        buffer.replace(buffer.length() - 1, buffer.length(), ")");
        return buffer.toString();
    }

    public static class SubElement
    {
        private final String mStr;

        public static SubElement empty()
        {
            return new SubElement("EMPTY");
        }

        public static SubElement gui4jComponent()
        {
            return new SubElement("(%" + ELEMENT_COMPONENT + ";|" + ELEMENT_GUI4JREF + ")");
        }

        public static SubElement gui4jRef()
        {
            return new SubElement(ELEMENT_GUI4JREF);
        }

        public static SubElement star(SubElement e)
        {
            return new SubElement("(" + e + "*" + ")");
        }

        public static SubElement plus(SubElement e)
        {
            return new SubElement("(" + e + "+" + ")");
        }

        public static SubElement once(SubElement e)
        {
            return new SubElement("(" + e + ")");
        }

        public static SubElement optional(SubElement e)
        {
            return new SubElement("(" + e + "?" + ")");
        }

        public static SubElement getInstance(String name)
        {
            return new SubElement(name);
        }

        public static SubElement or(SubElement[] subElement)
        {
            StringBuffer sb = new StringBuffer();
            sb.append('(');
            for (int i = 0; i < subElement.length; i++)
            {
                if (i > 0)
                {
                    sb.append('|');
                }
                sb.append(subElement[i]);
            }
            sb.append(')');
            return new SubElement(sb.toString());
        }

        public static SubElement seq(SubElement[] subElement)
        {
            StringBuffer sb = new StringBuffer();
            sb.append('(');
            for (int i = 0; i < subElement.length; i++)
            {
                if (i > 0)
                {
                    sb.append(',');
                }
                sb.append(subElement[i]);
            }
            sb.append(')');
            return new SubElement(sb.toString());
        }

        private SubElement(String str)
        {
            mStr = str;
        }

        public String toString()
        {
            return mStr;
        }
    }

    protected void checkErrorList(List errorList)
    {
        if (errorList.size() > 0)
        {
            for (Iterator it = errorList.iterator(); it.hasNext();)
            {
                Throwable t = (Throwable) it.next();
                if (!(t instanceof Gui4jUncheckedException.ErrorList))
                {
                    mLog.error(t, t);
                }
            }
            throw new Gui4jUncheckedException.ErrorList(errorList);
        }
    }

}