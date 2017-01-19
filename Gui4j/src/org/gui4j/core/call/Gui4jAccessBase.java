package org.gui4j.core.call;

import java.util.Map;

final class Gui4jAccessBase extends Gui4jAccessImpl
{
    private final Class mBaseClass;

    Gui4jAccessBase(Class gui4jControllerClass)
    {
        mBaseClass = gui4jControllerClass;
    }

    Class getResultClass()
    {
        return mBaseClass;
    }

    public boolean isConstant()
    {
        return false;
    }

    public Object getValue(Object baseInstance, Object thisInstance, Map paramMap)
    {
        return baseInstance;
    }

}
