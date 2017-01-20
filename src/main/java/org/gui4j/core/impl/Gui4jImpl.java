package org.gui4j.core.impl;

import java.awt.Dialog;
import java.awt.Frame;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.gui4j.ExcelCopyHandler;
import org.gui4j.ExcelCopyHandlerFactory;
import org.gui4j.Gui4jCallBase;
import org.gui4j.Gui4jController;
import org.gui4j.Gui4jDialog;
import org.gui4j.Gui4jResourceProvider;
import org.gui4j.Gui4jValidator;
import org.gui4j.Gui4jView;
import org.gui4j.Gui4jWindow;
import org.gui4j.core.Gui4jCallFactory;
import org.gui4j.core.Gui4jComponentContainer;
import org.gui4j.core.Gui4jComponentContainerManager;
import org.gui4j.core.Gui4jComponentManager;
import org.gui4j.core.Gui4jInternal;
import org.gui4j.core.Gui4jReflectionManager;
import org.gui4j.core.Gui4jThreadManager;
import org.gui4j.core.call.Gui4jCallParser;
import org.gui4j.core.interfaces.Gui4jWindowInternal;
import org.gui4j.core.util.MethodCall;
import org.gui4j.dflt.DefaultExcelCopyHandler;
import org.gui4j.event.SimpleEvent;
import org.gui4j.exception.Gui4jDefaultErrorHandler;
import org.gui4j.exception.Gui4jErrorHandler;
import org.gui4j.exception.Gui4jException;
import org.gui4j.exception.Gui4jExceptionHandler;


/**
 * This is the base initialization class of Gui4j. An instance of
 * Gui4j holds all other necessary instances to deal with 
 * graphical user interfaces with gui4j. Note that it is possible
 * to use different instances of Gui4j, but each instance has its
 * own workspace and they are completely independant; and each
 * instance maintains its own cache for reflection calls and
 * worker threads.
 */
final class Gui4jImpl implements Serializable, Gui4jInternal
{
    private Log mLogger = LogFactory.getLog(getClass());
    private final Gui4jComponentManager mGui4jComponentManager;
    private final Gui4jComponentContainerManager mGui4jComponentContainerManager;
    private final Gui4jReflectionManager mGui4jReflectionManager;
    private final Gui4jThreadManager mGui4jThreadManager;
    private final SimpleEvent eViewCollection = new SimpleEvent();
    private boolean mValidateXML;
    private boolean mLogInvoke;
    private boolean mTraceWorkerInvocation = false;
    private Gui4jErrorHandler mErrorHandler;
    private Gui4jResourceProvider mResourceProvider;
    private ExcelCopyHandlerFactory mExcelCopyHandlerFactory;

    private Set windowCollector = new HashSet();

    public Gui4jImpl(boolean validateXML, boolean logInvoke, int numberOfWorkerThreads, URL configURL)
    {
        mValidateXML = validateXML;
        mLogInvoke = logInvoke;
        mGui4jComponentManager = Gui4jComponentManager.getNewInstance(this);
        mGui4jComponentContainerManager = Gui4jComponentContainerManager.getNewInstance(this);
        mGui4jReflectionManager = Gui4jReflectionManager.getNewInstance();
        mGui4jThreadManager = Gui4jThreadManager.getNewInstance(this, numberOfWorkerThreads);
        mResourceProvider = new Gui4jResourceProviderImpl();
        mExcelCopyHandlerFactory = createExcelCopyHandlerFactoryDefault();
        configure(configURL);
    }

    /**
     * @return the contained <code>Gui4jComponentManager</code>
     */
    public Gui4jComponentManager getGui4jComponentManager()
    {
        return mGui4jComponentManager;
    }

    /**
     * @return the contained <code>Gui4jComponentContainerManager</code>
     */
    public Gui4jComponentContainerManager getGui4jComponentContainerManager()
    {
        return mGui4jComponentContainerManager;
    }

    /**
     * @return the contained <code>Gui4jReflectionManager</code>
     */
    public Gui4jReflectionManager getGui4jReflectionManager()
    {
        return mGui4jReflectionManager;
    }

    /**
     * @return the used <code>Gui4jThreadManager</code>
     */
    public Gui4jThreadManager getGui4jThreadManager()
    {
        return mGui4jThreadManager;
    }

    /**
     * @param configurationSource the URL of the XML configuration file for the
     * <code>Gui4jComponents</code> to be installed
     */
    private void configure(URL configurationSource)
    {
        assert configurationSource != null;
        mGui4jComponentManager.configure(configurationSource);
    }

    /**
     * Is called after the setter of an edit field returns without raising an
     * exception. If the given Controller (Gui4jCallBase) implements a suitable
     * method, that method is called, otherwise, nothing happens.
     * @param gui4jCallBase
     * @param context
     */
    public void handleSuccess(Gui4jCallBase gui4jCallBase, Object context)
    {
        if (context == null)
        {
            return;
        }

        try
        {
            Gui4jExceptionHandler exceptionHandler =
                gui4jCallBase != null ? gui4jCallBase.getExceptionHandler() : null;
            Gui4jExceptionHandler oldHandler = null;
            while (exceptionHandler != null && exceptionHandler != oldHandler)
            {
                oldHandler = exceptionHandler;
                MethodCall call =
                    getGui4jReflectionManager().getMethod(
                        "errorHandling",
                        exceptionHandler.getClass(),
                        "handleSuccess",
                        new Class[] { context.getClass()},
                        false);
                if (call != null)
                {
                    call.invoke(exceptionHandler, new Object[] { context });
                    break;
                }
                exceptionHandler = exceptionHandler.getDelegationExceptionHandler();
            }
        }
        catch (Throwable tHandler)
        {
            internalError(tHandler);
        }
    }

    private Throwable unpack(Throwable t)
    {
        while (t != null)
        {
            if (t.getCause() != null && t instanceof UndeclaredThrowableException)
            {
                t = t.getCause();
                continue;
            }
            if (t instanceof InvocationTargetException)
            {
                t = ((InvocationTargetException) t).getTargetException();
                continue;
            }
            break;
        }
        return t;
    }

    /**
     * Is called for all exceptions occuring during execution of methods. If the
     * given Controller (Gui4jCallBase) defines a suitable error handler, that
     * error handler is called. Otherwise an internal error is raisen.
     * @param gui4jCallBase
     * @param t
     * @param context
     */
    public void handleException(Gui4jCallBase gui4jCallBase, Throwable t, Object context)
    {
        assert t != null;
        t = unpack(t);
        boolean handled = false;
        Gui4jExceptionHandler topExceptionHandler =
            gui4jCallBase != null ? gui4jCallBase.getExceptionHandler() : null;
        try
        {
            Gui4jExceptionHandler exceptionHandler = topExceptionHandler;
            Gui4jExceptionHandler oldHandler = null;
            if (context != null)
            {
                while (!handled && exceptionHandler != null && oldHandler != exceptionHandler)
                {
                    oldHandler = exceptionHandler;
                    MethodCall call =
                        getGui4jReflectionManager().getMethod(
                            "errorHandling",
                            exceptionHandler.getClass(),
                            "handleException",
                            new Class[] { context.getClass(), t.getClass()},
                            false);
                    if (call != null)
                    {
                        handled = true;
                        call.invoke(exceptionHandler, new Object[] { context, t });
                    }
                    exceptionHandler = exceptionHandler.getDelegationExceptionHandler();
                }
            }
            exceptionHandler = topExceptionHandler;
            oldHandler = null;
            while (!handled && exceptionHandler != null && oldHandler != exceptionHandler)
            {
                oldHandler = exceptionHandler;
                MethodCall call =
                    getGui4jReflectionManager().getMethod(
                        "errorHandling",
                        exceptionHandler.getClass(),
                        "handleException",
                        new Class[] { t.getClass()},
                        false);
                if (call != null)
                {
                    handled = true;
                    call.invoke(exceptionHandler, new Object[] { t });
                }
            }
        }
        catch (Throwable tExceptionHandler)
        {
            handled = true;
            internalError(tExceptionHandler);
        }
        if (!handled)
        {
            internalError(t);
        }
    }

    private void internalError(Throwable t)
    {
        mLogger.error("Internal error", t);
        if (Thread.currentThread() instanceof Gui4jThreadManager.WorkerThread)
        {
            Gui4jThreadManager.WorkerThread wt = (Gui4jThreadManager.WorkerThread) Thread.currentThread();
            if (wt.getCallStack() != null)
            {
                mLogger.error("Invoker of thread [" + wt.getName() + "] was:", wt.getCallStack());
            }
        }
        try
        {
            if (mErrorHandler != null)
            {
                mErrorHandler.internalError(t);
            }
            else
            {
                Gui4jDefaultErrorHandler.getInstance().internalError(t);
            }
        }
        catch (Throwable tError)
        {
            mLogger.error("Error occured while handling internal error", tError);
        }
    }

    /**
     * Loads the specified resource file in the cache. The method can be used to
     * ensure that some XML resource files are loaded before serializing the
     * current state.
     * @param controllerClass the controller for the given resource file
     * @param resourceName the name of the xml resource to load
     */
    public void readResourceFile(Class controllerClass, String resourceName)
    {
        String fullyQuantifiedName =
            Gui4jComponentContainerManager.getResourceNameFullyQuantified(
                Gui4jComponentContainerManager.getBaseName(controllerClass),
                resourceName);

        Gui4jComponentContainer gui4jComponentContainer =
            mGui4jComponentContainerManager.getGui4jComponentContainer(controllerClass, fullyQuantifiedName);

        if (gui4jComponentContainer.isDefined("TOP"))
        {
            gui4jComponentContainer.getGui4jQualifiedComponent("TOP");
        }
        if (gui4jComponentContainer.isDefined("MENU"))
        {
            gui4jComponentContainer.getGui4jQualifiedComponent("MENU");
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException
    {
        mLogger = null;
        out.defaultWriteObject();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        in.defaultReadObject();
        mLogger = LogFactory.getLog(getClass());
    }

    /**
     * @return true if XML files should be validated
     */
    public boolean validateXML()
    {
        return mValidateXML;
    }

    /**
     * @return true if invocation calls should be logged
     */
    public boolean logInvoke()
    {
        return mLogInvoke;
    }

    /**
     * @return true if the call stack of invokers of a WorkerThread
     * should be saved and then printed in case of an internal error.
     */
    public boolean traceWorkerInvocation()
    {
        return mTraceWorkerInvocation;
    }

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
    public void setTraceWorkerInvocation(boolean b)
    {
        mTraceWorkerInvocation = b;
    }

    /**
     * @return true if special debug messages should be written. This 
     * method should be removed at some stage.
     */
    public boolean traceMode()
    {
        return false;
    }

    /**
     * Sets the errorHandler.
     * @param errorHandler The errorHandler to set
     */
    public void setErrorHandler(Gui4jErrorHandler errorHandler)
    {
        mErrorHandler = errorHandler;
    }

    /**
     * Returns the viewCollector.
     * @return Set
     */
    public Set getViewCollector()
    {
        return windowCollector;
    }

    public void addToWindowCollector(Gui4jWindowInternal window)
    {
        mLogger.debug("Adding window to list. Current length (before adding is): " + windowCollector.size());
        windowCollector.add(window);
        eViewCollection.fireEvent();
    }

    public void removeFromWindowCollector(Gui4jWindowInternal window)
    {
        windowCollector.remove(window);
        mLogger.debug(
            "Removing window from list. Current length (after removing is): " + windowCollector.size());
        eViewCollection.fireEvent();
    }

    
    /* (non-Javadoc)
     * @see org.gui4j.Gui4j#createView(java.lang.String, org.gui4j.Gui4jController, java.lang.String, boolean)
     */
    public Gui4jView createView(String viewResourceName, Gui4jController gui4jController, String title,
            boolean readOnlyMode)
    {
        return new Gui4jViewImpl(this, viewResourceName, gui4jController, title, readOnlyMode);
    }
    
    
    /* (non-Javadoc)
     * @see org.gui4j.Gui4j#createDialog(org.gui4j.Gui4jWindow, java.lang.String, org.gui4j.Gui4jController, java.lang.String, boolean)
     */
    public Gui4jDialog createDialog(Gui4jWindow owner, String viewResourceName, Gui4jController gui4jController,
            String title, boolean readOnlyMode)
    {
        return new Gui4jDialogImpl(this, owner, viewResourceName, gui4jController, title, readOnlyMode);
    }
    
    
    /* (non-Javadoc)
     * @see org.gui4j.Gui4j#createDialog(java.awt.Dialog, java.lang.String, org.gui4j.Gui4jController, java.lang.String, boolean)
     */
    public Gui4jDialog createDialog(Dialog owner, String viewResourceName, Gui4jController gui4jController, String title, boolean readOnlyMode)
    {
        return new Gui4jDialogImpl(this, owner, viewResourceName, gui4jController, title, readOnlyMode);
    }

    /* (non-Javadoc)
     * @see org.gui4j.Gui4j#createDialog(java.awt.Frame, java.lang.String, org.gui4j.Gui4jController, java.lang.String, boolean)
     */
    public Gui4jDialog createDialog(Frame owner, String viewResourceName, Gui4jController gui4jController, String title, boolean readOnlyMode)
    {
        return new Gui4jDialogImpl(this, owner, viewResourceName, gui4jController, title, readOnlyMode);
    }

    public Gui4jCallFactory createCallFactory()
    {
        return new Gui4jCallParser();
    }
    
    
    /* (non-Javadoc)
     * @see org.gui4j.Gui4j#writeDTD(java.io.File)
     */
    public void writeDTD(File outputFile) throws Gui4jException
    {
        getGui4jComponentManager().writeDTD(outputFile);
    }
    
    public void shutdown()
    {
        getGui4jThreadManager().shutdown();
        getGui4jComponentContainerManager().clearResources();
        getGui4jComponentManager().dispose();
        getGui4jReflectionManager().dispose();
    }
    
    /* (non-Javadoc)
     * @see org.gui4j.Gui4j#createValidator()
     */
    public Gui4jValidator createValidator()
    {
        return new Gui4jValidatorImpl(this);
    }

    /* (non-Javadoc)
     * @see org.gui4j.Gui4j#setResourceProvider(org.gui4j.Gui4jResourceProvider)
     */
    public void setResourceProvider(Gui4jResourceProvider resourceProvider)
    {
        mResourceProvider = resourceProvider;
    }

    /* (non-Javadoc)
     * @see org.gui4j.core.Gui4jInternal#getResourceProvider()
     */
    public Gui4jResourceProvider getResourceProvider()
    {
        return mResourceProvider;
    }
    
    
    public void setExcelCopyHandlerFactory(ExcelCopyHandlerFactory excelCopyHandlerFactory)
    {
        assert excelCopyHandlerFactory != null;
        mExcelCopyHandlerFactory = excelCopyHandlerFactory;
    }

    private ExcelCopyHandlerFactory createExcelCopyHandlerFactoryDefault() {
        return new ExcelCopyHandlerFactory() {
        
            public ExcelCopyHandler createExcelCopyHandler(String guiId)
            {
                return new DefaultExcelCopyHandler();
            }
        };
    }
    
    public ExcelCopyHandler createExcelCopyHandler(String guiId) {
        return mExcelCopyHandlerFactory.createExcelCopyHandler(guiId);
    }
    
    
}
