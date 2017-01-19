/*
 * Copyright (c) 2004 Christopher M Butler Copyright of changes to original ViewListener (c) 2005 Kay
 * Krüger-Barvels Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions: The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT
 * WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

/* Changes to original code (c) 2005 beck et al. projects GmbH */

package org.gui4j.core.swing;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;

import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Supports focus tracking for {@link org.gui4j.core.swing.IView}s.
 */
public class IViewListener implements PropertyChangeListener, ChangeListener, AWTEventListener
{

    private static final Log log = LogFactory.getLog(IViewListener.class);

    private static final String PERMANENT_FOCUS_OWNER = "permanentFocusOwner";
    private static final String ACTIVE_WINDOW = "activeWindow";

    private static final IViewListener SINGLETON = new IViewListener();
    private static HashSet PROP_EVENTS = new HashSet();

    static
    {
        log.debug("IViewListener static initializer");
        primeImpl();
    }

    /**
     * Call this to cause class loading. Everything else is automatically taken
     * care of.
     */
    public static void prime()
    {
    }

    private static void primeImpl()
    {
        PROP_EVENTS.add(PERMANENT_FOCUS_OWNER);
        PROP_EVENTS.add(ACTIVE_WINDOW);

        EventQueue.invokeLater(new Runnable() {
            public void run()
            {
                KeyboardFocusManager focusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
                focusManager.addPropertyChangeListener(SINGLETON);
            }
        });

        Toolkit.getDefaultToolkit().addAWTEventListener(SINGLETON, AWTEvent.MOUSE_EVENT_MASK);
    }

    public static IViewListener getInstance()
    {
        return SINGLETON;
    }

    private IViewListener()
    {
    }

    public void eventDispatched(AWTEvent event)
    {
        // catch all mousePressed events
        if (event.getID() != MouseEvent.MOUSE_PRESSED)
            return;

        MouseEvent evt = (MouseEvent) event;
        Component c = (Component) evt.getSource();

        // check to see if the event was targeted at the deepest component at
        // the current mouse loaction
        Container container = c instanceof Container ? (Container) c : null;
        if (container != null && container.getComponentCount() > 1)
        {
            // if not, find the deepest component
            Point p = evt.getPoint();
            c = SwingUtilities.getDeepestComponentAt(c, p.x, p.y);
        }

        // request activation of the view that encloses this component
        IViewTracker.requestViewActivation(c);
    }

    public void propertyChange(PropertyChangeEvent evt)
    {
        String pName = evt.getPropertyName();
        if (!PROP_EVENTS.contains(pName))
            return;

        Component oldVal = toComponent(evt.getOldValue());
        Component newVal = toComponent(evt.getNewValue());
        boolean switchTo = newVal != null;

        log.debug("Focus change signalled from " + oldVal + " to " + newVal);

        if (ACTIVE_WINDOW.equals(pName))
            handleWindowChange(evt, oldVal, newVal, switchTo);
        else
            handleFocusChange(evt, oldVal, newVal, switchTo);
    }

    private void handleWindowChange(PropertyChangeEvent evt, Component oldVal, Component newVal, boolean activate)
    {
        // notify the IViewTracker of the window change
        IViewTracker.windowActivated(newVal);

        Component srcComponent = activate ? newVal : oldVal;
        IViewTracker tracker = IViewTracker.getTracker(srcComponent);
        if (tracker != null)
            tracker.setActive(activate);
    }

    private void handleFocusChange(PropertyChangeEvent evt, Component oldVal, Component newVal, boolean switchTo)
    {
        if (!switchTo)
            return;

        if (newVal instanceof JTabbedPane)
        {
            final JTabbedPane tabbed = (JTabbedPane) newVal;
            if (tabbed.getClientProperty("gui4jPortletContainer") != null)
            {
                // if tabbed pane is container for stacked portlets/views,
                // we transfer focus to tab content
                EventQueue.invokeLater(new Runnable() {
                    public void run()
                    {
                        Component selected = tabbed.getSelectedComponent();
                        if (selected != null) {
                            selected.requestFocus();                            
                        } else {
                            tabbed.requestFocus();
                        }
                    }
                });
            } else {
                activateComponent(tabbed.getSelectedComponent());
            }
        }
        else
        {
            activateComponent(newVal);
        }
    }

    private void activateComponent(Component c)
    {
        log.debug("Will activate component: " + c);
        IViewTracker tracker = IViewTracker.getTracker(c);
        if (tracker != null)
        {
            IView view = c instanceof IView ? (IView) c : (IView) SwingUtilities.getAncestorOfClass(IView.class, c);
            tracker.setActive(view);
        }
    }

    public void stateChanged(ChangeEvent e)
    {
        Object obj = e.getSource();
        if (obj instanceof JTabbedPane)
        {
            JTabbedPane pane = (JTabbedPane) obj;
            Component c = pane.getSelectedComponent();
            if (c instanceof IView)
                activateComponent(c);
        }
    }

    private static Component toComponent(Object obj)
    {
        return obj instanceof Component ? (Component) obj : null;
    }

}