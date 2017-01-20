package org.gui4j.core.swing;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.KeyEventDispatcher;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.FocusManager;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * KKB, 8.4.03: This code was originally copied from a Swing Forum entry on
 * Sun's website: http://forum.java.sun.com/thread.jsp?forum=57&thread=294121
 * 
 * A Panel that can be blocked.<br>
 * Just set an instance of this class as the glassPane
 * of your JFrame and call <code>block()</code> as needed.
 */
public class BlockingGlassPane extends JPanel
{
    private static final Log log = LogFactory.getLog(BlockingGlassPane.class);

    private int blockCount = 0;
    private BlockMouse blockMouse = new BlockMouse();
    private BlockKeys blockKeys = new BlockKeys();
    protected boolean beep;

    /**
     * Constructor 
     * @param beep
     */
    public BlockingGlassPane(boolean beep)
    {
        this.beep = beep;
        setVisible(false);
        setOpaque(false);
        addMouseListener(blockMouse);
    }

    /**
     * Start or end blocking.
     * @param block   should blocking be started or ended
     */
    public void block(boolean block)
    {
        if (block)
        {
            if (blockCount == 0)
            {
                setVisible(true);
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                FocusManager.getCurrentManager().addKeyEventDispatcher(blockKeys);
            }
            blockCount++;
        }
        else
        {
            if (blockCount == 0) {
                log.warn("block(false) called with blockCount == 0");
            }
            if (blockCount > 0)
            {
                blockCount--;
            }
            if (blockCount == 0)
            {
                FocusManager.getCurrentManager().removeKeyEventDispatcher(blockKeys);
                setCursor(Cursor.getDefaultCursor());
                setVisible(false);
            }
        }
    }

    /**
     * Test if this glasspane is blocked.
     * @return    <code>true</code> if currently blocked
     */
    public boolean isBlocked()
    {
        return blockCount > 0;
    }

    /**
     * The key dispatcher to block the keys.
     */
    private class BlockKeys implements KeyEventDispatcher
    {
        public boolean dispatchKeyEvent(KeyEvent ev)
        {
            Component source = ev.getComponent();
            if (source != null && SwingUtilities.isDescendingFrom(source, getParent()))
            {
                if (beep)
                {
                    Toolkit.getDefaultToolkit().beep();
                }
                ev.consume();
                return true;
            }
            return false;
        }
    }

    /** The mouse listener used to block the mouse.
     */
    private class BlockMouse extends MouseAdapter
    {
        public void mouseClicked(MouseEvent ev)
        {
            if (beep)
            {
                Toolkit.getDefaultToolkit().beep();
            }
            ev.consume();
        }
    }
}