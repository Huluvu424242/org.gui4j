package org.gui4j.core.call;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JComponent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.gui4j.Gui4jCallBase;
import org.gui4j.core.Gui4jCall;
import org.gui4j.core.Gui4jComponent;
import org.gui4j.core.Gui4jComponentInstance;
import org.gui4j.core.Gui4jMap1;
import org.gui4j.core.Gui4jThreadManager;

public final class Gui4jInternalActionCall implements Gui4jCall
{
    private static final Log log = LogFactory.getLog(Gui4jInternalActionCall.class);

    private final Gui4jComponent mGui4jComponent;
    private final String mActionKey;

    public Gui4jInternalActionCall(Gui4jComponent gui4jComponent, String actionKey)
    {
        this.mGui4jComponent = gui4jComponent;
        this.mActionKey = actionKey;

        log.debug("Gui4jInternalActionCall created for: " + actionKey);
    }

    public Gui4jCall[] getDependantProperties()
    {
        return null;
    }

    public boolean hasTriggerEvents()
    {
        return false;
    }

    public Class getResultClass()
    {
        return null;
    }

    public Object getValue(Gui4jCallBase gui4jController, Map paramMap, Object defaultValue)
    {
        return getValueNoErrorChecking(gui4jController, paramMap, null);
    }

    public Class getValueClass()
    {
        return null;
    }

    public Class getValueClass(String paramName)
    {
        return null;
    }

    public Set getUsedParams()
    {
        return Collections.EMPTY_SET;
    }

    public Object getValueNoParams(Gui4jCallBase baseInstance, Object defaultValue)
    {
        return getValueNoErrorChecking(baseInstance, null, null);
    }

    public Object getValueUseDefaultParam(Gui4jCallBase baseInstance, Object defaultParamValue, Object defaultValue)
    {
        return getValueNoErrorChecking(baseInstance, new Gui4jMap1("", defaultParamValue), null);
    }

    public String getConfigurationName()
    {
        return mGui4jComponent.getGui4jComponentContainer().getConfigurationName();
    }

    public Object getValueNoErrorChecking(Gui4jCallBase callBase, Map paramMap, Gui4jComponentInstance componentInstance)
    {
        return performAction(componentInstance);
    }

    private Object performAction(Gui4jComponentInstance instance)
    {
        if (instance == null)
        {
            log.warn("Cannot perform internal action because Gui4jComponentInstance is not supplied.");
            return null;
        }
        final Action action = findAction(instance);
        if (action != null)
        {
            Gui4jThreadManager.executeInSwingThreadAndContinue(new Runnable() {

                public void run()
                {
                    action.actionPerformed(new ActionEvent(this, 0, mActionKey));
                }
            });
        }
        return null;
    }

    private Action findAction(Gui4jComponentInstance instance)
    {
        // Find named action in swing container hierarchy
        Component component = instance.getComponent();
        while (component != null)
        {
            if (component instanceof JComponent)
            {
                JComponent jcomponent = (JComponent) component;
                ActionMap actionMap = jcomponent.getActionMap();
                Action action = actionMap.get(mActionKey);
                if (action != null)
                {
                    log.debug("Action found: " + action);
                    return action;
                }
            }
            component = component.getParent();
        }
        return null;
    }

    public String toString()
    {
        return "Internal Action for: " + mActionKey;
    }

}
