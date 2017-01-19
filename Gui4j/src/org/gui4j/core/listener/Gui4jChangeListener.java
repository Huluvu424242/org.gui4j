package org.gui4j.core.listener;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.gui4j.Gui4jCallBase;
import org.gui4j.Gui4jDispose;
import org.gui4j.core.Gui4jCall;
import org.gui4j.core.Gui4jComponentInstance;
import org.gui4j.core.Gui4jMap1;
import org.gui4j.core.Gui4jThreadManager;


/**
 * @author Joachim Schmid
 */
public class Gui4jChangeListener implements ChangeListener, Gui4jDispose
{
    protected Gui4jCall mActionPeformed;
    protected Gui4jComponentInstance mGui4jComponentInstance;
    protected Gui4jCallBase mGui4jController;
    protected Gui4jThreadManager mGui4jThreadManager;
    // private URL mConfigurationSource;

    public Gui4jChangeListener(Gui4jComponentInstance gui4jComponentInstance)
    {
        mGui4jComponentInstance = gui4jComponentInstance;
        mGui4jController = mGui4jComponentInstance.getGui4jCallBase();
        mGui4jThreadManager = mGui4jComponentInstance.getGui4j().getGui4jThreadManager();
        // Component component = mGui4jComponentInstance.getComponent();
        // mConfigurationSource =
           // mGui4jComponentInstance.getGui4jComponent().getGui4jComponentContainer().getConfigurationSource();
    }

    /**
     * Sets the actionPeformed.
     * @param actionPeformed The actionPeformed to set
     */
    public void setActionPerformed(Gui4jCall actionPeformed)
    {
        mActionPeformed = actionPeformed;
    }

    /**
     * @see javax.swing.event.ChangeListener#stateChanged(ChangeEvent)
     */
    public void stateChanged(ChangeEvent e)
    {
        if (mActionPeformed != null)
        {
            /*
            mLogger.debug(
                "Invoking action for gui4jId "
                    + mGui4jComponentInstance.getGui4jComponent().getId()
                    + " in xml file "
                    + mConfigurationSource);
            */
            mGui4jThreadManager.performWork(mGui4jController, mActionPeformed, new Gui4jMap1("", e));
        }
    }

    /* (non-Javadoc)
     * @see org.gui4j.Gui4jDispose#dispose()
     */
    public void dispose()
    {
        mActionPeformed = null;
        mGui4jComponentInstance = null;
        mGui4jController = null;
        mGui4jThreadManager = null;
        // mConfigurationSource = null;
    }

}
