package org.gui4j.component;

import java.awt.Component;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxEditor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gui4j.Gui4jCallBase;
import org.gui4j.Gui4jDispose;
import org.gui4j.constants.Const;
import org.gui4j.core.Gui4jCall;
import org.gui4j.core.Gui4jComponentContainer;
import org.gui4j.core.Gui4jComponentInstance;
import org.gui4j.core.Gui4jMap1;
import org.gui4j.core.Gui4jQualifiedComponent;
import org.gui4j.core.Gui4jSwingContainer;
import org.gui4j.core.listener.Gui4jActionListenerComboBox;
import org.gui4j.core.swing.ComboBoxHorizontalScroll;
import org.gui4j.core.swing.Gui4jKeySelectionManager;
import org.gui4j.core.swing.TransformValue;
import org.gui4j.core.util.ComboBoxNullItem;

public final class Gui4jComboBox extends Gui4jJComponent
{
    private static final Log mLogger = LogFactory.getLog(Gui4jComboBox.class);

    protected Gui4jCall mRowValue;
    protected Gui4jCall mOnSelect;
    protected Gui4jCall mStringConvert;
    protected Gui4jCall mSelectedItem;
    protected Gui4jCall mNullItem;
    protected boolean mManualActionOnly;
    protected int mMaximumRowCount;

    /**
     * Constructor for Gui4jComboBox.
     * 
     * @param gui4jComponentContainer
     * @param id
     */
    public Gui4jComboBox(Gui4jComponentContainer gui4jComponentContainer, String id)
    {
        super(gui4jComponentContainer, ComboBoxHorizontalScroll.class, id);
    }

    public void setRowValue(Gui4jCall rowValue)
    {
        mRowValue = rowValue;
    }

    public void setSelectedItem(Gui4jCall selectedItem)
    {
        mSelectedItem = selectedItem;
    }

    public void setNullItem(Gui4jCall nullItem)
    {
        mNullItem = nullItem;
    }

    public void setManualActionOnly(boolean manualActionOnly)
    {
        mManualActionOnly = manualActionOnly;
    }
    
    public void setMaximumRowCount(int maximumRowCount) {
        mMaximumRowCount = maximumRowCount;
    }

    protected void setProperties(Gui4jComponentInstance gui4jComponentInstance)
    {
        super.setProperties(gui4jComponentInstance);
        // mLogger.debug("Setting comboBox properties of comboBox "+getId());
        ComboBoxHorizontalScroll comboBox = (ComboBoxHorizontalScroll) gui4jComponentInstance.getComponent();

        Gui4jActionListenerComboBox actionListener = null;
        if (mOnSelect != null)
        {
            ComboBoxModel model = comboBox.getModel();
            actionListener = new Gui4jActionListenerComboBox(gui4jComponentInstance, model);
            gui4jComponentInstance.getGui4jSwingContainer().addDispose(actionListener);
            actionListener.setActionPerformed(mOnSelect);
            comboBox.setActionListener(actionListener);
        }
        if (comboBox.getModel().getSize() > 0)
        {
            if (comboBox.getSelectedIndex() == -1)
            {
                // mLogger.debug("No selection active; setting selection index 0
                // of comboBox with id
                // "+getId());
                comboBox.setSelectedIndex(0);
            }
            else
            {
                comboBox.setSelectedIndex(comboBox.getSelectedIndex());
            }
        }
    }

    public void setOnSelect(Gui4jCall onSelect)
    {
        mOnSelect = onSelect;
    }

    public void setStringConvert(Gui4jCall stringConvert)
    {
        mStringConvert = stringConvert;
    }

    public void setArrayContent(JComboBox comboBox, Object[] content)
    {
        // TODO: setArrayContent should behave like setContent with regard to
        // activation/dactivation of action listener and retaining selection.
        comboBox.removeAllItems();
        for (int i = 0; i < content.length; i++)
        {
            comboBox.addItem(content[i]);
        }
    }

    public void setContent(Gui4jComponentInstance gui4jComponentInstance, Collection content)
    {
        ComboBoxHorizontalScroll comboBox = (ComboBoxHorizontalScroll) gui4jComponentInstance.getSwingComponent();
        // mLogger.debug("setContent called, selected index was: " +
        // comboBox.getSelectedIndex());
        Object selectItem = getSelectedItem(comboBox);

        Gui4jActionListenerComboBox actionListener = comboBox.getActionListener();
        if (actionListener != null)
        {
            actionListener.setActive(false);
        }
        comboBox.removeAllItems();
        if (mNullItem != null)
        {
            String text = (String) mNullItem.getValueNoParams(gui4jComponentInstance.getGui4jCallBase(), "(undefined)");
            if (text == null || text.length() == 0)
            {
                text = " "; // combobox can't deal correctly with null elements
                // and empty strings
            }
            comboBox.addItem(new ComboBoxNullItem(text));
        }
        if (content != null)
        {
            for (Iterator it = content.iterator(); it.hasNext();)
            {
                Object value = it.next();
                comboBox.addItem(value);
            }
        }
        if (actionListener != null)
        {
            actionListener.setActive(true);
        }

        if (mSelectedItem != null)
        {
            // Selektion wird durch Methodenaufruf bestimmt
            selectItem = mSelectedItem.getValueNoParams(gui4jComponentInstance.getGui4jCallBase(), null);
        }
        if (selectItem == null || (content != null && content.contains(selectItem)))
        {
            // mLogger.debug("Resetting selectedItem in setContent to
            // "+selectItem);
            setSelectedItem(comboBox, selectItem);
        }
    }

    public void setSelectedItem(JComboBox comboBox, Object item)
    {
        mLogger.debug("Setting selection to " + item + " of comboBox with id " + getId());
        Gui4jActionListenerComboBox actionListener = null;
        if (mManualActionOnly) {
            actionListener = ((ComboBoxHorizontalScroll)comboBox).getActionListener();
        }
        try
        {
            if (actionListener != null) {
                actionListener.setActive(false);
            }
            if (item != null)
            {
                comboBox.setSelectedItem(item);
            }
            else
            {
                if (mNullItem != null && comboBox.getItemCount() > 0)
                {
                    for (int index = 0; index < comboBox.getItemCount(); index++)
                    {
                        if (comboBox.getItemAt(index) instanceof ComboBoxNullItem)
                        {
                            comboBox.setSelectedIndex(index);
                            return;
                        }
                    }
                }
            }
        }
        finally
        {
            if (actionListener != null) {
                actionListener.setActive(true);
            }
        }
    }

    private Object getSelectedItem(JComboBox comboBox)
    {
        Object selection = comboBox.getSelectedItem();
        if (selection instanceof ComboBoxNullItem)
        {
            return null;
        }
        else
        {
            return selection;
        }
    }

    public void setEditable(JComboBox comboBox, boolean editable)
    {
        if (editable && mStringConvert == null && !String.class.isAssignableFrom(mRowValue.getResultClass()))
        {
            mLogger.warn("ComboBox (id " + getId() + "): Conversion from string to " + mRowValue.getResultClass()
                    + " is not defined");
        }
        comboBox.setEditable(editable);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.gui4j.core.Gui4jAbstractComponent#createComponentInstance(org.gui4j.core.Gui4jSwingContainer,
     *      org.gui4j.Gui4jCallBase, org.gui4j.core.Gui4jQualifiedComponent)
     */
    protected Gui4jComponentInstance createComponentInstance(Gui4jSwingContainer gui4jSwingContainer,
            final Gui4jCallBase gui4jCallBase, Gui4jQualifiedComponent gui4jComponentInPath)
    {
        ComboBoxHorizontalScroll comboBox = new ComboBoxHorizontalScroll();

        comboBox.setMaximumRowCount(mMaximumRowCount);
        
        TransformValue transformValue = new TransformValue() {
            public Object transform(Object value)
            {
                if (value instanceof ComboBoxNullItem)
                {
                    return ((ComboBoxNullItem) value).getText();
                }
                return value == null || mRowValue == null ? value : mRowValue.getValue(gui4jCallBase, new Gui4jMap1(
                        Const.PARAM_ITEM, value), null);
            }
        };

        ComboBoxEditor comboBoxEditor = new ComboBoxEditor(gui4jCallBase, transformValue);
        gui4jSwingContainer.addDispose(comboBoxEditor);
        comboBox.setEditor(comboBoxEditor);

        comboBox.setKeySelectionManager(new Gui4jKeySelectionManager(transformValue));
        ComboBoxCellRenderer cellRenderer = new ComboBoxCellRenderer(transformValue);
        gui4jSwingContainer.addDispose(cellRenderer);
        comboBox.setRenderer(cellRenderer);

        return new Gui4jComponentInstance(gui4jSwingContainer, comboBox, gui4jComponentInPath);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.gui4j.core.Gui4jComponent#dispose(org.gui4j.core.Gui4jComponentInstance)
     */
    public void dispose(Gui4jComponentInstance gui4jComponentInstance)
    {
        Gui4jActionListenerComboBox actionListenerComboBox = (Gui4jActionListenerComboBox) gui4jComponentInstance
                .getStorage(this);
        super.dispose(gui4jComponentInstance);
        JComboBox comboBox = (JComboBox) gui4jComponentInstance.getSwingComponent();
        if (actionListenerComboBox != null)
        {
            actionListenerComboBox.setActive(false);
        }
        if (comboBox != null)
        {
            // MA 05.11.04: comboBox.setEditor(null) seems to lead to a
            // NullPointerException cause
            // editor is removed later by swing
            // comboBox.setEditor(null);
            comboBox.setRenderer(null);
            comboBox.setSelectedItem(null);
            comboBox.removeAllItems();
        }
    }

    private class ComboBoxEditor extends BasicComboBoxEditor implements Gui4jDispose
    {
        private Gui4jCallBase mGui4jCallBase;
        private TransformValue mTransformValue;

        private ComboBoxEditor(Gui4jCallBase gui4jCallBase, TransformValue transformValue)
        {
            mGui4jCallBase = gui4jCallBase;
            mTransformValue = transformValue;
        }

        public Object getItem()
        {
            Object object = super.getItem();
            if (object != null && String.class.isAssignableFrom(object.getClass()) && mStringConvert != null)
            {
                return mStringConvert.getValueUseDefaultParam(mGui4jCallBase, object, null);
            }
            return object;
        }

        public void setItem(Object anObject)
        {
            if ((anObject != null) && !(String.class.isAssignableFrom(anObject.getClass())))
            {
                anObject = mTransformValue.transform(anObject);
                // anObject = mRowValue.getValue(mGui4jCallBase, new
                // Gui4jMap1(PARAM_ITEM, anObject), null);
            }
            super.setItem(anObject);
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.gui4j.Gui4jDispose#dispose()
         */
        public void dispose()
        {
            mGui4jCallBase = null;
            mTransformValue = null;
        }

    }

    private class ComboBoxCellRenderer extends DefaultListCellRenderer implements Gui4jDispose
    {
        private TransformValue mTransformValue;

        private ComboBoxCellRenderer(TransformValue transformValue)
        {
            mTransformValue = transformValue;
        }

        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                boolean cellHasFocus)
        {
            return super.getListCellRendererComponent(list, mTransformValue.transform(value), index, isSelected,
                    cellHasFocus);
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.gui4j.Gui4jDispose#dispose()
         */
        public void dispose()
        {
            mTransformValue = null;
        }

    }

}