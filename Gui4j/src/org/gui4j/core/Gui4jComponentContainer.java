package org.gui4j.core;

import java.util.Map;
import java.util.Set;

import org.dom4j.LElement;


/**
 * Contains the Gui4jComponent instances for a given resource file resolved for a given
 * controller class type
 */
public interface Gui4jComponentContainer
{
	/**
	 * @param e
	 * @return Gui4QualifiedComponent the gui4jComponent represented by the element
	*/
    public Gui4jQualifiedComponent extractGui4jComponent(LElement e);

	/**
	 * inserts additional attributes into the element correspondingly to defined styles
	 * @param e
	*/
    public void autoExtend(LElement e);
    
    /**
     * @param e
     * @param field
     * @return the attribute value of the field <code>field</code> where occuring
     * macros are substituted by their definitions
    */
    public String getAttrValue(LElement e, String field);

    /**
     * @param e
     * @param field
     * @param dflt
     * @return the attribute value of the field <code>field</code> where occuring
     * macros are substituted by their definitions
    */
    public boolean getBooleanAttrValue(LElement e, String field, boolean dflt);

    /**
     * @param e
     * @param field
     * @return the attribute value of the field <code>field</code> where occuring
     * macros are substituted by their definitions (macros my also occur inside a string)
    */
    public String getAttrValueReplaceAll(LElement e, String field);

    /**
     * @param field
     * @return the attribute value of <code>field</code> of the
     * topmost element of the resource.
     */
    String getToplevelAttrValue(String field);
    
    /**
     * Throws an exception if the corresponding component is not defined
     * @param id
     * @return Gui4jQualifiedComponent
    */
    public Gui4jQualifiedComponent getGui4jQualifiedComponent(String id);
    
    /**
     * @param id
     * @return true if a component with the given id is defined, otherwise false
    */
    public boolean isDefined(String id);
    
    /**
     * @return the controller class type used for this container
    */
    public Class getGui4jControllerClass();
    
    /**
     * @param aliasName
     * @return the class instance for a given alias name
    */
    public Class getClassForAliasName(String aliasName);
    
    public Gui4jInternal getGui4j();
 
 	/**
 	 * @return the name of the resource definition file
 	*/   
    public String getConfigurationName();
   
   
    /**
     * Liefert die Menge der zu setzenden Parameter beim Inlcudes des Containers
     * @return Set(String)
     */
    Set getParamIds();
    
    /**
     * Liefert die Menge der Ids zurück welche durch Parameterinstanziierung
     * benötigt werden
     * @return Set
     */
    Set getMappedIds();
    
    /**
     * Method getParamInstantiation.
     * @return Map(Gui4jQualifiedComponent -> Gui4jQualifiedComponent)
     */
    Map getParamInstantiation();
    
    // public List getGui4jComponentContainerIncludePath(Gui4jComponentContainer gui4jComponentContainer);
    
    Gui4jCall getTitleCall(); 

    Gui4jCall getWindowNameCall(); 

    
}

