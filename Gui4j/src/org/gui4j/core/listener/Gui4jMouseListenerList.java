package org.gui4j.core.listener;

import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JList;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.gui4j.constants.Const;
import org.gui4j.core.Gui4jCall;
import org.gui4j.core.Gui4jComponentInstance;
import org.gui4j.core.Gui4jMouseListener;
import org.gui4j.core.InternalElementRetriever;


/**
 * @author Joachim Schmid
 */
public final class Gui4jMouseListenerList extends Gui4jMouseListener implements ListSelectionListener
{
    
    private InternalElementRetriever mModel;
    private boolean mActive = true;

    private Gui4jCall mOnDoubleClick;    

    /**
     * Constructor for Gui4jMouseListenerList.
     * @param gui4jComponentInstance
     * @param model
     */
    public Gui4jMouseListenerList(Gui4jComponentInstance gui4jComponentInstance, InternalElementRetriever model)
    {
        super(gui4jComponentInstance);
        mModel = model;
    }

    private void performWork(final Gui4jCall call)
    {
        if (!mActive)
        {
            return;
        }
        
        JList list = (JList) mGui4jComponentInstance.getComponent();
        int[] rows = list.getSelectedIndices();
        int index = -1;
        Object value = null;
        if (rows.length == 1)
        {
            index = rows[0];
            value = mModel.getInternalElementAt(index);
        }
        Object[] items = new Object[rows.length];
        for (int i = 0; i < items.length; i++)
        {
            items[i] = mModel.getInternalElementAt(rows[i]);
        }
        final Map paramMap = new HashMap();
        paramMap.put(Const.PARAM_INDEX, new Integer(index));
        paramMap.put(Const.PARAM_INDICES, rows);
        paramMap.put(Const.PARAM_ITEM, value);
        paramMap.put(Const.PARAM_ITEMS, items);
        SwingUtilities.invokeLater(new Runnable() {

            public void run()
            {
                mGui4jThreadManager.performWork(mGui4jController, call, paramMap);
            }

        });
    }

    /**
     * @see java.awt.event.MouseListener#mouseClicked(MouseEvent)
     */
    public void mouseClicked(MouseEvent event)
    {
        if (event.getClickCount() == 2 && mOnDoubleClick != null)
        {
            performWork(mOnDoubleClick);
        }
    }

    /**
     * @see javax.swing.event.ListSelectionListener#valueChanged(ListSelectionEvent)
     */
    public void valueChanged(ListSelectionEvent e)
    {
        performWork(mOnClick);
    }

    public void setActive(boolean active)
    {
        mActive = active;
    }

    /**
     * @see org.gui4j.Gui4jDispose#dispose()
     */
    public void dispose()
    {
        super.dispose();
        mModel = null;
    }

    public void setOnDoubleClick(Gui4jCall onDoubleClick) {
        mOnDoubleClick = onDoubleClick;
    }

}
