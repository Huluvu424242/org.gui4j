package org.gui4j.component;

import java.util.Map;

import javax.swing.JMenuItem;

import org.gui4j.Gui4jCallBase;
import org.gui4j.constants.Const;
import org.gui4j.core.Gui4jAbstractComponent;
import org.gui4j.core.Gui4jCall;
import org.gui4j.core.Gui4jComponentContainer;
import org.gui4j.core.Gui4jComponentInstance;
import org.gui4j.core.Gui4jMap1;
import org.gui4j.core.Gui4jSwingContainer;
import org.gui4j.core.listener.Gui4jActionListener;
import org.gui4j.core.listener.Gui4jActionListenerPopupMenuItem;


public class Gui4jPopupMenuItem extends Gui4jMenuItem
{

    private Gui4jCall mEnabledCall;
    private Gui4jCall mVisibleCall;
    private Gui4jCall mText;
    private final Class mContextType;

    /**
     * Constructor for Gui4jMenuItem.
     * @param gui4jComponentContainer
     * @param buttonClass
     * @param contextType
     * @param id
     */
    public Gui4jPopupMenuItem(
        Gui4jComponentContainer gui4jComponentContainer,
        Class buttonClass,
        Class contextType,
        String id)
    {
        super(gui4jComponentContainer, buttonClass, id);
        mContextType = contextType;
    }

    protected Gui4jActionListener createActionListener(Gui4jComponentInstance gui4jComponentInstance)
    {
        return new Gui4jActionListenerPopupMenuItem(gui4jComponentInstance);
    }

    public Class getContextType()
    {
        return mContextType;
    }

    public void setEnabled(Gui4jCall enabledCall)
    {
        mEnabledCall = enabledCall;
    }

    public void setVisible(Gui4jCall visible)
    {
        mVisibleCall = visible;
    }

    public void setText(Gui4jCall text)
    {
        mText = text;
    }

    protected void applyInitialProperties(Gui4jComponentInstance gui4jComponentInstance, Gui4jCallBase gui4jController, boolean handleThreads)
    {
        Gui4jSwingContainer swingContainer = gui4jComponentInstance.getGui4jSwingContainer();
        Object context = swingContainer.getStorage(Gui4jPopupMenu.class, Gui4jAbstractComponent.STORAGE_POPUP_CONTEXT);
        if ((context == null && mContextType == null)
            || mContextType == null
            || (context != null && mContextType.isAssignableFrom(context.getClass())))
        {
            super.applyInitialProperties(gui4jComponentInstance, gui4jController, handleThreads);
            if (mEnabledCall != null || mVisibleCall != null || mText != null)
            {
                // popup menu context needed for "enabled" and "visible" properties
                Map paramMap = new Gui4jMap1(Const.PARAM_CONTEXT, context);

                boolean visible = true;
                if (mVisibleCall != null)
                {
                    visible = ((Boolean) mVisibleCall.getValue(gui4jController, paramMap, Boolean.TRUE)).booleanValue();
                }
                gui4jComponentInstance.getComponent().setVisible(visible);

                boolean enabled = true;
                if (!visible)
                {
                    enabled = false;
                }
                else if (mEnabledCall != null)
                {
                    enabled = ((Boolean) mEnabledCall.getValue(gui4jController, paramMap, Boolean.TRUE)).booleanValue();
                }
                gui4jComponentInstance.getComponent().setEnabled(enabled);

                if (mText != null)
                {
                    String text = (String) mText.getValue(gui4jController, paramMap, "");
                    ((JMenuItem) gui4jComponentInstance.getComponent()).setText(text);
                }
            }
            else
            {
                gui4jComponentInstance.getComponent().setVisible(true);
                gui4jComponentInstance.getComponent().setEnabled(true);
            }
        }
        else
        {
            gui4jComponentInstance.getComponent().setVisible(false);
            gui4jComponentInstance.getComponent().setEnabled(false);

        }
    }

}
