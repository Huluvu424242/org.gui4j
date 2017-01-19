package org.gui4j.core.swing;

// File: GroupableTableHeader.java
//
/*
 * (swing1.1beta3)
 * 
 */

import java.util.Enumeration;
import java.util.Vector;

import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
  * GroupableTableHeader
  *
  * @version 1.0 10/20/98
  * @author Nobuo Tamemasa
  */

public class GroupableTableHeader extends JTableHeader
{
    protected Vector columnGroups = null;

    public GroupableTableHeader(TableColumnModel model)
    {
        super(model);
        setUI(new GroupableTableHeaderUI());
        setReorderingAllowed(false);
    }

    public void setReorderingAllowed(boolean b)
    {
        reorderingAllowed = false;
    }

    public void addColumnGroup(ColumnGroup g)
    {
        if (columnGroups == null)
        {
            columnGroups = new Vector();
        }
        columnGroups.addElement(g);
    }

    public Enumeration getColumnGroups(TableColumn col)
    {
        if (columnGroups == null)
            return null;
        Enumeration en = columnGroups.elements();
        while (en.hasMoreElements())
        {
            ColumnGroup cGroup = (ColumnGroup) en.nextElement();
            Vector v_ret = cGroup.getColumnGroups(col, new Vector());
            if (v_ret != null)
            {
                return v_ret.elements();
            }
        }
        return null;
    }

    public void setColumnMargin()
    {
        if (columnGroups == null)
            return;
        int columnMargin = getColumnModel().getColumnMargin();
        Enumeration en = columnGroups.elements();
        while (en.hasMoreElements())
        {
            ColumnGroup cGroup = (ColumnGroup) en.nextElement();
            cGroup.setColumnMargin(columnMargin);
        }
    }

}