package org.gui4j.examples.util;

import org.gui4j.Gui4j;
import org.gui4j.Gui4jController;
import org.gui4j.Gui4jWindow;

public class WindowController extends LightController implements Gui4jController
{
    protected Gui4jWindow gui4jWindow;

    public WindowController(Gui4j gui4j)
    {
        super(gui4j);
    }
    
    protected String getResourceName()
    {
        String packageName = getClass().getPackage().getName();
        String className = getClass().getName();
        return className.substring(packageName.length()+1)+".xml";
    }
    
    protected String getTitle()
    {
        return getClass().getName();
    }
    
    protected void createGui4jWindow()
    {
        gui4jWindow = getGui4j().createView(getResourceName(), this,  getTitle(), false);
    }
    
    protected Gui4jWindow getGui4jWindow()
    {
        assert gui4jWindow != null;
        return gui4jWindow;
    }
    
    protected void prepareGui4jWindow(int width, int height)
    {
        gui4jWindow.setWindowSize(width, height);
        gui4jWindow.prepare();
    }
    
    public void display() {
        display(800, 600);
    }
    
    public void display(int width, int height)
    {
        createGui4jWindow();
        prepareGui4jWindow(width, height);
        gui4jWindow.show();
    }

    public final void close() {
        if (gui4jWindow != null) {
            gui4jWindow.close();
            gui4jWindow = null;
        }
    }

    
    /* (non-Javadoc)
     * @see de.bea.gui4j.Gui4jController#onWindowClosing()
     */
    public boolean onWindowClosing()
    {
        gui4jWindow.close();
        if (getGui4j().getViewCollector().isEmpty())
        {
            System.exit(0);
        }
        return false;
    }
    
    /* (non-Javadoc)
     * @see de.bea.gui4j.Gui4jController#windowClosed()
     */
    public void windowClosed()
    {
    }
}
