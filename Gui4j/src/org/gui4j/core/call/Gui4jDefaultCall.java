package org.gui4j.core.call;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gui4j.Gui4jCallBase;
import org.gui4j.core.Gui4jCall;
import org.gui4j.core.Gui4jComponent;
import org.gui4j.core.Gui4jComponentInstance;
import org.gui4j.core.Gui4jMap1;
import org.gui4j.exception.ErrorTags;

final class Gui4jDefaultCall implements ErrorTags, Serializable, Gui4jCall
{
    private final Gui4jComponent mGui4jComponent;
    private final Gui4jAccessImpl mGui4jAccessImpl;
    private final List mDependantProperties;
    private final Map mValueClassMap;
    private final Set mUsedParams;

    Gui4jDefaultCall(Gui4jComponent gui4jComponent, Gui4jAccessImpl gui4jAccessImpl, List dependantProperties,
            Map valueClassMap)
    {
        this(gui4jComponent, gui4jAccessImpl, dependantProperties, valueClassMap, Collections.EMPTY_SET);
    }

    Gui4jDefaultCall(Gui4jComponent gui4jComponent, Gui4jAccessImpl gui4jAccessImpl, List dependantProperties,
            Map valueClassMap, Set usedParams)
    {
        mGui4jComponent = gui4jComponent;
        mGui4jAccessImpl = gui4jAccessImpl;
        mDependantProperties = dependantProperties;
        mValueClassMap = valueClassMap;
        mUsedParams = usedParams;
    }

    public Class getValueClass()
    {
        return getValueClass("");
    }

    public Class getValueClass(String paramName)
    {
        return (Class) mValueClassMap.get(paramName);
    }

    public boolean hasTriggerEvents()
    {
        return mDependantProperties != null && mDependantProperties.size() > 0;
    }

    public Gui4jCall[] getDependantProperties()
    {
        if (mDependantProperties == null)
        {
            return null;
        }
        Gui4jDefaultCall[] gui4jAccess = new Gui4jDefaultCall[mDependantProperties.size()];
        for (int i = 0; i < mDependantProperties.size(); i++)
        {
            gui4jAccess[i] = (Gui4jDefaultCall) mDependantProperties.get(i);
        }
        return gui4jAccess;
    }

    public String getConfigurationName()
    {
        return mGui4jComponent.getGui4jComponentContainer().getConfigurationName();
    }

    /**
     * @param baseInstance
     *            the root class used for reflection
     * @param defaultValue
     * @return Object
     */
    public Object getValueNoParams(Gui4jCallBase baseInstance, Object defaultValue)
    {
        return getValue(baseInstance, baseInstance, null, defaultValue);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.bea.gui4j.call.Gui4jGetValue#getValue(de.bea.gui4j.Gui4jCallBase,
     *      java.util.Map, java.lang.Object)
     */
    public Object getValue(Gui4jCallBase gui4jController, Map paramMap, Object defaultValue)
    {
        return getValue(gui4jController, gui4jController, paramMap, defaultValue);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.gui4j.Gui4jGetValue#getValueNoErrorChecking(org.gui4j.Gui4jCallBase,
     *      java.util.Map, java.lang.Object)
     */
    public Object getValueNoErrorChecking(Gui4jCallBase gui4jController, Map paramMap, Gui4jComponentInstance componentInstance)
    {
        return mGui4jAccessImpl.getValue(gui4jController, gui4jController, paramMap);
    }

    /**
     * @param baseInstance
     *            the root class used for reflection
     * @param defaultParamValue
     *            the value substituted for '?' in the access path
     * @param defaultValue
     * @return Object
     */
    public Object getValueUseDefaultParam(Gui4jCallBase baseInstance, Object defaultParamValue, Object defaultValue)
    {
        Map m = new Gui4jMap1("", defaultParamValue);
        return getValue(baseInstance, baseInstance, m, defaultValue);
    }

    Object getValue(Gui4jCallBase gui4jController, Object thisInstance, Map paramMap, Object defaultValue)
    {
        try
        {
            return mGui4jAccessImpl.getValue(gui4jController, thisInstance, paramMap);
        }
        catch (Throwable e)
        {
            gui4jController.getGui4j().handleException(gui4jController, e, null);
            return defaultValue;
        }
    }

    public Class getResultClass()
    {
        return mGui4jAccessImpl == null ? null : mGui4jAccessImpl.getResultClass();
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return mGui4jAccessImpl.toString();
    }

    public Set getUsedParams()
    {
        return mUsedParams;
    }

}