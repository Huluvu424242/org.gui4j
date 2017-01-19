package org.gui4j.core.call;

import java.util.Map;

import org.gui4j.exception.Gui4jUncheckedException;


final class Gui4jAccessConstant extends Gui4jAccessImpl
{
    private final String mStr;

    Gui4jAccessConstant(ParseCtx parseCtx, String accessPath)
    {
        int startIndex = parseCtx.i;
        startIndex++; // accessPath.charAt(startIndex)='\''
        int endIndex = accessPath.indexOf('\'', startIndex);
        if (endIndex == -1)
        {
            Object[] args = { accessPath, new Integer(startIndex)};
            throw new Gui4jUncheckedException.ResourceError(
                parseCtx.getConfigurationName(),
                parseCtx.getLineNumber(),
                RESOURCE_ERROR_access_unexpected_end,
                args);
        }
        mStr = accessPath.substring(startIndex, endIndex);
        parseCtx.i = endIndex + 1;
    }

    Class getResultClass()
    {
        return String.class;
    }

    public boolean isConstant()
    {
        return true;
    }

    public Object getValue(Object baseInstance, Object thisInstance, Map paramMap)
    {
        return mStr;
    }

}
