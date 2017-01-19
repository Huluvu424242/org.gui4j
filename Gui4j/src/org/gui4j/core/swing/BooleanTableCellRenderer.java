package org.gui4j.core.swing;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;

public class BooleanTableCellRenderer extends JCheckBox implements TableCellRenderer
{

    private final Border noFocusBorder;

    // background color used when the cell is not selected
    private Color unselectedBackground;

    public BooleanTableCellRenderer(Border noFocusBorder)
    {
        super();
        setHorizontalAlignment(SwingConstants.CENTER);
        this.noFocusBorder = noFocusBorder;

        setBorder(noFocusBorder);
        setBorderPainted(true);
        setOpaque(true);
    }

    public Component getTableCellRendererComponent(
        JTable table,
        Object value,
        boolean isSelected,
        boolean hasFocus,
        int row,
        int column)
    {

        // is the table cell selected?
        if (isSelected)
        {
            setForeground(table.getSelectionForeground());
            setBackground(table.getSelectionBackground());
        }
        else
        {
            setForeground(table.getForeground());
            setBackground(table.getBackground());
        }

        // should the checkmark be set?
        setSelected((value != null && ((Boolean) value).booleanValue()));

        // should we paint a focus border?
        if (hasFocus)
        {
            setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
            if (table.isCellEditable(row, column))
            {
                super.setForeground(UIManager.getColor("Table.focusCellForeground"));
                super.setBackground(UIManager.getColor("Table.focusCellBackground"));
            }
        }
        else
        {
            setBorder(noFocusBorder);
        }

        return this;
    }

    public void setUnselectedBackground(Color unselectedBackground)
    {
        this.unselectedBackground = unselectedBackground;
    }    
    
    /**
     * This method returns the background color to use for an unselected table cell.
     * This method is provided for custom swing UI delegates that need to know the
     * original background color, e.g. for rendering the checkbox icon independently
     * from the cell being selected or not.
     * @return The currently active background color for non-selected table cells.
     */
    public Color getUnselectedBackground()
    {
        return unselectedBackground;
    }

}

