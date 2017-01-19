package org.gui4j.core.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.gui4j.Gui4jCallBase;
import org.gui4j.Gui4jDispose;
import org.gui4j.core.Gui4jCall;
import org.gui4j.core.Gui4jComponentInstance;
import org.gui4j.core.Gui4jMap1;
import org.gui4j.core.Gui4jThreadManager;

/**
 * @author Joachim Schmid
 */
public class Gui4jActionListener implements ActionListener, Gui4jDispose
{
    private final Log mLogger = LogFactory.getLog(Gui4jActionListener.class);

    protected Gui4jCall mActionPerformed;
    protected Gui4jComponentInstance mGui4jComponentInstance;
    protected Gui4jCallBase mGui4jController;
    protected Gui4jThreadManager mGui4jThreadManager;
    protected String mConfigurationName;

    public Gui4jActionListener(Gui4jComponentInstance gui4jComponentInstance)
    {
        mGui4jComponentInstance = gui4jComponentInstance;
        mGui4jController = mGui4jComponentInstance.getGui4jCallBase();
        mGui4jThreadManager = mGui4jComponentInstance.getGui4j().getGui4jThreadManager();
        mConfigurationName = mGui4jComponentInstance.getGui4jComponent().getGui4jComponentContainer()
                .getConfigurationName();
    }

    public void actionPerformed(ActionEvent e)
    {
        if (mActionPerformed != null)
        {
            if (mLogger.isDebugEnabled())
            {
                mLogger.debug("Invoking action for gui4jId '" + mGui4jComponentInstance.getGui4jComponent().getId()
                        + "' in xml file '" + mConfigurationName + "'");
            }
            mGui4jThreadManager.performWork(mGui4jController, mActionPerformed, new Gui4jMap1("", e),
                    mGui4jComponentInstance);
        }
    }

    /**
     * Sets the actionPeformed.
     * 
     * @param actionPeformed
     *            The actionPeformed to set
     */
    public void setActionPerformed(Gui4jCall actionPeformed)
    {
        mActionPerformed = actionPeformed;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.gui4j.Gui4jDispose#dispose()
     */
    public void dispose()
    {
        mActionPerformed = null;
        mGui4jComponentInstance = null;
        mGui4jController = null;
        mGui4jThreadManager = null;
        mConfigurationName = null;
    }

}