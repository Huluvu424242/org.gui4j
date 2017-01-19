package org.gui4j;

import java.awt.Dimension;
import java.awt.Window;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JComponent;

/**
 */
public interface Gui4jWindow
{
    /**
     * Wait until all gui events are executed.
     */
    void waitForGUI();

    /**
     * Method prepare. Call this methid, before show, hide, setVisible 
     * @return Gui4jWindow (the same instance)
     */
    Gui4jWindow prepare();

    /**
     * Shows or hides the window.
     * @param show true or false
     */
    void setVisible(boolean show);

    /**
     * This method closes the view. If the view is not open,
     * nothing happens
     */
    void close();

    /**
     * Show the window.
     */
    void show();

    /**
     * Hide the window.
     */
    void hide();

    /**
     * @return true if the window is closed
     */
    boolean isClosed();

    /**
     * Resizes this view's JFrame so that all components get
     * their preferred sizes.
     */
    void resize();

    /**
     * @return the underlying swing window.
     */
    Window getWindow();

    /**
     * This method is needed to change a frame's title
     * @deprecated Use {@link #setTitle(String)} instead.
     * @param title
     */
    void changeWindowTitle(String title);

    /**
     * @return the window title
     */
    String getTitle();

    /**
     * Resets the window title to the specified String.
     * @param title
     */
    void setTitle(String title);
    
    /**
     * Maximize the window on the desktop.
     */
    void maximize();

    /**
     * Maximize the window, but stop at the given dimension.
     * @param maxWidth maximum width
     * @param maxHeight maximum height
     */
    void maximize(int maxWidth, int maxHeight);

    /**
     * Maximize the window, but the maximum siye is limited to 1024 x 768
     */
    void maximize1024x768();

    /**
     * Center the window.
     * @param centered if true, the window will be centered
     */
    void center(boolean centered);

    /**
     * Convenience method for {@link #setWindowSize(int, int)}
     * @param d must not be <code>null</code>
     */
    void setWindowSize(Dimension d);

    /**
     * Sets the window size.
     * @param width the width
     * @param height the height
     */
    void setWindowSize(int width, int height);

    /**
     * Display the working cursor. A counter variable remembers the the number of calls of setWorkingCursor and
     * setNormalCursor, so that only the last call of setNormalCursor really displays the normal cursor.
     */
    void setWorkingCursor();

    /**
     * Restores the normal cursor. See setWorkingCursor.
     */
    void setNormalCursor();

    /**
     * @return true if the window blocks for all events
     */
    boolean isBlocked();

    /**
     * @param busy if true, then the wait cursor will be shown and the gui will be blocked. A counter variable remembers the number
     * of calls with busy true and false and the last call of setBusy(false) will unlock the gui and the normal cursor will be
     * displayed.
     */
    void setBusy(final boolean busy);

    /**
     * Sets the gui to enabled false
     */
    void disable();

    /**
     * Sets the gui to enabled true
     */
    void enable();

    /**
     * Sets the enabled state.
     * @param flag the enabled state
     */
    void setEnabled(boolean flag);

    Gui4jController getGui4jController();

    void dispose();
    
    void refreshAll();

    /**
     * Returns the swing component with the given guiId.
     * @param id the guiId from the xml file.
     * @return JComponent
     */
    JComponent getSwingComponent(String id);

    void saveAsJPG(OutputStream out, float quality) throws IOException;
    
    void saveAsPNG(OutputStream out) throws IOException;
    
}
