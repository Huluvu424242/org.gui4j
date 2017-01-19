package org.gui4j.core;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.SwingUtilities;

import org.gui4j.Gui4jCallBase;
import org.gui4j.Gui4jDispose;

public class Gui4jMouseListener implements MouseListener, Gui4jDispose
{
    protected Gui4jCallBase mGui4jController;
    protected Gui4jComponentInstance mGui4jComponentInstance;
    protected Gui4jCall mOnClick;
    protected Gui4jThreadManager mGui4jThreadManager;
    private boolean mPopup; // do we have a popup menu?

    public void setOnClick(Gui4jCall onClick)
    {
        mOnClick = onClick;
    }

    public void setPopup(boolean popup)
    {
        mPopup = popup;
    }

    /**
     * Constructor for Gui4jMouseListener.
     * 
     * @param gui4jComponentInstance
     */
    public Gui4jMouseListener(Gui4jComponentInstance gui4jComponentInstance)
    {
        mGui4jComponentInstance = gui4jComponentInstance;
        mGui4jController = gui4jComponentInstance.getGui4jCallBase();
        mGui4jThreadManager = mGui4jComponentInstance.getGui4j().getGui4jThreadManager();
    }

    /**
     * @see java.awt.event.MouseListener#mouseClicked(MouseEvent)
     */
    public void mouseClicked(final MouseEvent event)
    {
        if (event.getClickCount() == 1 && mOnClick != null)
        {
            SwingUtilities.invokeLater(new Runnable() {

                public void run()
                {
                    mGui4jThreadManager.performWork(mGui4jController, mOnClick, new Gui4jMap1("", event));
                }

            });
        }
    }

    /**
     * @see java.awt.event.MouseListener#mousePressed(MouseEvent)
     */
    public void mousePressed(MouseEvent e)
    {
        maybeShowPopup(e);
    }

    /**
     * @see java.awt.event.MouseListener#mouseReleased(MouseEvent)
     */
    public void mouseReleased(MouseEvent e)
    {
        maybeShowPopup(e);
    }

    /**
     * @see java.awt.event.MouseListener#mouseEntered(MouseEvent)
     */
    public void mouseEntered(MouseEvent arg0)
    {
    }

    /**
     * @see java.awt.event.MouseListener#mouseExited(MouseEvent)
     */
    public void mouseExited(MouseEvent arg0)
    {
    }

    /**
     * @see org.gui4j.Gui4jDispose#dispose()
     */
    public void dispose()
    {
        mGui4jController = null;
        mGui4jComponentInstance = null;
        mOnClick = null;
        mGui4jThreadManager = null;
    }

    private void maybeShowPopup(MouseEvent e)
    {
        if (mPopup && e.isPopupTrigger())
        {
            Gui4jComponent gui4jComponent = mGui4jComponentInstance.getGui4jComponent();
            gui4jComponent.showPopupMenu(mGui4jComponentInstance, e);
        }
    }

}
