package org.gui4j;

import java.util.List;

public interface Gui4jValidator
{
    /**
     * Reads the given resource file and checks if the syntax and all 
     * reflection calls are still valid. This method can be used in
     * JUnit test to ensure that XML resource files are valid.
     * @param controllerClass the controller for the resource file
     * @param resourceName the name of the resource containing the Gui4j definitions
     * @return true if everything is ok
     */
    boolean validateResourceFile(Class controllerClass, String resourceName);

    /**
     * @param controllerClass
     * @param resourceName
     * @param ids
     * @return -1 if all ids are defined, otherwise the position in the given list of ids.
     */
    int validateExistenceOfGuiIDs(Class controllerClass, String resourceName, List ids);
}