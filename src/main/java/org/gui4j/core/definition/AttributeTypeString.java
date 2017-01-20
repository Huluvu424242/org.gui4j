package org.gui4j.core.definition;


public class AttributeTypeString implements AttributeType
{
    public String getDTDTypeDefinition()
    {
        return DTDWriter.CDATA;
    }

    public void accept(AttributeTypeVisitor visitor)
    {
        visitor.visit(this);
    }    
}