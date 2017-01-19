package org.gui4j.component;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import org.gui4j.core.Gui4jComponentContainer;
import org.gui4j.core.Gui4jComponentInstance;
import org.gui4j.core.Gui4jQualifiedComponent;


public class Gui4jPopupMenu extends Gui4jJComponent
{
    private static final String STORAGE_MENUITEMS = "menuitems";
    
    private final List mMenuItems;

    public Gui4jPopupMenu(Gui4jComponentContainer gui4jComponentContainer, String id)
    {
        super(gui4jComponentContainer, JPopupMenu.class, id);
        mMenuItems = new ArrayList();
    }

    public void addSeparator()
    {
        mMenuItems.add(null);
    }

    public void addMenuItem(Gui4jQualifiedComponent gui4jMenuItem)
    {
        mMenuItems.add(gui4jMenuItem);
    }

    protected void setProperties(Gui4jComponentInstance gui4jComponentInstance)
    {
        super.setProperties(gui4jComponentInstance);
        JPopupMenu popupMenu = (JPopupMenu) gui4jComponentInstance.getComponent();
        List subInstances = new ArrayList();
        for (Iterator it = mMenuItems.iterator(); it.hasNext();)
        {
            Gui4jQualifiedComponent gui4jSubComponent = (Gui4jQualifiedComponent) it.next();
            if (gui4jSubComponent == null)
            {
                popupMenu.addSeparator();
            }
            else
            {
                Gui4jComponentInstance subInstance = gui4jComponentInstance.getGui4jComponentInstance(gui4jSubComponent);
                subInstances.add(subInstance);
                JMenuItem menuItem = (JMenuItem) subInstance.getComponent();
                popupMenu.add(menuItem);
            }
        }
        gui4jComponentInstance.setStorage(STORAGE_MENUITEMS, subInstances);
    }

    /**
     * Shows the popup menu contained in <code>popupInstance</code>.
     * Any unnessary separators are made invisible prior to showing the
     * popup menu (separators at the top or bottom of the menu as well as duplicate separators).
     * @param popupInstance popup to show
     * @param invoker Component where popup call originated
     * @param x position where popup menu should appear (specified in invokers coordinates)
     * @param y position where popup menu should appear (specified in invokers coordinates)
     */
    public void show(Gui4jComponentInstance popupInstance, Component invoker, int x, int y)
    {
        JPopupMenu popup = (JPopupMenu) popupInstance.getComponent();

        // make any unnecessary separators invisible
        boolean anyElementVisible = false;
        Component previousVisibleElement = null;
        Component[] elements = popup.getComponents();
        for (int i = 0; i < elements.length; i++)
        {
            Component element = elements[i];
            if (element instanceof JSeparator) {
                element.setVisible(!(previousVisibleElement == null || previousVisibleElement instanceof JSeparator));
            } else {
                if (element.isVisible()) {
                    anyElementVisible = true;
                }                
            }
            if (element.isVisible()) {
                previousVisibleElement = element;
            }
        }
        if (previousVisibleElement instanceof JSeparator) {
            previousVisibleElement.setVisible(false);  // don't display separator at end of menu
        }
        
        // display popup menu if at least one element is visible
        if (anyElementVisible)
        {
            popup.show(invoker, x, y);
        }
    }

    public void refreshComponent(Gui4jComponentInstance gui4jComponentInstance)
    {
        super.refreshComponent(gui4jComponentInstance);

        // refresh all subinstances = popup menu items, e.g. to re-evaluate enabled attribute
        List subInstances = (List) gui4jComponentInstance.getStorage(STORAGE_MENUITEMS);
        for (Iterator iter = subInstances.iterator(); iter.hasNext();)
        {
            Gui4jComponentInstance subInstance = (Gui4jComponentInstance) iter.next();
            subInstance.refreshComponent();
        }
    }

}
