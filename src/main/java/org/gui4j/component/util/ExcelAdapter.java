package org.gui4j.component.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

import org.gui4j.ExcelCopyHandler;
import org.gui4j.core.swing.ColumnGroup;
import org.gui4j.core.swing.GroupableTableHeader;

/**
 * ExcelAdapter enables Copy-Paste Clipboard functionality on JTables. The
 * clipboard data format used by the adapter is compatible with the clipboard
 * format used by Excel. This provides for clipboard interoperability between
 * enabled JTables and Excel.
 */
public class ExcelAdapter implements ActionListener
{
    private JTable table;
    private JList rowHeader;
    private final ExcelCopyHandler excelCopyHandler;

    private static final String ACTION_COPY = "Copy";
    private static final String ACTION_COPY_REMOVE_UNITS = "CopyRemoveUnits";

    /**
     * The Excel Adapter is constructed with a JTable on which it enables
     * Copy-Paste and acts as a Clipboard listener.
     */
    public ExcelAdapter(JTable myJTable, JList rowHeader, ExcelCopyHandler excelCopyHandler)
    {
        this.table = myJTable;
        this.rowHeader = rowHeader;
        this.excelCopyHandler = excelCopyHandler;

        KeyStroke copyRemoveUnits = excelCopyHandler.getKeyStrokeCopyRemoveUnits();
        KeyStroke copy = excelCopyHandler.getKeyStrokeCopy();

        if (copy != null) {
            table.registerKeyboardAction(this, ACTION_COPY, copy, JComponent.WHEN_FOCUSED);
        }
        if (copyRemoveUnits != null) {
            table.registerKeyboardAction(this, ACTION_COPY_REMOVE_UNITS, copyRemoveUnits, JComponent.WHEN_FOCUSED);
        }
    }

    /**
     * This method is activated on the Keystrokes we are listening to in this
     * implementation. Here it listens for Copy and Paste ActionCommands.
     */
    public void actionPerformed(ActionEvent e)
    {
        // COPY
        if (e.getActionCommand().equals(ACTION_COPY) || e.getActionCommand().equals(ACTION_COPY_REMOVE_UNITS))
        {
            // check whether to remove units or not
            boolean removeUnits = false;
            if (e.getActionCommand().compareTo(ACTION_COPY_REMOVE_UNITS) == 0)
                removeUnits = true;

            // get table dimensions
            int numcols = table.getColumnCount();
            int numrows = table.getRowCount();

            excelCopyHandler.init();

            // table header freakout =)
            // converts header column groups (column headers over more than one
            // column)
            // into an excel string... funny work though
            convertColumnGroups();

            // append headers to buffer
            if (rowHeader != null)
            {
                excelCopyHandler.addColumn("");
            }
            for (int j = 0; j < numcols; j++)
            {
                excelCopyHandler.addColumn(clean(table.getTableHeader().getColumnModel().getColumn(j).getHeaderValue(),
                        removeUnits));
            }
            excelCopyHandler.nextLine();

            // append rowheader and table data to buffer
            for (int i = 0; i < numrows; i++)
            {
                if (rowHeader != null)
                {
                    excelCopyHandler.addColumn(clean(rowHeader.getModel().getElementAt(i), removeUnits));
                }
                for (int j = 0; j < numcols; j++)
                {
                    // if celldata is null, display empty string, data otherwise
                    excelCopyHandler.addColumn(table.getValueAt(i, j) == null ? "" : clean(table.getValueAt(i, j),
                            removeUnits));
                }
                excelCopyHandler.nextLine();
            }

            // put buffer to clipboard
            excelCopyHandler.copyToClipboard();
        }

    }

    // removes html-tags from a string
    protected String clean(Object toClean, boolean removeUnits)
    {
        if (toClean instanceof String)
        {
            if (removeUnits)
            {
                return excelCopyHandler.removeUnits(html2string((String) toClean));
            }
            else
            {
                return html2string((String) toClean);
            }
        }
        return toClean.toString();
    }

    // removes html-tags from a string
    protected String html2string(String toClean)
    {
        if (toClean.indexOf("<html>") != -1)
            toClean = toClean.replaceAll("<br>", " ");
        toClean = toClean.replaceAll("<br/>", " ");
        toClean = toClean.replaceAll("<.*?>", "");
        return toClean;
    }

    // returns the columngroups a column is in as a list
    private List getColGroupsList(TableColumn col)
    {
        Enumeration en = null;
        if (table.getTableHeader() instanceof GroupableTableHeader)
        {
            en = ((GroupableTableHeader) table.getTableHeader()).getColumnGroups(col);
        }
        else
        {
            // TODO: untersuchen, wann ein table header kein groupable
            // tableheader ist, und was
            // dann passieren soll!
        }
        List list = new ArrayList();
        if (en != null)
        {
            while (en.hasMoreElements())
            {
                list.add(en.nextElement());
            }
        }
        return list;
    }

    // appends the contents of multiple columnheaders (headers that span over
    // multiple columns)
    // to a stringbuffer
    private void convertColumnGroups()
    {
        JTableHeader header = table.getTableHeader();
        Map map = new HashMap();

        // get maximum number of column groups a column is in, map columns to
        // column groups
        int maxColGroups = 0;
        if (header.getColumnModel() != null)
        {
            Enumeration enumeration = header.getColumnModel().getColumns();
            while (enumeration.hasMoreElements())
            {
                TableColumn aColumn = (TableColumn) enumeration.nextElement();
                map.put(aColumn, getColGroupsList(aColumn));
                maxColGroups = Math.max(maxColGroups, getColGroupsList(aColumn).size());
            }
        }

        // init string array describing position of header column groups' text
        // values
        String[][] headers = null;
        int numcols = table.getColumnCount();
        if (rowHeader != null)
        {
            headers = new String[numcols + 1][maxColGroups];
        }
        else
        {
            headers = new String[numcols][maxColGroups];
        }
        for (int x = 0; x < headers.length; x++)
            for (int y = 0; y < headers[0].length; y++)
                headers[x][y] = "";

        // get values from column groups
        int xCounter = 0;
        if (rowHeader != null)
        {
            xCounter = 1;
        }
        if (header.getColumnModel() != null)
        {
            Enumeration enumeration = header.getColumnModel().getColumns();
            while (enumeration.hasMoreElements())
            {
                TableColumn aColumn = (TableColumn) enumeration.nextElement();
                List cols = (List) map.get(aColumn);
                Iterator it = cols.iterator();
                int yCounter = 0;
                while (it.hasNext())
                {
                    headers[xCounter][yCounter] = (String) ((ColumnGroup) it.next()).getHeaderValue();
                    yCounter++;
                }
                xCounter++;
            }
        }

        // convert array to string, put into stringbuffer
        for (int y = 0; y < headers[0].length; y++)
        {
            for (int x = 0; x < headers.length; x++)
            {
                excelCopyHandler.addColumn(headers[x][y]);
            }
            excelCopyHandler.nextLine();
        }
    }
}