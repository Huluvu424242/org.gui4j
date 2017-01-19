package org.gui4j.core.util;

/**
 * Utility-Klasse für verschiedene Extraktionsfunktionen
 */
public class Extract
{

    /**
     * Constructor for Extract.
     */
    private Extract()
    {
        super();
    }

    public static String getClassname(Class c)
    {
        String className = c.getName();
        int idx = className.lastIndexOf('.');
        if (idx!=-1)
        {
            return className.substring(idx+1);
        }
        else
        {
            return className;
        }
    }
}
