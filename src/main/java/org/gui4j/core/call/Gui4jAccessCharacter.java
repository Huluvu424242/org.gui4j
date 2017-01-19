package org.gui4j.core.call;

import java.util.Map;

import org.gui4j.exception.Gui4jUncheckedException;


final class Gui4jAccessCharacter extends Gui4jAccessImpl
{
    private final Character mValue;

    Gui4jAccessCharacter(ParseCtx parseCtx, String accessPath)
    {
        int startIndex = parseCtx.i;
        startIndex++; // accessPath.charAt(startIndex)='%'
        if (startIndex >= accessPath.length())
        {
            Object[] args = { accessPath, new Integer(startIndex)};
            throw new Gui4jUncheckedException.ResourceError(
                parseCtx.getConfigurationName(),
                parseCtx.getLineNumber(),
                RESOURCE_ERROR_access_unexpected_end,
                args);
        }

        mValue = new Character(accessPath.charAt(startIndex));
        parseCtx.i = startIndex + 1;
    }

    Class getResultClass()
    {
        return Character.TYPE;
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
