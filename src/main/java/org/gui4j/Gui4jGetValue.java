package org.gui4j;

import java.util.Map;

import org.gui4j.core.Gui4jComponentInstance;



/**
 * Only for internal use.
 */
public interface Gui4jGetValue
{
    /**
     * Evaluates the call and handles occuring exceptions.
     * 
     * TODO: add parameter for Gui4jComponentInstance and refactor all implementors
     * and callers.
     * 
     * @param callBase the controller instance.
     * @param paramMap  map with parameter values which might be used by the method call
     * @param defaultValue if an error occurs this value is returned.
     * @return Object The result of the call or the <code>defaultValue</code> if
     * an exception occurred.
     */
    Object getValue(Gui4jCallBase callBase, Map paramMap, Object defaultValue);
    
    /**
     * Evaluates the call and doesn't catch exceptions.
     * @param callBase the controller instance.
     * @param paramMap map with parameter values which might be used by the method call
     * @param componentInstance
     * @return Object
     */
    Object getValueNoErrorChecking(Gui4jCallBase callBase, Map paramMap, Gui4jComponentInstance componentInstance);
    
}
