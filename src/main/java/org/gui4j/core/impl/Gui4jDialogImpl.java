package org.gui4j.core.impl;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.KeyStroke;
import javax.swing.RootPaneContainer;
import javax.swing.WindowConstants;

import org.gui4j.Gui4jController;
import org.gui4j.core.interfaces.Gui4jDialogInternal;
import org.gui4j.core.interfaces.Gui4jViewInternal;


final class Gui4jDialogImpl extends Gui4jWindowImpl implements Gui4jDialogInternal
{    
    private final Object mOwner;

    public Gui4jDialogImpl(
        Gui4jImpl gui4j,
        Object owner,
        String viewResourceName,
        Gui4jController gui4jController,
        String title,
        boolean readOnlyMode)
    {
        super(gui4j, viewResourceName, gui4jController, title, readOnlyMode);
        mOwner = owner;
    }

    protected Window createWindow()
    {
        JDialog dialog;
        if (mOwner == null)
        {
            dialog = new JDialog((Frame) null, getTitle(), true);
        }
        else if (mOwner instanceof Gui4jViewInternal)
        {
            dialog = new JDialog((Frame)((Gui4jViewImpl) mOwner).getWindow(), getTitle(), true);
        }
        else if (mOwner instanceof Gui4jDialogInternal)
        {
            dialog = new JDialog(((Gui4jDialogImpl) mOwner).getDialog(), getTitle(), true);
        } else if (mOwner instanceof Frame)
        {
            dialog = new JDialog((Frame) mOwner, getTitle(), true);
        } else
        {
            assert mOwner instanceof Dialog;
            dialog = new JDialog((Dialog) mOwner, getTitle(), true);
        }
        
        dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        return dialog;
    }

    /**
     * This method is used to change the dialog's title.
     * @param title
     */
    public void changeWindowTitle(String title)
    {
        if (getDialog() != null)
        {
            getDialog().setTitle(title);
        }
    }

    private JDialog getDialog()
    {
        return (JDialog) getWindow();
    }

    protected RootPaneContainer getRootPaneContainer()
    {
        return getDialog();
    }


    protected void defineWindowActions()
    {
        
        // pressing ESCAPE should have same effect as klicking the "close window" cross
        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
        Action escapeAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                requestWindowClosing();
            }
        };
        getRootPaneContainer().getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKeyStroke, "ESCAPE");
        getRootPaneContainer().getRootPane().getActionMap().put("ESCAPE", escapeAction);
    }

}
