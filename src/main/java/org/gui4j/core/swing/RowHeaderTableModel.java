package org.gui4j.core.swing;

import javax.swing.table.*;

public interface RowHeaderTableModel extends TableModel
{
  public String getRowName(int row);
  public void setRowName(int rowNumber, Object newName);
}
