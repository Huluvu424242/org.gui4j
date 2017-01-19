package org.gui4j.core.definition;

public class AttributeTypeAlias implements AttributeType
{

    public AttributeTypeAlias()
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