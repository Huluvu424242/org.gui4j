package org.gui4j.component;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.gui4j.Gui4jCallBase;
import org.gui4j.core.Gui4jComponentContainer;
import org.gui4j.core.Gui4jComponentInstance;
import org.gui4j.core.Gui4jQualifiedComponent;
import org.gui4j.core.Gui4jSwingContainer;
import org.gui4j.core.swing.TableLayout;
import org.gui4j.core.swing.TableLayoutConstraints;


public final class Gui4jTableLayout extends Gui4jJComponent
{
    private final Map mEntries = new HashMap(); // Placement -> Gui4jQualifiedComponent
    private final double[][] mSize;

    /**
     * Constructor for Gui4jTableLayout.
     * @param gui4jComponentContainer
     * @param size
     * @param id
     */
    public Gui4jTableLayout(Gui4jComponentContainer gui4jComponentContainer, double[][] size, String id)
    {
        super(gui4jComponentContainer, JPanel.class, id);
        assert size != null && size.length == 2;
        this.mSize = size;
    }

    public void addPlacement(TableLayoutConstraints constraints, Gui4jQualifiedComponent gui4jQualifiedComponent)
    {
        mEntries.put(constraints, gui4jQualifiedComponent);
    }

    /* (non-Javadoc)
     * @see org.gui4j.core.Gui4jAbstractComponent#createComponentInstance(org.gui4j.core.Gui4jSwingContainer, org.gui4j.Gui4jCallBase, org.gui4j.core.Gui4jQualifiedComponent)
     */
    protected Gui4jComponentInstance createComponentInstance(
        Gui4jSwingContainer gui4jSwingContainer,
        Gui4jCallBase gui4jCallBase,
        Gui4jQualifiedComponent gui4jComponentInPath)
    {
        TableLayout tableLayout = new TableLayout(mSize);
        JPanel panel = new JPanel(tableLayout);
        Gui4jComponentInstance gui4jComponentInstance =
            new Gui4jComponentInstance(gui4jSwingContainer, panel, gui4jComponentInPath);
        for (Iterator it = mEntries.entrySet().iterator(); it.hasNext();)
        {
            Map.Entry entry = (Map.Entry) it.next();
            TableLayoutConstraints constraints = (TableLayoutConstraints) entry.getKey();
            Gui4jQualifiedComponent qualifiedComponent = (Gui4jQualifiedComponent) entry.getValue();
            Gui4jComponentInstance subInstance =
                gui4jSwingContainer.getGui4jComponentInstance(
                    gui4jComponentInPath.getGui4jComponentPath(),
                    qualifiedComponent);
            panel.add(subInstance.getComponent(), constraints);
        }
        return gui4jComponentInstance;
    }

    public void setHSpacing(JComponent jComponent, int hSpacing)
    {
        ((TableLayout) jComponent.getLayout()).setHGap(hSpacing);
    }

    public void setVSpacing(JComponent jComponent, int vSpacing)
    {
        ((TableLayout) jComponent.getLayout()).setVGap(vSpacing);
    }
}
