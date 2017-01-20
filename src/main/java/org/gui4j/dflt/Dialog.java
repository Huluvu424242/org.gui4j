package org.gui4j.dflt;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import org.gui4j.Gui4jWindow;


/**  
 */
public final class Dialog
{

    /**
     * Zeigt eine Dialogbox mit Yes/No Button. Der Cancel-Button ist optional.
     * @param gui4jWindow
     * @param msg
     * @param title
     * @param withCancel
     * @return Result
     */
    public static Result confirm(Gui4jWindow gui4jWindow, String msg, String title, boolean withCancel)
    {
        Component parent = gui4jWindow == null ? null : gui4jWindow.getWindow();
        return showConfirm(
            parent,
            msg,
            title,
            withCancel ? JOptionPane.YES_NO_CANCEL_OPTION : JOptionPane.YES_NO_OPTION,
            withCancel ? Result.CANCEL : Result.NO);
    }

    /**
     * Zeigt eine Info-Box mit Ok-Button an.
     * @param gui4jWindow
     * @param msg
     * @param title
     */
    public static void info(Gui4jWindow gui4jWindow, String msg, String title)
    {
        Component parent = gui4jWindow == null ? null : gui4jWindow.getWindow();
        JOptionPane.showMessageDialog(parent, msg, title, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Zeigt eine Warnung mit Ok-Button an.
     * @param gui4jWindow
     * @param msg
     * @param title
     */
    public static void warning(Gui4jWindow gui4jWindow, String msg, String title)
    {
        Component parent = gui4jWindow == null ? null : gui4jWindow.getWindow();
        JOptionPane.showMessageDialog(parent, msg, title, JOptionPane.WARNING_MESSAGE);
    }

	/**
	 * Zeigt eine Warnung mit Ok-Button an.
	 * @param parent Parent-Component
	 * @param msg Message
	 * @param title Title of Messagebox
	 */
	public static void warning(Component parent, String msg, String title)
	{
		JOptionPane.showMessageDialog(parent, msg, title, JOptionPane.WARNING_MESSAGE);
	}

    /**
     * Zeigt ein Text-Eingabefeld an. Wird der Abbrechen-Button gedrückt wird null zurückgegeben.
     * @param gui4jWindow
     * @param msg
     * @param value
     * @return String
     */
    public static String input(Gui4jWindow gui4jWindow, String msg, String value)
    {
        Component parent = gui4jWindow == null ? null : gui4jWindow.getWindow();
        return JOptionPane.showInputDialog(parent, msg, value);
    }

    /**
     * Method zeigt mittels der übergebenen Parameter einen JOptionPane an.
     * @param parent
     * @param msg
     * @param title
     * @param option
     * @param closeResult
     * @return Result
     */
    private static Result showConfirm(
        Component parent,
        String msg,
        String title,
        int option,
        Result closeResult)
    {
        int result = JOptionPane.showConfirmDialog(parent, msg, title, option);
        if (result == JOptionPane.CLOSED_OPTION)
        {
            return closeResult;
        }
        Result res = Result.getResult(result);
        assert res != null;
        return res;
    }

    public static class Result
    {
        public static final Result OK;
        public static final Result CANCEL;
        public static final Result YES;
        public static final Result NO;

        private static Map resultMap;

        static {
            resultMap = new HashMap();
            OK = new Result(JOptionPane.OK_OPTION);
            YES = new Result(JOptionPane.YES_OPTION);
            NO = new Result(JOptionPane.NO_OPTION);
            CANCEL = new Result(JOptionPane.CANCEL_OPTION);
        }

        protected static Result getResult(int i)
        {
            return (Result) resultMap.get(new Integer(i));
        }

        private Result(int i)
        {
            resultMap.put(new Integer(i), this);
        }
    }
}
