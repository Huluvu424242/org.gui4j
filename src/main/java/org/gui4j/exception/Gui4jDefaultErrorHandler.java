package org.gui4j.exception;

import javax.swing.JOptionPane;



/**
 * Default Implementierung für <code>Gui4jErrorHandler</code>. Diese
 * Implementierung zeigt ein Fenster mit einem <b>Ok</b> und
 * einem <b>Abort</b> Button an.
 */
public class Gui4jDefaultErrorHandler implements Gui4jErrorHandler
{
    private static final Gui4jErrorHandler DEFAULT_HANDLER = new Gui4jDefaultErrorHandler();
    private boolean dialogAlreadyOpen;

    /**
     * Constructor for Gui4jDefaultErrorHandler.
     */
    private Gui4jDefaultErrorHandler()
    {
        super();
    }

    /**
     * @return immer die gleiche Instanz von <code>Gui4jErrorHandler</code>, welche
     * ein Dialogfenster mit <code>Ok</code> und <code>Abort</code> Button enthält.
    */
    public static Gui4jErrorHandler getInstance()
    {
        return DEFAULT_HANDLER;
    }

    /**
     * @see org.gui4j.exception.Gui4jErrorHandler#internalError(Throwable)
     */
    public void internalError(final Throwable e)
    {
        final Gui4jErrorHandler THIS = this;
        synchronized (THIS)
        {
            if (dialogAlreadyOpen)
            {
                // do not show more dialogs
                return;
            }
            else
            {
                dialogAlreadyOpen = true;
            }
        }
        Object[] options = { "OK", "ABORT" };
        int result =
            JOptionPane.showOptionDialog(
                null,
                "Internal Error occured. Click OK to continue.\n\nDetails:\n" + e,
                "Warning",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                options,
                options[0]);
        dialogAlreadyOpen = false;
        if (result == 1)
        {
            System.exit(1);
        }
    }

}
