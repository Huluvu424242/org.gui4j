package org.gui4j.component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import org.gui4j.Gui4jCallBase;
import org.gui4j.Gui4jGetValue;
import org.gui4j.Gui4jWindow;
import org.gui4j.core.Gui4jComponentContainer;
import org.gui4j.core.Gui4jComponentInstance;
import org.gui4j.core.Gui4jInternal;
import org.gui4j.core.Gui4jQualifiedComponent;
import org.gui4j.core.Gui4jSwingContainer;
import org.gui4j.core.interfaces.Gui4jWindowInternal;


public final class Gui4jMenu extends Gui4jMenuItem
{
    private final List mMenuItems;
    private final boolean mWindowList;

    /**
     * Constructor for Gui4jMenu.
     * @param gui4jComponentContainer
     * @param windowList
     * @param id
     */
    public Gui4jMenu(Gui4jComponentContainer gui4jComponentContainer, boolean windowList, String id)
    {
        super(gui4jComponentContainer, JMenu.class, id);
        mMenuItems = new ArrayList();
        mWindowList = windowList;
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
        JMenu menu = (JMenu) gui4jComponentInstance.getComponent();
        Gui4jSwingContainer gui4jSwingContainer = gui4jComponentInstance.getGui4jSwingContainer();
        if (mWindowList)
        {
            setWindowList(menu, getGui4j(), gui4jSwingContainer);
        }
        else
        {
            for (Iterator it = mMenuItems.iterator(); it.hasNext();)
            {
                Gui4jQualifiedComponent gui4jSubComponent = (Gui4jQualifiedComponent) it.next();
                if (gui4jSubComponent == null)
                {
                    menu.addSeparator();
                }
                else
                {
                    Gui4jComponentInstance subInstance =
                        gui4jSwingContainer.getGui4jComponentInstance(
                            gui4jComponentInstance.getGui4jComponentInPath().getGui4jComponentPath(),
                            gui4jSubComponent);
                    JMenuItem menuItem = (JMenuItem) subInstance.getComponent();
                    menu.add(menuItem);
                }
            }
        }
    }

    public static void setWindowList(final JMenu menu, final Gui4jInternal gui4j, final Gui4jSwingContainer current)
    {
        // Menge mit offenen Fenstern erzeugen
        menu.addMenuListener(new MenuListener()
        {
            public void menuSelected(MenuEvent e)
            {
				refreshWindowList(menu, gui4j, current);
            }

            public void menuDeselected(MenuEvent e)
            {
            }

            public void menuCanceled(MenuEvent e)
            {
            }
        });
    }

    protected static void refreshWindowList(JMenu menu, final Gui4jInternal gui4j, Gui4jSwingContainer current)
    {
        menu.removeAll();
        for (Iterator it = gui4j.getViewCollector().iterator(); it.hasNext();)
        {
            final Gui4jWindowInternal window = (Gui4jWindowInternal) it.next();
            if (true || window != current) // auch aktuelles Fenster anzeigen
            {
                String title = window.getTitle();
                JMenuItem menuItem = new JMenuItem(title);
                if (title.length()>0)
                {
					menuItem.setMnemonic(title.charAt(0));
                }
                menuItem.addActionListener(new ActionListener()
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        Gui4jGetValue action = new Gui4jGetValue()
                        {
                            public Object getValue(
                                Gui4jCallBase gui4jController,
                                Map paramMap,
                                Object defaultValue)
                            {
                                window.show();
                                return null;
                            }

                            public Object getValueNoErrorChecking(
                                Gui4jCallBase gui4jController,
                                Map paramMap,
                                Gui4jComponentInstance componentInstance)
                            {
                                return getValue(gui4jController, paramMap, null);
                            }

                            public String toString()
                            {
                                return Gui4jWindow.class.getName() + ".show()";
                            }
                            
                        };
                        gui4j.getGui4jThreadManager().performWork(null, action, null);
                    }
                });
                menu.add(menuItem);
            }
        }
    }

}
