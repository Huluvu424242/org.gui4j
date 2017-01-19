package org.gui4j;

public interface Gui4jControllerAdvanced extends Gui4jController
{

    /**
     * Invoked the first time a window is made visible.
     */
    public void windowOpened();

    /**
     * Invoked when a window is changed from a normal to a minimized state. For
     * many platforms, a minimized window is displayed as the icon specified in
     * the window's iconImage property.
     */
    public void windowIconified();

    /**
     * Invoked when a window is changed from a minimized to a normal state.
     */
    public void windowDeiconified();

    /**
     * Invoked when the Window is set to be the active Window. Only a Frame or a
     * Dialog can be the active Window. The native windowing system may denote
     * the active Window or its children with special decorations, such as a
     * highlighted title bar. The active Window is always either the focused
     * Window, or the first Frame or Dialog that is an owner of the focused
     * Window.
     */
    public void windowActivated();

    /**
     * Invoked when a Window is no longer the active Window. Only a Frame or a
     * Dialog can be the active Window. The native windowing system may denote
     * the active Window or its children with special decorations, such as a
     * highlighted title bar. The active Window is always either the focused
     * Window, or the first Frame or Dialog that is an owner of the focused
     * Window.
     */
    public void windowDeactivated();

}