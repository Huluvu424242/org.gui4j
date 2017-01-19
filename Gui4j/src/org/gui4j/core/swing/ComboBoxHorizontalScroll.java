package org.gui4j.core.swing;

import java.awt.IllegalComponentStateException;
import java.awt.event.FocusEvent;
import java.lang.reflect.Field;

import javax.swing.CellEditor;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JWindow;
import javax.swing.plaf.ComboBoxUI;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.gui4j.core.listener.Gui4jActionListenerComboBox;

/**
 * ComboBox which supports vertical and horizontal scrollbars.
 */
public class ComboBoxHorizontalScroll extends JComboBox
{
    private static final Log log = LogFactory.getLog(ComboBoxHorizontalScroll.class);

    private CellEditor cellEditor;
    private Gui4jActionListenerComboBox actionListener;
    private boolean isAutoPopup;

    /**
     * @param model
     */
    public ComboBoxHorizontalScroll(ComboBoxModel model)
    {
        super(model);
    }

    public ComboBoxHorizontalScroll()
    {
        super();
    }

    public void updateUI()
    {
        super.updateUI();
        enableHorizontalScrollbar(getUI());
    }

    public CellEditor getCellEditor()
    {
        return cellEditor;
    }

    protected void processFocusEvent(FocusEvent e)
    {
        super.processFocusEvent(e);
        // TODO: what if whole application loses focus? stop editing then?
        if (e.getID() == FocusEvent.FOCUS_LOST && cellEditor != null && !(e.getOppositeComponent() instanceof JWindow)
                && !(e.getOppositeComponent() instanceof JScrollBar))
        {
            cellEditor.stopCellEditing();
        }
        if(e.getID() == FocusEvent.FOCUS_GAINED && isAutoPopup()) {
            try {
                if(!isVisible()) {
                    setVisible(true);
                }
                showPopup();
            }
            catch(IllegalComponentStateException exception) {
                log.error("ComboBoxHorizontalScroll autoExtend: Popup could not be shown.");
            }
        }
    }

    public void setCellEditor(CellEditor cellEditor)
    {
        this.cellEditor = cellEditor;
    }

    public void setActionListener(Gui4jActionListenerComboBox listener)
    {
        actionListener = listener;
        addActionListener(actionListener);
    }

    public Gui4jActionListenerComboBox getActionListener()
    {
        return actionListener;
    }

    private void enableHorizontalScrollbar(ComboBoxUI comboUI)
    {
        if (comboUI instanceof BasicComboBoxUI)
        {
            try
            {
                Field popupField = BasicComboBoxUI.class.getDeclaredField("popup");
                popupField.setAccessible(true);
                ComboPopup popup = (ComboPopup) popupField.get(comboUI);
                if (popup instanceof BasicComboPopup)
                {
                    popup.getList().setVisibleRowCount(getMaximumRowCount());
                    
                    Field scrollerField = BasicComboPopup.class.getDeclaredField("scroller");
                    scrollerField.setAccessible(true);
                    JScrollPane scroller = (JScrollPane) scrollerField.get(popup);
                    scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                    if (scroller.getHorizontalScrollBar() == null)
                    {
                        // workaround for JDK 1.5 Comboboxes that explicitly set their horizontal scrollbars to null
                        JScrollBar scrollBar = new JScrollBar(JScrollBar.HORIZONTAL);
                        scroller.setHorizontalScrollBar(scrollBar);
                    }
                    scroller.getHorizontalScrollBar().setFocusable(false);
                }
            }
            catch (Exception e)
            {
                log.warn("Could not enable horizontal scrollbar for combobox.", e);
            }
        }
    }

    public boolean isAutoPopup()
    {
        return isAutoPopup;
    }

    public void setAutoPopup(boolean isAutoPopup)
    {
        this.isAutoPopup = isAutoPopup;
    }

}
