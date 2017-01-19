package org.gui4j.component;

import java.util.Iterator;
import java.util.List;

import javax.swing.Box;
import javax.swing.JComponent;

import org.gui4j.Gui4jCallBase;
import org.gui4j.component.util.Gui4jAlignment;
import org.gui4j.core.Gui4jComponentContainer;
import org.gui4j.core.Gui4jComponentInstance;
import org.gui4j.core.Gui4jQualifiedComponent;
import org.gui4j.core.Gui4jSwingContainer;

/**
 * Base class for <code>Gui4jBoxV</code> and <code>Gui4jBoxH</code>. This
 * class is similar to the Swing LayoutManager <code>Box</code>.
 * 
 * @author Joachim Schmid
 */
abstract public class Gui4jBox extends Gui4jJComponent
{

    private final List mGui4jComponents;
    private final String mAlignment;

    protected Gui4jBox(Gui4jComponentContainer gui4jComponentContainer, String id, List gui4jComponents,
            String alignment)
    {
        super(gui4jComponentContainer, Box.class, id);
        mGui4jComponents = gui4jComponents;
        mAlignment = alignment;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.gui4j.core.Gui4jAbstractComponent#createComponentInstance(org.gui4j.core.Gui4jSwingContainer,
     *      org.gui4j.Gui4jCallBase, org.gui4j.core.Gui4jQualifiedComponent)
     */
    protected Gui4jComponentInstance createComponentInstance(Gui4jSwingContainer gui4jSwingContainer,
            Gui4jCallBase gui4jCallBase, Gui4jQualifiedComponent gui4jComponentInPath)
    {
        Box container = createLayoutContainer();
        if (insertLeftGlue())
        {
            container.add(Box.createGlue());
        }
        for (Iterator it = mGui4jComponents.iterator(); it.hasNext();)
        {
            Gui4jQualifiedComponent gui4jComponentInPathSub = (Gui4jQualifiedComponent) it.next();
            Gui4jComponentInstance gui4jComponentInstance = gui4jSwingContainer.getGui4jComponentInstance(
                    gui4jComponentInPath.getGui4jComponentPath(), gui4jComponentInPathSub);
            JComponent component = gui4jComponentInstance.getSwingComponent();
            setAlignment(component);
            container.add(component);
        }
        if (insertRightGlue())
        {
            container.add(Box.createGlue());
        }
        Gui4jComponentInstance gui4jComponentInstance = new Gui4jComponentInstance(gui4jSwingContainer, container,
                gui4jComponentInPath);
        return gui4jComponentInstance;
    }

    protected abstract Box createLayoutContainer();

    protected abstract void setAlignment(JComponent component);

    private boolean insertLeftGlue()
    {
        return Gui4jAlignment.CENTER.equals(mAlignment) || Gui4jAlignment.RIGHT.equals(mAlignment)
                || Gui4jAlignment.TRAILING.equals(mAlignment) || Gui4jAlignment.BOTTOM.equals(mAlignment);
    }

    private boolean insertRightGlue()
    {
        return Gui4jAlignment.CENTER.equals(mAlignment) || Gui4jAlignment.LEFT.equals(mAlignment)
                || Gui4jAlignment.LEADING.equals(mAlignment) || Gui4jAlignment.TOP.equals(mAlignment);
    }
}
