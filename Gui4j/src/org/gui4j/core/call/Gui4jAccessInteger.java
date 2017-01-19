package org.gui4j.core.call;

import java.util.Map;

final class Gui4jAccessInteger extends Gui4jAccessImpl
{
    private final Integer mValue;

    Gui4jAccessInteger(ParseCtx parseCtx, String accessPath)
    {
        int startIndex = parseCtx.i;
        int endIndex = minIndex(accessPath, ";.),}", startIndex);

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
        mValue = Integer.valueOf(str);
    }

    Class getResultClass()
    {
        return Integer.TYPE;
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
