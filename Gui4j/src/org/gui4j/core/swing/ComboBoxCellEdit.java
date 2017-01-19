package org.gui4j.core.swing;

import java.awt.Component;
import java.awt.Font;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.swing.CellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxEditor;

import org.gui4j.Gui4jCallBase;
import org.gui4j.core.Gui4jCall;
import org.gui4j.core.util.ComboBoxNullItem;


public class ComboBoxCellEdit extends DefaultCellEditor
{
    private final JComboBox mComboBox;
    private final CellComboBoxModel mComboBoxModel;
    protected final Gui4jCallBase mGui4jController;
    protected final String mParamName;
    protected final Gui4jCall mValue;
    protected final Gui4jCall mStringConvert;
    private Font mFont;
    protected Map mParamMap;

    private static final int MAX_ROW_COUNT_DEFAULT = 8;
    
    public static ComboBoxCellEdit getInstance(
        final Gui4jCallBase gui4jController,
        final Gui4jCall value,
        final Gui4jCall stringConvert,
        final String paramName,
        Font font)
    {
        return getInstance(gui4jController, value, stringConvert, paramName, font, MAX_ROW_COUNT_DEFAULT);
    }
    
    public static ComboBoxCellEdit getInstance(
            final Gui4jCallBase gui4jController,
            final Gui4jCall value,
            final Gui4jCall stringConvert,
            final String paramName,
            Font font,
            int maximumRowCount)
        {
            ComboBoxHorizontalScroll comboBox = new ComboBoxHorizontalScroll();
            comboBox.setMaximumRowCount(maximumRowCount);
            ComboBoxCellEdit comboBoxCellEdit =
                new ComboBoxCellEdit(comboBox, gui4jController, value, stringConvert, paramName, font);
            comboBox.setCellEditor(comboBoxCellEdit);
            return comboBoxCellEdit;
        }

    public void setEditable(boolean editable)
    {
        mComboBox.setEditable(editable);
    }

    public void setContent(Collection collection, String nullItemText)
    {
        int idx = mComboBox.getSelectedIndex();
        mComboBoxModel.setContent(collection, nullItemText);
        if (idx != -1 && idx < collection.size())
        {
            mComboBox.setSelectedIndex(idx);
        }
        else
        {
            Object item = getCellEditorValue();
            idx = mComboBoxModel.getIndexOf(item);
            if (idx != -1)
            {
                mComboBox.setSelectedIndex(idx);
            }
            else
            {
            }
        }

    }

    public void setParamMap(Map paramMap)
    {
        mParamMap = paramMap;
    }

    public void setSelectedItem(Object item)
    {
        if (item != null)
        {
            mComboBox.setSelectedItem(item);
        }
        else if (mComboBox.getItemCount() > 0)
        {
            for (int index = 0; index < mComboBox.getItemCount(); index++)
            {
                if (mComboBox.getItemAt(index) instanceof ComboBoxNullItem)
                {
                    mComboBox.setSelectedIndex(index);
                    return;
                }
            }
        }
    }
    
    public void setMaximumRowCount(int maximumRowCount) {
        mComboBox.setMaximumRowCount(maximumRowCount);
    }

    private ComboBoxCellEdit(
        final JComboBox comboBox,
        final Gui4jCallBase gui4jController,
        final Gui4jCall value,
        final Gui4jCall stringConvert,
        final String paramName,
        Font font)
    {
        super(comboBox);
        mFont = font;
        mGui4jController = gui4jController;
        mComboBox = comboBox;
        mParamName = paramName;
        mValue = value;
        mStringConvert = stringConvert;
        ComboBoxCellEditFocus editor = new ComboBoxCellEditFocus();
        comboBox.setFont(mFont);
        comboBox.setEditor(editor);
        editor.setCellEditor(this);

        final TransformValue transformValue = new TransformValue()
        {
            public Object transform(Object pValue)
            {
                if (pValue instanceof ComboBoxNullItem)
                {
                    return ((ComboBoxNullItem) pValue).getText();
                }
                if (mParamMap != null)
                {
                    Object oldValue = mParamMap.get(paramName);
                    mParamMap.put(paramName, pValue);
                    Object val = mValue.getValue(mGui4jController, mParamMap, pValue);
                    if (oldValue == null)
                    {
                        mParamMap.remove(paramName);
                    }
                    else
                    {
                        mParamMap.put(paramName, oldValue);
                    }
                    return val;
                }
                else
                {
                    return pValue;
                }
            }
        };

        comboBox.setKeySelectionManager(new Gui4jKeySelectionManager(transformValue));
        DefaultListCellRenderer cellRenderer = new DefaultListCellRenderer()
        {
            public Component getListCellRendererComponent(
                JList list,
                Object pValue,
                int index,
                boolean isSelected,
                boolean cellHasFocus)
            {
                return super.getListCellRendererComponent(
                    list,
                    transformValue.transform(pValue),
                    index,
                    isSelected,
                    cellHasFocus);
            }
        };
        mComboBox.setRenderer(cellRenderer);
        mComboBoxModel = new CellComboBoxModel();
        mComboBox.setModel(mComboBoxModel);
    }

    public void setFont(Font font)
    {
        mFont = font;
        mComboBox.setFont(font);
    }

    // *******************************************************************************

    private class CellComboBoxModel extends DefaultComboBoxModel
    {
        public void setContent(Collection collection, String nullItemText)
        {
            removeAllElements();
            if (nullItemText != null)
            {
                addElement(new ComboBoxNullItem(nullItemText));
            }
            if (collection != null)
            {
                for (Iterator it = collection.iterator(); it.hasNext();)
                {
                    Object element = it.next();
                    addElement(element);
                }
            }
        }

    }

    public final class ComboBoxCellEditFocus extends BasicComboBoxEditor
    {
        private final org.gui4j.core.swing.Gui4jCellEditor.CellEditorTextField cellEditorTextField;
        public ComboBoxCellEditFocus()
        {
            super();
            cellEditorTextField = new org.gui4j.core.swing.Gui4jCellEditor.CellEditorTextField(false);
            editor = cellEditorTextField;
        }

        /**
         * @see javax.swing.ComboBoxEditor#getItem()
         */
        public Object getItem()
        {
            Object object = super.getItem();
            if (object != null && String.class.isAssignableFrom(object.getClass()) && mStringConvert != null)
            {
                return mStringConvert.getValueUseDefaultParam(mGui4jController, object, null);
            }
            return object;
        }

        /**
         * @see javax.swing.ComboBoxEditor#setItem(Object)
         */
        public void setItem(Object anObject)
        {
            // System.out.println("setItem: anObject=" + anObject);
            if (anObject instanceof ComboBoxNullItem) {
                anObject = ((ComboBoxNullItem) anObject).getText();
            }
            if ((anObject != null) && (mParamMap != null) && !(String.class.isAssignableFrom(anObject.getClass())))
            {
                mParamMap.put(mParamName, anObject);
                anObject = mValue.getValue(mGui4jController, mParamMap, null);
            }
            super.setItem(anObject);
        }

        public void setCellEditor(CellEditor cellEditor)
        {
            cellEditorTextField.setCellEditor(cellEditor);
        }
    }

}
