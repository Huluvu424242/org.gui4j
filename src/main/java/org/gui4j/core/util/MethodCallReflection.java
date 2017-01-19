package org.gui4j.core.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Entspricht der Klasse {@link java.lang.reflect.Method}, ausser
 * dass diese Klasse serialisierbar ist.
 */
final public class MethodCallReflection implements MethodCall
{
    private Method method;

    /**
     * Constructor for MethodCall.
     * @param method
     */
    private MethodCallReflection(Method method)
    {
        this.method = method;
    }
    
    public static MethodCall getInstance(Method method)
    {
        return method == null ? null : new MethodCallReflection(method);
    }
    
    public Object invoke(Object base, Object[] args) throws InvocationTargetException, IllegalAccessException
    {
        return method.invoke(base,args);
    }
    
    public int getModifiers()
    {
        return method.getModifiers();
    }    
    
    public Class getReturnType()
    {
        return method.getReturnType();
    }
    
    private void writeObject(ObjectOutputStream out) throws IOException
    {
        out.writeObject(method.getDeclaringClass());
        out.writeObject(method.getName());
        out.writeObject(method.getParameterTypes());
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        Class c = (Class)in.readObject();
        String name = (String)in.readObject();
        Class[] argTypes = (Class[])in.readObject();
        try
        {
            method = c.getMethod(name,argTypes);
        }
        catch (NoSuchMethodException e)
        {
            // should not happen
            e.printStackTrace();
            assert false;
        }
    }
	
	public String toString()
	{
		return method.toString();
	}

}
