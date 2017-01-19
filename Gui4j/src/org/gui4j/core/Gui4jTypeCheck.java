package org.gui4j.core;

import org.gui4j.exception.ErrorTags;
import org.gui4j.exception.Gui4jUncheckedException;

public final class Gui4jTypeCheck implements ErrorTags
{
    public static void ensureType(Class expectedType, Gui4jCall gui4jAccess)
    {
        ensureType(expectedType, gui4jAccess, null);
    }

    public static void ensureType(Class expectedType, Gui4jCall gui4jAccess, String accessPath)
    {
        if (expectedType != null && gui4jAccess != null)
        {
            Class resultClass = gui4jAccess.getResultClass();
            ensureType(resultClass, expectedType, gui4jAccess.getConfigurationName(), accessPath);
        }
    }

    public static void ensureType(Class resultClass, Class expectedType, String configurationName, String accessPath)
    {
        boolean valid = false;
        if (resultClass != null)
        {
            if (resultClass.isPrimitive())
            {
                valid |= resultClass == expectedType;
                /*
                 * valid |= resultClass.isAssignableFrom(Integer.TYPE) &&
                 * expectedType.isAssignableFrom(Integer.class); valid |=
                 * resultClass.isAssignableFrom(Boolean.TYPE) &&
                 * expectedType.isAssignableFrom(Boolean.class); valid |=
                 * resultClass.isAssignableFrom(Character.TYPE) &&
                 * expectedType.isAssignableFrom(Character.class);
                 */
            }
            else
            {
                valid = expectedType.isAssignableFrom(resultClass);
            }
        }
        if (!valid)
        {
            if (accessPath != null)
            {
                Object[] args = { resultClass, expectedType, accessPath };
                throw new Gui4jUncheckedException.ResourceError(configurationName, -1,
                        RESOURCE_ERROR_access_type_not_compatible, args);
            }
            else
            {
                Object[] args = { resultClass, expectedType };
                throw new Gui4jUncheckedException.ResourceError(configurationName, -1,
                        RESOURCE_ERROR_type_not_compatible, args);
            }
        }
    }

}
