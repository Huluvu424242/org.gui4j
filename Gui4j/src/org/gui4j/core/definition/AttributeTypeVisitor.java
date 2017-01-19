package org.gui4j.core.definition;

public interface AttributeTypeVisitor
{
    void visit(AttributeTypeAlias type);
    void visit(AttributeTypeEnumeration type);
    void visit(AttributeTypeFloatingPoint type);
    void visit(AttributeTypeID type);
    void visit(AttributeTypeInteger type);
    void visit(AttributeTypeMethodCall type);
    void visit(AttributeTypeString type);
    
}
