package org.gui4j.component;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gui4j.Gui4jCallBase;
import org.gui4j.core.Gui4jComponentContainer;
import org.gui4j.core.Gui4jComponentInstance;
import org.gui4j.core.Gui4jQualifiedComponent;
import org.gui4j.core.Gui4jSwingContainer;


public final class Gui4jBorderLayout extends Gui4jJComponent
{
    private static final Log mLogger = LogFactory.getLog(Gui4jBorderLayout.class);
    private final List mDirectionList;
    private final List mGui4jComponentList;

    public Gui4jBorderLayout(Gui4jComponentContainer gui4jComponentContainer, String id)
    {
        super(gui4jComponentContainer, JPanel.class, id);
        mDirectionList = new Vector();
        mGui4jComponentList = new Vector();
    }

    public void addPlacement(String direction, Gui4jQualifiedComponent gui4jComponentInPath)
    {
        mDirectionList.add(direction);
        mGui4jComponentList.add(gui4jComponentInPath);
    }

    /* (non-Javadoc)
     * @see org.gui4j.core.Gui4jAbstractComponent#createComponentInstance(org.gui4j.core.Gui4jSwingContainer, org.gui4j.Gui4jCallBase, org.gui4j.core.Gui4jQualifiedComponent)
     */
    protected Gui4jComponentInstance createComponentInstance(
        Gui4jSwingContainer gui4jSwingContainer,
        Gui4jCallBase gui4jCallBase,
        Gui4jQualifiedComponent gui4jComponentInPath)
    {
        JPanel panel = new JPanel(new BorderLayout());
        Gui4jComponentInstance gui4jComponentInstance =
            new Gui4jComponentInstance(gui4jSwingContainer, panel, gui4jComponentInPath);
        for (Iterator itDir = mDirectionList.iterator(), itGui4j = mGui4jComponentList.iterator();
            itDir.hasNext();
            )
        {
            String direction = (String) itDir.next();
            Gui4jQualifiedComponent gui4jComponentInPathSub = (Gui4jQualifiedComponent) itGui4j.next();
            Gui4jComponentInstance subInstance =
                gui4jSwingContainer.getGui4jComponentInstance(
                    gui4jComponentInPath.getGui4jComponentPath(),
                    gui4jComponentInPathSub);
            if (getGui4j().traceMode())
            {
                Component c = subInstance.getComponent();
                mLogger.debug(
                    "BorderLayout: "
                        + getId()
                        + "; preferred size of component at "
                        + direction
                        + " is "
                        + c.getPreferredSize());
            }
            panel.add(direction, subInstance.getComponent());

        }
        if (getGui4j().traceMode())
        {
            mLogger.debug("Preferred size of " + getId() + " is " + panel.getPreferredSize());
        }
        return gui4jComponentInstance;
    }

    public void setHSpacing(JComponent jComponent, int hSpacing)
    {
        ((BorderLayout) jComponent.getLayout()).setHgap(hSpacing);
    }

    public void setVSpacing(JComponent jComponent, int vSpacing)
    {
        ((BorderLayout) jComponent.getLayout()).setVgap(vSpacing);
    }


}
