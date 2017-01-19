package org.gui4j.core.swing;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.FocusEvent;
import java.util.Map;

import javax.swing.CellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.gui4j.Gui4jCallBase;
import org.gui4j.Gui4jGetValue;
import org.gui4j.core.Gui4jComponentInstance;

public class Gui4jJTable extends JTable
{
    private final Gui4jTableListener mGui4jTableListener;
    private final Gui4jGetValue mAutomaticRefresh;

    /**
     * Constructor for Gui4jJTable.
     * 
     * @param dm
     * @param gui4jTableListener
     */
    public Gui4jJTable(TableModel dm, Gui4jTableListener gui4jTableListener)
    {
        super(dm);
        mGui4jTableListener = gui4jTableListener;
        mAutomaticRefresh = new Gui4jGetValue() {
            public Object getValue(Gui4jCallBase gui4jController, Map paramMap, Object defaultValue)
            {
                TableModel tableModel = getModel();
                if (tableModel instanceof AbstractTableModel)
                {
                    // follow Swing's single thread rule when updating
                    // components
                    final AbstractTableModel abstractTableModel = (AbstractTableModel) tableModel;
                    EventQueue.invokeLater(new Runnable() {
                        public void run()
                        {
                            Gui4jJTable table = Gui4jJTable.this;
                            int selectedCol = table.getSelectedColumn();
                            int selectedRow = table.getSelectedRow();
                            abstractTableModel.fireTableDataChanged();
                            if (selectedCol != -1 && selectedRow != -1
                                    && selectedRow < abstractTableModel.getRowCount()
                                    && selectedCol < abstractTableModel.getColumnCount())
                            {
                                table.setRowSelectionInterval(selectedRow, selectedRow);
                                table.setColumnSelectionInterval(selectedCol, selectedCol);
                            }
                        }
                    });
                }
                return null;
            }

            public Object getValueNoErrorChecking(Gui4jCallBase gui4jController, Map paramMap,
                    Gui4jComponentInstance componentInstance)
            {
                return getValue(gui4jController, paramMap, null);
            }

            public String toString()
            {
                return AbstractTableModel.class.getName() + ".fireTableDataChanged()";
            }

        };
    }

    public Gui4jGetValue getAutomaticRefreshAction()
    {
        return mAutomaticRefresh;
    }

    protected void processFocusEvent(FocusEvent e)
    {
        boolean ok = e.getID() == FocusEvent.FOCUS_LOST && cellEditor != null;
        Component c = e.getOppositeComponent();
        if (ok && c instanceof Gui4jCellEditor.CellEditorTextField)
        {
            Gui4jCellEditor.CellEditorTextField textField = (Gui4jCellEditor.CellEditorTextField) c;
            ok = textField.getCellEditor() != getCellEditor();
        }
        if (ok && c instanceof ComboBoxHorizontalScroll)
        {
            ComboBoxHorizontalScroll comboBoxHorizontalScroll = (ComboBoxHorizontalScroll) c;
            ok = comboBoxHorizontalScroll.getCellEditor() != getCellEditor();
        }
        if (ok && c instanceof JCheckBox)
        {
            CellEditor lCellEditor = getDefaultEditor(Boolean.class);
            if (lCellEditor instanceof DefaultCellEditor)
            {
                DefaultCellEditor defaultCellEditor = (DefaultCellEditor) lCellEditor;
                ok = defaultCellEditor.getComponent() != c;
            }
        }
        if (ok)
        {
            cellEditor.stopCellEditing();
        }
        
        super.processFocusEvent(e);
    }

    public void endCellEditing()
    {
        TableCellEditor lCellEditor = getCellEditor();
        if (lCellEditor != null)
        {
            lCellEditor.stopCellEditing();
        }
    }

    /**
     * @see javax.swing.JTable#getCellEditor(int, int)
     */
    public TableCellEditor getCellEditor(int row, int column)
    {
        TableColumn tableColumn = getColumnModel().getColumn(column);
        TableCellEditor editor = tableColumn.getCellEditor();
        if (editor == null)
        {
            Class c = getColumnClass(column);
            if (c.equals(Object.class))
            {
                Object o = getValueAt(row, column);
                if (o != null)
                {
                    c = getValueAt(row, column).getClass();
                }
            }
            editor = getDefaultEditor(c);
        }
        return mGui4jTableListener.getCellEditor(editor, row, column);
    }

    /*
     * (non-Javadoc comment)
     * 
     * @see javax.swing.JTable#getCellRenderer(int, int)
     */
    public TableCellRenderer getCellRenderer(int row, int column)
    {
        TableColumn tableColumn = getColumnModel().getColumn(column);
        TableCellRenderer renderer = tableColumn.getCellRenderer();
        if (renderer == null)
        {
            Class c = getColumnClass(column);
            if (c.equals(Object.class))
            {
                Object o = getValueAt(row, column);
                if (o != null)
                {
                    c = o.getClass();
                }
            }
            renderer = getDefaultRenderer(c);
        }
        return mGui4jTableListener.getCellRenderer(renderer, row, column);
    }

    /**
     * @see javax.swing.JTable#prepareEditor(TableCellEditor, int, int)
     */
    public Component prepareEditor(TableCellEditor editor, int row, int column)
    {
        Component component = super.prepareEditor(editor, row, column);
        mGui4jTableListener.prepareEditor(editor, row, column);
        return component;
    }

}
