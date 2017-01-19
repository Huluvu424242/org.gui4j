package org.gui4j.core.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.gui4j.Gui4jValidator;
import org.gui4j.component.Gui4jButton;
import org.gui4j.core.Gui4jComponent;
import org.gui4j.core.Gui4jComponentContainer;
import org.gui4j.core.Gui4jComponentContainerManager;
import org.gui4j.core.Gui4jComponentManager;
import org.gui4j.core.Gui4jInternal;
import org.gui4j.exception.ErrorTags;
import org.gui4j.exception.Gui4jUncheckedException;

final class Gui4jValidatorImpl implements Gui4jValidator
{
    private Log mLogger = LogFactory.getLog(getClass());
    private final Gui4jInternal mGui4j;
    
    Gui4jValidatorImpl(Gui4jInternal gui4j)
    {
        this.mGui4j = gui4j;
    }

    /**
     * Reads the given resource file and checks if the syntax and all 
     * reflection calls are still valid. This method can be used in
     * JUnit test to ensure that XML resource files are valid.
     * @param controllerClass the controller for the resource file
     * @param resourceName the name of the resource containing the Gui4j definitions
     * @return true if everything is ok
     */
    public boolean validateResourceFile(Class controllerClass, String resourceName)
    {
        String fullyQualifiedName =
            Gui4jComponentContainerManager.getResourceNameFullyQuantified(
                Gui4jComponentContainerManager.getBaseName(controllerClass),
                resourceName);

        Gui4jComponentContainer gui4jComponentContainer =
            mGui4j.getGui4jComponentContainerManager().getGui4jComponentContainer(controllerClass, fullyQualifiedName);

        String top =
            gui4jComponentContainer.getToplevelAttrValue(Gui4jComponentManager.FIELD_Gui4jViewTopComponent);
        top = top == null ? "TOP" : top;
        List errorList = new ArrayList();
        if (!gui4jComponentContainer.isDefined(top))
        {
            Object[] args = new Object[] { top };
            errorList.add(
                new Gui4jUncheckedException.ResourceError(
                    fullyQualifiedName,
                    0,
                    ErrorTags.RESOURCE_ERROR_gui4jComponent_not_defined,
                    args));
        }

        try
        {
            gui4jComponentContainer.getGui4jQualifiedComponent(top);
        }
        catch (Throwable t)
        {
            errorList.add(t);
        }
        if (gui4jComponentContainer.isDefined("MENU"))
        {
            try
            {
                gui4jComponentContainer.getGui4jQualifiedComponent("MENU");
            }
            catch (Throwable t)
            {
                errorList.add(t);
            }
        }

        String defaultButton =
            gui4jComponentContainer.getToplevelAttrValue(Gui4jComponentManager.FIELD_Gui4jViewDefaultButton);
        if (defaultButton != null)
        {
            try
            {
                Gui4jComponent xc =
                    gui4jComponentContainer.getGui4jQualifiedComponent(defaultButton).getGui4jComponent();
                if (!(xc instanceof Gui4jButton))
                {
                    Object[] args = new Object[] { defaultButton };
                    throw new Gui4jUncheckedException.ResourceError(
                        fullyQualifiedName,
                        0,
                        ErrorTags.RESOURCE_ERROR_invalid_defaultButton,
                        args);
                }
            }
            catch (Throwable t)
            {
                errorList.add(t);
            }

        }

        for (Iterator it = gui4jComponentContainer.getMappedIds().iterator(); it.hasNext();)
        {
            String id = (String) it.next();
            try
            {
                gui4jComponentContainer.getGui4jQualifiedComponent(id);
            }
            catch (Throwable t)
            {
                errorList.add(t);
            }
        }

        if (errorList.size() > 0)
        {
            RuntimeException t = new Gui4jUncheckedException.ErrorList(errorList);
            String message = t.getMessage();
            mLogger.error(message, t);
            System.err.println(message);
            throw t;
        }

        return true;
    }

    /**
     * @param controllerClass
     * @param resourceName
     * @param ids
     * @return -1 if all ids are defined, otherwise the position in the given list of ids.
     */
    public int validateExistenceOfGuiIDs(Class controllerClass, String resourceName, List ids)
    {
        String fullyQuantifiedName =
            Gui4jComponentContainerManager.getResourceNameFullyQuantified(
                Gui4jComponentContainerManager.getBaseName(controllerClass),
                resourceName);
        Gui4jComponentContainer gui4jComponentContainer =
            mGui4j.getGui4jComponentContainerManager().getGui4jComponentContainer(controllerClass, fullyQuantifiedName);
        int pos = 0;
        boolean ok = gui4jComponentContainer.isDefined("TOP");
        if (ok)
        {
            gui4jComponentContainer.getGui4jQualifiedComponent("TOP");
            if (gui4jComponentContainer.isDefined("MENU"))
            {
                gui4jComponentContainer.getGui4jQualifiedComponent("MENU");
            }
        }
        for (Iterator it = ids.iterator(); it.hasNext(); pos++)
        {
            String id = (String) it.next();
            if (!gui4jComponentContainer.isDefined(id))
            {
                return pos;
            }

        }
        return -1;
    }


}