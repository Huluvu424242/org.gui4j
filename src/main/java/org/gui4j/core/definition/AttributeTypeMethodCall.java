package org.gui4j.core.definition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AttributeTypeMethodCall implements AttributeType, ParameterAttributeType
{
    final private List params; // contains instances of Param
    final private Class returnType;
    final private boolean eventAware;

    public AttributeTypeMethodCall(Class returnType)
    {
        this(returnType, new ArrayList(), false);
    }

    public AttributeTypeMethodCall(Class returnType, boolean eventAware)
    {
        this(returnType, new ArrayList(), eventAware);
    }

    public AttributeTypeMethodCall(Class returnType, Collection params)
    {
        this(returnType, params, false);
    }

    public AttributeTypeMethodCall(Class returnType, Collection params, boolean eventAware)
    {
        this.returnType = returnType == null ? void.class : returnType;
        this.params = new ArrayList(params);
        this.eventAware = eventAware;
    }

    public String getDTDTypeDefinition()
    {
        return DTDWriter.CDATA;
    }

    public String getReturnType()
    {
        return Param.getTypeDescription(returnType);
    }

    public boolean isEventAware()
    {
        return eventAware;
    }

    public Collection getParams()
    {
        return params;
    }

    public String getParamDescription()
    {
        return "Allowed parameters in method call";
    }

    public String getParamPrefix()
    {
        return "?";
    }
    
    public void accept(AttributeTypeVisitor visitor)
    {
        visitor.visit(this);
    }    
}