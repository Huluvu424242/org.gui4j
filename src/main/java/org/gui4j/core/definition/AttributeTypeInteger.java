package org.gui4j.core.definition;

public class AttributeTypeInteger implements DefaultValueAttributeType
{
    private final String defaultValue;

    public AttributeTypeInteger()
    {
        this.defaultValue = null;
    }

    public AttributeTypeInteger(int defaultValue)
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