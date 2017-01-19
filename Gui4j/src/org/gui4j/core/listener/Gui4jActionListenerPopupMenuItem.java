package org.gui4j.core.listener;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

import org.gui4j.component.Gui4jPopupMenu;
import org.gui4j.constants.Const;
import org.gui4j.core.Gui4jAbstractComponent;
import org.gui4j.core.Gui4jComponentInstance;
import org.gui4j.core.Gui4jSwingContainer;


public class Gui4jActionListenerPopupMenuItem extends Gui4jActionListener
{

    public Gui4jActionListenerPopupMenuItem(Gui4jComponentInstance gui4jComponentInstance)
    {
        super(gui4jComponentInstance);
    }

    public void actionPerformed(ActionEvent e)
    {
        if (mActionPerformed != null)
        {
            Gui4jSwingContainer swingContainer = mGui4jComponentInstance.getGui4jSwingContainer();
            Object context = swingContainer.getStorage(Gui4jPopupMenu.class, Gui4jAbstractComponent.STORAGE_POPUP_CONTEXT);
            Map paramMap = new HashMap();
            paramMap.put(Const.PARAM_CONTEXT, context);
            mGui4jThreadManager.performWork(mGui4jController, mActionPerformed, paramMap, mGui4jComponentInstance);
        }
    }

}
