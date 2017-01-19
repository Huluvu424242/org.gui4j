package org.gui4j.core.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.util.Map;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import org.gui4j.Gui4jCallBase;
import org.gui4j.core.Gui4jCall;


public class Gui4jCellEditor extends DefaultCellEditor
{

    // infos for notification callback of temporary edit value
    private Gui4jCallBase mNotifyCallBase;
    private Gui4jCall mNotifyCall;
    private Map mNotifyParams;
    private Object mNotifyParamsValueKey;

    public static Gui4jCellEditor createTextEditor(Font font, boolean withBorder)
    {
        NotificationDocument document = new NotificationDocument();
        CellEditorTextField textField = new CellEditorTextField(document, withBorder);
        textField.setBorder(new LineBorder(Color.black));
        textField.setFont(font);
        Gui4jCellEditor editor = new Gui4jCellEditor(textField);
        textField.cellEditor = editor;
        document.setCellEditor(editor);
        return editor;
    }

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
        int column)
    {
        Component c = super.getTableCellEditorComponent(table, value, isSelected, row, column);
        JTextField textField = (JTextField) c;
        textField.selectAll();
        return c;
    }

    public void setNotificationCallback(Gui4jCallBase gui4jController, Gui4jCall notifyTempValue, Map params,
            Object paramsValueKey)
    {
        this.mNotifyCallBase = gui4jController;
        this.mNotifyCall = notifyTempValue;
        this.mNotifyParams = params;
        this.mNotifyParamsValueKey = paramsValueKey;
    }

    /**
     * Method called by an edit component to notify the cell editor about the
     * current temporary (non-committed) value of the editing component.
     * 
     * @param value
     */
    public void notifyTempValue(String value)
    {
        // use call back method to pass value on to client controller
        if (mNotifyCall != null)
        {
            mNotifyParams.put(mNotifyParamsValueKey, value);
            mNotifyCall.getValue(mNotifyCallBase, mNotifyParams, null);
        }
    }

    /**
     * Constructor for CellEditor.
     * 
     * @param textField
     */
    private Gui4jCellEditor(final JTextField textField)
    {
        super(textField);
    }

    public static final class CellEditorTextField extends JTextField
    {
        protected javax.swing.CellEditor cellEditor;
        private final boolean withBorder;

        public CellEditorTextField(boolean withBorder)
        {
            this(null, withBorder);
        }

        public CellEditorTextField(NotificationDocument document, boolean withBorder)
        {
            super(document, null, 0);
            this.withBorder = withBorder;
        }

        public void setBorder(Border border)
        {
            if (withBorder)
            {
                super.setBorder(border);
            }
        }

        public javax.swing.CellEditor getCellEditor()
        {
            return cellEditor;
        }

        public void setCellEditor(javax.swing.CellEditor cellEditor)
        {
            this.cellEditor = cellEditor;
        }

        protected void processFocusEvent(FocusEvent e)
        {
            super.processFocusEvent(e);
            if (e.getID() == FocusEvent.FOCUS_LOST && !(e.getOppositeComponent() instanceof JWindow))
            {
                if (cellEditor != null)
                {
                    cellEditor.stopCellEditing();
                }
            }
        }

        public void replaceSelection(String content) {
            super.replaceSelection(content);
            if(cellEditor instanceof Gui4jCellEditor) {
                Gui4jCellEditor editor = (Gui4jCellEditor) cellEditor;
                editor.notifyTempValue(getText());
            }
        }

    }

    private static final class NotificationDocument extends PlainDocument
    {

        private Gui4jCellEditor cellEditor;
        
        public NotificationDocument()
        {
            super();
        }
        
        public void setCellEditor(Gui4jCellEditor cellEditor)
        {
            this.cellEditor = cellEditor;
        }

        public void insertString(int offs, String str, AttributeSet a) throws BadLocationException
        {
            super.insertString(offs, str, a);
            //notifyCellEditor();
        }

        public void remove(int offs, int len) throws BadLocationException
        {
            super.remove(offs, len);
            //notifyCellEditor();
        }
        
        /*
        private void notifyCellEditor() throws BadLocationException
        {
            // notify cell editor of current temporary (non-committed) value
            if (cellEditor != null)
            {
                    String value = getText(0, getLength());
                    cellEditor.notifyTempValue(value);          
            }
        }
        */
        
    }

}