package org.gui4j.core.swing;

// Chris's entension of JTable
//
// Inspired by the RowHeaderExample by Nobuo Tamemasa.
//
// known problems:
// - Doesn't enable/disable row header when table row selection is
//   enabled/disabled
// - Dragging on the list of row headers doesn't scroll the table.  (I
// - can't figure out a good way to catch list scrolling events.)
//

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import javax.swing.AbstractListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;

public class RowHeaderTable extends Gui4jJTable
{
    protected JList rowHeader;
    protected RowHeaderTable.RowHeaderListModel rowHeaderModel;// former: ListModel

    // duplicate most of JTable's constructors for convenience

    public RowHeaderTable(RowHeaderTableModel dm, Gui4jTableListener gui4jTableListener)
    {
        super(dm, gui4jTableListener);
        initUI();
    }

    protected void initUI()
    {
        buildRowHeaders();
    }

    protected TableModel createDefaultDataModel()
    {
        return new RowHeaderDefaultTableModel();
    }

    public RowHeaderTable.RowHeaderListModel getRowHeaderModel() {
        return rowHeaderModel;
    }

    protected void buildRowHeaders()
    {
        if (rowHeader == null)
        {
            rowHeaderModel = new RowHeaderListModel();/*AbstractListModel()
            {
                private boolean ignoreBackground = false;
                public int getSize()
                {
                    return (getModel() == null) ? 0 : getModel().getRowCount();
                }
                public Object getElementAt(int index)
                {
                    TableModel dm = getModel();
                    if (dm == null)
                    {
                        return null;
                    }
                    return ((RowHeaderTableModel) dm).getRowName(index);
                }
                public void setIgnoreBackgound(boolean flag)
                {
                    ignoreBackground = flag;
                }
                public boolean ignoreBackground(boolean flag)
                {
                    return ignoreBackground;
                }
            };
*/
            rowHeader = new JList(rowHeaderModel);
            rowHeader.setFocusable(false);
            rowHeader.setSelectionModel(getSelectionModel());
            rowHeader.setFixedCellWidth(100);
            JTableHeader columnHeader = getTableHeader();
            rowHeader.setForeground(columnHeader.getForeground());
            rowHeader.setBackground(columnHeader.getBackground());
            rowHeader.setFont(columnHeader.getFont());

            rowHeader.setFixedCellHeight(getRowHeight() // + getRowMargin()
            /*+
            				   getIntercellSpacing().height*/
            );
            rowHeader.setCellRenderer(new RowHeaderRenderer());

            columnHeader.addPropertyChangeListener(new PropertyChangeListener()
            {
                public void propertyChange(PropertyChangeEvent event)
                {
                    try
                    {
                        PropertyDescriptor propDesc =
                            new PropertyDescriptor(event.getPropertyName(), JList.class);
                        Method writeMethod = propDesc.getWriteMethod();
                        Object[] params = { event.getNewValue()};
                        writeMethod.invoke(rowHeader, params);
                    }
                    catch (Exception e)
                    {
                        /* fail silently
                        System.err.println("Couldn't forward prop change for prop '" +
                        	       event.getPropertyName() + "':" + e);
                        	       */
                    }
                }
            });
        }
    }

    protected void configureEnclosingScrollPane()
    {
        super.configureEnclosingScrollPane();
        Container parent = getParent();
        if (parent instanceof JViewport)
        {
            Container grandParent = parent.getParent();
            if (grandParent instanceof JScrollPane)
            {
                JScrollPane scrollPane = (JScrollPane) grandParent;
                // Make certain we are the viewPort's view and not, for
                // example, the rowHeaderView of the scrollPane -
                // an implementor of fixed columns might do this.
                JViewport viewport = scrollPane.getViewport();
                if (viewport == null || viewport.getView() != this)
                {
                    return;
                }
                scrollPane.setRowHeaderView(rowHeader);
                rowHeader.setBackground(scrollPane.getBackground());
            }
        }
    }

    public void setRowHeaderWidth(int width)
    {
        rowHeader.setFixedCellWidth(width);
    }

    public void setRowHeaderHeight(int height)
    {
        rowHeader.setFixedCellHeight(height);
    }

    /*
    public void setRowPreferredScrollableHeight(int height)
    {
    	rowHeader.getPreferredScrollableViewportSize().height = height;
    }
    */

    public void setRowHeaderFont(Font font)
    {
        rowHeader.setFont(font);
        ((RowHeaderRenderer) rowHeader.getCellRenderer()).setFont(font);
    }

    public void refreshRowHeaders()
    {
        if (rowHeader != null)
        {
            rowHeader.repaint();
        }
    }

    // ***************************************************************************

    public class RowHeaderRenderer implements ListCellRenderer
    {

        private final Border border;
        private final Color foreground;
        private Color background;
        private Font font;
        private final JLabel label;

        RowHeaderRenderer()
        {
            JTableHeader header = RowHeaderTable.this.getTableHeader();
            border = UIManager.getBorder("TableHeader.cellBorder");
            foreground = header.getForeground();
            background = header.getBackground();
            font = header.getFont();
            label = new JLabel();
        }
        
        public void setBackground(Color background)
        {
        	this.background = background;
        }

        public Component getListCellRendererComponent(
            JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus)
        {
            Component result;
            if (value instanceof Component)
            {
                result = (Component) value;
            }
            else
            {
                label.setText((value == null) ? "" : value.toString());
                label.setOpaque(true);
                label.setHorizontalAlignment(SwingConstants.LEFT);
                label.setForeground(foreground);
                if (!rowHeaderModel.ignoreBackgound())
                {
                    label.setBackground(background);
                }
                else {
                    label.setBackground(Color.white);
                }
                label.setBorder(border);
                label.setFont(font);
                result = label;
            }
            return result;
        }

        public void setFont(Font font)
        {
            this.font = font;
        }
    }

    /**
     * Returns the rowHeader.
     * @return JList
     */
    public JList getRowHeader()
    {
        return rowHeader;
    }

    public class RowHeaderListModel extends AbstractListModel
    {
        private boolean ignoreBackground = false;
        public int getSize()
        {
            return (getModel() == null) ? 0 : getModel().getRowCount();
        }
        public Object getElementAt(int index)
        {
            TableModel dm = getModel();
            if (dm == null)
            {
                return null;
            }
            return ((RowHeaderTableModel) dm).getRowName(index);
        }
        public void setIgnoreBackgound(boolean flag)
        {
            ignoreBackground = flag;
        }
        public boolean ignoreBackground(boolean flag)
        {
            return ignoreBackground;
        }
        public boolean ignoreBackgound() {
            return ignoreBackground;
        }
    }

}
