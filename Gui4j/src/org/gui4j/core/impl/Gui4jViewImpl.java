package org.gui4j.core.impl;

import java.awt.Frame;
import java.awt.Image;
import java.awt.Window;

import javax.swing.JFrame;
import javax.swing.RootPaneContainer;
import javax.swing.WindowConstants;

import org.gui4j.Gui4jController;
import org.gui4j.core.interfaces.Gui4jViewInternal;


final class Gui4jViewImpl extends Gui4jWindowImpl implements Gui4jViewInternal 
{

   private Image iconImage;
    
    Gui4jViewImpl(
        Gui4jImpl gui4j,
        String viewResourceName,
        Gui4jController gui4jController,
        String title,
        boolean readOnlyMode)
    {
        super(gui4j, viewResourceName, gui4jController, title, readOnlyMode);
    }
    
    protected Window createWindow()
    {
        JFrame frame = new JFrame(getTitle());
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        if (iconImage != null)
        {
            frame.setIconImage(iconImage);
        }
        return frame;
    }

    /**
     * This method is needed to change a frame's title
     * @param title
     */
    public void changeWindowTitle(String title)
    {
        if (getFrame() != null)
        {
            getFrame().setTitle(title);
        }
    }


    private JFrame getFrame()
    {
        return (JFrame)getWindow();
    }


    protected RootPaneContainer getRootPaneContainer()
    {
        return getFrame();
    }
    
    public void setIconImage(Image image)
    {
        this.iconImage = image;
        if (getFrame() != null)
        {
            getFrame().setIconImage(iconImage);
        }
    }

    protected void maximizeWindow()
    {
        getFrame().setExtendedState(Frame.MAXIMIZED_BOTH);
    }

    protected void restoreWindow() {
        getFrame().setExtendedState(Frame.NORMAL);
    }
    
    protected void defineWindowActions()
    {
        // we have no default action mappings for a Gui4jView / JFrame
    }

    public void setResizable(boolean resize) {
        if (getFrame() != null) {
            getFrame().setResizable(resize);
        }
    }
    
    
}
