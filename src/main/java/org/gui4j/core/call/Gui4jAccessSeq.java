package org.gui4j.core.call;

import java.util.Map;

final class Gui4jAccessSeq extends Gui4jAccessImpl
{
    private final Gui4jAccessImpl mFirst;
    private final Gui4jAccessImpl mSecond;

    Gui4jAccessSeq(Gui4jAccessImpl first, Gui4jAccessImpl second)
    {
        mFirst = first;
        mSecond = second;
    }

    Class getResultClass()
    {
        return mSecond.getResultClass();
    }

    public boolean isConstant()
    {
        return mFirst.isConstant() && mSecond.isConstant();
    }

    public Object getValue(Object baseInstance, Object thisInstance, Map paramMap)
    {
        mFirst.getValue(baseInstance, thisInstance, paramMap);
        return mSecond.getValue(baseInstance, thisInstance, paramMap);
    }

}
