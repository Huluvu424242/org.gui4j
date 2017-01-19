package org.gui4j.core.listener;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Map;

import javax.swing.JTextArea;

import org.gui4j.component.Gui4jTextArea;
import org.gui4j.core.Gui4jCall;
import org.gui4j.core.Gui4jComponentInstance;
import org.gui4j.core.Gui4jMap1;

public final class Gui4jListenerTextArea extends Gui4jActionListener implements FocusListener
{

    /**
     * Constructor for Gui4jListenerTextArea.
     * 
     * @param gui4jComponentInstance
     */
    public Gui4jListenerTextArea(Gui4jComponentInstance gui4jComponentInstance)
    {
        super(gui4jComponentInstance);
    }

    /**
     * @see java.awt.event.FocusListener#focusGained(FocusEvent)
     */
    public void focusGained(FocusEvent e)
    {
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
     * but the current value for a text area has not been set because no focus
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
        if (mActionPerformed != null)
        {
            JTextArea textArea = (JTextArea) mGui4jComponentInstance.getComponent();
            String value = textArea.getText();
            Map paramMap = new Gui4jMap1(Gui4jTextArea.PARAM_VALUE, value);
            Gui4jCall[] work = { mActionPerformed };
            mGui4jThreadManager
                    .performWorkSpecialSuccessHandling(mGui4jController, work, paramMap, mGui4jComponentInstance, !useWorkerThread, true);
        }
    }

}
