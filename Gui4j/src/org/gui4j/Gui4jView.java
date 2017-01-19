package org.gui4j;

import java.awt.Image;

public interface Gui4jView extends Gui4jWindow
{

    /**
     * Set the icon for the windows.
     * 
     * @param image
     *            the icon
     */
    void setIconImage(Image image);

    /**
     * Defines whether the window can be resized or not.
     * 
     * @param resize
     *            if true, then the window is resizable.
     */
    void setResizable(boolean resize);

}
