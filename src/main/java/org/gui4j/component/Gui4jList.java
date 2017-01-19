package org.gui4j.component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gui4j.Gui4jCallBase;
import org.gui4j.constants.Const;
import org.gui4j.core.Gui4jCall;
import org.gui4j.core.Gui4jComponentContainer;
import org.gui4j.core.Gui4jComponentInstance;
import org.gui4j.core.Gui4jQualifiedComponent;
import org.gui4j.core.Gui4jSwingContainer;
import org.gui4j.core.InternalElementRetriever;
import org.gui4j.core.listener.Gui4jMouseListenerList;


public final class Gui4jList extends Gui4jJComponent
{
    protected static final Log mLogger = LogFactory.getLog(Gui4jList.class);

    protected Gui4jCall mRowValue;
    private Gui4jCall mOnSelect;
    private Gui4jCall mOnDoubleClick;
    private Gui4jCall mSelectedItemsCall;
    private Gui4jCall mSelectedItemCall;
    private final int mVisibleRowCount;

    private int mListSelectionMode = ListSelectionModel.SINGLE_SELECTION;

    /**
     * Constructor for Gui4jList.
     * @param gui4jComponentContainer
     * @param id
     * @param visibleRowCount
     */
    public Gui4jList(Gui4jComponentContainer gui4jComponentContainer, String id, int visibleRowCount)
    {
        super(gui4jComponentContainer, JList.class, id);
        mVisibleRowCount = visibleRowCount;
    }

    public void setRowValue(Gui4jCall rowValue)
    {
        mRowValue = rowValue;
    }

    public void setOnSelect(Gui4jCall onSelect)
    {
        mOnSelect = onSelect;
    }

    public void setOnDoubleClick(Gui4jCall onDoubleClick) {
        mOnDoubleClick = onDoubleClick;
    }

    protected void setProperties(Gui4jComponentInstance gui4jComponentInstance)
    {
        JList list = (JList) gui4jComponentInstance.getComponent();
        list.setSelectionMode(mListSelectionMode);
        super.setProperties(gui4jComponentInstance);
        if (mOnSelect != null || mOnDoubleClick != null)
        {
            Gui4jListModel model = (Gui4jListModel) list.getModel();
            Gui4jMouseListenerList mouseListener = new Gui4jMouseListenerList(gui4jComponentInstance, model);
            gui4jComponentInstance.getGui4jSwingContainer().addDispose(mouseListener);

            // add listener to model for later retrieval
            model.setListener(mouseListener);
            
            if (mOnSelect != null) {
                mouseListener.setOnClick(mOnSelect);
                list.addListSelectionListener(mouseListener);
            }
            if (mOnDoubleClick != null) {
                mouseListener.setOnDoubleClick(mOnDoubleClick);
                list.addMouseListener(mouseListener);
            }                                
        }

        if (mVisibleRowCount != 0)
        {
            if (mVisibleRowCount == -1)
            {
                int numberOfItems = list.getModel().getSize();
                if (numberOfItems > 0)
                {
                    list.setVisibleRowCount(numberOfItems);
                }
            }
            else
            {
                list.setVisibleRowCount(mVisibleRowCount);
            }
        }
    }

    public void setSelectionMode(int selectionMode)
    {
        mListSelectionMode = selectionMode;
    }

    public void setArrayContent(JList list, Object[] content)
    {
        Gui4jListModel model = (Gui4jListModel) list.getModel();
        model.removeAllElements();
        if (content != null)
        {
            for (int i = 0; i < content.length; i++)
            {
                model.addElement(content[i]);
            }
        }
        list.repaint();
    }

    public void setContent(Gui4jComponentInstance gui4jComponentInstance, Collection content)
    {
        //mLogger.debug("setContent called with: " + content);

        JList list = (JList) gui4jComponentInstance.getSwingComponent();

        Gui4jListModel model = (Gui4jListModel) list.getModel();
        Gui4jMouseListenerList listener = model.getListener();

        // deactivate listener to prevent updates caused by refill of content
        if (listener != null)
        {
            listener.setActive(false);
        }

        // remember selected items
        Collection selectedItems = new ArrayList();
        int[] selectedIndices = getSelectedIndices(list);
        for (int i = 0; i < selectedIndices.length; i++)
        {
            selectedItems.add(model.getInternalElementAt(i));
        }

        model.removeAllElements();
        if (content != null)
        {
            for (Iterator it = content.iterator(); it.hasNext();)
            {
                Object value = it.next();
                model.addElement(value);
            }
        }

        if (listener != null)
        {
            listener.setActive(true);
        }

        if (mSelectedItemsCall != null)
        {
            selectedItems =
                (Collection) mSelectedItemsCall.getValueNoParams(gui4jComponentInstance.getGui4jCallBase(), null);
        } else if (mSelectedItemCall != null) {
            Object selectedItem = mSelectedItemCall.getValueNoParams(gui4jComponentInstance.getGui4jCallBase(), null);
            selectedItems = null;
            setSelectedItem(list, selectedItem);
        }
        if (selectedItems != null)
        {
            setSelectedItems(list, selectedItems);
        }

        list.repaint();
    }

    public void setSelectedIndices(JList list, int[] selectedIndices)
    {
        list.setSelectedIndices(selectedIndices);
    }

    public void setSelectedItems(JList list, Collection elements)
    {
        //mLogger.debug("setSelectedItems called with: " + elements);
        int[] indices = new int[elements == null ? 0 : elements.size()];
        if (elements != null)
        {
            Gui4jListModel model = (Gui4jListModel) list.getModel();
            int count = 0;
            for (Iterator it = elements.iterator(); it.hasNext();)
            {
                Object object = it.next();
                int idx = model.indexOf(object);
                if (idx != -1)
                {
                    indices[count] = idx;
                    count++;
                }
            }
            if (count != elements.size())
            {
                int[] newIndices = new int[count];
                for (int i = 0; i < count; i++)
                {
                    newIndices[i] = indices[i];
                }
                indices = newIndices;
            }
            if (elements.size() == 1 && indices.length > 0)
            {
            	list.scrollRectToVisible(list.getCellBounds(indices[0],indices[0]+1));
            }
        }

        list.setSelectedIndices(indices);
    }

    public void setSelectedItem(JList list, Object element)
    {
        Gui4jListModel model = (Gui4jListModel) list.getModel();
        int index = model.indexOf(element);
        if (index != -1)
        {
            list.setSelectedIndex(index);
            list.scrollRectToVisible(list.getCellBounds(index,index+1));
        }
    }

    private int[] getSelectedIndices(JList list)
    {
        return list.getSelectedIndices();
    }

    /* (non-Javadoc)
     * @see org.gui4j.core.Gui4jAbstractComponent#createComponentInstance(org.gui4j.core.Gui4jSwingContainer, org.gui4j.Gui4jCallBase, org.gui4j.core.Gui4jQualifiedComponent)
     */
    protected Gui4jComponentInstance createComponentInstance(
        Gui4jSwingContainer gui4jSwingContainer,
        Gui4jCallBase gui4jCallBase,
        Gui4jQualifiedComponent gui4jComponentInPath)
    {
        Gui4jListModel model = new Gui4jListModel(gui4jCallBase);
        JList list = new JList();
        list.setModel(model);
        return new Gui4jComponentInstance(gui4jSwingContainer, list, gui4jComponentInPath);
    }

    /**
     * @see org.gui4j.core.Gui4jComponent#dispose(Gui4jComponentInstance)
     */
    public void dispose(Gui4jComponentInstance gui4jComponentInstance)
    {
        super.dispose(gui4jComponentInstance);
        JList list = (JList) gui4jComponentInstance.getSwingComponent();
        Gui4jListModel model = (Gui4jListModel) list.getModel();
        model.dispose();
    }

    public void setSelectedItemsCall(Gui4jCall selectedItemsCall)
    {
        mSelectedItemsCall = selectedItemsCall;
    }

    public void setSelectedItemCall(Gui4jCall selectedItemCall) {
        mSelectedItemCall = selectedItemCall;
    }

    public class Gui4jListModel extends DefaultListModel implements InternalElementRetriever
    {
        private Gui4jCallBase mGui4jController;

        private Gui4jMouseListenerList mMouseListener;

        public Gui4jListModel(Gui4jCallBase gui4jController)
        {
            mGui4jController = gui4jController;
        }

        public Object getInternalElementAt(int index)
        {
            return super.getElementAt(index);
        }

        public Object getElementAt(int index)
        {
            Object value = super.getElementAt(index);
            if (value != null)
            {
                Map paramMap = new HashMap();
                paramMap.put(Const.PARAM_INDEX, new Integer(index));
                paramMap.put(Const.PARAM_ITEM, value);
                if (mRowValue == null)
                {
                    mLogger.warn(getId() + ": rowValue not defined");
                }
                return mRowValue == null ? null : mRowValue.getValue(mGui4jController, paramMap, null);
            }
            else
            {
                return null;
            }
        }

        public void dispose()
        {
            if (mMouseListener != null)
            {
                mMouseListener.setActive(false);
            }
            mMouseListener = null;
            mGui4jController = null;
            removeAllElements();
        }

        public void setListener(Gui4jMouseListenerList mouseListener)
        {
            mMouseListener = mouseListener;
        }

        public Gui4jMouseListenerList getListener()
        {
            return mMouseListener;
        }

    }

}
