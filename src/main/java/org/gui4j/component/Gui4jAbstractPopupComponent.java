package org.gui4j.component;

import java.awt.Point;
import java.awt.event.MouseEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gui4j.core.Gui4jAbstractComponent;
import org.gui4j.core.Gui4jComponentContainer;
import org.gui4j.core.Gui4jComponentInstance;
import org.gui4j.core.Gui4jSwingContainer;


public abstract class Gui4jAbstractPopupComponent extends Gui4jAbstractComponent
{
    private static final Log mLogger = LogFactory.getLog(Gui4jAbstractPopupComponent.class);

    /**
     * @param gui4jComponentContainer
     * @param componentClass
     * @param id
     */
    public Gui4jAbstractPopupComponent(Gui4jComponentContainer gui4jComponentContainer, Class componentClass, String id)
    {
        super(gui4jComponentContainer, componentClass, id);
    }
    
    /*
     * @see de.bea.gui4j.Gui4jComponent#showPopupMenu(de.bea.gui4j.Gui4jComponentInstance, java.awt.event.MouseEvent)
     */
    public final void showPopupMenu(Gui4jComponentInstance gui4jComponentInstance, MouseEvent mouseEvent)
    {
        if (mPopupMenuInPath == null)
        {
            mLogger.warn("showPopupMenu called but no popup menu defined for: " + gui4jComponentInstance);
            return;
        }

        // store current context in swing container's storage
        Object context = getPopupContext(gui4jComponentInstance, mouseEvent);
        Gui4jComponentInstance popupInstance = gui4jComponentInstance.getGui4jComponentInstance(mPopupMenuInPath);
        Gui4jSwingContainer swingContainer = popupInstance.getGui4jSwingContainer();
        swingContainer.putStorage(Gui4jPopupMenu.class, STORAGE_POPUP_CONTEXT, context);
        swingContainer.putStorage(Gui4jPopupMenu.class, STORAGE_POPUP_ORIGIN, gui4jComponentInstance);

        // refresh popup menu's attributes and show it
        popupInstance.refreshComponent();
        Point location = getPopupLocation(gui4jComponentInstance, mouseEvent, context);
        if (location != null)
        {
            Gui4jPopupMenu popup = (Gui4jPopupMenu) popupInstance.getGui4jComponent();
            popup.show(popupInstance, gui4jComponentInstance.getComponent(), location.x, location.y);
        }
    }

    
}
