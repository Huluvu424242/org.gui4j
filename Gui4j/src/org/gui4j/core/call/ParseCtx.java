package org.gui4j.core.call;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.gui4j.core.Gui4jComponent;
import org.gui4j.core.Gui4jComponentContainer;
import org.gui4j.core.Gui4jReflectionManager;

final class ParseCtx implements Serializable
{
    int i;
    private final Gui4jComponent mGui4jComponent;
    private final Gui4jComponentContainer mGui4jComponentContainer;
    private final Gui4jReflectionManager mGui4jReflectionManager;
    private final Map mValueClassMap;
    private final Gui4jAccessImpl mBaseAccess;
    private final Gui4jCallParser mParser;
    private final int mLineNumber;
    final String mAccessPath;
    private Set mUsedParams;

    ParseCtx(Gui4jCallParser parser, Gui4jComponent gui4jComponent, int lineNumber, Map valueClassMap,
            Gui4jAccessImpl baseAccess, String accessPath)
    {
        mParser = parser;
        mGui4jComponent = gui4jComponent;
        mGui4jComponentContainer = gui4jComponent.getGui4jComponentContainer();
        mValueClassMap = valueClassMap;
        mBaseAccess = baseAccess;
        mGui4jReflectionManager = mGui4jComponentContainer.getGui4j().getGui4jReflectionManager();
        mAccessPath = accessPath;
        mLineNumber = lineNumber;
        mUsedParams = new HashSet();
    }

    Class getBaseClass()
    {
        return mGui4jComponentContainer.getGui4jControllerClass();
    }

    Class getValueClass(String paramName)
    {
        return mValueClassMap == null ? null : (Class) mValueClassMap.get(paramName);
    }

    Gui4jAccessImpl getBaseAccess()
    {
        return mBaseAccess;
    }

    Class getClassForAliasName(String aliasName)
    {
        return mGui4jComponentContainer.getClassForAliasName(aliasName);
    }

    Gui4jReflectionManager getGui4jReflectionManager()
    {
        return mGui4jReflectionManager;
    }

    Gui4jComponentContainer getGui4jComponentContainer()
    {
        return mGui4jComponentContainer;
    }

    Gui4jComponent getGui4jComponent()
    {
        return mGui4jComponent;
    }

    String getConfigurationName()
    {
        return getGui4jComponentContainer().getConfigurationName();
    }

    int getLineNumber()
    {
        return mLineNumber;
    }

    String getAccessPath()
    {
        return mAccessPath;
    }

    Gui4jCallParser getGui4jCallParser()
    {
        return mParser;
    }

    /**
     * @return
     */
    public Set getUsedParams()
    {
        return mUsedParams;
    }

    public void addUsedParam(String paramName)
    {
        mUsedParams.add(paramName);
    }

}