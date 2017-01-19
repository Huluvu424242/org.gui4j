package org.gui4j.core;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class Gui4jTypeManager implements Serializable
{
    private final Map typeMap;

    public Gui4jTypeManager()
    {
        typeMap = new HashMap();
    }

    public Gui4jTypeManager(Map m)
    {
        typeMap = new HashMap();
        add(m);
    }
    
    public int size()
    {
        return typeMap.size();
    }

    public void add(Map map)
    {
        for (Iterator it = map.entrySet().iterator(); it.hasNext();)
        {
            Map.Entry entry = (Map.Entry) it.next();
            Class c = (Class) entry.getKey();
            Object instance = entry.getValue();
            add(c, instance);
        }
    }

    public void add(Class type, Object instance)
    {
        assert !typeMap.containsKey(type);
        // XXX: JS->Kay ;-) ---  Ordentliche Fehlermeldung erzeugen
        typeMap.put(type, instance);
    }

    public Object getExact(Class classType)
    {
        return typeMap.get(classType);
    }

    public Object get(Class classType)
    {
        Class lastClass = null;
        Object lastObject = null;
        for (Iterator it = typeMap.entrySet().iterator(); it.hasNext();)
        {
            Map.Entry entry = (Map.Entry) it.next();
            Class c = (Class) entry.getKey();
            Object object = entry.getValue();
            if (c.isAssignableFrom(classType))
            {
                if (lastClass == null || lastClass.isAssignableFrom(c))
                {
                    lastClass = c;
                    lastObject = object;
                }
            }
        }
        return lastObject;
    }
}
