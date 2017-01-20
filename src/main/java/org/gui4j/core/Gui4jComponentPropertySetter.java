package org.gui4j.core;

import java.awt.EventQueue;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

import org.gui4j.Gui4jCallBase;
import org.gui4j.core.util.MethodCall;
import org.gui4j.exception.ErrorTags;
import org.gui4j.exception.Gui4jUncheckedException;


public final class Gui4jComponentPropertySetter implements ErrorTags, Gui4jComponentProperty, Serializable
{
    private final Gui4jCall mGui4jAccess;
    protected final MethodCall mMethod;
    private final boolean mUseSwingComponent;
    private final boolean mUseContext;

    public Gui4jComponentPropertySetter(
        Gui4jCall gui4jAccess,
        MethodCall method,
        boolean useSwingComponent,
        boolean useContext)
    {
        mGui4jAccess = gui4jAccess;
        mMethod = method;
        mUseSwingComponent = useSwingComponent;
        mUseContext = useContext;
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
        if (gui4jComponentInstance.getComponent() == null)
        {
            return;
        }
        if (gui4jComponentInstance.getGui4jSwingContainer().isInClosing())
        {
        	return;
        }
        Object value;
        if (mUseContext)
        {
            value =
                mGui4jAccess.getValue(
                    gui4jController,
                    new Gui4jMap1(Gui4jComponentFactory.CONTEXT, gui4jComponentInstance.getContext()),
                    null);
        }
        else
        {
            value = mGui4jAccess.getValueNoParams(gui4jController, null);
        }
        final Object[] args = { sourceClass, value };
        if (true || args[1] != null) // execute always
        {
            if (!handleThreads)
            {
                // do not handle thread problem with swing
                try
                {
                    mMethod.invoke(gui4jComponentInstance.getGui4jComponent(), args);
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
                Runnable worker = new Runnable()
                {
                    public void run()
                    {
                        try
                        {
                            Gui4jComponent gui4jComponent = gui4jComponentInstance.getGui4jComponent();
                            if (gui4jComponent != null)
                            {
                                mMethod.invoke(gui4jComponent, args);
                            }
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
                if (EventQueue.isDispatchThread())
                {
                    EventQueue.invokeLater(worker);
                }
                else
                {
                    Gui4jThreadManager.executeInSwingThreadAndWait(worker);
                }
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
        return true;
    }

}
