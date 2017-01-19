package org.gui4j.core.listener;

import java.awt.event.ActionEvent;
import java.util.Map;

import javax.swing.AbstractButton;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.gui4j.constants.Const;
import org.gui4j.core.Gui4jComponentInstance;
import org.gui4j.core.Gui4jMap1;

public class Gui4jActionListenerButton extends Gui4jActionListener
{

    private static final Log mLogger = LogFactory.getLog(Gui4jActionListenerButton.class);

    /**
     * Constructor for Gui4jActionListenerButton.
     * 
     * @param gui4jComponentInstance
     */
    public Gui4jActionListenerButton(Gui4jComponentInstance gui4jComponentInstance)
    {
        super(gui4jComponentInstance);
    }

    public void actionPerformed(ActionEvent e)
    {
        if (mActionPerformed != null)
        {
            AbstractButton button = (AbstractButton) mGui4jComponentInstance.getComponent();

            if (mLogger.isInfoEnabled() && button != null)
            {
                String id = mGui4jComponentInstance.getGui4jComponent() == null ? "undefined" : mGui4jComponentInstance
                        .getGui4jComponent().getId();
                mLogger.info("Invoking action of button with text '" + button.getText() + "' and gui4jId '" + id
                        + "' in xml file '" + mConfigurationName + "'");
            }

            boolean selected = button.isSelected();
            Map paramMap = new Gui4jMap1(Const.PARAM_VALUE, new Boolean(selected));
            mGui4jThreadManager.performWork(mGui4jController, mActionPerformed, paramMap, mGui4jComponentInstance);
        }
    }

}
