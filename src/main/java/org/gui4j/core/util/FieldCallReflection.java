package org.gui4j.core.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;

/**
 * Entspricht der Klasse {@link java.lang.reflect.Field}, ausser
 * dass diese Klasse serialisierbar ist.
 */
final public class FieldCallReflection implements FieldCall
{
    private Field field;

    /**
     * Constructor for FieldCallReflection.
     * @param field
     */
    private FieldCallReflection(Field field)
    {
        this.field = field;
    }

    public static FieldCall getInstance(Field field)
    {
        return field == null ? null : new FieldCallReflection(field);
    }

    public Class getType()
    {
        return field.getType();
    }

    public static FieldCall getField(Class c, String fieldName) throws NoSuchFieldException
    {
        Field field = c.getField(fieldName);
        return field == null ? null : new FieldCallReflection(field);
    }

    private void writeObject(ObjectOutputStream out) throws IOException
    {
        out.writeObject(field.getDeclaringClass());
        out.writeObject(field.getName());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        Class c = (Class) in.readObject();
        String fieldName = (String) in.readObject();
        try
        {
            field = c.getField(fieldName);
        }
        catch (NoSuchFieldException e)
        {
            // should not happen
            assert false;
        }
    }

    public Object get(Object base) throws IllegalAccessException
    {
        try
        {
            return field.get(base);
        }
        catch (IllegalArgumentException e)
        {
        	System.err.println("Field = "+field.getName()+" base = "+base);
            throw e;
        }
    }

}
