package org.gui4j.component;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gui4j.Gui4jCallBase;
import org.gui4j.core.Gui4jCall;
import org.gui4j.core.Gui4jComponentContainer;
import org.gui4j.core.Gui4jComponentInstance;
import org.gui4j.core.Gui4jQualifiedComponent;
import org.gui4j.core.Gui4jSwingContainer;
import org.gui4j.event.Gui4jEventListener;


public final class Gui4jSwitch extends Gui4jJComponent
{
    protected static final Log mLogger = LogFactory.getLog(Gui4jSwitch.class);
    protected final List mConditions; // List(Gui4jCall)
    protected final List mGui4jComponents; // List(Gui4jComponentInPath)
    protected final Gui4jQualifiedComponent mGui4jComponentDefault;

    private Gui4jCall[] mRefresh;

    /**
     * Constructor for Gui4jSwitch.
     * @param gui4jComponentContainer
     * @param id
     * @param gui4jComponentDefault
     */
    public Gui4jSwitch(
        Gui4jComponentContainer gui4jComponentContainer,
        String id,
        Gui4jQualifiedComponent gui4jComponentDefault)
    {
        super(gui4jComponentContainer, JPanel.class, id);
        mConditions = new ArrayList();
        mGui4jComponents = new ArrayList();
        assert gui4jComponentDefault != null;
        mGui4jComponentDefault = gui4jComponentDefault;
    }

    public void setRefresh(Gui4jCall[] refresh)
    {
        mRefresh = refresh;
    }

    public void addPlacement(Gui4jCall condition, Gui4jQualifiedComponent gui4jComponentInPath)
    {
        assert condition != null;
        assert gui4jComponentInPath != null;
        mConditions.add(condition);
        mGui4jComponents.add(gui4jComponentInPath);
    }

    /* (non-Javadoc)
     * @see org.gui4j.core.Gui4jAbstractComponent#createComponentInstance(org.gui4j.core.Gui4jSwingContainer, org.gui4j.Gui4jCallBase, org.gui4j.core.Gui4jQualifiedComponent)
     */
    protected Gui4jComponentInstance createComponentInstance(
        Gui4jSwingContainer gui4jSwingContainer,
        Gui4jCallBase gui4jCallBase,
        Gui4jQualifiedComponent gui4jComponentInPath)
    {
        BorderLayout borderLayout = new BorderLayout();
        JPanel panel = new JPanel(borderLayout);
        Gui4jComponentInstance gui4jComponentInstance =
            new Gui4jComponentInstance(gui4jSwingContainer, panel, gui4jComponentInPath);
        SwitchListener listener = new SwitchListener(gui4jComponentInstance);
        registerEvents(gui4jSwingContainer, gui4jCallBase, mRefresh, listener);
        listener.eventOccured();
        return gui4jComponentInstance;
    }

    class SwitchListener implements Gui4jEventListener
    {
        private final Gui4jComponentInstance mGui4jComponentInstance;
        private final Gui4jCallBase mGui4jCallBase;
        private final Gui4jSwingContainer mGui4jSwingContainer;

        public SwitchListener(Gui4jComponentInstance gui4jComponentInstance)
        {
            this.mGui4jComponentInstance = gui4jComponentInstance;
            this.mGui4jCallBase = gui4jComponentInstance.getGui4jCallBase();
            this.mGui4jSwingContainer = gui4jComponentInstance.getGui4jSwingContainer();
        }

        public void eventOccured()
        {
            // evaluate conditions in given order and display first 
            // element where the condition is valid
            boolean found = false;
            for (Iterator itCondition = mConditions.iterator(), itComponent = mGui4jComponents.iterator();
                !found && itCondition.hasNext();
                )
            {
                Gui4jCall condition = (Gui4jCall) itCondition.next();
                Gui4jQualifiedComponent gui4jComponentInPath = (Gui4jQualifiedComponent) itComponent.next();
                Boolean result = (Boolean) condition.getValueNoParams(mGui4jCallBase, Boolean.FALSE);
                if (Boolean.TRUE.equals(result))
                {
                    useGui4jComponent(gui4jComponentInPath);
                    found = true;
                }
            }
            if (!found)
            {
                useGui4jComponent(mGui4jComponentDefault);
            }
        }

        private void useGui4jComponent(Gui4jQualifiedComponent gui4jComponentInPath)
        {
            JPanel panel = (JPanel) mGui4jComponentInstance.getComponent();
            mLogger.debug("Using component " + gui4jComponentInPath.getQualifiedId() + " for switch with id " + getId());
            Gui4jComponentInstance subInstance =
                mGui4jSwingContainer.getGui4jComponentInstance(
                    mGui4jComponentInstance.getGui4jComponentInPath().getGui4jComponentPath(),
                    gui4jComponentInPath);
            Component component = subInstance.getComponent();
            panel.setVisible(false);
            panel.removeAll();
            panel.add(component);
            panel.setVisible(true);
        }

    }
}
