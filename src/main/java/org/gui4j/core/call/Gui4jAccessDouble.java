package org.gui4j.core.call;

import java.util.Map;

final class Gui4jAccessDouble extends Gui4jAccessImpl
{
    private final Double mValue;

    Gui4jAccessDouble(ParseCtx parseCtx, String accessPath)
    {
        int startIndex = parseCtx.i;
        int endIndex = minIndex(accessPath, ";),}", startIndex);

        String str;
        if (endIndex != -1)
        {
            str = accessPath.substring(startIndex, endIndex);
            parseCtx.i = endIndex;
        }
        else
        {
            str = accessPath.substring(startIndex);
            parseCtx.i = accessPath.length();
        }
        mValue = Double.valueOf(str);
    }

    Class getResultClass()
    {
        return Double.TYPE;
    }

    public boolean isConstant()
    {
        return true;
    }

    public Object getValue(Object baseInstance, Object thisInstance, Map paramMap)
    {
        return mValue;
    }

}
