package org.gui4j.core.listener;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.gui4j.Gui4jCallBase;
import org.gui4j.core.Gui4jComponentInstance;
import org.gui4j.core.Gui4jThreadManager;

/**
 * @author Joachim Schmid
 */
public final class Gui4jMouseListenerMatrix implements MouseListener, ListSelectionListener
{
    protected final Gui4jThreadManager mGui4jThreadManager;
    protected final Gui4jCallBase mGui4jController;

    private final int mClicks;
    private final JTable mTable;
    private final CellListener mCellListener;

    private boolean valueChangeActive = true;

    /**
     * Constructor for Gui4jMouseListenerTables.
     * 
     * @param gui4jComponentInstance
     * @param clicks
     * @param cellListener
     */
    public Gui4jMouseListenerMatrix(Gui4jComponentInstance gui4jComponentInstance, int clicks, CellListener cellListener)
    {
        mTable = (JTable) gui4jComponentInstance.getSwingComponent();
        mCellListener = cellListener;
        mGui4jThreadManager = gui4jComponentInstance.getGui4j().getGui4jThreadManager();
        mGui4jController = gui4jComponentInstance.getGui4jCallBase();
        mClicks = clicks;
    }

    private void performWork()
    {
        final int[] rows = mTable.getSelectedRows();
        final int[] columns = mTable.getSelectedColumns();
        SwingUtilities.invokeLater(new Runnable() {

            public void run()
            {
                mCellListener.handle(rows, columns, mGui4jController, mGui4jThreadManager);
            }

        });
    }

    /*
     * @see java.awt.event.MouseListener#mouseClicked(MouseEvent)
     */
    public void mouseClicked(MouseEvent event)
    {
        if (event.getClickCount() == mClicks)
        {
            performWork();
        }
    }

    /*
     * @see javax.swing.event.ListSelectionListener#valueChanged(ListSelectionEvent)
     */
    public void valueChanged(ListSelectionEvent e)
    {
        if (valueChangeActive)
        {
            performWork();
        }
    }

    public void setValueChangeActive(boolean active)
    {
        this.valueChangeActive = active;
    }

    /*
     * @see java.awt.event.MouseListener#mouseEntered(MouseEvent)
     */
    public void mouseEntered(MouseEvent e)
    {
    }

    /*
     * @see java.awt.event.MouseListener#mouseExited(MouseEvent)
     */
    public void mouseExited(MouseEvent e)
    {
    }

    /*
     * @see java.awt.event.MouseListener#mousePressed(MouseEvent)
     */
    public void mousePressed(MouseEvent e)
    {
        selectOnRightClick(e);
    }

    /*
     * @see java.awt.event.MouseListener#mouseReleased(MouseEvent)
     */
    public void mouseReleased(MouseEvent e)
    {
    }

    private void selectOnRightClick(MouseEvent e)
    {
        if (!SwingUtilities.isRightMouseButton(e))
        {
            return;
        }
        Point p = e.getPoint();
        JTable table = mTable;
        int row = table.rowAtPoint(p);
        int column = table.columnAtPoint(p);

        // The autoscroller can generate drag events outside the Table's range.
        if ((column == -1) || (row == -1))
        {
            return;
        }

        if (table.isRequestFocusEnabled())
        {
            table.requestFocus();
        }

        table.changeSelection(row, column, e.isControlDown(), e.isShiftDown());
    }

    // ******************************************************************

    public interface CellListener
    {
        void handle(int[] row, int[] col, Gui4jCallBase gui4jController, Gui4jThreadManager gui4jThreadManager);
    }

}
