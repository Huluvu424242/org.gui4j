package org.gui4j.core.swing;

import javax.swing.table.*;

public abstract class RowHeaderAbstractTableModel extends AbstractTableModel
implements RowHeaderTableModel
{
  public RowHeaderAbstractTableModel()
  {
    super();
  }

  public abstract String getRowName(int row);

  public abstract void setRowName(int rowNumber, String newName);
}
