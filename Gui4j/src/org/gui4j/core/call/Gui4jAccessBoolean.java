package org.gui4j.core.call;

import java.util.Map;

import org.gui4j.exception.Gui4jUncheckedException;


final class Gui4jAccessBoolean extends Gui4jAccessImpl
{
    private final Boolean mValue;

    Gui4jAccessBoolean(ParseCtx parseCtx, String accessPath)
    {
        int startIndex = parseCtx.i;
        startIndex++; // accessPath.charAt(startIndex)='~'
        if (startIndex >= accessPath.length())
        {
            Object[] args = { accessPath, new Integer(startIndex)};
            throw new Gui4jUncheckedException.ResourceError(
                parseCtx.getConfigurationName(),
                parseCtx.getLineNumber(),
                RESOURCE_ERROR_access_unexpected_end,
                args);
        }

        switch (accessPath.charAt(startIndex))
        {
            case 't' :
            case 'T' :
                mValue = Boolean.TRUE;
                break;
            case 'f' :
            case 'F' :
                mValue = Boolean.FALSE;
                break;
            default :
                {
                    Object[] args = { accessPath, new Integer(startIndex)};
                    throw new Gui4jUncheckedException.ResourceError(
                        parseCtx.getConfigurationName(),
                        parseCtx.getLineNumber(),
                        RESOURCE_ERROR_access_unexpected_character,
                        args);
                }
        }
        parseCtx.i = startIndex + 1;
    }

    Class getResultClass()
    {
        return Boolean.TYPE;
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
