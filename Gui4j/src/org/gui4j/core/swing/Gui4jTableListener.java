package org.gui4j.core.swing;

import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

public interface Gui4jTableListener
{
    void prepareEditor(TableCellEditor editor, int row, int column);
    TableCellEditor getCellEditor(TableCellEditor editor, int row, int column);
    TableCellRenderer getCellRenderer(TableCellRenderer renderer, int row, int column);

}
