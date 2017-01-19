package org.gui4j.component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

import org.gui4j.Gui4jCallBase;
import org.gui4j.core.Gui4jAbstractComponent;
import org.gui4j.core.Gui4jComponentContainer;
import org.gui4j.core.Gui4jComponentInstance;
import org.gui4j.core.Gui4jSwingContainer;

public class Gui4jTreePopupMenuItem extends Gui4jPopupMenuItem
{
    protected final int mCommand;

    /**
     * Constructor for Gui4jMenuItem.
     * 
     * @param gui4jComponentContainer
     * @param buttonClass
     * @param contextType
     * @param command
     * @param id
     */
    public Gui4jTreePopupMenuItem(Gui4jComponentContainer gui4jComponentContainer, Class buttonClass,
            Class contextType, int command, String id)
    {
        super(gui4jComponentContainer, buttonClass, contextType, id);
        mCommand = command;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.gui4j.core.Gui4jAbstractComponent#setProperties(org.gui4j.core.Gui4jComponentInstance)
     */
    protected void setProperties(final Gui4jComponentInstance gui4jComponentInstance)
    {
        super.setProperties(gui4jComponentInstance);
        JMenuItem menuItem = (JMenuItem) gui4jComponentInstance.getComponent();
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                Object context = gui4jComponentInstance.getGui4jSwingContainer().getStorage(Gui4jPopupMenu.class,
                        Gui4jAbstractComponent.STORAGE_POPUP_CONTEXT);
                Gui4jComponentInstance origin = (Gui4jComponentInstance) gui4jComponentInstance
                        .getGui4jSwingContainer().getStorage(Gui4jPopupMenu.class,
                                Gui4jAbstractComponent.STORAGE_POPUP_ORIGIN);
                if (origin != null && origin.getGui4jComponent() instanceof Gui4jTree)
                {
                    ((Gui4jTree) origin.getGui4jComponent()).handleAction(origin, context, mCommand);
                }
            }
        });
    }

    protected void applyInitialProperties(Gui4jComponentInstance gui4jComponentInstance, Gui4jCallBase gui4jController, boolean handleThreads)
    {
        Gui4jSwingContainer swingContainer = gui4jComponentInstance.getGui4jSwingContainer();
        Gui4jComponentInstance origin = (Gui4jComponentInstance) swingContainer.getStorage(Gui4jPopupMenu.class,
                Gui4jAbstractComponent.STORAGE_POPUP_ORIGIN);
        if (origin != null && origin.getGui4jComponent() instanceof Gui4jTree)
        {
            super.applyInitialProperties(gui4jComponentInstance, gui4jController, handleThreads);
        }
        else
        {
            gui4jComponentInstance.getComponent().setVisible(false);
        }
    }

}
