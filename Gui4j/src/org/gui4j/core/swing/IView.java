package org.gui4j.core.swing;

import java.awt.Container;

/**
 * Interface to be implemented by components that need to be notified about
 * the keyboard focus being inside or outside their own container hierarchy.
 * The classes {@link org.gui4j.core.swing.IViewListener} and {@link org.gui4j.core.swing.IViewTracker}
 * implement the notification mechanism.
 */
public interface IView
{
    
    /**
     * Notifies this <code>IView</code> that the keyboard focus has moved out of or into its
     * container hierarchy. Called by the notification mechanism implemented by
     * {@link IViewListener} and {@link IViewTracker}.
     * The component can then decide to react by altering its visual
     * appearance (Typical example: simple internal frames only paint their headers coloured
     * if they contain the focussed component, i.e. they are active).
     * @param active <code>true</code>, if the focus has moved to a component inside this <code>IView</code>s 
     * container hierarchy. <code>false</code>, if the focus has moved to a component outside this
     * <code>IView</code>s container hierarchy.
     */
    void setActive(boolean active);
    
    
    /**
     * Returns the current active state of the <code>IView</code>.
     * @return <code>true</code>, if the <code>IView</code> is currently active,
     * <code>false</code> otherwise.
     */
    boolean isActive();
    
    /**
     * Defines this <code>IView</code>s container hierarchy.
     * @return The <code>Container</code> representing this <code>IView</code>s
     * containment hierarchy.
     */
    Container getContainer();
}
