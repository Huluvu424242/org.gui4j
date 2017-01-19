package org.gui4j;


public interface Gui4jController extends Gui4jCallBase
{
    /**
     * This method is called when the user closes the window (e.g. by clicking on the
     * X-Button or by Pressing Alt-F4).
     * 
     * @return boolean if <code>false</code> the window will not close, if <code>true</code> the window
     *         will close.
     */
    boolean onWindowClosing();

    /**
     * This method is called after the window has been closed.
     */
    void windowClosed();
}