package org.gui4j.core.listener;

import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.SwingUtilities;

import org.gui4j.core.Gui4jComponentInstance;
import org.gui4j.core.Gui4jMouseListener;


/**
 * MouseListener um bei der rechten Maustaste das Popup anzuzeigen und
 * um vorher die Selektion zu setzen.
 */
public class Gui4jMouseListenerTablePopup extends Gui4jMouseListener
{
    /**
     * @param gui4jComponentInstance
     */
    public Gui4jMouseListenerTablePopup(Gui4jComponentInstance gui4jComponentInstance)
    {
        super(gui4jComponentInstance);
    }

    public void mousePressed(MouseEvent e)
    {
        selectOnRightClick(e);
        super.mousePressed(e);
    }

    private void selectOnRightClick(MouseEvent e)
    {
		if (!SwingUtilities.isRightMouseButton(e))
		{
			return;
		}
        Point p = e.getPoint();
        JTable table = (JTable) mGui4jComponentInstance.getSwingComponent();
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
}