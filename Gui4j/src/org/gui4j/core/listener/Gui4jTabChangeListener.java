package org.gui4j.core.listener;

import java.util.Iterator;
import java.util.List;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;

import org.gui4j.core.Gui4jComponentInstance;
import org.gui4j.core.util.EntrySelection;


/**
 * Change Listener fuer Tabbed Panes
 */
public class Gui4jTabChangeListener extends Gui4jChangeListener
{
    private JTabbedPane mTabbedPane;
    private List mEntrySelections;

    /**
     * @param gui4jComponentInstance
     * @param entrySelections
     */
    public Gui4jTabChangeListener(Gui4jComponentInstance gui4jComponentInstance, List entrySelections)
    {
        super(gui4jComponentInstance);
        mTabbedPane = (JTabbedPane) gui4jComponentInstance.getComponent();
        mEntrySelections = entrySelections;
    }

    /*
     * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
     * @param e
     */
    public void stateChanged(ChangeEvent e)
    {
        super.stateChanged(e);
        if (mTabbedPane == null || mEntrySelections == null)
        {
            return;
        }
        int idx = mTabbedPane.getSelectedIndex();
        if (idx != -1)
        {
            for (Iterator it = mEntrySelections.iterator(); it.hasNext();)
            {
                EntrySelection entrySelection = (EntrySelection) it.next();
                if (entrySelection.getTabIndex() == idx)
                {
                    entrySelection.call();
                }
            }
        }
    }

    /*
     * @see de.bea.gui4j.Gui4jDispose#dispose()
     * 
     */
    public void dispose()
    {
        super.dispose();
        mTabbedPane = null;
        mEntrySelections = null;
    }

}
