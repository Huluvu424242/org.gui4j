package org.gui4j.core;

import org.gui4j.ExcelCopyHandler;
import org.gui4j.Gui4j;
import org.gui4j.Gui4jResourceProvider;
import org.gui4j.core.interfaces.Gui4jWindowInternal;

public interface Gui4jInternal extends Gui4j
{
    /**
     * @return the contained <code>Gui4jComponentManager</code>
     */
    Gui4jComponentManager getGui4jComponentManager();

    /**
     * @return the contained <code>Gui4jComponentContainerManager</code>
     */
    Gui4jComponentContainerManager getGui4jComponentContainerManager();

    /**
     * @return the contained <code>Gui4jReflectionManager</code>
     */
    Gui4jReflectionManager getGui4jReflectionManager();

    /**
     * @return the used <code>Gui4jThreadManager</code>
     */
    Gui4jThreadManager getGui4jThreadManager();

    /**
     * Loads the specified resource file in the cache. The method can be used to
     * ensure that some XML resource files are loaded before serializing the
     * current state.
     * @param controllerClass the controller for the given resource file
     * @param resourceName the name of the xml resource to load
     */
    void readResourceFile(Class controllerClass, String resourceName);

    /**
     * @return true if XML files should be validated
     */
    boolean validateXML();

    /**
     * @return true if invocation calls should be logged
     */
    boolean logInvoke();

    /**
     * @return true if the call stack of invokers of a WorkerThread
     * should be saved and then printed in case of an internal error.
     */
    boolean traceWorkerInvocation();

    /**
     * @return true if special debug messages should be written. This 
     * method should be removed at some stage.
     */
    boolean traceMode();


    void addToWindowCollector(Gui4jWindowInternal window);

    void removeFromWindowCollector(Gui4jWindowInternal window);

    Gui4jCallFactory createCallFactory();
    
    /**
     * @return the ressource provider if defined
     */
    Gui4jResourceProvider getResourceProvider();
    
    ExcelCopyHandler createExcelCopyHandler(String guiId);
    
}