package org.gui4j.core.listener;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.gui4j.Gui4jCallBase;
import org.gui4j.Gui4jDispose;
import org.gui4j.constants.Const;
import org.gui4j.core.Gui4jCall;
import org.gui4j.core.Gui4jComponentInstance;
import org.gui4j.core.Gui4jThreadManager;
import org.gui4j.core.Gui4jTypeManager;
import org.gui4j.core.swing.RowRetriever;

/**
 * @author Joachim Schmid
 */
public final class Gui4jMouseListenerTable implements MouseListener, ListSelectionListener, Gui4jDispose
{
    protected Gui4jThreadManager mGui4jThreadManager;
    protected Gui4jCallBase mGui4jController;

    private final int mClicks;
    private Class mDefaultType;
    private JTable mTable;
    private RowRetriever mTableModel;
    private Gui4jTypeManager mAction; // Class -> Gui4jAccess
    private boolean mActive = true;

    /**
     * Constructor for Gui4jMouseListenerTables.
     * 
     * @param gui4jComponentInstance
     * @param clicks
     * @param defaultType
     * @param action
     */
    public Gui4jMouseListenerTable(Gui4jComponentInstance gui4jComponentInstance, int clicks, Class defaultType,
            Gui4jTypeManager action)
    {
        mTable = (JTable) gui4jComponentInstance.getSwingComponent();
        mTableModel = (RowRetriever) mTable.getModel();
        mGui4jThreadManager = gui4jComponentInstance.getGui4j().getGui4jThreadManager();
        mGui4jController = gui4jComponentInstance.getGui4jCallBase();
        mClicks = clicks;
        mDefaultType = defaultType;
        mAction = action;
    }

    private void performWork()
    {
        if (!mActive)
        {
            return;
        }
        final Map paramMap = new HashMap();
        final Gui4jCall work = getGui4jCall(paramMap);
        if (work != null)
        {
            SwingUtilities.invokeLater(new Runnable() {

                public void run()
                {
                    mGui4jThreadManager.performWork(mGui4jController, work, paramMap);
                }

            });
        }
    }

    public Gui4jCall getGui4jCall(Map paramMap)
    {
        if (mTable == null)
        {
            return null;
        }
        int[] rows = mTable.getSelectedRows();
        int[] columns = mTable.getSelectedColumns();
        int colIndex = mTable.getSelectedColumn();
        int rowIndex = -1;
        Object value = null;
        if (rows.length == 1)
        {
            rowIndex = rows[0];
            if (rowIndex >= mTable.getRowCount())
            {
                value = null;
            }
            else
            {
                value = mTableModel.getRow(rowIndex);
            }
        }
        Gui4jCall call = getAction(value == null ? mDefaultType : value.getClass());
        if (call != null)
        {
            paramMap.put(Const.PARAM_ROW_INDEX, new Integer(rowIndex));
            paramMap.put(Const.PARAM_ROW_INDICES, rows);
            paramMap.put(Const.PARAM_COL_INDEX, new Integer(colIndex));
            paramMap.put(Const.PARAM_COL_INDICES, columns);
            paramMap.put(Const.PARAM_ITEM, value);
        }
        return call;
    }

    /**
     * @see java.awt.event.MouseListener#mouseClicked(MouseEvent)
     */
    public void mouseClicked(MouseEvent event)
    {
        if (event.getClickCount() == mClicks)
        {
            performWork();
        }
    }

    /**
     * @see javax.swing.event.ListSelectionListener#valueChanged(ListSelectionEvent)
     */
    public void valueChanged(ListSelectionEvent e)
    {
        performWork();
    }

    /**
     * @see java.awt.event.MouseListener#mouseEntered(MouseEvent)
     */
    public void mouseEntered(MouseEvent e)
    {
    }

    /**
     * @see java.awt.event.MouseListener#mouseExited(MouseEvent)
     */
    public void mouseExited(MouseEvent e)
    {
    }

    /**
     * @see java.awt.event.MouseListener#mousePressed(MouseEvent)
     */
    public void mousePressed(MouseEvent e)
    {
    }

    /**
     * @see java.awt.event.MouseListener#mouseReleased(MouseEvent)
     */
    public void mouseReleased(MouseEvent e)
    {
    }

    public void setActive(boolean active)
    {
        mActive = active;
    }

    private Gui4jCall getAction(Class classType)
    {
        return (Gui4jCall) mAction.get(classType);
    }

    /**
     * @see org.gui4j.Gui4jDispose#dispose()
     */
    public void dispose()
    {
        mGui4jThreadManager = null;
        mGui4jController = null;

        mDefaultType = null;
        mTable = null;
        mTableModel = null;
        mAction = null;
    }

}