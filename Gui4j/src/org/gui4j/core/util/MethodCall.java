package org.gui4j.core.util;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

public interface MethodCall extends Serializable
{
    Object invoke(Object base, Object[] args) throws InvocationTargetException, IllegalAccessException;
    
    int getModifiers();
    
    Class getReturnType();
}
