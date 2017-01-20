package org.gui4j.core.definition;


public class AttributeTypeFloatingPoint implements DefaultValueAttributeType
{
    private final String defaultValue;

    public AttributeTypeFloatingPoint()
    {
        this.defaultValue = null;
    }

    public AttributeTypeFloatingPoint(double defaultValue)
    {
        this.defaultValue = "" + defaultValue;
    }

    public String getDTDTypeDefinition()
    {
        return DTDWriter.CDATA;
    }

    public String getDefaultValue()
    {
        return defaultValue;
    }
    
    public void accept(AttributeTypeVisitor visitor)
    {
        visitor.visit(this);
    }    
}