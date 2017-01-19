package org.gui4j.dflt;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.KeyStroke;

import org.gui4j.ExcelCopyHandler;

public class DefaultExcelCopyHandler implements ExcelCopyHandler
{
    private StringBuffer content;
    private boolean firstColumn;

    public DefaultExcelCopyHandler()
    {
        super();
    }

    public void init()
    {
        content = new StringBuffer();
        firstColumn = true;
    }

    public void addColumn(String value)
    {
        value = value.replace('\n',' ');
        if (firstColumn)
        {
            content.append(value);
            firstColumn = false;
        }
        else
        {
            content.append("\t" + value);
        }
    }

    public void nextLine()
    {
        content.append("\n");
        firstColumn = true;
    }

    /**
     * @return Keystroke for copy and removing units first
     */
    public KeyStroke getKeyStrokeCopyRemoveUnits()
    {
        return KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK, false);
    }

    public KeyStroke getKeyStrokeCopy()
    {
        return KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.SHIFT_MASK | ActionEvent.CTRL_MASK, false);
    }

    public void copyToClipboard()
    {
        StringSelection stsel = new StringSelection(content.toString());
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stsel, stsel);
    }

    // checks whether a string displays a numerical value
    protected boolean isNumeric(String toCheck)
    {
        NumberFormat nf = NumberFormat.getInstance();
        try
        {
            nf.parse(toCheck);
        }
        catch (ParseException e)
        {
            return false;
        }
        return true;
    }

    // removes units from a string, leaving only the numerical value
    public String removeUnits(String toClean)
    {
        // get 'last token'
        StringTokenizer st = new StringTokenizer(toClean);
        List tokens = new ArrayList();
        while (st.hasMoreTokens())
        {
            tokens.add(st.nextToken());
        }
        if (tokens.size() == 2)
        {
            // if 'last token' is not numeric
            if (isNumeric((String) tokens.get(0)) && !isNumeric((String) tokens.get(1)))
            {
                toClean = (String) tokens.get(0);
            }
        }
        return toClean;
    }

}
