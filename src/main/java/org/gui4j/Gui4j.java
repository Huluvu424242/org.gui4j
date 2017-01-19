package org.gui4j;

import java.awt.Dialog;
import java.awt.Frame;
import java.io.File;
import java.util.Set;

import org.gui4j.exception.Gui4jErrorHandler;
import org.gui4j.exception.Gui4jException;

public interface Gui4j
{
    /**
     * Is called for all exceptions occuring during execution of methods. If the
     * given Controller (Gui4jCallBase) defines a suitable error handler, that
     * error handler is called. Otherwise an internal error is raised.
     * @param gui4jCallBase
     * @param t
     * @param context
     */
    void handleException(Gui4jCallBase gui4jCallBase, Throwable t, Object context);

    /**
     * Is called after the setter of an edit field returns without raising an
     * exception. If the given Controller (Gui4jCallBase) implements a suitable
     * method, that method is called, otherwise, nothing happens.
     * @param gui4jCallBase
     * @param context
     */
    void handleSuccess(Gui4jCallBase gui4jCallBase, Object context);

    
    /**
     * @param viewResourceName
     * @param gui4jController
     * @param title if null uses the title defined in the resource
     * @param readOnlyMode
     * @return Gui4jView
     */
    Gui4jView createView(String viewResourceName, Gui4jController gui4jController, String title, boolean readOnlyMode);

    /**
     * @param owner
     * @param viewResourceName
     * @param gui4jController
     * @param title if null uses the title defined in the resource
     * @param readOnlyMode
     * @return Gui4jDialog
     */
    Gui4jDialog createDialog(Gui4jWindow owner, String viewResourceName, Gui4jController gui4jController, String title,
            boolean readOnlyMode);

    /**
     * @param owner
     * @param viewResourceName
     * @param gui4jController
     * @param title if null uses the title defined in the resource
     * @param readOnlyMode
     * @return Gui4jDialog
     */
    Gui4jDialog createDialog(Frame owner, String viewResourceName, Gui4jController gui4jController, String title,
            boolean readOnlyMode);

    /**
     * @param owner
     * @param viewResourceName
     * @param gui4jController
     * @param title if null uses the title defined in the resource
     * @param readOnlyMode
     * @return Gui4jDialog
     */
    Gui4jDialog createDialog(Dialog owner, String viewResourceName, Gui4jController gui4jController, String title,
            boolean readOnlyMode);

    /**
     * Returns the set of active (open) views.
     * @return Set(Gui4vView) 
     */
    Set getViewCollector();

    /**
     * Defines if the call stack of invokers of WorkerThreads should
     * always be saved so that it can be printed together with the
     * thread's own stack trace in case of an internal error.<br>
     * This feature is recommended only for debugging
     * purposes since the negative performance impact of creating a
     * call stack (i.e. instance of Throwable) for each invocation of a
     * WorkerThread has not been measured.
     * @param b true, to turn the feature on, false to turn it off
     */
    void setTraceWorkerInvocation(boolean b);

    /**
     * Sets the errorHandler.
     * @param errorHandler The errorHandler to set
     */
    void setErrorHandler(Gui4jErrorHandler errorHandler);
    
    /**
     * Sets the ressource provider to load xml gui4j definitions
     * @param ressourceProvider
     */
    void setResourceProvider(Gui4jResourceProvider ressourceProvider);
    
    /**
     * Writes the DTD to the specified file
     * @param outputFile
     * @throws Gui4jException
     */
    void writeDTD(File outputFile) throws Gui4jException;
    
    /**
     * Creates an instance of Gui4jValidator to validate xml files
     * @return Gui4jValidator
     */
    Gui4jValidator createValidator();
    
    /**
     * Shuts down the framework and releases all resources.
     */
    void shutdown();
    
    /**
     * Define the excel copier factory to copy the content of tables to the clipboard.
     * @param excelCopyHandlerFactory the factory to create ExcelCopyHandlers (must not be null)
     */
    void setExcelCopyHandlerFactory(ExcelCopyHandlerFactory excelCopyHandlerFactory);
}