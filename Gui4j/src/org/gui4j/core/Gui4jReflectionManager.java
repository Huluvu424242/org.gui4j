package org.gui4j.core;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gui4j.core.util.MethodCall;
import org.gui4j.core.util.MethodCallReflection;
import org.gui4j.exception.ErrorTags;
import org.gui4j.exception.Gui4jUncheckedException;


/**
 * Supports methods to method declarations by most specific argument types
 * There is always one instance of this manager in order to cache
 * method declarations.
 * 
 * @author Joachim Schmid
 */
public final class Gui4jReflectionManager implements ErrorTags, Serializable
{
    private static final Log mLogger = LogFactory.getLog(Gui4jReflectionManager.class);
    private final Map mMethodsClass;
    private final Map mMethodsName;

    private Gui4jReflectionManager()
    {
        mMethodsClass = new HashMap();
        mMethodsName = new HashMap();
    }
    
    public void dispose()
    {
        mMethodsClass.clear();
        mMethodsName.clear();
    }

    /**
     * Returns always the same instance
     * @return Gui4jReflectionManager
    */
    public static Gui4jReflectionManager getNewInstance()
    {
        return new Gui4jReflectionManager();
    }

    public MethodCall getMethod(
        String context,
        Class c,
        String methodName,
        Class[] argumentsInit,
        boolean throwExceptionIfNotFound)
    {
        Method[] methods = getMethods(c, methodName);
        Class[] arguments = new Class[argumentsInit.length];
        String[] argumentsStr = new String[argumentsInit.length];
        for (int i = 0; i < argumentsInit.length; i++)
        {
            arguments[i] = argumentsInit[i];
            argumentsStr[i] = argumentsInit[i].getName();
            /*
            if (arguments[i].equals(Integer.TYPE))
            {
                arguments[i] = Integer.class;
            }
            */
        }

        Method lastMethod = null;
        for (int i = 0; i < methods.length; i++)
        {
            if (matching(methods[i], arguments))
            {
                if (lastMethod == null)
                {
                    lastMethod = methods[i];
                }
                else
                {
                    if (moreSpecific(methods[i], lastMethod))
                    {
                        lastMethod = methods[i];
                    }
                    else if (moreSpecific(lastMethod, methods[i]))
                    {
                        // do nothing
                    }
                    else
                    {
                        String signature = arr2List(argumentsStr).toString();
                        signature = signature.replace('[', '(');
                        signature = signature.replace(']', ')');
                        Object[] args = { c, methodName, signature };
                        mLogger.warn("Method ambiguous; context " + context);
                        mLogger.info("Method 1 = " + lastMethod);
                        mLogger.info("Method 2 = " + methods[i]);
                        throw new Gui4jUncheckedException.ProgrammingError(
                            PROGRAMMING_ERROR_method_ambiguous,
                            args);
                    }
                }
            }
        }
        if (lastMethod == null && throwExceptionIfNotFound)
        {
            String signature = arr2List(argumentsStr).toString();
            signature = signature.replace('[', '(');
            signature = signature.replace(']', ')');
            Object[] args = { c.getName(), methodName, signature, context };
            mLogger.warn("Method not defined; context " + context);
            throw new Gui4jUncheckedException.ProgrammingError(PROGRAMMING_ERROR_method_not_found, args);
        }

        /*
        if (methodName.indexOf("getValue")!=-1 && c.getName().indexOf("Ansprache")!=-1)
        {
            String signature = arr2List(argumentsStr).toString();
            signature = signature.replace('[', '(');
            signature = signature.replace(']', ')');
        
            mLogger.debug("Resolve of "+c.getName()+"."+methodName+signature+" = " +lastMethod);
        }
        */
        return MethodCallReflection.getInstance(lastMethod);
    }

    /**
     * Searches for the given class the most specific method. If no
     * method is found, or if the result is ambiguous, an exception is raised
     * @param context
     * @param c
     * @param methodName
     * @param argumentsInit
     * @return MethodCall
    */
    public MethodCall getMethod(String context, Class c, String methodName, Class[] argumentsInit)
    {
        return getMethod(context, c, methodName, argumentsInit, true);
    }

    private List arr2List(Object[] arr)
    {
        List l = new ArrayList();
        for (int i = 0; i < arr.length; i++)
        {
            l.add(arr[i]);
        }
        return l;
    }

    private boolean moreSpecific(Method m1, Method m2)
    {
        Class[] parameterTypes1 = m1.getParameterTypes();
        Class[] parameterTypes2 = m2.getParameterTypes();
        int n = parameterTypes1.length;
        for (int i = 0; i < n; i++)
        {
            if (!parameterTypes2[i].isAssignableFrom(parameterTypes1[i]))
            {
                return false;
            }
        }
        return true;
    }

    private boolean matching(Method m, Class[] arguments)
    {
        Class[] parameterTypes = m.getParameterTypes();
        if (parameterTypes.length != arguments.length)
        {
            return false;
        }
        for (int i = 0; i < parameterTypes.length; i++)
        {
            if (!parameterTypes[i].isAssignableFrom(arguments[i]))
                //				!arguments[i].isAssignableFrom(parameterTypes[i]))
            {
                return false;
            }
        }
        return true;
    }

    private synchronized Method[] getMethods(Class c, String methodName)
    {
        Method[] m = (Method[]) mMethodsName.get(c + "/" + methodName);
        if (m == null)
        {
            Method[] methods = getMethods(c);
            List methodList = new ArrayList();
            for (int i = 0; i < methods.length; i++)
            {
                if (methods[i].getName().equals(methodName))
                {
                    methodList.add(methods[i]);
                }
            }
            m = new Method[methodList.size()];
            for (int i = 0; i < m.length; i++)
            {
                m[i] = (Method) methodList.get(i);
            }
            mMethodsName.put(c + "/" + methodName, m);
        }
        return m;
    }

    private synchronized Method[] getMethods(Class c)
    {
        Method[] methods = (Method[]) mMethodsClass.get(c);
        if (methods == null)
        {
            methods = c.getMethods();
            /*
            for (int i = 0; i < methods.length; i++)
            {
                Method m = methods[i];
                try
                {
                    methods[i] = c.getMethod(m.getName(),m.getParameterTypes());
                    System.out.println("Class: "+c.getName()+": "+methods[i]);
                }
                catch (NoSuchMethodException e)
                {
                    String[] args = { c.getName(), m.getName(), m.toString() };
                    throw new Gui4jUncheckedException.ProgrammingError(PROGRAMMING_ERROR_method_not_found,args);
                }
            }
            */
            mMethodsClass.put(c, methods);
        }
        return methods;
    }

    /**
     * Clear the cache in order to use new method declarations
    */
    public synchronized void reload()
    {
        mMethodsClass.clear();
        mMethodsName.clear();
    }

    public static void handleInvocationTargetException(InvocationTargetException e)
    {
        Throwable t = e.getTargetException();
        if (t != null)
        {
            if (t instanceof RuntimeException)
            {
                throw (RuntimeException) t;
            }
            if (t instanceof Error)
            {
                throw (Error) t;
            }
        }
    }

}
