package org.gui4j.component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuBar;

import org.gui4j.core.Gui4jComponentContainer;
import org.gui4j.core.Gui4jComponentInstance;
import org.gui4j.core.Gui4jQualifiedComponent;
import org.gui4j.core.Gui4jSwingContainer;


public final class Gui4jMenuBar extends Gui4jJComponent
{
    private final List mMenuList;

    /**
     * Constructor for Gui4jMenuBar.
     * @param gui4jComponentContainer
     * @param id
     */
    public Gui4jMenuBar(Gui4jComponentContainer gui4jComponentContainer, String id)
    {
        super(gui4jComponentContainer, JMenuBar.class, id);
        mMenuList = new ArrayList();
    }

    public void addMenu(Gui4jQualifiedComponent gui4jMenu)
    {
        mMenuList.add(gui4jMenu);
    }

    protected void setProperties(Gui4jComponentInstance gui4jComponentInstance)
    {
        super.setProperties(gui4jComponentInstance);
        JMenuBar menuBar = (JMenuBar) gui4jComponentInstance.getComponent();
        Gui4jSwingContainer gui4jSwingContainer = gui4jComponentInstance.getGui4jSwingContainer();
        for (Iterator it = mMenuList.iterator(); it.hasNext();)
        {
            Gui4jQualifiedComponent gui4jSubComponent = (Gui4jQualifiedComponent) it.next();
            Gui4jComponentInstance subInstance =
                gui4jSwingContainer.getGui4jComponentInstance(
                    gui4jComponentInstance.getGui4jComponentInPath().getGui4jComponentPath(),
                    gui4jSubComponent);
            JMenu menu = (JMenu) subInstance.getComponent();
            menuBar.add(menu);
        }
    }

}
