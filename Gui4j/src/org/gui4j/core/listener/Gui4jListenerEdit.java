package org.gui4j.core.listener;

import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Map;

import javax.swing.JTextField;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.gui4j.component.Gui4jEdit;
import org.gui4j.core.Gui4jCall;
import org.gui4j.core.Gui4jComponentInstance;
import org.gui4j.core.Gui4jMap1;

/**
 */
public class Gui4jListenerEdit extends Gui4jActionListener implements FocusListener
{
    private static final Log log = LogFactory.getLog(Gui4jListenerEdit.class);
    
    private Gui4jCall mSetValue;
    private String lastContent = null;

    /**
     * Constructor for Gui4jListenerEdit.
     * 
     * @param gui4jComponentInstance
     */
    public Gui4jListenerEdit(Gui4jComponentInstance gui4jComponentInstance)
    {
        super(gui4jComponentInstance);
    }

    public void setSetValue(Gui4jCall setValue)
    {
        mSetValue = setValue;
    }

    public void actionPerformed(ActionEvent e)
    {
        JTextField textField = (JTextField) mGui4jComponentInstance.getComponent();
        String value = textField.getText();
        Map paramMap = new Gui4jMap1(Gui4jEdit.PARAM_VALUE, value);
        if (mSetValue != null)
        {
            Gui4jCall[] work = { mSetValue };
            lastContent = textField.getText();
            textField.selectAll();
            if (log.isDebugEnabled()) {
                log.debug("Asking ThreadManager to perform work for setValue.");
            }
            mGui4jThreadManager.performWorkHighPriority(mGui4jController, work, paramMap, mGui4jComponentInstance);
        }
        // if (mActionPerformed != null && mSetValue != null)
        // {
        // Gui4jCall[] work = { mSetValue, mActionPerformed };
        // mGui4jThreadManager.performWork(mGui4jController, work, paramMap,
        // mGui4jComponentInstance);
        // }
        if (mActionPerformed != null)
        {
            if (log.isDebugEnabled()) {
                log.debug("Asking ThreadManager to perform work for actionCommand.");
            }
            mGui4jThreadManager.performWork(mGui4jController, mActionPerformed, paramMap, mGui4jComponentInstance);
        }
        // if (mActionPerformed == null && mSetValue != null)
        // {
        // Gui4jCall[] work = { mSetValue };
        // lastContent = textField.getText();
        // textField.selectAll();
        // mGui4jThreadManager.performWork(mGui4jController, work, paramMap,
        // mGui4jComponentInstance);
        // }
    }

    /**
     * @see java.awt.event.FocusListener#focusGained(FocusEvent)
     */
    public void focusGained(FocusEvent e)
    {
        lastContent = null;
    }

    /**
     * @see java.awt.event.FocusListener#focusLost(FocusEvent)
     */
    public void focusLost(FocusEvent e)
    {
        saveValue(true);
    }

    /**
     * This method can be used directly by client code to manually circumvent
     * the problem where a worker thread needs to work with current model values
     * but the current value for an edit field has not been set because no focus
     * lost event has been triggered. In this case, the controller can get the
     * currently focused Swing component from
     * {@link java.awt.KeyboardFocusManager#getFocusOwner()}, check for the
     * presence of a listener of this type and call this method. <br>
     * Note: This method is regarded as a workaround for a problem that should
     * really be solved conceptually in Gui4j.
     */
    public void saveValueImmediately()
    {
        saveValue(false);
    }

    private void saveValue(boolean useWorkerThread)
    {
        if (mSetValue != null)
        {
            JTextField textField = (JTextField) mGui4jComponentInstance.getComponent();
            String fieldValue = textField.getText();
            if (!fieldValue.equals(lastContent))
            {
                Map paramMap = new Gui4jMap1(Gui4jEdit.PARAM_VALUE, fieldValue);
                Gui4jCall[] work = { mSetValue };
                lastContent = fieldValue;
                textField.selectAll();
                mGui4jThreadManager.performWorkHighPriority(mGui4jController, work, paramMap, mGui4jComponentInstance,
                        !useWorkerThread);
            }
        }
    }

}
