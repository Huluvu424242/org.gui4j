package org.gui4j.core.definition;

import java.util.Collection;

public interface ParameterAttributeType
{
    /**
     * @return A collection of Param instances
     */
    Collection getParams();
    
    /**
     * Return a description of what kind of parameters this type uses.
     * @return String
     */
    String getParamDescription();
    
    /**
     * Return the prefix, if any, that is required by the gui4j syntax to prepend to the parameter name.
     * @return String
     */
    String getParamPrefix();
}
