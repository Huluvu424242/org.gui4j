package org.gui4j.component;

import java.awt.Container;

import javax.swing.Icon;
import javax.swing.JToolBar;

import org.gui4j.Gui4jCallBase;
import org.gui4j.core.Gui4jCall;
import org.gui4j.core.Gui4jComponentContainer;
import org.gui4j.core.Gui4jComponentInstance;
import org.gui4j.core.Gui4jQualifiedComponent;
import org.gui4j.core.Gui4jSwingContainer;
import org.gui4j.core.listener.Gui4jMouseListenerSimpleInternalFrame;
import org.gui4j.core.swing.IView;
import org.gui4j.core.swing.IViewListener;
import org.gui4j.core.swing.SimpleInternalFrame;

public class Gui4jSimpleInternalFrame extends Gui4jJComponent
{

    public static final String SELECTED_ALWAYS = "always";
    public static final String SELECTED_NEVER = "never";
    public static final String SELECTED_ONFOCUS = "whenFocused";

    private final Gui4jQualifiedComponent content;
    private final Gui4jQualifiedComponent toolbar;
    private final String selectionMode;

    private Gui4jCall actionCommand;

    public Gui4jSimpleInternalFrame(Gui4jComponentContainer gui4jComponentContainer, String id,
            Gui4jQualifiedComponent content, Gui4jQualifiedComponent toolbar, String selectionMode)
    {
        super(gui4jComponentContainer, SimpleInternalFrame.class, id);
        this.content = content;
        this.selectionMode = selectionMode;
        this.toolbar = toolbar;
    }

    protected Gui4jComponentInstance createComponentInstance(Gui4jSwingContainer gui4jSwingContainer,
            Gui4jCallBase gui4jCallBase, Gui4jQualifiedComponent gui4jComponentInPath)
    {

        SimpleInternalFrame sif;

        if (SELECTED_ONFOCUS.equals(selectionMode))
        {
            sif = new View(); // create subclass that takes part in focus
                                // change listening
            sif.setSelected(false);
        }
        else
        {
            sif = new SimpleInternalFrame();
            if (SELECTED_NEVER.equals(selectionMode))
            {
                sif.setSelected(false);
            }
            else
            {
                // default
                sif.setSelected(true);
            }
        }

        Gui4jComponentInstance gui4jComponentInstance = new Gui4jComponentInstance(gui4jSwingContainer, sif,
                gui4jComponentInPath);

        return gui4jComponentInstance;
    }

    protected void setProperties(Gui4jComponentInstance gui4jComponentInstance)
    {
        super.setProperties(gui4jComponentInstance);

        SimpleInternalFrame sif = (SimpleInternalFrame) gui4jComponentInstance.getComponent();

        // set content
        Gui4jComponentInstance contentInstance = gui4jComponentInstance.getGui4jSwingContainer()
                .getGui4jComponentInstance(gui4jComponentInstance.getGui4jComponentInPath().getGui4jComponentPath(),
                        content);
        sif.setContent(contentInstance.getComponent());

        // set toolbar
        if (toolbar != null)
        {
            Gui4jComponentInstance toolbarInstance = gui4jComponentInstance.getGui4jSwingContainer()
                    .getGui4jComponentInstance(
                            gui4jComponentInstance.getGui4jComponentInPath().getGui4jComponentPath(), toolbar);
            sif.setToolBar((JToolBar) toolbarInstance.getComponent());
        }

        if (actionCommand != null)
        {
            Gui4jMouseListenerSimpleInternalFrame listener = new Gui4jMouseListenerSimpleInternalFrame(
                    gui4jComponentInstance);
            listener.setOnDoubleClick(actionCommand);
            sif.addHeaderListener(listener);
        }
    }

    public void setTitle(SimpleInternalFrame sif, String title)
    {
        sif.setTitle(title);
    }

    public void setTabText(SimpleInternalFrame sif, String tabText)
    {
        sif.setTabText(tabText);
    }
    
    public void setInfoText(SimpleInternalFrame sif, String infoText) {
        sif.setInfoText(infoText);
    }
    
    public void setIcon(SimpleInternalFrame sif, Icon icon)
    {
        sif.setFrameIcon(icon);
    }

    public void setTabIcon(SimpleInternalFrame sif, Icon icon)
    {
        sif.setTabIcon(icon);
    }
    
    public void setActionCommand(Gui4jCall actionCommand)
    {
        this.actionCommand = actionCommand;
    }

    // ******

    private static class View extends SimpleInternalFrame implements IView
    {

        public View()
        {
            super();
            IViewListener.prime();
        }

        public Container getContainer()
        {
            return this;
        }

        public boolean isActive()
        {
            return isSelected();
        }

        public void setActive(boolean active)
        {
            setSelected(active);
        }
    }
}