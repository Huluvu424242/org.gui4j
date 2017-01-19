package org.gui4j.core.definition;

public class AttributeTypeID implements AttributeType
{
    public AttributeTypeID()
    {
    }

    public String getDTDTypeDefinition()
    {
        return DTDWriter.CDATA;
    }

    public void accept(AttributeTypeVisitor visitor)
    {
        visitor.visit(this);
    }
}