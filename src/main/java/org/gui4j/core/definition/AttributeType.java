package org.gui4j.core.definition;

public interface AttributeType
{
    String getDTDTypeDefinition();

    void accept(AttributeTypeVisitor visitor);
}