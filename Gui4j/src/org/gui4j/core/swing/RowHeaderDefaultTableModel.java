package org.gui4j.core.swing;

import javax.swing.table.*;
import java.util.Vector;

public class RowHeaderDefaultTableModel extends DefaultTableModel implements RowHeaderTableModel
{
    protected Vector rowIdentifiers;

    public RowHeaderDefaultTableModel()
    {
        super((Vector) null, 0);
        initRowIdentifiers(0);
    }

    public RowHeaderDefaultTableModel(int numRows, int numColumns)
    {
        super(numRows, numColumns);
        initRowIdentifiers(numRows);
    }

    protected void initRowIdentifiers(int numRows)
    {
        rowIdentifiers = new Vector(numRows);
        rowIdentifiers.setSize(numRows);
    }

    public String getRowName(int row)
    {
        String result;
        if (row < rowIdentifiers.size())
        {
            Object id = rowIdentifiers.elementAt(row);
            if (id == null)
            {
                result = null;
            }
            else
            {
                result = id.toString();
            }
        }
        else
        {
            result = null;
        }
        return result;
    }

    public void setRowName(int rowNumber, Object newName)
    {
        if (rowNumber < rowIdentifiers.size())
        {
            rowIdentifiers.setElementAt(newName, rowNumber);
        }
        else
        {
            throw new ArrayIndexOutOfBoundsException(rowNumber);
        }
    }

    public void addRow(Vector rowData)
    {
        addRow(null, rowData);
    }

    public void addRow(Object[] rowData)
    {
        addRow(null, convertToVector(rowData));
    }

    public void addRow(Object rowName, Vector rowData)
    {
        super.addRow(rowData);
        rowIdentifiers.addElement(rowName);
    }

    public void insertRow(int row, Vector rowData)
    {
        insertRow(row, null, rowData);
    }

    public void insertRow(int row, Object[] rowData)
    {
        insertRow(row, null, convertToVector(rowData));
    }

    public void insertRow(int row, Object rowName, Vector rowData)
    {
        super.insertRow(row, rowData);
        rowIdentifiers.insertElementAt(rowName, row);
    }

    public void moveRow(int startIndex, int endIndex, int toIndex)
    {
        super.moveRow(startIndex, endIndex, toIndex);

        if ((startIndex <= toIndex) && (toIndex <= endIndex))
        {
            return; // Nothing to move
        }

        boolean shift = toIndex < startIndex;

        // Do the move by first removing the row, then reinserting it
        for (int i = startIndex; i <= endIndex; i++)
        {
            Object rowName = rowIdentifiers.elementAt(i);
            rowIdentifiers.removeElementAt(i);
            rowIdentifiers.insertElementAt(rowName, toIndex);

            if (shift)
            {
                toIndex++;
            }
        }
    }

    public void removeRow(int row)
    {
        super.removeRow(row);
        rowIdentifiers.removeElementAt(row);
    }
}
