package org.gui4j.core.impl;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gui4j.Gui4jCallBase;
import org.gui4j.Gui4jController;
import org.gui4j.Gui4jControllerAdvanced;
import org.gui4j.Gui4jGetValue;
import org.gui4j.Gui4jWindow;
import org.gui4j.core.Gui4jCall;
import org.gui4j.core.Gui4jComponentInstance;
import org.gui4j.core.Gui4jComponentManager;
import org.gui4j.core.Gui4jSwingContainer;
import org.gui4j.core.Gui4jThreadManager;
import org.gui4j.core.interfaces.Gui4jWindowInternal;
import org.gui4j.core.swing.BlockingGlassPane;
import org.gui4j.exception.ErrorTags;
import org.gui4j.exception.Gui4jUncheckedException;

abstract class Gui4jWindowImpl extends Gui4jSwingContainer implements ErrorTags, Gui4jWindowInternal
{
    private static final Log mLogger = LogFactory.getLog(Gui4jWindowImpl.class);

    private int workingCount;
    private boolean created;
    private boolean center = true;
    private boolean maximize = false;
    private boolean maximizeSize = false; // Maximimiert, aber mit Obergrenze
    private boolean setSize = false;
    // falls gesetzt, dann wird die Gre des Fenster auf (height, width)
    // gesetzt
    private int maximizeMaxHeight;
    private int maximizeMaxWidth;
    private int height;
    private int width;
    private WindowHandler windowHandler;

    private Window mWindow;
    private String mTitle;
    private String mWindowName;
    private Component mToplevelComponent;

    // private Gui4j gui4j;

    /**
     * Constructor for Gui4jWindow.
     * 
     * @param gui4j
     * @param viewResourceName
     * @param gui4jController
     * @param title
     * @param readOnlyMode
     */
    public Gui4jWindowImpl(Gui4jImpl gui4j, String viewResourceName, Gui4jController gui4jController, String title,
            boolean readOnlyMode)
    {
        super(gui4j, viewResourceName, gui4jController, readOnlyMode);
        // this.gui4j = gui4j;
        if (gui4j == null)
        {
            throw new Gui4jUncheckedException.ProgrammingError(PROGRAMMING_ERROR_parameter_null);
        }

        this.mTitle = title;

        this.mToplevelComponent = null;
        String top = getToplevelAttrValue(Gui4jComponentManager.FIELD_Gui4jViewTopComponent);
        if (top == null)
        {
            top = "TOP";
        }
        try
        {
            mToplevelComponent = getComponent(top);
        }
        catch (Throwable e)
        {
            gui4j.handleException(getGui4jController(), e, null);
        }
        Gui4jCall titleCall = getTitleCall();
        if (titleCall != null && mTitle == null)
        {
            this.mTitle = (String) titleCall.getValueNoParams(gui4jController, "");
        }
        Gui4jCall windowNameCall = getWindowNameCall();
        if (windowNameCall != null)
        {
            this.mWindowName = (String) windowNameCall.getValueNoParams(gui4jController, "");
        }
    }

    /**
     * Der Aufruf dieser Methode kehrt erst dann zurueck, wenn alle aktuell
     * anstehenden Swing-Events abgearbeitet worden sind.
     */
    public void waitForGUI()
    {
        if (SwingUtilities.isEventDispatchThread())
        {
            // do nothing
            mLogger.info("Cannot wait for GUI, because we are in the GUI Thread");
        }
        else
        {
            try
            {
                SwingUtilities.invokeAndWait(new Runnable() {
                    public void run()
                    {
                        // do nothing
                    }
                });
            }
            catch (Exception e)
            {
                mLogger.error(e, e);
            }
        }
    }

    /**
     * Method prepare. Muss aufgerufen werden, bevor show, hide oder setVisible
     * aufgerufen werden
     * 
     * @return Gui4jWindow (this Instanz)
     */
    public Gui4jWindow prepare()
    {
        Gui4jThreadManager.executeInSwingThreadAndWait(new Runnable() {
            public void run() {
                prepareImpl();
            }
        });
        return this;
    }
    
    private void prepareImpl()
    {
        if (created) {
            throw new IllegalStateException("Must not call prepare() twice.");
        }
        assert mToplevelComponent != null;
        assert mToplevelComponent instanceof Container;
        try
        {
            mWindow = createWindow();
            if (mWindowName != null)
            {
                mWindow.setName(mWindowName);
            }
            getRootPaneContainer().setGlassPane(new BlockingGlassPane(beepWhenBlocked()));
            {
                Container container = (Container) mToplevelComponent;
                getRootPaneContainer().setContentPane(container);
            }

            if (isDefined("MENU"))
            {
                getRootPaneContainer().getRootPane().setJMenuBar((JMenuBar) getComponent("MENU"));
            }
            String defaultButtonId = getToplevelAttrValue(Gui4jComponentManager.FIELD_Gui4jViewDefaultButton);
            if (defaultButtonId != null)
            {
                getRootPaneContainer().getRootPane().setDefaultButton((JButton) getComponent(defaultButtonId));
            }

            // getWindow().pack();
            setSizeAndLocation();
            windowHandler = new WindowHandler();
            getWindow().addWindowListener(windowHandler);
            defineWindowActions();
            getGui4j().addToWindowCollector(this);
            mLogger.debug("Window with title " + getTitle() + " created");
            created = true;
        }
        catch (Throwable e)
        {
            getGui4j().handleException(getGui4jController(), e, null);
        }
    }

    public void setVisible(final boolean show)
    {
        if (!isClosed())
        {
            // we need to wait for execution since we want to keep the expected
            // behaviour of blocking the current thread when displaying a dialog
            Gui4jThreadManager.executeInSwingThreadAndWait(new Runnable() {
                public void run()
                {
                    if (!created) {
                        throw new IllegalStateException("You must call prepare() before your first call of setVisible().");
                    }
                    getWindow().setVisible(show);
                }
            });
        }
    }

    /**
     * This method closes the view. If the view is not open, nothing happens
     */
    public void close()
    {
        if (isClosed())
        {
            return;
        }
        RootPaneContainer rootPaneContainer = getRootPaneContainer();
        setVisible(false);
        waitForGUI();
        inClosing = true;
        getGui4j().removeFromWindowCollector(this);
        Window window = getWindow();
        if (window != null)
        {
            window.removeWindowListener(windowHandler);
        }
        windowHandler = null;
        try
        {
            if (SwingUtilities.isEventDispatchThread())
            {
                getWindow().dispose();
                setTitle("");
            }
            else
            {
                SwingUtilities.invokeAndWait(new Runnable() {
                    public void run()
                    {
                        Window w = getWindow();
                        if (w != null)
                        {
                            w.dispose();
                        }
                        changeWindowTitle("");
                    }
                });
            }
        }
        catch (InvocationTargetException e)
        {
            mLogger.error("Error while disposing window", e);
        }
        catch (InterruptedException e)
        {
            mLogger.error("Error while disposing window", e);
        }
        if (rootPaneContainer != null)
        {
            clear(rootPaneContainer.getContentPane());
        }
        super.dispose();
        mWindow = null;
        if (getGui4jController() != null)
        {
            getGui4jController().windowClosed();
        }
        cleanUp();
    }

    public void show()
    {
        inClosing = false;
        setVisible(true);
    }

    public void hide()
    {
        setVisible(false);
    }

    protected final void requestWindowClosing()
    {
        windowHandler.windowClosing(null);
    }

    protected final boolean beepWhenBlocked()
    {
        return false;
    }

    private void clear(Container container)
    {
        Component[] components = container.getComponents();
        container.removeAll();
        for (int i = 0; i < components.length; i++)
        {
            Component component = components[i];
            if (component instanceof Container)
            {
                clear((Container) component);
            }
        }
    }

    private void setSizeAndLocation()
    {
        assert !created;
        Window window = getWindow();
        assert window != null;

        boolean needToPack = true;
        if (setSize)
        {
            window.setSize(width, height);
            needToPack = false;
        }
        if (maximize && !maximizeSize)
        {
            if (needToPack)
            {
                window.pack();
                needToPack = false;
            }
            maximizeWindow();
        }
        if (maximizeSize)
        {
            Toolkit tk = Toolkit.getDefaultToolkit();
            Dimension screenSize = tk.getScreenSize();
            if (screenSize.width > maximizeMaxWidth && screenSize.height > maximizeMaxHeight)
            {
                window.setSize(new Dimension(maximizeMaxWidth, maximizeMaxHeight));
                needToPack = false;
            }
            else
            {
                if (needToPack)
                {
                    window.pack();
                    needToPack = false;
                }
                maximizeWindow();
            }
        }
        if (needToPack)
        {
            window.pack();
        }

        if (center)
        {
            centerWindow();
        }
    }

    public boolean isClosed()
    {
        return getWindow() == null;
    }

    /**
     * Method maximizeWindow
     */
    protected void maximizeWindow()
    {
    }

    protected void restoreWindow()
    {
    }

    /**
     * Resizes this view's JFrame so that all components get their preferred
     * sizes.
     */
    public void resize()
    {
        if (getWindow() != null)
        {
            // follow Swing's single thread rule when updating components
            EventQueue.invokeLater(new Runnable() {
                public void run()
                {
                    getWindow().pack();
                }
            });
        }
    }

    public final Window getWindow()
    {
        return mWindow;
    }

    protected abstract RootPaneContainer getRootPaneContainer();

    protected abstract Window createWindow();

    protected abstract void defineWindowActions();

    protected BlockingGlassPane getBlockingGlassPane()
    {
        RootPaneContainer rootPaneContainer = getRootPaneContainer();
        if (rootPaneContainer != null)
        {
            return (BlockingGlassPane) rootPaneContainer.getGlassPane();
        }
        else
        {
            return null;
        }
    }

    /**
     * Changes a frame's title
     * 
     * @deprecated Use {@link #setTitle(String)} instead.
     * @param title
     */
    // Note: this method should be protected and not part of the
    // Gui4jWindow interface.
    public abstract void changeWindowTitle(String title);

    public void setTitle(String title)
    {
        mTitle = title;
        changeWindowTitle(title);
    }

    public String getTitle()
    {
        return mTitle;
    }

    public void maximize()
    {
        assert !created;
        this.maximize = true;
    }

    public void maximize(int maxWidth, int maxHeight)
    {
        assert !created;
        maximizeSize = true;
        maximizeMaxHeight = maxHeight;
        maximizeMaxWidth = maxWidth;
    }

    public void maximize1024x768()
    {
        maximize(1024, 768);
    }

    public void center(boolean centered)
    {
        assert !created;
        this.center = centered;
    }

    /**
     * Convenience method for {@link #setWindowSize(int, int)}
     * 
     * @param d
     *            must not be <code>null</code>
     */
    public void setWindowSize(Dimension d)
    {
        assert d != null;
        setWindowSize(d.width, d.height);
    }

    public void setWindowSize(int width, int height)
    {
        assert !created;
        this.setSize = true;
        this.height = height;
        this.width = width;
    }

    private void centerWindow()
    {
        Window window = getWindow();
        if (window != null)
        {
            Toolkit tk = Toolkit.getDefaultToolkit();
            Dimension screenDim = tk.getScreenSize();
            Dimension windowDim = window.getSize();
            int widthDiff = (screenDim.width - windowDim.width) / 2;
            int heightDiff = (screenDim.height - windowDim.height) / 2;
            window.setLocation(widthDiff >= 0 ? widthDiff : 0, heightDiff >= 0 ? heightDiff : 0);
        }
    }

    public synchronized void setWorkingCursor()
    {
        if (getWindow() != null)
        {
            getWindow().setCursor(new Cursor(Cursor.WAIT_CURSOR));
            workingCount++;
        }
    }

    public synchronized void setNormalCursor()
    {
        if (getWindow() != null)
        {
            if (workingCount > 0)
            {
                workingCount--;
            }
            if (workingCount == 0)
            {
                getWindow().setCursor(Cursor.getDefaultCursor());
            }
        }
    }

    public boolean isBlocked()
    {
        if (getBlockingGlassPane() != null)
        {
            return getBlockingGlassPane().isBlocked();
        }
        else
        {
            return false;
        }
    }

    public void setBusy(final boolean busy)
    {
        final BlockingGlassPane glassPane = getBlockingGlassPane();
        if (glassPane != null)
        {
            // need to execute this in Swing thread, since the glass pane might
            // cause an invalidate() on its parent component (this has lead to
            // deadlocks)
            if (EventQueue.isDispatchThread())
            {
                glassPane.block(busy);
            }
            else
            {
                Gui4jThreadManager.executeInSwingThreadAndWait(new Runnable() {
                    public void run()
                    {
                        glassPane.block(busy);
                    }
                });
            }
        }
    }

    public void disable()
    {
        if (getWindow() != null)
        {
            getWindow().setEnabled(false);
        }
    }

    public void enable()
    {
        if (getWindow() != null)
        {
            getWindow().setEnabled(true);
        }
    }

    public void setEnabled(boolean flag)
    {
        if (getWindow() != null)
        {
            getWindow().setEnabled(flag);
        }
    }

    protected void setWindow(Window window)
    {
        this.mWindow = window;
    }

    public JComponent getSwingComponent(String id)
    {
        Gui4jComponentInstance instance = getGui4jComponentInstance(id);
        return instance == null ? null : instance.getSwingComponent();
    }

    public void saveAsJPG(OutputStream out, float quality) throws IOException
    {
        RootPaneContainer rootPaneContainer = getRootPaneContainer();
        Component c = rootPaneContainer.getContentPane();
        int w = c.getWidth();
        int h = c.getHeight();
        BufferedImage bi = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
        Graphics2D big2d = bi.createGraphics();
        c.paint(big2d);
        ImageIO.write(bi,"jpeg",out);
//        JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
//        JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(bi);
//        param.setQuality(quality, false);
//        encoder.setJPEGEncodeParam(param);
//        encoder.encode(bi);
    }

    public void saveAsPNG(OutputStream out) throws IOException
    {
        RootPaneContainer rootPaneContainer = getRootPaneContainer();
        Component c = rootPaneContainer.getContentPane();
        int w = c.getWidth();
        int h = c.getHeight();
        BufferedImage bi = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
        Graphics2D big2d = bi.createGraphics();
        c.paint(big2d);
        ImageIO.write(bi,"png",out);
    }

    
    
    // **********************************************************************************

    private class WindowHandler implements WindowListener
    {
        private boolean windowClosingInProgress = false;

        public void windowActivated(WindowEvent e)
        {
            // TODO delegate calls to Gui4jControllerAdvanced if super's
            // mGui4jController is of this type.
            // see method windowOpened
        }

        public void windowClosed(WindowEvent e)
        {
            // TODO shouldn't this method delegate to
            // getGui4jController().windowClosed()
        }

        protected synchronized void windowClosingFinished()
        {
            windowClosingInProgress = false;
        }

        public void windowClosing(WindowEvent e)
        {
            synchronized (this)
            {
                if (windowClosingInProgress)
                {
                    return;
                }
                windowClosingInProgress = true;
            }
            Gui4jGetValue work = new Gui4jGetValue() {
                public Object getValue(Gui4jCallBase gui4jCallBase, Map paramMap, Object defaultValue)
                {
                    try
                    {
                        if (getGui4jController() != null && getGui4jController().onWindowClosing())
                        {
                            close();
                        }
                    }
                    finally
                    {
                        windowClosingFinished();
                    }
                    return null;
                }

                public Object getValueNoErrorChecking(Gui4jCallBase gui4jCallBase, Map paramMap,
                        Gui4jComponentInstance componentInstance)
                {
                    return getValue(gui4jCallBase, paramMap, null);
                }

                public String toString()
                {
                    return getGui4jController().getClass().getName() + ".onWindowClosing()";
                }

            };
            getGui4j().getGui4jThreadManager().performWork(null, work, null);
        }

        public void windowDeactivated(WindowEvent e)
        {
            // TODO delegate calls to Gui4jControllerAdvanced if super's
            // mGui4jController is of this type.
            // see method windowOpened
        }

        public void windowDeiconified(WindowEvent e)
        {
            // TODO delegate calls to Gui4jControllerAdvanced if super's
            // mGui4jController is of this type.
            // see method windowOpened
        }

        public void windowIconified(WindowEvent e)
        {
            // TODO delegate calls to Gui4jControllerAdvanced if super's
            // mGui4jController is of this type.
            // see method windowOpened
        }

        public void windowOpened(WindowEvent e)
        {
            Gui4jController gui4jController = getGui4jController();
            if (gui4jController == null)
            {
                return;
            }
            if (!(gui4jController instanceof Gui4jControllerAdvanced))
            {
                return;
            }

            final Gui4jControllerAdvanced gui4jControllerAdvanced = (Gui4jControllerAdvanced) gui4jController;

            Gui4jGetValue work = new Gui4jGetValue() {
                public Object getValue(Gui4jCallBase gui4jCallBase, Map paramMap, Object defaultValue)
                {
                    gui4jControllerAdvanced.windowOpened();
                    return null;
                }

                public Object getValueNoErrorChecking(Gui4jCallBase gui4jCallBase, Map paramMap,
                        Gui4jComponentInstance componentInstance)
                {
                    return getValue(gui4jCallBase, paramMap, null);
                }

                public String toString()
                {
                    return gui4jControllerAdvanced.getClass().getName() + ".windowOpened()";
                }

            };
            getGui4j().getGui4jThreadManager().performWork(null, work, null);

        }

    }


}