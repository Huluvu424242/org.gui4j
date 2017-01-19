package org.gui4j.core.call;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gui4j.exception.Gui4jUncheckedException;


final class Gui4jAccessSetValue extends Gui4jAccessImpl
{
    static final Log mLogger = LogFactory.getLog(Gui4jAccessSetValue.class);

    private final ParseCtx mParseCtx;
    private final String mParamName;

    Gui4jAccessSetValue(ParseCtx parseCtx, String accessPath)
    {
        mParseCtx = parseCtx;
        // accessPath.charAt(parsePosition.i) = '?'
        parseCtx.i = parseCtx.i + 1;
        int endIdx = minIndex(accessPath, ".,;)}", parseCtx.i);
        if (endIdx == -1)
        {
            mParamName = accessPath.substring(parseCtx.i);
            parseCtx.i = accessPath.length();
        }
        else
        {
            mParamName = accessPath.substring(parseCtx.i, endIdx);
            parseCtx.i = endIdx;
        }
        if (parseCtx.getValueClass(mParamName) == null)
        {
            Object[] args = { mParamName, accessPath };
            throw new Gui4jUncheckedException.ResourceError(
                parseCtx.getConfigurationName(),
                parseCtx.getLineNumber(),
                RESOURCE_ERROR_access_value_type_not_defined,
                args);
        }
        parseCtx.addUsedParam(mParamName);
    }

    Class getResultClass()
    {
        Class c = mParseCtx.getValueClass(mParamName);
        return c;
    }

    public boolean isConstant()
    {
        return false;
    }

    public Object getValue(Object baseInstance, Object thisInstance, Map paramMap)
    {
        Object value = null;
        if (paramMap != null)
        {
            value = paramMap.get(mParamName);
        }
        if (value == null && !paramMap.containsKey(mParamName))
        {
            mLogger.warn(
                "Parameter "
                    + mParamName
                    + " is not defined in paramMap, accessPath="
                    + mParseCtx.getAccessPath());
        }
        return value;
    }

}
