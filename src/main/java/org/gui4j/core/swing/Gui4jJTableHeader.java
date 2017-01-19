package org.gui4j.core.swing;

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;


public final class Gui4jJTableHeader extends JTableHeader
{
    private final int height;
    
    /**
     * @param cm
     * @param font
     * @param lines
     */
    public Gui4jJTableHeader(TableColumnModel cm, Font font, int lines)
    {
        super(cm);
        JLabel label = new JLabel("XXX");
        label.setFont(font);
        height = (label.getPreferredSize().height + 1) * lines + 3;
    }

    /**
     * @see java.awt.Component#getPreferredSize()
     */
    public Dimension getPreferredSize()
    {
        Dimension d = super.getPreferredSize();
        if (d.height < height)
        {
            d.height = height;
        }
        return d;
    }

}