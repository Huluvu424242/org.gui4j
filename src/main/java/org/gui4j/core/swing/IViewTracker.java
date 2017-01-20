/*
 * Copyright (c) 2004 Christopher M Butler Copyright of changes to original ViewTracker (c) 2005 Kay
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

import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.KeyboardFocusManager;
import java.util.WeakHashMap;

import javax.swing.SwingUtilities;

/**
 * Implements focus tracking for {@link org.gui4j.core.swing.IView}s.
 */
public class IViewTracker {
    private static final WeakHashMap TRACKERS_BY_WINDOW = new WeakHashMap();
    private static IViewTracker currentTracker;
    private static final Object LOCK = new Object();
    private IView currentView;

    public static IViewTracker getTracker(Component component) {
        RootWindow window = RootWindow.getRootContainer(component);
        return getTracker(window);
    }

    public static IViewTracker getCurrentTracker() {
        synchronized (LOCK) {
            return currentTracker;
        }
    }

    private static IViewTracker getTracker(RootWindow window) {
        if (window == null)
            return null;

        Component root = window.getRootContainer();
        IViewTracker tracker = (IViewTracker) TRACKERS_BY_WINDOW.get(root);

        if (tracker == null) {
            tracker = new IViewTracker();

            TRACKERS_BY_WINDOW.put(root, tracker);
        }
        return tracker;
    }

    static void windowActivated(Component c) {
        RootWindow window = RootWindow.getRootContainer(c);
        IViewTracker tracker = getTracker(window);
        synchronized (LOCK) {
            currentTracker = tracker;
        }
    }

    public static void requestViewActivation(Component c) {
        if (c == null)
            return;

        IView view = c instanceof IView ? (IView) c : (IView) SwingUtilities.getAncestorOfClass(IView.class, c);
        if (view != null) {
            requestViewActivation(c, view);
        }
    }

    public static void requestViewActivation(final Component c, final IView view) {
        if (c == null || view == null)
            return;

        // make sure the window is currently active
        activateWindow(c);

        Thread t = new Thread() {
            public void run() {
                Runnable r = new Runnable() {
                    public void run() {
                        focusView(c, view);
                    }
                };
                EventQueue.invokeLater(r);
            }
        };
        t.start();
    }

    private static void focusView(Component child, IView parentView) {
        // if the view is already active, then leave it alone
        if (parentView.isActive())
            return;

        Component focuser = getNearestFocusableComponent(child, parentView.getContainer());
        if (focuser == null)
            focuser = parentView.getContainer();
        focuser.requestFocus();
    }

    public IViewTracker() {

    }

    public void setActive(boolean b) {
        if (currentView == null)
            return;

        currentView.setActive(b);
    }

    public void setActive(IView view) {
        if (view != currentView) {
            setActive(false);
            currentView = view;
            setActive(true);
        }
    }

    // copied from org.flexdock.util.SwingUtility
    private static void activateWindow(Component c) {
        RootWindow window = RootWindow.getRootContainer(c);
        if (window != null && !window.isActive())
            window.toFront();
    }

    // copied from org.flexdock.util.SwingUtility
    private static Component getNearestFocusableComponent(Component c, Container desiredRoot) {
        if (c == null)
            c = desiredRoot;
        if (c == null)
            c = KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow();

        boolean cachedFocusCycleRoot = false;
        // make the desiredRoot into a focusCycleRoot
        if (desiredRoot != null) {
            cachedFocusCycleRoot = desiredRoot.isFocusCycleRoot();
            if (!cachedFocusCycleRoot)
                desiredRoot.setFocusCycleRoot(true);
        }

        Container focusRoot = null;
        if (c instanceof Container) {
            Container cnt = (Container) c;
            focusRoot = cnt.isFocusCycleRoot(cnt) ? cnt : cnt.getFocusCycleRootAncestor();
        } else
            focusRoot = c.getFocusCycleRootAncestor();

        Component focuser = null;
        if (focusRoot != null)
            focuser = focusRoot.getFocusTraversalPolicy().getLastComponent(focusRoot);

        // restore the desiredRoot to its previous state
        if (desiredRoot != null && !cachedFocusCycleRoot) {
            desiredRoot.setFocusCycleRoot(cachedFocusCycleRoot);
        }
        return focuser;
    }

}