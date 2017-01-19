package org.gui4j.core;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import javax.swing.SwingUtilities;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gui4j.Gui4jCallBase;
import org.gui4j.core.util.MethodCall;
import org.gui4j.exception.ErrorTags;
import org.gui4j.exception.Gui4jUncheckedException;


public final class Gui4jComponentPropertyGetter implements ErrorTags, Gui4jComponentProperty
{
    private static final Log mLogger = LogFactory.getLog(Gui4jComponentPropertyGetter.class);
    private final Gui4jCall mGui4jAccess;
    protected final MethodCall mMethod;
    private final boolean mUseSwingComponent;

    public Gui4jComponentPropertyGetter(Gui4jCall gui4jAccess, MethodCall method, boolean useSwingComponent)
    {
        mGui4jAccess = gui4jAccess;
        mMethod = method;
        mUseSwingComponent = useSwingComponent;
    }

    public boolean usesSwingComponent()
    {
        return mUseSwingComponent;
    }

    public void apply(
        final Gui4jComponentInstance gui4jComponentInstance,
        Object sourceClass,
        Gui4jCallBase gui4jController,
        boolean handleThreads)
    {
        if (gui4jComponentInstance == null)
        {
            mLogger.warn("Gui4jComponentInstance is null");
            return;
        }
        final Object[] args = { sourceClass };
        if (!handleThreads)
        {
            // do not handle thread problem with swing
            try
            {
                Object value = mMethod.invoke(gui4jComponentInstance.getGui4jComponent(), args);
                mGui4jAccess.getValueUseDefaultParam(gui4jController, value, null);
            }
            catch (InvocationTargetException e)
            {
                Gui4jReflectionManager.handleInvocationTargetException(e);
                throw new Gui4jUncheckedException.ProgrammingError(
                    PROGRAMMING_ERROR_invocation_target_exception,
                    e);
            }
            catch (IllegalAccessException e)
            {
                throw new Gui4jUncheckedException.ProgrammingError(
                    PROGRAMMING_ERROR_illegal_access_exception,
                    e);
            }
        }
        else
        {
            // handle thread problem with swing
            if (SwingUtilities.isEventDispatchThread())
            {
                mLogger.warn("Strange, we are in the Event-Dispatch Thread");
                try
                {
                    Object value = mMethod.invoke(gui4jComponentInstance.getGui4jComponent(), args);
                    Map m = new Gui4jMap1("", value);
                    Gui4jThreadManager gui4jThreadManager = gui4jComponentInstance.getGui4j().getGui4jThreadManager();
                    gui4jThreadManager.performWork(gui4jController, mGui4jAccess, m);
                }
                catch (InvocationTargetException e)
                {
                    Gui4jReflectionManager.handleInvocationTargetException(e);
                    throw new Gui4jUncheckedException.ProgrammingError(
                        PROGRAMMING_ERROR_invocation_target_exception,
                        e);
                }
                catch (IllegalAccessException e)
                {
                    throw new Gui4jUncheckedException.ProgrammingError(
                        PROGRAMMING_ERROR_illegal_access_exception,
                        e);
                }
            }
            else
            {
                final Element element = new Element();
                Runnable work = new Runnable()
                {
                    public void run()
                    {
                        try
                        {
                            element.mValue = mMethod.invoke(gui4jComponentInstance.getGui4jComponent(), args);
                        }
                        catch (InvocationTargetException e)
                        {
                            Gui4jReflectionManager.handleInvocationTargetException(e);
                            throw new Gui4jUncheckedException.ProgrammingError(
                                PROGRAMMING_ERROR_invocation_target_exception,
                                e);
                        }
                        catch (IllegalAccessException e)
                        {
                            throw new Gui4jUncheckedException.ProgrammingError(
                                PROGRAMMING_ERROR_illegal_access_exception,
                                e);
                        }
                    }
                };
                Gui4jThreadManager.executeInSwingThreadAndWait(work);
                mGui4jAccess.getValueUseDefaultParam(gui4jController, element.mValue, null);

            }
        }
    }

    public void apply(
        Gui4jComponentInstance gui4jComponentInstance,
        Gui4jCallBase gui4jController,
        boolean handleThreads)
    {
        if (mUseSwingComponent)
        {
            apply(gui4jComponentInstance, gui4jComponentInstance.getComponent(), gui4jController, handleThreads);
        }
        else
        {
            apply(gui4jComponentInstance, gui4jComponentInstance, gui4jController, handleThreads);
        }
    }

    public Gui4jCall getGui4jAccess()
    {
        return mGui4jAccess;
    }

    public boolean applyInitially()
    {
        return false;
    }

    private static class Element
    {
        public Object mValue;
    }
}
