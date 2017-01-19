package org.gui4j.component;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gui4j.Gui4jCallBase;
import org.gui4j.Gui4jGetValue;
import org.gui4j.component.util.ExcelAdapter;
import org.gui4j.component.util.StringUtil;
import org.gui4j.core.Gui4jCall;
import org.gui4j.core.Gui4jComponentContainer;
import org.gui4j.core.Gui4jComponentInstance;
import org.gui4j.core.Gui4jMap1;
import org.gui4j.core.Gui4jQualifiedComponent;
import org.gui4j.core.Gui4jSwingContainer;
import org.gui4j.core.Gui4jTextAttribute;
import org.gui4j.core.Gui4jThreadManager;
import org.gui4j.core.swing.BooleanTableCellRenderer;
import org.gui4j.core.swing.ComboBoxCellEdit;
import org.gui4j.core.swing.Gui4jCellEditor;
import org.gui4j.core.swing.Gui4jJTable;
import org.gui4j.core.swing.Gui4jRefreshTable;
import org.gui4j.core.swing.Gui4jTableListener;
import org.gui4j.core.swing.MultiLineLabelUI;
import org.gui4j.core.swing.RowHeaderAbstractTableModel;
import org.gui4j.core.swing.RowHeaderTable;
import org.gui4j.core.util.ComboBoxNullItem;
import org.gui4j.event.Gui4jEventListener;

/**
 * Table where display (reflection) methods can be defined for each cell
 * independantly. The table always contains a header row.
 */
public class Gui4jCellTable extends Gui4jJComponent
{
    public static final String PARAM_ROW = "row";
    public static final String PARAM_COL = "col";
    public static final String PARAM_VALUE = "value";
    public static final String PARAM_COLVALUE = "colValue";
    public static final String PARAM_ROWVALUE = "rowValue";
    public static final String PARAM_LIST = "list";
    public static final String PARAM_LIST_ITEM = "listItem";

    protected static final Log mLogger = LogFactory.getLog(Gui4jCellTable.class);

    protected final Gui4jCall[] mRowValue;
    protected final Gui4jCall[] mRowName;
    protected final Gui4jTextAttribute[] mRowTextAttribute;
    protected final String[] mRowIndentation;

    protected final int mRows;
    protected final int mVisibleRows;
    protected final int mCols;
    protected final Gui4jColumnTable[] mColumn;
    protected final Gui4jCell[][] mCell;
    protected final Gui4jThreadManager mGui4jThreadManager;
    protected Gui4jCall[] mRefresh;
    protected Gui4jCall mOnSetValue;
    private boolean mReorderingAllowed = true;
    private boolean mRowSelectionAllowed = true;
    private int mRowSelectionMode = ListSelectionModel.SINGLE_SELECTION;
    private int mColSelectionMode = ListSelectionModel.SINGLE_SELECTION;
    private final boolean mUseRowHeaders;
    private final boolean mUseColumnsHeaders;
    private Gui4jCall mHeaderBackground;
    protected final boolean mAutomaticRefresh;

    /**
     * Constructor for Gui4jCellTable
     * 
     * @param gui4jComponentContainer
     * @param id
     * @param rows
     * @param cols
     * @param visibleRows
     * @param useRowHeaders
     * @param useColumnHeaders
     * @param automaticRefresh
     */
    public Gui4jCellTable(Gui4jComponentContainer gui4jComponentContainer, String id, int rows, int cols,
            int visibleRows, boolean useRowHeaders, boolean useColumnHeaders, boolean automaticRefresh)
    {
        super(gui4jComponentContainer, useRowHeaders ? RowHeaderTable.class : Gui4jJTable.class, id);
        mUseRowHeaders = useRowHeaders;
        mUseColumnsHeaders = useColumnHeaders;
        mAutomaticRefresh = automaticRefresh;
        mColumn = new Gui4jColumnTable[cols];
        mRowValue = new Gui4jCall[rows];
        mRowName = new Gui4jCall[rows];
        mRowTextAttribute = new Gui4jTextAttribute[rows];
        mRowIndentation = new String[rows];
        for (int i = 0; i < mRowIndentation.length; i++)
        {
            mRowIndentation[i] = "";
        }
        mRows = rows;
        mCols = cols;
        if (visibleRows > rows)
        {
            mVisibleRows = rows;
        }
        else
        {
            mVisibleRows = visibleRows;
        }
        mCell = new Gui4jCell[mRows][mCols];
        mGui4jThreadManager = getGui4j().getGui4jThreadManager();
    }

    /**
     * Sets the headerBackground.
     * 
     * @param headerBackground
     */
    public void setHeaderBackground(Gui4jCall headerBackground)
    {
        mHeaderBackground = headerBackground;
    }

    protected void setProperties(Gui4jComponentInstance gui4jComponentInstance)
    {
        super.setProperties(gui4jComponentInstance);
        Gui4jCallBase gui4jController = gui4jComponentInstance.getGui4jCallBase();
        Gui4jJTable table = (Gui4jJTable) gui4jComponentInstance.getComponent();
        Font font = table.getFont();
        table.setDefaultEditor(String.class, Gui4jCellEditor.createTextEditor(font, true));
        JTableHeader tableHeader = table.getTableHeader();

        Color headerBackground = null;
        if (mHeaderBackground != null)
        {
            headerBackground = (Color) mHeaderBackground.getValueNoParams(gui4jComponentInstance.getGui4jCallBase(),
                    null);
        }

        if (tableHeader != null)
        {
            tableHeader.setReorderingAllowed(mReorderingAllowed);
            tableHeader.setFont(font);
        }
        {
            JLabel l = new JLabel("X");
            l.setFont(font);
            table.setRowHeight(l.getPreferredSize().height + 4);
        }
        if (tableHeader != null)
        {
            // mLogger.debug("Setting font to "+font);
            TableCellRenderer renderer = tableHeader.getDefaultRenderer();
            Component c = renderer.getTableCellRendererComponent(table, "", false, false, 0, 0);
            if (c instanceof JLabel)
            {
                ((JLabel) c).setUI(MultiLineLabelUI.getInstance());
            }

            if (mHeaderBackground != null)
            {
                if (headerBackground != null)
                {
                    tableHeader.setBackground(headerBackground);
                }
            }
        }

        int sumWidth = 0;
        double sumWeight = 0.0;
        // double headerHeight = tableHeader.getPreferredSize().getHeight();
        for (int i = 0; i < mColumn.length; i++)
        {
            TableColumn tableColumn = table.getColumnModel().getColumn(i);
            sumWidth += tableColumn.getPreferredWidth();
            Gui4jColumnTable gui4jColumnTable = mColumn[i];
            String name;
            if (gui4jColumnTable != null)
            {
                sumWeight += gui4jColumnTable.getWeight();
                name = gui4jColumnTable.getName(gui4jController);
            }
            else
            {
                sumWeight += 1.0;
                name = "";
            }

            // TableCellRenderer renderer = tableHeader.getDefaultRenderer();
            {
                JLabel label = new JLabel(name);
                label.setFont(font);
                label.setUI(MultiLineLabelUI.getInstance());
                /*
                 * double height = label.getPreferredSize().getHeight()+4; if
                 * (height > headerHeight) { headerHeight = height; }
                 */
            }
        }

        for (int i = 0; i < mColumn.length; i++)
        {
            TableColumn tableColumn = table.getColumnModel().getColumn(i);
            Gui4jColumnTable gui4jColumnTable = mColumn[i];
            double weight = 1.0;
            if (gui4jColumnTable != null)
            {
                gui4jColumnTable.setColumnAttributes(tableColumn);
                weight = gui4jColumnTable.getWeight();
            }
            tableColumn.setPreferredWidth((int) (sumWidth * weight / sumWeight));
        }

        if (mVisibleRows != -1)
        {
            Dimension d = new Dimension(table.getPreferredSize());
            d.setSize(d.getWidth(), table.getRowHeight() * mVisibleRows + 3);
            table.setPreferredScrollableViewportSize(d);
        }
        {
            Dimension d = new Dimension(table.getPreferredSize());
            d.setSize(d.getWidth(), table.getRowHeight() * mRows + 3);
            table.setMaximumSize(d);
        }
        
        RowHeaderTable ctable = null;
        if (mUseRowHeaders)
        {
            Gui4jTableModel tableModel = (Gui4jTableModel) table.getModel();
            JLabel label = new JLabel();
            label.setFont(font);
            int preferredWidth = 10;
            for (int i = 0; i < mRows; i++)
            {
                label.setText(tableModel.getRowName(i));
                int width = label.getPreferredSize().width + 8;
                if (width > preferredWidth)
                {
                    preferredWidth = width;
                }
            }
            ctable = (RowHeaderTable) table;
            ctable.setRowHeaderWidth(preferredWidth);
            ctable.setRowHeaderFont(font);
            ctable.setRowHeaderHeight(table.getRowHeight());

            if (headerBackground != null)
            {
                ((RowHeaderTable.RowHeaderRenderer) ctable.getRowHeader().getCellRenderer())
                        .setBackground(headerBackground);
            }

        }
        
        // init excel export adapter
        if(ctable != null)
            new ExcelAdapter(table, ctable.getRowHeader(),getGui4j().createExcelCopyHandler(getId()));
        else
            new ExcelAdapter(table, null, getGui4j().createExcelCopyHandler(getId()));
        // end init

        if (getGui4j().traceMode())
        {
            mLogger.debug("Preferred size of table with id " + getId() + " is " + table.getPreferredSize());
            mLogger.debug("Preferred scrollable viewport size of table with id " + getId() + " is "
                    + table.getPreferredScrollableViewportSize());
        }
    }

    protected Gui4jComponentInstance createComponentInstance(Gui4jSwingContainer gui4jSwingContainer,
            Gui4jCallBase gui4jCallBase, Gui4jQualifiedComponent gui4jComponentInPath)
    {
        Gui4jTableModel tm = new Gui4jTableModel(gui4jSwingContainer, gui4jCallBase);

        registerEvents(gui4jSwingContainer, gui4jCallBase, mRefresh, tm);

        Gui4jJTable table;
        if (mUseRowHeaders)
        {
            table = new RowHeaderTable(tm, tm);
        }
        else
        {
            table = new Gui4jJTable(tm, tm);
        }
        if (!mUseColumnsHeaders)
        {
            table.setTableHeader(null);
            // table.getTableHeader().setVisible(mUseColumnsHeaders);
        }
        table.getSelectionModel().setSelectionMode(mRowSelectionMode);
        table.getColumnModel().getSelectionModel().setSelectionMode(mColSelectionMode);
        table.setRowSelectionAllowed(mRowSelectionAllowed);
        Gui4jComponentInstance gui4jComponentInstance = new Gui4jComponentInstance(gui4jSwingContainer, table,
                gui4jComponentInPath);
        tm.setGui4jComponentInstance(gui4jComponentInstance);
        gui4jComponentInstance.setStorage(Gui4jTable.class, tm);

        TableCellRenderer renderer = new CellRenderer(gui4jComponentInstance, table, tm);
        table.setDefaultRenderer(String.class, renderer);
        table.setDefaultRenderer(Boolean.class, renderer);

        return gui4jComponentInstance;
    }

    protected void setColumn(int col, Gui4jColumnTable gui4jColumnTable)
    {
        mColumn[col] = gui4jColumnTable;
    }

    public Class getColumnValueType(int col)
    {
        Gui4jColumnTable gui4jColumnTable = mColumn[col];
        if (gui4jColumnTable != null)
        {
            return gui4jColumnTable.getColumnClass();
        }
        else
        {
            return null;
        }
    }

    public Class getRowValueType(int row)
    {
        Gui4jCall value = mRowValue[row];
        if (value != null)
        {
            return value.getResultClass();
        }
        return null;
    }

    public void setRow(int row, Gui4jCall rowValue, Gui4jCall rowName, Gui4jTextAttribute gui4jTextAttribute,
            int indentation)
    {
        mRowValue[row] = rowValue;
        mRowName[row] = rowName;
        mRowTextAttribute[row] = gui4jTextAttribute;
        mRowIndentation[row] = StringUtil.copy(' ', indentation);
    }

    public void setRefresh(Gui4jCall[] refresh)
    {
        mRefresh = refresh;
    }

    /**
     * Sets the onSetValue.
     * 
     * @param onSetValue
     *            The onSetValue to set
     */
    public void setOnSetValue(Gui4jCall onSetValue)
    {
        mOnSetValue = onSetValue;
    }

    public boolean isDefined(int row, int col)
    {
        assert row >= 0 && row < mRows;
        assert col >= 0 && col < mCols;
        return mCell[row][col] != null;
    }

    protected void setCell(int row, int col, Gui4jCell cell)
    {
        assert row >= 0 && row < mRows;
        assert col >= 0 && col < mCols;
        assert mCell[row][col] == null;
        mCell[row][col] = cell;
    }

    /**
     * Sets the reorderingAllowed.
     * 
     * @param reorderingAllowed
     *            The reorderingAllowed to set
     */
    public void setReorderingAllowed(boolean reorderingAllowed)
    {
        mReorderingAllowed = reorderingAllowed;
    }

    /**
     * Sets the colSelectionMode.
     * 
     * @param colSelectionMode
     *            The colSelectionMode to set
     */
    public void setColSelectionMode(int colSelectionMode)
    {
        mColSelectionMode = colSelectionMode;
    }

    /**
     * Sets the rowSelectionMode.
     * 
     * @param rowSelectionMode
     *            The rowSelectionMode to set
     */
    public void setRowSelectionMode(int rowSelectionMode)
    {
        mRowSelectionMode = rowSelectionMode;
    }

    /**
     * Sets the rowSelectionAllowed.
     * 
     * @param rowSelectionAllowed
     *            The rowSelectionAllowed to set
     */
    public void setRowSelectionAllowed(boolean rowSelectionAllowed)
    {
        mRowSelectionAllowed = rowSelectionAllowed;
    }

    protected Point getPopupLocation(Gui4jComponentInstance gui4jComponentInstance, MouseEvent mouseEvent,
            Object context)
    {
        if (mouseEvent != null)
        {
            return mouseEvent.getPoint();
        }

        JTable table = (JTable) gui4jComponentInstance.getComponent();
        Rectangle selection = table.getCellRect(table.getSelectedRow(), table.getSelectedColumn(), true);
        return new Point(selection.x + selection.width / 2, selection.y + selection.height / 2);
    }

    /**
     * @see org.gui4j.core.Gui4jComponent#refreshComponent(Gui4jComponentInstance)
     */
    public void refreshComponent(Gui4jComponentInstance gui4jComponentInstance)
    {
        super.refreshComponent(gui4jComponentInstance);
        Gui4jJTable table = (Gui4jJTable) gui4jComponentInstance.getComponent();
        Gui4jTableModel model = (Gui4jTableModel) table.getModel();
        model.eventOccured();
    }

    /**
     * @see org.gui4j.core.Gui4jComponent#dispose(Gui4jComponentInstance)
     */
    public void dispose(Gui4jComponentInstance gui4jComponentInstance)
    {
        super.dispose(gui4jComponentInstance);
        Gui4jJTable jTable = (Gui4jJTable) gui4jComponentInstance.getSwingComponent();
        if (jTable != null)
        {
            Gui4jTableModel model = (Gui4jTableModel) jTable.getModel();
            model.dispose();
        }
    }

    // ***************************************************************************************

    public class Gui4jColumnTable implements Serializable
    {
        private final Gui4jCall mEnabled;
        protected final Gui4jCall mColumnName;
        protected final Gui4jCall mColumnValue;
        private final Gui4jTextAttribute mGui4jTextAttribute;

        private double mWeight = 1.0;

        public Gui4jColumnTable(int col, Gui4jCall columnName, Gui4jCall columnValue,
                Gui4jTextAttribute gui4jTextAttribute, Gui4jCall enabled)
        {
            mColumnName = columnName;
            mColumnValue = columnValue;
            mEnabled = enabled;
            mGui4jTextAttribute = gui4jTextAttribute;
            setColumn(col, this);
        }

        public String getName(Gui4jCallBase gui4jController)
        {
            if (mColumnName != null)
            {
                Map m = null;
                if (mColumnValue != null)
                {
                    Map nullMap = null;
                    m = new Gui4jMap1(PARAM_COLVALUE, mColumnValue.getValue(gui4jController, nullMap, null));
                }
                return (String) mColumnName.getValue(gui4jController, m, null);
            }
            else
            {
                return "";
            }
        }

        public boolean isEnabled(Gui4jCallBase gui4jController)
        {
            Map m = null;
            return mEnabled == null || !Boolean.FALSE.equals(mEnabled.getValue(gui4jController, m, null));
        }

        public Class getColumnClass()
        {
            if (mColumnValue != null)
            {
                return mColumnValue.getResultClass();
            }
            else
            {
                return Object.class;
            }
        }

        public void setColumnAttributes(TableColumn tableColumn)
        {
            // currently empty
        }

        /**
         * Returns the weight.
         * 
         * @return double
         */
        public double getWeight()
        {
            return mWeight;
        }

        /**
         * Sets the weight.
         * 
         * @param weight
         *            The weight to set
         */
        public void setWeight(double weight)
        {
            mWeight = weight;
        }

        /**
         * Returns the gui4jTextAttribute.
         * 
         * @return Gui4jTextAttribute
         */
        public Gui4jTextAttribute getGui4jTextAttribute()
        {
            return mGui4jTextAttribute;
        }

    }

    public interface Enabled
    {
        boolean isEnabled(int row, int column);
    }

    public final class CellRenderer extends DefaultTableCellRenderer
    {
        private final Color mDefaultForeground;
        private final Color mDefaultBackground;
        private final Font mDefaultFont;

        private final Color[][] mForeground;
        private final Color[][] mBackground;
        private final Font[][] mFont;
        private final int[][] mAlignment;
        private final TableCellRenderer booleanCellRenderer;
        private Enabled mEnabledInstance;

        public CellRenderer(Gui4jComponentInstance gui4jComponentInstance, Gui4jJTable table, Enabled enabledInstance)
        {
            mEnabledInstance = enabledInstance;
            Gui4jCallBase gui4jController = gui4jComponentInstance.getGui4jCallBase();
            mDefaultBackground = getBackground();
            mDefaultForeground = getForeground();
            mDefaultFont = getFont();

            mForeground = new Color[mRows][mCols];
            mBackground = new Color[mRows][mCols];
            mAlignment = new int[mRows][mCols];
            mFont = new Font[mRows][mCols];

            booleanCellRenderer = new BooleanTableCellRenderer(noFocusBorder);

            Gui4jTextAttribute[] columnTextAttribute = new Gui4jTextAttribute[mCols];
            for (int col = 0; col < mCols; col++)
            {
                Gui4jColumnTable tableColumn = mColumn[col];
                if (tableColumn != null)
                {
                    columnTextAttribute[col] = tableColumn.getGui4jTextAttribute();
                }
            }

            for (int row = 0; row < mRows; row++)
            {
                for (int col = 0; col < mCols; col++)
                {
                    Gui4jTextAttribute cellTextAttribute = null;
                    Gui4jCell cell = mCell[row][col];
                    if (cell != null)
                    {
                        cellTextAttribute = cell.getGui4jTextAttribute();
                    }
                    mForeground[row][col] = Gui4jTextAttribute.getForeground(gui4jController, cellTextAttribute,
                            columnTextAttribute[col], mRowTextAttribute[row]);
                    mBackground[row][col] = Gui4jTextAttribute.getBackground(gui4jController, cellTextAttribute,
                            columnTextAttribute[col], mRowTextAttribute[row]);
                    mFont[row][col] = Gui4jTextAttribute.getFont(gui4jController, cellTextAttribute,
                            columnTextAttribute[col], mRowTextAttribute[row]);
                    mAlignment[row][col] = Gui4jTextAttribute.getAlignment(cellTextAttribute, columnTextAttribute[col],
                            mRowTextAttribute[row]);

                }
            }
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column)
        {
            setForeground(mDefaultForeground);
            setBackground(mDefaultBackground);
            setFont(mDefaultFont);
            setEnabled(mEnabledInstance.isEnabled(row, column));

            JComponent c;
            if ((value != null && value.getClass() == Boolean.class)
                    || (value == null && table.getColumnClass(column).equals(Boolean.class)))
            {
                c = (JComponent) booleanCellRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus,
                        row, column);
            }
            else
            {
                c = (JComponent) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }

            if (!isSelected)
            {
                Color foreground = mForeground[row][column];
                if (foreground != null)
                {
                    c.setForeground(foreground);
                }
                Color background = mBackground[row][column];
                if (background != null)
                {
                    c.setBackground(background);
                }
            }
            Font font = mFont[row][column];
            if (font != null)
            {
                c.setFont(font);
            }
            if (c instanceof JLabel)
            {
                JLabel l = (JLabel) c;
                l.setHorizontalAlignment(mAlignment[row][column]);
            }
            return c;
        }
    }

    public final class Gui4jCell implements Serializable
    {
        private final Gui4jCall mGetValue;
        private final Gui4jCall mSetValue;
        private final Gui4jCall mEnabled;
        private final int mCol;
        private final int mRow;
        private final Gui4jTextAttribute mGui4jTextAttribute;
        private final Gui4jCall mList;
        private final Gui4jCall mListItem;
        private final Gui4jCall mListNullItem;
        private final Gui4jCall mListEditable;
        private final Gui4jCall mStringConvert;

        public Gui4jCell(Gui4jCall getValue, Gui4jCall setValue, Gui4jCall enabled, Gui4jCall list, Gui4jCall listItem,
                Gui4jCall listNullItem, Gui4jCall listEditable, Gui4jCall stringConvert, int row, int col,
                Gui4jTextAttribute gui4jTextAttribute)
        {
            mGetValue = getValue;
            mSetValue = setValue;
            mEnabled = enabled;
            mCol = col;
            mRow = row;
            mGui4jTextAttribute = gui4jTextAttribute;
            mList = list;
            mListItem = listItem;
            mListNullItem = listNullItem;
            mListEditable = listEditable;
            mStringConvert = stringConvert;
            setCell(row, col, this);
        }

        protected void prepareEditor(Gui4jCallBase gui4jController, TableCellEditor editor)
        {
            if (mList != null)
            {
                ComboBoxCellEdit edit = (ComboBoxCellEdit) editor;
                Map m = getParamMap(gui4jController, true);
                edit.setParamMap(m);
                String nullItemText = null;
                if (mListNullItem != null)
                {
                    nullItemText = (String) mListNullItem.getValueNoParams(gui4jController, "(undefined)");
                    if (nullItemText == null || nullItemText.length() == 0)
                    {
                        nullItemText = " "; // combobox can't deal correctly
                                            // with null elements and empty
                        // strings
                    }
                }
                edit.setContent((Collection) m.get(PARAM_LIST), nullItemText);
                edit.setSelectedItem(m.get(PARAM_LIST_ITEM));
            }
        }

        protected TableCellEditor getCellEditor(Gui4jCallBase gui4jController, Font font, TableCellEditor editor)
        {
            if (mList != null)
            {
                if (mGui4jTextAttribute != null && mGui4jTextAttribute.getFont() != null)
                {
                    font = (Font) mGui4jTextAttribute.getFont().getValueNoParams(gui4jController, font);
                }
                ComboBoxCellEdit edit = ComboBoxCellEdit.getInstance(gui4jController, mGetValue, mStringConvert,
                        PARAM_LIST_ITEM, font);
                edit.setParamMap(getParamMap(gui4jController, true));
                if (mListEditable != null)
                {
                    Boolean editable = (Boolean) mListEditable.getValueNoParams(gui4jController, null);
                    edit.setEditable(Boolean.TRUE.equals(editable));
                }

                return edit;
            }
            else
            {
                return editor;
            }
        }

        boolean hasSetter(Gui4jCallBase gui4jController)
        {
            return mSetValue != null && isEnabled(gui4jController);
        }

        private Map getParamMap(Gui4jCallBase gui4jController, boolean includeListItem)
        {
            Map m = new HashMap();
            m.put(PARAM_COL, new Integer(mCol));
            m.put(PARAM_ROW, new Integer(mRow));

            Gui4jCall columnValue = null;
            if (mColumn[mCol] != null)
            {
                columnValue = mColumn[mCol].mColumnValue;
            }
            Gui4jCall rowValue = mRowValue[mRow];
            if (columnValue != null)
            {
                Map nullMap = null;
                m.put(PARAM_COLVALUE, columnValue.getValue(gui4jController, nullMap, null));
            }
            if (rowValue != null)
            {
                Map nullMap = null;
                m.put(PARAM_ROWVALUE, rowValue.getValue(gui4jController, nullMap, null));
            }

            if (mList != null)
            {
                Collection collection = (Collection) mList.getValue(gui4jController, m, null);
                m.put(PARAM_LIST, collection);
                if (includeListItem && mListItem != null)
                {
                    Object listItem = mListItem.getValue(gui4jController, m, null);
                    m.put(PARAM_LIST_ITEM, listItem);
                }
            }

            return m;
        }

        boolean isEnabled(Gui4jCallBase gui4jController)
        {
            boolean columnEnabled = mColumn[mCol] == null || mColumn[mCol].isEnabled(gui4jController);
            if (columnEnabled)
            {
                if (mEnabled != null)
                {
                    Map m = getParamMap(gui4jController, true);

                    Boolean enabled = (Boolean) mEnabled.getValue(gui4jController, m, Boolean.TRUE);
                    if (enabled != null)
                    {
                        return enabled.booleanValue();
                    }
                    else
                    {
                        return false;
                    }
                }
                else
                {
                    return true;
                }
            }
            else
            {
                return false;
            }
        }

        Object getValue(Gui4jCallBase gui4jController)
        {
            if (mGetValue != null)
            {
                Map m = getParamMap(gui4jController, true);
                return mGetValue.getValue(gui4jController, m, null);
            }
            else
            {
                return null;
            }
        }

        void setValue(Gui4jCallBase gui4jController, Object value, Gui4jTableModel model)
        {
            if (mSetValue != null)
            {
                Map paramMap = getParamMap(gui4jController, false);

                if (value instanceof ComboBoxNullItem)
                {
                    value = null;
                }

                paramMap.put(PARAM_VALUE, value);

                Gui4jGetValue[] work;
                if (mOnSetValue != null)
                {
                    work = new Gui4jGetValue[] { mSetValue, new Gui4jRefreshTable(model, mRow, mCol),
                            model.getAutomaticRefresh(), mOnSetValue };
                }
                else
                {
                    work = new Gui4jGetValue[] { mSetValue, new Gui4jRefreshTable(model, mRow, mCol),
                            model.getAutomaticRefresh() };
                }
                mGui4jThreadManager.performWork(gui4jController, work, paramMap);
            }
        }

        Class getCellClass()
        {
            if (mGetValue != null)
            {
                return mGetValue.getResultClass();
            }
            else
            {
                return Object.class;
            }
        }

        /**
         * Returns the gui4jTextAttribute.
         * 
         * @return Gui4jTextAttribute
         */
        public Gui4jTextAttribute getGui4jTextAttribute()
        {
            return mGui4jTextAttribute;
        }

    }

    public class Gui4jTableModel extends RowHeaderAbstractTableModel implements Gui4jEventListener, Enabled,
            Gui4jTableListener
    {

        private Gui4jSwingContainer mGui4jSwingContainer;
        private Gui4jCallBase mGui4jController;
        private Gui4jComponentInstance mGui4jComponentInstance;

        public Gui4jTableModel(Gui4jSwingContainer gui4jSwingContainer, Gui4jCallBase gui4jCallBase)
        {
            mGui4jSwingContainer = gui4jSwingContainer;
            mGui4jController = gui4jCallBase;
        }

        public void eventOccured()
        {
            Runnable run = new Runnable() {
                public void run()
                {
                    fireTableRowsUpdated(0, getRowCount() - 1);
                }
            };
            Gui4jThreadManager.executeInSwingThreadAndWait(run);
        }

        public Gui4jGetValue getAutomaticRefresh()
        {
            Gui4jJTable table = (Gui4jJTable) mGui4jComponentInstance.getSwingComponent();
            if (mAutomaticRefresh && table != null)
            {
                return table.getAutomaticRefreshAction();
            }
            else
            {
                return null;
            }
        }

        public int getColumnCount()
        {
            return mCols;
        }

        public int getRowCount()
        {
            return mRows;
        }

        public boolean isEnabled(int row, int column)
        {
            if (row >= 0 && column >= 0 && row < mRows && column < mCols)
            {
                Gui4jCell cell = mCell[row][column];
                if (cell != null)
                {
                    return cell.isEnabled(mGui4jController);
                }
                else
                {
                    return true;
                }
            }
            else
            {
                return true;
            }
        }

        public String getRowName(int row)
        {
            if (mRowName[row] != null)
            {
                Map paramMap = new HashMap();
                paramMap.put(PARAM_ROW, new Integer(row));
                if (mRowValue[row] != null)
                {
                    Map nullMap = null;
                    paramMap.put(PARAM_ROWVALUE, mRowValue[row].getValue(mGui4jController, nullMap, null));
                }
                String name = (String) mRowName[row].getValue(mGui4jController, paramMap, null);
                if (name == null || name.equals(""))
                {
                    return mRowIndentation[row];
                }
                return mRowIndentation[row] + name;
            }
            else
            {
                mLogger.info("Zeilenname für Zeile " + row + " ist nicht definiert");
                return "";
            }
        }

        public void setRowName(int row, String name)
        {
        }

        public void setRowName(int row, Object name)
        {
        }

        public boolean isCellEditable(int row, int col)
        {
            if (handleReadOnly() && mGui4jSwingContainer.inReadOnlyMode())
            {
                return false;
            }
            else
            {

                Gui4jCell cell = mCell[row][col];
                if (cell != null)
                {
                    return cell.hasSetter(mGui4jController);
                }
                else
                {
                    return false;
                }
            }
        }

        public Object getValueAt(int row, int col)
        {
            Gui4jCell cell = mCell[row][col];
            if (cell != null)
            {
                return cell.getValue(mGui4jController);
            }
            else
            {
                return null;
            }
        }

        public void setValueAt(Object value, int row, int col)
        {
            Gui4jCell cell = mCell[row][col];
            if (cell != null)
            {
                cell.setValue(mGui4jController, value, this);
            }
        }

        public Class getColumnClass(int columnIndex)
        {
            Class commonClass = null;
            boolean different = false;
            for (int r = 0; r < mRows; r++)
            {
                Gui4jCell cell = mCell[r][columnIndex];
                if (cell != null)
                {
                    Class c = cell.getCellClass();
                    if (commonClass == null)
                    {
                        commonClass = c;
                    }
                    else
                    {
                        different |= commonClass != c;
                    }
                }
            }
            if (commonClass != null && !different)
            {
                return commonClass;
            }
            else
            {
                return Object.class;
            }
        }

        public String getColumnName(int columnIndex)
        {
            if (mColumn[columnIndex] != null)
            {
                String name = mColumn[columnIndex].getName(mGui4jController);
                if (name == null || name.equals(""))
                {
                    return " ";
                }
                return name;
            }
            else
            {
                mLogger.info(getId() + ": Spaltenname für Spalte " + columnIndex
                        + " ist nicht definiert in XML Datei: " + getConfigurationName());
                return " ";
            }
        }

        public void setGui4jComponentInstance(Gui4jComponentInstance gui4jComponentInstance)
        {
            mGui4jComponentInstance = gui4jComponentInstance;
        }

        /**
         * @see org.gui4j.core.swing.Gui4jTableListener#prepareEditor(TableCellEditor,
         *      int, int)
         */
        public void prepareEditor(TableCellEditor editor, int row, int column)
        {
            Gui4jCell cell = mCell[row][column];
            if (cell != null)
            {
                cell.prepareEditor(mGui4jController, editor);
            }
        }

        /**
         * @see org.gui4j.core.swing.Gui4jTableListener#getCellEditor(TableCellEditor,
         *      int, int)
         */
        public TableCellEditor getCellEditor(TableCellEditor editor, int row, int column)
        {
            Gui4jCell cell = mCell[row][column];
            return cell == null ? editor : cell.getCellEditor(mGui4jController, mGui4jComponentInstance
                    .getSwingComponent().getFont(), editor);
        }

        /**
         * @see org.gui4j.core.swing.Gui4jTableListener#getCellRenderer(TableCellRenderer,
         *      int, int)
         */
        public TableCellRenderer getCellRenderer(TableCellRenderer renderer, int row, int column)
        {
            return renderer;
        }

        public void dispose()
        {
            mGui4jSwingContainer = null;
            mGui4jController = null;
            mGui4jComponentInstance = null;
        }

    }

}