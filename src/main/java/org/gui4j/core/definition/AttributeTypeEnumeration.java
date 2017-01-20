package org.gui4j.core.definition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;



public class AttributeTypeEnumeration implements DefaultValueAttributeType,
        ParameterAttributeType
{
    final private List params; // contains instances of Param
    final private String defaultValue;

    public AttributeTypeEnumeration(Collection params)
    {
        this(params, null);
    }

    public AttributeTypeEnumeration(Collection params, String defaultValue)
    {
        this.params = new ArrayList(params);
        this.defaultValue = defaultValue;
    }

    public AttributeTypeEnumeration(Map mappingNameInt)
    {
        this(mappingNameInt, null);
    }

    public AttributeTypeEnumeration(Map mappingNameInt, String defaultValue)
    {
        this.params = new ArrayList();
        for (Iterator it = mappingNameInt.keySet().iterator(); it.hasNext();)
        {
            String name = (String) it.next();
            this.params.add(new Param(name));
        }
        this.defaultValue = defaultValue;
    }

    public static AttributeTypeEnumeration getBooleanInstance()
    {
        List l = new ArrayList();
        l.add(new Param("true"));
        l.add(new Param("false"));
        return new AttributeTypeEnumeration(l);
    }

    public static AttributeTypeEnumeration getBooleanInstance(boolean dflt)
    {
        List l = new ArrayList();
        l.add(new Param("true"));
        l.add(new Param("false"));
        return new AttributeTypeEnumeration(l, dflt ? "true" : "false");
    }

    public String getDTDTypeDefinition()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("(");
        for (Iterator it = params.iterator(); it.hasNext();)
        {
            String type = ((Param) it.next()).getName();
            sb.append(type);
            sb.append("|");
        }
        if (params.size() > 0)
        {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append(")");
        return sb.toString();
    }

    public Collection getParams()
    {
        return params;
    }

    public String getDefaultValue()
    {
        return defaultValue;
    }

    public String getParamDescription()
    {
        return "Allowed values";
    }

    public String getParamPrefix()
    {
        return "";
    }
    
    public void accept(AttributeTypeVisitor visitor)
    {
        visitor.visit(this);
    }    
}