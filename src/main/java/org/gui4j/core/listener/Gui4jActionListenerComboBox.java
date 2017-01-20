package org.gui4j.core.listener;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ComboBoxEditor;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.gui4j.constants.Const;
import org.gui4j.core.Gui4jComponentInstance;
import org.gui4j.core.util.ComboBoxNullItem;


/**
 * @author Joachim Schmid
 */
public final class Gui4jActionListenerComboBox extends Gui4jActionListener
{
    private static final Log log = LogFactory.getLog(Gui4jActionListenerComboBox.class);
    
    private final ComboBoxModel mModel;
    private boolean mActive = true;

    /**
     * Constructor for Gui4jActionListenerComboBox.
     * @param gui4jComponentInstance
     * @param model
     */
    public Gui4jActionListenerComboBox(Gui4jComponentInstance gui4jComponentInstance, ComboBoxModel model)
    {
        super(gui4jComponentInstance);
        mModel = model;
    }

    public void actionPerformed(ActionEvent e)
    {
        if (mActive && mActionPerformed != null)
        {
            JComboBox comboBox = (JComboBox) mGui4jComponentInstance.getComponent();
            int index = comboBox.getSelectedIndex();

            Object item;
            if (index == -1 && comboBox.isEditable())
            {
                ComboBoxEditor editor = comboBox.getEditor();
                item = editor.getItem();
            }
            else
            {

                item = mModel.getElementAt(index);
            }
            if (item instanceof ComboBoxNullItem)
            {
                item = null;
            }
            Map paramMap = new HashMap();
            paramMap.put(Const.PARAM_ITEM, item);
            paramMap.put(Const.PARAM_INDEX, new Integer(index));
            log.debug("About to initiate onSelect call.");
            mGui4jThreadManager.performWork(mGui4jController, mActionPerformed, paramMap, mGui4jComponentInstance);
        }
    }

    public void setActive(boolean active)
    {
        mActive = active;
    }

}