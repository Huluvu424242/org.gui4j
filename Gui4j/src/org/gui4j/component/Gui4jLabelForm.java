package org.gui4j.component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gui4j.Gui4jCallBase;
import org.gui4j.core.Gui4jCall;
import org.gui4j.core.Gui4jComponentContainer;
import org.gui4j.core.Gui4jComponentInstance;
import org.gui4j.core.Gui4jQualifiedComponent;
import org.gui4j.core.Gui4jSwingContainer;
import org.gui4j.core.swing.TableLayout;
import org.gui4j.core.swing.TableLayoutConstants;
import org.gui4j.core.swing.TableLayoutConstraints;

public class Gui4jLabelForm extends Gui4jJComponent
{
    protected static final Log log = LogFactory.getLog(Gui4jLabelForm.class);

    private static final int PREFERRED = (int) TableLayoutConstants.PREFERRED;
    private static final int FILL = (int) TableLayoutConstants.FILL;

    protected final List columns; // defined labelColumns

    private Gui4jCall hspCall;
    private Gui4jCall vspCall;

    protected int numColumns; // number of auto columns requested
    private int colSpacing;
    private int suffixSpacing;

    private int[] componentWidths;
    private int[] componentFormatsH;
    private int[] componentFormatsV;

    public Gui4jLabelForm(Gui4jComponentContainer gui4jComponentContainer, String id)
    {
        super(gui4jComponentContainer, JPanel.class, id);

        columns = new ArrayList();
    }

    public void addColumn(Column column)
    {
        columns.add(column);
    }

    public void setHspCall(Gui4jCall hspCall)
    {
        this.hspCall = hspCall;
    }

    public void setVspCall(Gui4jCall vspCall)
    {
        this.vspCall = vspCall;
    }

    public void setNumColumns(int columns)
    {
        this.numColumns = columns;
    }

    public void setColSpacing(int colSpacing)
    {
        this.colSpacing = colSpacing;
    }

    public void setSuffixSpacing(int suffixSpacing)
    {
        this.suffixSpacing = suffixSpacing;
    }

    public void setComponentWidths(String widths)
    {
        if (widths == null)
        {
            componentWidths = null;
            return;
        }
        StringTokenizer tokenizer = new StringTokenizer(widths, ",");
        componentWidths = new int[tokenizer.countTokens()];
        for (int col = 0; col < componentWidths.length; col++)
        {
            componentWidths[col] = parseComponentWidth(tokenizer.nextToken());
        }
    }

    public void setComponentFormats(String formats)
    {
        if (formats == null)
        {
            componentFormatsH = null;
            componentFormatsV = null;
            return;
        }
        StringTokenizer tokenizer = new StringTokenizer(formats, ",");
        componentFormatsH = new int[tokenizer.countTokens()];
        componentFormatsV = new int[tokenizer.countTokens()];
        for (int col = 0; col < componentFormatsH.length; col++)
        {
            String token = tokenizer.nextToken();
            componentFormatsH[col] = parseComponentFormatH(token);
            componentFormatsV[col] = parseComponentFormatV(token);
        }
    }

    static int parseComponentWidth(String width)
    {
        width = width.trim();
        if (width.toUpperCase().equals("P"))
        {
            return PREFERRED;
        }
        if (width.toUpperCase().equals("F"))
        {
            return FILL;
        }

        try
        {
            int parsed = Integer.parseInt(width);
            return parsed;
        }
        catch (NumberFormatException e)
        {
            log.warn("Invalid column width parameter: " + width);
            return FILL;
        }

    }

    static int parseComponentFormatH(String width)
    {
        width = width.trim();
        char c = ' ';
        if (width.length() > 0)
        {
            c = width.toUpperCase().charAt(0);
        }
        if (c == 'L')
        {
            return TableLayoutConstants.LEFT;
        }
        if (c == 'R')
        {
            return TableLayoutConstants.RIGHT;
        }
        if (c == 'C')
        {
            return TableLayoutConstants.CENTER;
        }
        return TableLayoutConstants.FULL;
    }

    static int parseComponentFormatV(String width)
    {
        width = width.trim();
        char c = ' ';
        if (width.length() > 1)
        {
            c = width.toUpperCase().charAt(1);
        }
        if (c == 'T')
        {
            return TableLayoutConstants.TOP;
        }
        if (c == 'B')
        {
            return TableLayoutConstants.BOTTOM;
        }
        if (c == 'C')
        {
            return TableLayoutConstants.CENTER;
        }
        return TableLayoutConstants.FULL;
    }

    protected Gui4jComponentInstance createComponentInstance(Gui4jSwingContainer gui4jSwingContainer,
            Gui4jCallBase gui4jCallBase, Gui4jQualifiedComponent gui4jComponentInPath)
    {

        log.debug("Preparing labelForm: " + gui4jComponentInPath.getQualifiedId());

        // retrieve hspacing and vspacing values
        int hSpacing = getHSpacing(gui4jCallBase);
        int vSpacing = getVSpacing(gui4jCallBase);

        // calculate allocation of labels and components to logical layout cells
        CellAllocation allocation = new CellAllocation();

        // define TableLayout column sizes
        int tableColsPerColumn = 7; // colgap, marker, label, hspacing,
                                    // component, suffSpacing, suffix
        int tableCols = allocation.getCols() * tableColsPerColumn - 1; // first column doesn't have colgap
        double colSizes[] = new double[tableCols];
        int layoutCol = 0;
        for (int col = 0; col < allocation.getCols(); col++)
        {
            if (col >= 1)
            {
                // only 2nd and higher label/component numColumns have colgap
                colSizes[layoutCol++] = colSpacing;
            }
            colSizes[layoutCol++] = TableLayoutConstants.PREFERRED; // marker column
            colSizes[layoutCol++] = TableLayoutConstants.PREFERRED; // label column
            colSizes[layoutCol++] = hSpacing;
            colSizes[layoutCol++] = getComponentWidth(col); // component column
            colSizes[layoutCol++] = suffixSpacing;
            colSizes[layoutCol++] = TableLayoutConstants.PREFERRED; // suffix column
        }

        // define TableLayout row sizes
        int tableRowsPerRow = 2; // content row and vspacing row
        int tableRows = allocation.getRows() * tableRowsPerRow - 1; // first row doesnt'thave leading
        // vspacing
        double rowSizes[] = new double[tableRows];
        int layoutRow = 0;
        for (int i = 0; i < allocation.getRows(); i++)
        {
            if (i >= 1)
            {
                // 2nd and higher rows have leading vspacing row
                rowSizes[layoutRow++] = vSpacing;
            }
            rowSizes[layoutRow++] = TableLayoutConstants.PREFERRED;
        }

        // create TableLayout
        double[][] layoutSizes = { colSizes, rowSizes };
        TableLayout tableLayout = new TableLayout(layoutSizes);
        JPanel panel = new JPanel(tableLayout);
        log.debug("Defined tableLayout with " + colSizes.length + " cols and " + rowSizes.length + " rows.");

        // prepare constraints
        TableLayoutConstraints markerConstraints = new TableLayoutConstraints();
        markerConstraints.hAlign = TableLayoutConstants.FULL;
        markerConstraints.vAlign = TableLayoutConstants.TOP;
        TableLayoutConstraints labelConstraints = new TableLayoutConstraints();
        labelConstraints.hAlign = TableLayoutConstants.FULL;
        labelConstraints.vAlign = TableLayoutConstants.FULL;
        TableLayoutConstraints componentConstraints = new TableLayoutConstraints();
        componentConstraints.hAlign = TableLayoutConstants.FULL;
        componentConstraints.vAlign = TableLayoutConstants.TOP;
        TableLayoutConstraints suffixConstraints = new TableLayoutConstraints();
        componentConstraints.hAlign = TableLayoutConstants.LEFT;
        componentConstraints.vAlign = TableLayoutConstants.TOP;

        // loop through all logical cells and define layout cells
        for (int col = 0; col < allocation.getCols(); col++)
        {
            layoutRow = 0;
            for (int row = 0; row < allocation.getRows(); row++)
            {
                // reset table column
                layoutCol = col * tableColsPerColumn;

                Cell cell = allocation.getCell(col, row);
                if (cell != null)
                {

                    // insert marker into table layout
                    Gui4jQualifiedComponent markerPath = cell.getMarker();
                    if (markerPath != null)
                    {
                        Gui4jComponentInstance markerInstance = gui4jSwingContainer.getGui4jComponentInstance(
                                gui4jComponentInPath.getGui4jComponentPath(), markerPath);
                        JComponent marker = markerInstance.getSwingComponent();
                        setConstraintCell(markerConstraints, layoutCol, layoutRow);
                        panel.add(marker, markerConstraints);
                        log.debug("Filling layout [" + layoutCol + "][" + layoutRow + "] with marker.");
                    }
                    layoutCol++;

                    // insert label into table layout
                    Gui4jQualifiedComponent labelPath = cell.getLabel();
                    JComponent label = null;
                    if (labelPath != null)
                    {
                        Gui4jComponentInstance labelInstance = gui4jSwingContainer.getGui4jComponentInstance(
                                gui4jComponentInPath.getGui4jComponentPath(), labelPath);
                        label = labelInstance.getSwingComponent();
                        setConstraintCell(labelConstraints, layoutCol, layoutRow);
                        panel.add(label, labelConstraints);
                        log.debug("Filling layout [" + layoutCol + "][" + layoutRow + "] with label.");
                    }
                    layoutCol += 2; // skip hSpacing column

                    // insert component into table layout
                    Gui4jQualifiedComponent componentPath = cell.getComponent();
                    JComponent component = null;
                    if (componentPath != null)
                    {
                        Gui4jComponentInstance componentInstance = gui4jSwingContainer.getGui4jComponentInstance(
                                gui4jComponentInPath.getGui4jComponentPath(), componentPath);
                        component = componentInstance.getSwingComponent();
                        int spanCol = (cell.getSpanTo() * tableColsPerColumn) + 3;
                        int componentNo = col*allocation.getRows()+row;
                        componentConstraints.hAlign = TableLayoutConstants.FULL;
                        componentConstraints.vAlign = TableLayoutConstants.FULL;
                        if (componentFormatsH != null && componentNo < componentFormatsH.length) {
                            componentConstraints.hAlign = componentFormatsH[componentNo];
                        }
                        if (componentFormatsV != null && componentNo < componentFormatsV.length) {
                            componentConstraints.vAlign = componentFormatsV[componentNo];
                        }
                        setConstraintCell(componentConstraints, layoutCol, layoutRow, spanCol);
                        panel.add(component, componentConstraints);
                        log.debug("Filling layout [" + layoutCol + "][" + layoutRow + "] with component.");
                    }
                    layoutCol++;
                    layoutCol++;
                    // insert marker into table layout
                    Gui4jQualifiedComponent suffixPath = cell.getSuffix();
                    if (suffixPath != null)
                    {
                        Gui4jComponentInstance suffixInstance = gui4jSwingContainer.getGui4jComponentInstance(
                                gui4jComponentInPath.getGui4jComponentPath(), suffixPath);
                        JComponent suffix = suffixInstance.getSwingComponent();
                        setConstraintCell(suffixConstraints, layoutCol, layoutRow);
                        panel.add(suffix, suffixConstraints);
                        log.debug("Filling layout [" + layoutCol + "][" + layoutRow + "] with suffix.");
                    }
                    layoutCol++;

                    // associate label and component
                    if (label != null && component != null && label instanceof JLabel)
                    {
                        ((JLabel) label).setLabelFor(component);
                    }

                    layoutRow += 2; // skip vspacing row

                }
            }
        }

        Gui4jComponentInstance gui4jComponentInstance = new Gui4jComponentInstance(gui4jSwingContainer, panel,
                gui4jComponentInPath);

        return gui4jComponentInstance;
    }

    private int getVSpacing(Gui4jCallBase gui4jCallBase)
    {
        int vSpacing = 0;
        if (vspCall != null)
        {
            Integer vspCallResult = (Integer) vspCall.getValue(gui4jCallBase, Collections.EMPTY_MAP, new Integer(0));
            if (vspCallResult != null)
            {
                vSpacing = vspCallResult.intValue();
            }
        }
        return vSpacing;
    }

    private int getHSpacing(Gui4jCallBase gui4jCallBase)
    {
        int hSpacing = 0;
        if (hspCall != null)
        {
            Integer hspCallResult = (Integer) hspCall.getValue(gui4jCallBase, Collections.EMPTY_MAP, new Integer(0));
            if (hspCallResult != null)
            {
                hSpacing = hspCallResult.intValue();
            }
        }
        return hSpacing;
    }

    private double getColSize(int i)
    {
        if (i == PREFERRED)
        {
            return TableLayoutConstants.PREFERRED;
        }
        if (i == FILL)
        {
            return TableLayoutConstants.FILL;
        }
        return i;
    }

    private double getComponentWidth(int col)
    {
        if (col < columns.size())
        {
            Column column = (Column) columns.get(col);
            if (column != null && column.getComponentWidth() != null)
            {
                return getColSize(parseComponentWidth(column.getComponentWidth()));
            }
        }
        if (componentWidths != null && componentWidths.length > col)
        {
            return getColSize(componentWidths[col]);
        }
        else
        {
            return TableLayoutConstants.FILL;
        }
    }

    private void setConstraintCell(TableLayoutConstraints constraints, int col, int row)
    {
        // log.debug("Setting constraints to col: " + col + ", row: " + row);
        constraints.col1 = col;
        constraints.col2 = col;
        constraints.row1 = row;
        constraints.row2 = row;
    }

    private void setConstraintCell(TableLayoutConstraints constraints, int col, int row, int spanTo)
    {
        // log.debug("Setting constraints to col: " + col + ", row: " + row);
        constraints.col1 = col;
        constraints.col2 = spanTo;
        constraints.row1 = row;
        constraints.row2 = row;
    }

    // *************************************************************************

    public static class Column
    {
        public static final int SPAN_ALL = 0;

        private final List labels;
        private final List components;
        private final List markers;
        private final List suffixes;

        private String componentWidth;
        private int[] colSpans;

        public Column()
        {
            super();
            labels = new ArrayList();
            components = new ArrayList();
            markers = new ArrayList();
            suffixes = new ArrayList();
        }

        public int size()
        {
            return Math.max(labels.size(), components.size());
        }

        public String getComponentWidth()
        {
            return componentWidth;
        }

        public void setComponentWidth(String componentWidth)
        {
            this.componentWidth = componentWidth;
        }

        public Gui4jQualifiedComponent getMarker(int index)
        {
            return getElement(markers, index);
        }

        public Gui4jQualifiedComponent getSuffix(int index)
        {
            return getElement(suffixes, index);
        }

        public Gui4jQualifiedComponent getLabel(int index)
        {
            return getElement(labels, index);
        }

        public Gui4jQualifiedComponent getComponent(int index)
        {
            return getElement(components, index);
        }

        private static Gui4jQualifiedComponent getElement(List list, int index)
        {
            if (index < list.size())
            {
                return (Gui4jQualifiedComponent) list.get(index);
            }
            else
            {
                return null;
            }
        }

        public void addLabel(Gui4jQualifiedComponent label)
        {
            labels.add(label);
        }

        public void addComponent(Gui4jQualifiedComponent component)
        {
            components.add(component);
        }

        public void addMarker(Gui4jQualifiedComponent marker)
        {
            markers.add(marker);
        }

        public void addSuffix(Gui4jQualifiedComponent suffix)
        {
            suffixes.add(suffix);
        }

        public List getComponents()
        {
            return components;
        }

        public List getLabels()
        {
            return labels;
        }

        public List getMarkers()
        {
            return markers;
        }

        public List getSuffices()
        {
            return suffixes;
        }

        public void setColSpans(String spans)
        {
            if (spans == null)
            {
                this.colSpans = null;
                return;
            }
            StringTokenizer tokenizer = new StringTokenizer(spans, ",");
            this.colSpans = new int[tokenizer.countTokens()];
            for (int index = 0; index < this.colSpans.length; index++)
            {
                this.colSpans[index] = parseColSpan(tokenizer.nextToken());
            }
        }

        private static int parseColSpan(String span)
        {
            if ("all".equals(span.toLowerCase()))
            {
                return SPAN_ALL;
            }

            try
            {
                return Integer.parseInt(span);
            }
            catch (NumberFormatException e)
            {
                log.warn("Invalid colSpan parameter: " + span);
                return 1;
            }
        }

        /**
         * Returns the requested colSpan for the element in this column with the
         * given index. Default is 1, if no colSpan has been explicitly set.
         * 
         * @param index
         * @return int
         */
        public int getColSpan(int index)
        {
            if (colSpans == null || index >= colSpans.length)
            {
                return 1;
            }
            return colSpans[index];
        }
    }

    private static class Cell
    {
        public static final Cell SPANNED = new Cell(null, null, null, null);

        private final Gui4jQualifiedComponent marker;
        private final Gui4jQualifiedComponent label;
        private final Gui4jQualifiedComponent component;
        private final Gui4jQualifiedComponent suffix;

        private int spanTo; // column this cell spans to

        public Cell(final Gui4jQualifiedComponent marker, final Gui4jQualifiedComponent label,
                final Gui4jQualifiedComponent component, final Gui4jQualifiedComponent suffix)
        {
            super();
            this.marker = marker;
            this.label = label;
            this.component = component;
            this.suffix = suffix;
        }

        public int getSpanTo()
        {
            return spanTo;
        }

        public void setSpanTo(int spanTo)
        {
            this.spanTo = spanTo;
        }

        public Gui4jQualifiedComponent getComponent()
        {
            return component;
        }

        public Gui4jQualifiedComponent getLabel()
        {
            return label;
        }

        public Gui4jQualifiedComponent getMarker()
        {
            return marker;
        }

        public Gui4jQualifiedComponent getSuffix()
        {
            return suffix;
        }
    }

    private class CellAllocation
    {
        private Cell[][] allocation;
        private int rows;
        private int cols;

        public CellAllocation()
        {
            allocateCells();
            log.debug("Allocated " + cols + " columns and " + rows + " rows.");
        }

        private void allocateCells()
        {
            cols = columns.size();

            // handle auto columns feature (automatic creation of several
            // coumns)
            boolean autoColumns = (cols == 1 && numColumns > 1);
            int autoRows = 0;
            if (autoColumns)
            {
                int maxIndex = ((Column) columns.get(0)).size();
                autoRows = maxIndex / numColumns;
                if (maxIndex % numColumns > 0)
                {
                    autoRows++;
                }
                cols = numColumns;
            }

            // create allocation array (maximum possible size)
            if (autoColumns)
            {
                allocation = new Cell[cols][autoRows];
            }
            else
            {
                int maxRows = 0;
                for (Iterator iter = columns.iterator(); iter.hasNext();)
                {
                    Column column = (Column) iter.next();
                    maxRows += column.size();
                }
                allocation = new Cell[cols][maxRows];
            }

            // create allocation of cells
            int col = 0;
            rows = 0; // number of allocated rows
            for (Iterator iter = columns.iterator(); iter.hasNext();)
            {
                Column column = (Column) iter.next();

                int row = 0;
                for (int index = 0; index < column.size(); index++)
                {
                    // (note: simultaneous use of autocolumns and colSpans is
                    // not supported)

                    // skip cells occupied by other cells that span several
                    // columns
                    while (allocation[col][row] == Cell.SPANNED)
                    {
                        row++;
                    }

                    // allocate cell
                    log.debug("Allocating [" + col + "][" + row + "]");
                    allocation[col][row] = new Cell(column.getMarker(index), column.getLabel(index), column
                            .getComponent(index), column.getSuffix(index));

                    // mark spanned columns
                    int colSpan = column.getColSpan(index);
                    int spanTo;
                    if (colSpan == 1)
                    {
                        spanTo = col;
                    }
                    else if (colSpan == Column.SPAN_ALL)
                    {
                        spanTo = cols - 1;
                    }
                    else
                    {
                        spanTo = Math.min(cols - 1, col + colSpan - 1);
                    }
                    for (int spanned = col + 1; spanned <= spanTo; spanned++)
                    {
                        allocation[spanned][row] = Cell.SPANNED;
                    }
                    allocation[col][row].setSpanTo(spanTo);

                    // define maximum row number
                    rows = Math.max(row, rows);

                    // advance to next row
                    row++;
                    if (autoColumns && row >= autoRows)
                    {
                        row = 0;
                        col++;
                    }

                }
                col++;
            }
            rows++;

            return;
        }

        public int getCols()
        {
            return cols;
        }

        public int getRows()
        {
            return rows;
        }

        public Cell getCell(int col, int row)
        {
            assert col < cols;
            assert row < rows;
            return allocation[col][row];
        }

    }

}