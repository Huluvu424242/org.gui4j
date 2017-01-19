package org.gui4j.core;

import java.util.Set;

import org.gui4j.Gui4jCallBase;
import org.gui4j.Gui4jGetValue;



/**
 * Represents the interface for all reflection call. Method calls, field accesses,
 * integer constants, and also string constants are instances of this
 * interface. Parsing of reflection calls is done by

 */
public interface Gui4jCall extends Gui4jGetValue
{
    /**
     * Method getDependantProperties. Returns the list of
     * dependant events.
     * @return Gui4jCall[]
     */
    Gui4jCall[] getDependantProperties();

    /**
     * Method hasTriggerEvents. Returns <code>true</code> if the
     * list of dependant events is non-empty.
     * @return boolean
     */
    boolean hasTriggerEvents();

    /**
     * Method getResultClass. 
     * @return Class the return-type of the reflection call
     */
    Class getResultClass();

    /**
     * Method getValueClass.
     * @return Class the type of the default parameter (name "")
     */
    Class getValueClass();

    /**
     * Method getValueClass.
     * @param paramName
     * @return the type of the given parameter
     */
    Class getValueClass(String paramName);

    /**
     * Returns actually used parameter names.
     * @return a set containing the names of all actually used parameters in this call. The
     * returned set is empty (not <code>null</code>) if no parameters were used. 
     */
    Set getUsedParams();
    
    /**
     * Method getValueNoParams. Evaluates the reflection call and uses
     * no parameter instantiation.
     * @param baseInstance
     * @param defaultValue the value if the call fails
     * @return Object
     */
    Object getValueNoParams(Gui4jCallBase baseInstance, Object defaultValue);

    /**
     * Method getValueUseDefaultParam. Evaluates the reflection call with given
     * value for the default parameter (name "").
     * @param baseInstance
     * @param defaultParamValue
     * @param defaultValue the value if the call fails
     * @return Object
     */
    Object getValueUseDefaultParam(Gui4jCallBase baseInstance, Object defaultParamValue, Object defaultValue);

    /**
     * Method getConfigurationSource.
     * @return String. The name of the XML-file where the reflection call was defined. This
     * is usually used only for error reporting.
     */
    String getConfigurationName();

}
