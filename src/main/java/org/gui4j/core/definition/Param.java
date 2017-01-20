package org.gui4j.core.definition;

public class Param
{
    private final String name;
    private final Class type;
    private final String typeOrigin;

    public Param(String name)
    {
        this.name = name;
        this.type = null;
        this.typeOrigin = null;
    }

    public Param(String name, Class type)
    {
        this.name = name;
        this.type = type;
        this.typeOrigin = null;
    }

    public Param(String name, String typeOrigin)
    {
        this.name = name;
        this.typeOrigin = typeOrigin;
        this.type = null;
    }

    public String getName()
    {
        return name;
    }

    public String getType()
    {
        return getTypeDescription(type);
    }

    public String getTypeOrigin()
    {
        return typeOrigin;
    }
    
    public static String getTypeDescription(Class type) {
        if (type == null) {
            return null;
        }
        if (type.isArray()) {
            return type.getComponentType().getName() + "[]";
        }
        return type.getName();        
    }
}