package org.gui4j.core.swing;

import java.awt.EventQueue;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import org.gui4j.Gui4jCallBase;
import org.gui4j.Gui4jGetValue;
import org.gui4j.core.Gui4jComponentInstance;

/**
 * Wird verwendet, um einzelne Zellen zu refreshen
 */
public class Gui4jRefreshTable implements Gui4jGetValue
{
    private final AbstractTableModel mTableModel;
    private final int mRow;
    private final int mCol;

    public Gui4jRefreshTable(AbstractTableModel tableModel, int row, int col)
    {
        this.mTableModel = tableModel;
        this.mRow = row;
        this.mCol = col;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.gui4j.Gui4jGetValue#getValue(org.gui4j.Gui4jCallBase,
     *      java.util.Map, java.lang.Object)
     */
    public Object getValue(Gui4jCallBase gui4jController, Map paramMap, Object defaultValue)
    {
        // follow Swing's single thread rule when updating components
        EventQueue.invokeLater(new Runnable() {
            public void run()
            {
                mTableModel.fireTableCellUpdated(mRow, mCol);
            }
        });
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.gui4j.Gui4jGetValue#getValueNoErrorChecking(org.gui4j.Gui4jCallBase,
     *      java.util.Map, java.lang.Object)
     */
    public Object getValueNoErrorChecking(Gui4jCallBase gui4jController, Map paramMap,
            Gui4jComponentInstance componentInstance)
    {
        return getValue(gui4jController, paramMap, null);
    }

    public String toString()
    {
        return AbstractTableModel.class.getName() + ".fireTableCellUpdated(" + mRow + ", " + mCol + ")";
    }

}
