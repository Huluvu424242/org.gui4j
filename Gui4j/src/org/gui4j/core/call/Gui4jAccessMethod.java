package org.gui4j.core.call;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.gui4j.core.Gui4jReflectionManager;
import org.gui4j.core.util.FieldCall;
import org.gui4j.core.util.FieldCallReflection;
import org.gui4j.core.util.MethodCall;
import org.gui4j.exception.Gui4jUncheckedException;


final class Gui4jAccessMethod extends Gui4jAccessImpl
{
    static final Log mLogger = LogFactory.getLog(Gui4jAccessMethod.class);

    private final Gui4jAccessImpl[] mParameter;
    private MethodCall mMethod;
    private FieldCall mField;
    private final Gui4jAccessImpl mThisAccess;
    private final boolean mLogInvoke;

    Gui4jAccessMethod(ParseCtx parseCtx, Gui4jAccessImpl thisAccess, String accessPath)
    {
        mThisAccess = thisAccess;
        mLogInvoke = parseCtx.getGui4jComponentContainer().getGui4j().logInvoke();
        int len = accessPath.length();
        int startIndex = parseCtx.i;
        int lParenIndex = accessPath.indexOf('(', startIndex);
        int endIndex = minIndex(accessPath, ".,;)}", startIndex);
        if (endIndex == -1)
        {
            endIndex = accessPath.length();
        }

        Gui4jAccessImpl[] parameter = new Gui4jAccessImpl[0];
        boolean parenthesisUsed;
        String methodName;
        if (lParenIndex != -1 && lParenIndex < endIndex)
        {
            // parameters are provided
            parenthesisUsed = true;
            methodName = accessPath.substring(startIndex, lParenIndex);
            parseCtx.i = lParenIndex + 1;
            List parameterList = new ArrayList();
            while (true)
            {
                Gui4jCallParser.skipSpaces(parseCtx, len);
                if (parseCtx.i >= len)
                {
                    Object[] args = { accessPath, new Integer(parseCtx.i)};
                    throw new Gui4jUncheckedException.ResourceError(
                        parseCtx.getConfigurationName(),
                        parseCtx.getLineNumber(),
                        RESOURCE_ERROR_access_unexpected_character,
                        args);
                }
                if (accessPath.charAt(parseCtx.i) == ',')
                {
                    parseCtx.i = parseCtx.i + 1;
                    continue;
                }

                if (accessPath.charAt(parseCtx.i) == ')')
                {
                    parseCtx.i = parseCtx.i + 1;
                    parameter = new Gui4jAccessImpl[parameterList.size()];
                    for (int i = 0; i < parameter.length; i++)
                    {
                        parameter[i] = (Gui4jAccessImpl) parameterList.get(i);
                    }
                    break;
                }

                // Parameter expected
                Gui4jAccessImpl gui4jAccess = parseCtx.getGui4jCallParser().parseCallSequence(parseCtx, accessPath);
                /*
                    getInstance(
                        parseCtx,
                        parseCtx.getBaseAccess(),
                        accessPath);
                        */
                parameterList.add(gui4jAccess);
            }
        }
        else
        {
            // no parameters
            parenthesisUsed = false;
            methodName = accessPath.substring(startIndex, endIndex);
            parseCtx.i = endIndex;
        }

        // lookup method
        mParameter = parameter;
        if (!parenthesisUsed)
        {
            // try to access field
            try
            {
                mField = FieldCallReflection.getField(thisAccess.getResultClass(), methodName);
            }
            catch (NoSuchFieldException e)
            {
                // simply ignore, because we ar trying to find a corresponding method
            }
        }
        if (mField == null)
        {
            Class[] arguments = new Class[parameter.length];
            for (int i = 0; i < parameter.length; i++)
            {
                arguments[i] = parameter[i].getResultClass();
            }
            String context = parseCtx.getConfigurationName();
            {
                int lineNumber = parseCtx.getLineNumber();
                if (lineNumber != -1)
                {
                    context = context + "(:" + lineNumber+")";
                }
            }
            mMethod =
                parseCtx.getGui4jReflectionManager().getMethod(
                    context,
                    thisAccess.getResultClass(),
                    methodName,
                    arguments);
        }
        else
        {
            mMethod = null;
        }
    }

    public Class getResultClass()
    {
        if (mField != null)
        {
            return mField.getType();
        }
        else
        {
            return mMethod.getReturnType();
        }
    }

    public boolean isConstant()
    {
        return false;
    }

    public Object getValue(Object baseInstance, Object orgThisInstance, Map paramMap)
    {
        Object thisInstance = mThisAccess.getValue(baseInstance, orgThisInstance, paramMap);
        if (mField != null)
        {
            try
            {
                Object result = mField.get(thisInstance);
                return result;
            }
            catch (IllegalAccessException e)
            {
                throw new Gui4jUncheckedException.ProgrammingError(
                    PROGRAMMING_ERROR_illegal_access_exception,
                    e);
            }
        }
        else
        {
            Object[] argumentValue = mParameter.length == 0 ? null : new Object[mParameter.length];
            for (int i = 0; i < mParameter.length; i++)
            {
                argumentValue[i] = mParameter[i].getValue(baseInstance, thisInstance, paramMap);
            }
            try
            {
                if (mLogInvoke)
                {
                    List l = new ArrayList();
                    if (argumentValue != null)
                    {
                        l.addAll(Arrays.asList(argumentValue));
                    }
                    mLogger.debug(
                        "Invoking: " + mMethod + ", thisInstance = " + thisInstance + ", arguments=" + l);
                }
                boolean ok = thisInstance != null || ((mMethod.getModifiers() & Modifier.STATIC) != 0);
                if (!ok)
                {
                    List l = new ArrayList();
                    if (argumentValue != null)
                    {
                        l.addAll(Arrays.asList(argumentValue));
                    }
                    mLogger.warn(
                        "Invoking non-static method " + mMethod + " with null instance and arguments " + l);
                }
                assert ok;
                Object result = null;
                try
                {
                    result = mMethod.invoke(thisInstance, argumentValue);
                }
                catch (IllegalArgumentException e)
                {
                    List l = new ArrayList();
                    if (argumentValue != null)
                    {
						l.addAll(Arrays.asList(argumentValue));
                    }
                    mLogger.debug(
                        "Invocation: "
                            + mMethod
                            + ", thisInstance = "
                            + thisInstance
                            + ", arguments="
                            + l
                            + " failed");
                    throw e;
                }
                if (mLogInvoke)
                {
                    List l = new ArrayList();
                    if (argumentValue != null)
                    {
						l.addAll(Arrays.asList(argumentValue));
                    }
                    mLogger.debug(
                        "Result of Invocation: "
                            + mMethod
                            + ", thisInstance = "
                            + thisInstance
                            + ", arguments="
                            + l
                            + ", result = "
                            + result);
                }
                return result;
            }
            catch (IllegalAccessException e)
            {
                mLogger.warn(e.getMessage() + mMethod);
                throw new Gui4jUncheckedException.ProgrammingError(
                    PROGRAMMING_ERROR_illegal_access_exception,
                    e);
            }
            catch (InvocationTargetException e)
            {
                // mLogger.warn(e.getTargetException().getMessage() + mMethod, e.getTargetException());
                Gui4jReflectionManager.handleInvocationTargetException(e);
                throw new Gui4jUncheckedException.ProgrammingError(
                    PROGRAMMING_ERROR_invocation_target_exception,
                    e);
            }
        }
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return mMethod.toString();
    }

}
