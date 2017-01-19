package org.gui4j.core.listener;

import java.awt.event.MouseEvent;

import org.gui4j.core.Gui4jCall;
import org.gui4j.core.Gui4jComponentInstance;
import org.gui4j.core.Gui4jMouseListener;

public class Gui4jMouseListenerSimpleInternalFrame extends Gui4jMouseListener {

    private Gui4jCall mOnDoubleClick;

    public Gui4jMouseListenerSimpleInternalFrame(Gui4jComponentInstance gui4jComponentInstance) {
        super(gui4jComponentInstance);
    }

    public void mouseClicked(MouseEvent event) {
        super.mouseClicked(event);

        if (event.getClickCount() == 2 && mOnDoubleClick != null) {
            performWork(mOnDoubleClick);
        }
    }

    private void performWork(Gui4jCall call) {
        mGui4jThreadManager.performWork(mGui4jController, call, null, mGui4jComponentInstance);
    }
    
    
    public void setOnDoubleClick(Gui4jCall onDoubleClick) {
        mOnDoubleClick = onDoubleClick;
    }
}