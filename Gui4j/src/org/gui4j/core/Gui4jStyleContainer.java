package org.gui4j.core;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Attribute;
import org.dom4j.LElement;

import org.gui4j.exception.ErrorTags;
import org.gui4j.exception.Gui4jUncheckedException;

public final class Gui4jStyleContainer implements Serializable, ErrorTags
{
    private final Map mStyleMap;
    private final Map mStyleMapResourceName;
    private final String mConfigurationName;
    private final String NOTHING = "Nothing";
    private final String DEFAULT = "Default";

    public Gui4jStyleContainer(String configurationName)
    {
        mStyleMap = new HashMap();
        mStyleMapResourceName = new HashMap();
        mConfigurationName = configurationName;
    }

    /**
     * @param resourceName 
     * @param styleName name of style
     * @param styleExtends name of style to be extended
    */
    public void createStyle(String resourceName, String styleName, String styleExtends)
    {
        Map style = getStyle(styleName, false);
        if (style != null && !resourceName.equals(mStyleMapResourceName.get(styleName)))
        {
            Object[] args = { styleName };
            throw new Gui4jUncheckedException.ResourceError(
                mConfigurationName,
                -1,
                RESOURCE_ERROR_style_defined_twice,
                args);
        }

        style = new HashMap();
        mStyleMap.put(styleName, style);
        mStyleMapResourceName.put(styleName, resourceName);

        if (styleExtends != null)
        {
            Map styleE = getStyle(styleExtends, true);
            for (Iterator it = styleE.entrySet().iterator(); it.hasNext();)
            {
                Map.Entry entry = (Map.Entry) it.next();
                String elementName = (String) entry.getKey();
                Map attributes = (Map) entry.getValue();
                style.put(elementName, attributes);
            }
        }
    }

    private Map getStyle(String styleName, boolean checkExistent)
    {
        Map style = (Map) mStyleMap.get(styleName);
        if (checkExistent && style == null)
        {
            Object[] args = { styleName };
            throw new Gui4jUncheckedException.ResourceError(
                mConfigurationName,
                -1,
                RESOURCE_ERROR_style_not_defined,
                args);
        }
        return style;
    }

    public void addAttributes(String elementName, String styleName, List attributes)
    {
        Map style = getStyle(styleName, true);
        Map attrs = (Map) style.get(elementName);
        if (attrs != null)
        {
            Map oldAttrs = attrs;
            attrs = new HashMap();
            style.put(elementName, attrs);
            for (Iterator it = oldAttrs.entrySet().iterator(); it.hasNext();)
            {
                Map.Entry entry = (Map.Entry) it.next();
                String name = (String) entry.getKey();
                String value = (String) entry.getValue();
                attrs.put(name, value);
            }
        }
        else
        {
            attrs = new HashMap();
            style.put(elementName, attrs);
        }

        for (Iterator it = attributes.iterator(); it.hasNext();)
        {
            Attribute attr = (Attribute) it.next();
            attrs.put(attr.getName(), attr.getValue());
        }
    }

    /**
     * Copies all defined styles of <code>gui4jStyleContainer</code>
     * If such a style is already defined, an exception is thrown.
     * @param gui4jStyleContainer
    */
    public void extendBy(Gui4jStyleContainer gui4jStyleContainer)
    {
        for (Iterator it = gui4jStyleContainer.mStyleMap.entrySet().iterator(); it.hasNext();)
        {
            Map.Entry entry = (Map.Entry) it.next();
            String styleName = (String) entry.getKey();
            Map style = (Map) entry.getValue();

            Map thisStyle = getStyle(styleName, false);

            if (thisStyle != null
                && !mStyleMapResourceName.get(styleName).equals(
                    gui4jStyleContainer.mStyleMapResourceName.get(styleName)))
            {
                Object[] args = { styleName };
                throw new Gui4jUncheckedException.ResourceError(
                    mConfigurationName,
                    -1,
                    RESOURCE_ERROR_style_defined_twice,
                    args);
            }

            mStyleMap.put(styleName, style);
            mStyleMapResourceName.put(styleName,gui4jStyleContainer.mStyleMapResourceName.get(styleName));
        }
    }

    /**
     * Element <code>e</code> represents a <code>Gui4jComponent</code>. The method
     * inserts all attributes in that instance defined by this style which are not defined by
     * the element
     * @param e
    */
    public void extend(LElement e)
    {
        String styleName = e.attributeValue(Gui4jComponentFactory.FIELD_Style);
        if (styleName != null && styleName.equals(NOTHING))
        {
            return;
        }
        String name = e.getName();
        Map attrs = null;
        if (styleName != null)
        {
            Map style = getStyle(styleName, true);
            attrs = (Map) style.get(name);
            if (attrs == null)
            {
                // maybe we should not throw an exception
                // there is no problem if there are no attributes for this element

                /*
                throw new UncheckedGui4jException(
                	mConfigurationSource.toString()
                		+ ": style with name "
                		+ styleName
                		+ " for element "
                		+ name
                		+ " not defined");
                */
            }
        }
        else
        {
            Map style = getStyle(DEFAULT, false);
            if (style != null)
            {
                attrs = (Map) style.get(name);
            }
        }
        if (attrs != null)
        {
            Set names = new HashSet();
            List attrList = e.attributes();
            for (Iterator it = attrList.iterator(); it.hasNext();)
            {
                Attribute attr = (Attribute) it.next();
                names.add(attr.getName());
            }

            boolean modified = false;
            for (Iterator it = attrs.entrySet().iterator(); it.hasNext();)
            {
                Map.Entry entry = (Map.Entry) it.next();
                if (!names.contains(entry.getKey()))
                {
                    e.addAttribute((String) entry.getKey(), (String) entry.getValue());
                    /*
                    attrList.add(
                    	new Attribute(
                    		(String) entry.getKey(),
                    		(String) entry.getValue()));
                    		*/
                    modified = true;
                }
            }

            if (modified)
            {
                // e.setAttributes(attrList);
            }
        }
        e.addAttribute(Gui4jComponentFactory.FIELD_Style, NOTHING);
    }

}
