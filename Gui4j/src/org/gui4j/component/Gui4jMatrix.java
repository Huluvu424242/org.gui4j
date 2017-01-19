package org.gui4j.component;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
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
import org.gui4j.core.Gui4jQualifiedComponent;
import org.gui4j.core.Gui4jSwingContainer;
import org.gui4j.core.Gui4jTextAttribute;
import org.gui4j.core.Gui4jThreadManager;
import org.gui4j.core.Gui4jTypeManager;
import org.gui4j.core.listener.Gui4jMouseListenerMatrix;
import org.gui4j.core.swing.BooleanTableCellRenderer;
import org.gui4j.core.swing.ComboBoxCellEdit;
import org.gui4j.core.swing.ComboBoxHorizontalScroll;
import org.gui4j.core.swing.Gui4jCellEditor;
import org.gui4j.core.swing.Gui4jJTable;
import org.gui4j.core.swing.Gui4jJTableHeader;
import org.gui4j.core.swing.Gui4jRefreshTable;
import org.gui4j.core.swing.Gui4jTableListener;
import org.gui4j.core.swing.MultiLineLabelUI;
import org.gui4j.core.swing.RowHeaderAbstractTableModel;
import org.gui4j.core.swing.RowHeaderTable;
import org.gui4j.core.util.ComboBoxNullItem;
import org.gui4j.core.util.SparseMatrix;
import org.gui4j.event.Gui4jEventListener;
import org.gui4j.util.Pair;

/**
 * Table supporting dynamic count of columns and rows
 */
public class Gui4jMatrix extends Gui4jJComponent
{
    protected static final Log mLogger = LogFactory.getLog(Gui4jMatrix.class);

    public static final String PARAM_COL = "col";
    public static final String PARAM_ROW = "row";
    public static final String PARAM_VALUE = "value";
    public static final String PARAM_COLVALUE = "colValue";
    public static final String PARAM_ROWVALUE = "rowValue";
    public static final String PARAM_LIST = "list";
    public static final String PARAM_LIST_ITEM = "listItem";

    private static final String SELECTION_LISTENER_ID = "selectionListener";
    
    protected final Gui4jTypeManager mColManager;
    protected final Gui4jTypeManager mRowManager;
    protected final SparseMatrix mCellManager;
    protected final boolean mUseCacheDflt;

    protected final int mVisibleRows;
    protected final int mHeaderLines;
    protected final boolean mUseRowHeaders;
    protected final boolean mUseColumnHeaders;
    protected final boolean mAutomaticRefresh;
    protected final Gui4jThreadManager mGui4jThreadManager;

    protected int mRowSelectionMode = ListSelectionModel.SINGLE_SELECTION;
    protected int mColSelectionMode = ListSelectionModel.SINGLE_SELECTION;
    protected boolean mReorderingAllowed = true;
    protected boolean mRowSelectionAllowed = true;

    protected Gui4jCall[] mRefresh;
    protected Gui4jCall mOnSetValue;
    private Gui4jCall mHeaderBackground;

    protected final int mResizeMode;

    /**
     * Constructor Gui4jMatrix.
     * 
     * @param gui4jComponentContainer
     * @param id
     * @param visibleRows
     * @param headerLines
     * @param useCache
     * @param useRowHeaders
     * @param useColumnHeaders
     * @param automaticRefresh
     * @param resizeMode
     */
    public Gui4jMatrix(Gui4jComponentContainer gui4jComponentContainer, String id, int visibleRows, int headerLines,
            boolean useCache, boolean useRowHeaders, boolean useColumnHeaders, boolean automaticRefresh, int resizeMode)
    {
        super(gui4jComponentContainer, useRowHeaders ? RowHeaderTable.class : Gui4jJTable.class, id);
        mUseCacheDflt = useCache;
        mUseRowHeaders = useRowHeaders;
        mUseColumnHeaders = useColumnHeaders;
        mAutomaticRefresh = automaticRefresh;
        mGui4jThreadManager = getGui4j().getGui4jThreadManager();
        mVisibleRows = visibleRows;
        mHeaderLines = headerLines;
        mResizeMode = resizeMode;

        mColManager = new Gui4jTypeManager();
        mRowManager = new Gui4jTypeManager();
        mCellManager = new SparseMatrix();

    }

    public void addGui4jCol(Class type, Gui4jCol gui4jCol)
    {
        mColManager.add(type, gui4jCol);
    }

    public void addGui4jRow(Class type, Gui4jRow gui4jRow)
    {
        mRowManager.add(type, gui4jRow);
    }

    public void addGui4jCell(Gui4jCell gui4jCell)
    {
        // mLogger.debug("setting row="+gui4jCell.getGui4jRow().mClassType+",
        // col="+gui4jCell.getGui4jCol().mClassType+", cell="+gui4jCell.mValue);
        mCellManager.set(gui4jCell.getGui4jRow(), gui4jCell.getGui4jCol(), gui4jCell);
    }

    public void setColumns(Gui4jComponentInstance gui4jComponentInstance, final Collection columns)
    {
        Gui4jJTable jTable = (Gui4jJTable) gui4jComponentInstance.getSwingComponent();
        jTable.endCellEditing();
        final Gui4jTableModel tm = (Gui4jTableModel) jTable.getModel();

        Gui4jThreadManager.executeInSwingThreadAndContinue(new Runnable() {
            public void run()
            {
                tm.setColumns(columns);
            }
        });
    }

    public void setRows(Gui4jComponentInstance gui4jComponentInstance, final Collection rows)
    {
        Gui4jJTable jTable = (Gui4jJTable) gui4jComponentInstance.getSwingComponent();
        jTable.endCellEditing();
        final Gui4jTableModel model = (Gui4jTableModel) jTable.getModel();
        Gui4jThreadManager.executeInSwingThreadAndContinue(new Runnable() {
            public void run()
            {
                model.setRows(rows);
            }
        });

    }

    public void setContent(Gui4jComponentInstance gui4jComponentInstance, Pair content)
    {
        Gui4jJTable jTable = (Gui4jJTable) gui4jComponentInstance.getSwingComponent();
        if (jTable == null) {
            return;
        }
        jTable.endCellEditing();
        final Gui4jTableModel model = (Gui4jTableModel) jTable.getModel();
        final Object first = content.getFirst();
        final Object second = content.getSecond();
        if (!(first instanceof Collection))
        {
            mLogger.error("First part of pair must be instanceof Collection: " + first, new Throwable());
            return;
        }
        if (!(second instanceof Collection))
        {
            mLogger.error("Second part of pair must be instanceof Collection: " + second, new Throwable());
            return;
        }

        Gui4jThreadManager.executeInSwingThreadAndContinue(new Runnable() {
            public void run()
            {
                model.setContent((Collection) first, (Collection) second);
            }
        });

    }

    public void setRefresh(Gui4jCall[] refresh)
    {
        mRefresh = refresh;
    }

    public boolean setCellSelectionPair(Gui4jComponentInstance componentInstance, Pair pair)
    {
        JTable table = (JTable) componentInstance.getSwingComponent();
        if (pair != null)
        {
            Gui4jTableModel model = (Gui4jTableModel) table.getModel();
            Gui4jMouseListenerMatrix selectionListener = (Gui4jMouseListenerMatrix) componentInstance
                    .getStorage(SELECTION_LISTENER_ID);

            int row = model.getRowIndex(pair.getFirst());
            int col = model.getColumnIndex(pair.getSecond());

            //System.out.println("Gui4jMatrix: setCellSelectionPair. row: " + row + " col: " + col);
            
            if (row >= 0 && row < table.getRowCount() && col >= 0 && col < table.getColumnCount())
            {
                if (table.getCellEditor() != null && table.isEditing())
                {
                    table.getCellEditor().stopCellEditing();
                }
                if (selectionListener != null)
                {
                    selectionListener.setValueChangeActive(false);
                }
                
                table.setRowSelectionInterval(row, row);
                table.setColumnSelectionInterval(col, col);
                
                if (selectionListener != null)
                {
                    selectionListener.setValueChangeActive(true);
                    selectionListener.valueChanged(null); 
                    // fire only one event for the combined row and column change
                }
                Rectangle rect = table.getCellRect(row, col, true);
                table.scrollRectToVisible(rect);
                
                if(table.hasFocus() && !table.isEditing()) {
                    checkAndExpandComboBox(model);
                }
                
                return true;
            }
        }
        return false;
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

    /**
     * Sets the headerBackground.
     * 
     * @param headerBackground
     */
    public void setHeaderBackground(Gui4jCall headerBackground)
    {
        mHeaderBackground = headerBackground;
    }

    /**
     * @see org.gui4j.core.Gui4jAbstractComponent#setProperties(Gui4jComponentInstance)
     */
    protected void setProperties(Gui4jComponentInstance gui4jComponentInstance)
    {
        super.setProperties(gui4jComponentInstance);
        Gui4jJTable table = (Gui4jJTable) gui4jComponentInstance.getSwingComponent();
        table.setAutoResizeMode(mResizeMode);
        Font font = table.getFont();
        table.setDefaultEditor(Object.class, Gui4jCellEditor.createTextEditor(font, true));
        
        Color headerBackground = null;
        if (mHeaderBackground != null)
        {
            headerBackground = (Color) mHeaderBackground.getValueNoParams(gui4jComponentInstance.getGui4jCallBase(),
                    null);
        }

        if (mUseColumnHeaders)
        {
            JTableHeader tableHeader = new Gui4jJTableHeader(table.getColumnModel(), font, mHeaderLines);
            if (headerBackground != null)
            {
                tableHeader.setBackground(headerBackground);
            }
            table.setTableHeader(tableHeader);
        }

        JTableHeader tableHeader = table.getTableHeader();
        if (tableHeader != null)
        {
            tableHeader.setReorderingAllowed(mReorderingAllowed);
            tableHeader.setFont(font);
            TableCellRenderer renderer = tableHeader.getDefaultRenderer();
            Component c = renderer.getTableCellRendererComponent(table, "", false, false, 0, 0);
            if (mHeaderLines == 1 && c instanceof JLabel)
            {
                ((JLabel) c).setUI(MultiLineLabelUI.getInstance());
            }
        }

        {
            JLabel l = new JLabel("X");
            l.setFont(font);
            table.setRowHeight(l.getPreferredSize().height + 4);
        }

        if (mVisibleRows != -1)
        {
            Dimension d = new Dimension(table.getPreferredSize());
            d.setSize(d.getWidth(), table.getRowHeight() * mVisibleRows + 3);
            table.setPreferredScrollableViewportSize(d);
        }

        final Gui4jTableModel tableModel = (Gui4jTableModel) table.getModel();
        RowHeaderTable ctable = null;
        if (mUseRowHeaders)
        {
            JLabel label = new JLabel();
            label.setFont(font);
            /*RowHeaderTable*/ ctable = (RowHeaderTable) table;
            ctable.setRowHeaderFont(font);
            ctable.setRowHeaderHeight(table.getRowHeight());
            tableModel.refreshRows();
            if (headerBackground != null)
            {
                ((RowHeaderTable.RowHeaderRenderer) ctable.getRowHeader().getCellRenderer())
                        .setBackground(headerBackground);
            }
        }
        tableModel.setInitialized();
        tableModel.refreshColumns(tableModel);
        
        // init excel export adapter
        if(ctable != null)
            new ExcelAdapter(table, ctable.getRowHeader(), getGui4j().createExcelCopyHandler(getId()));
        else
            new ExcelAdapter(table, null, getGui4j().createExcelCopyHandler(getId()));
        // end init
        
        final Flag hasOnCellSelect = new Flag();
        final Flag hasOnCellClick = new Flag();
        final Flag hasOnCellDblClick = new Flag();
        mCellManager.traverse(new SparseMatrix.Traverser() {
            public void work(Object row, Object col, Object value)
            {
                Gui4jCell gui4jCell = (Gui4jCell) value;
                hasOnCellSelect.val |= gui4jCell.mOnCellSelect != null;
                hasOnCellClick.val |= gui4jCell.mOnCellClick != null;
                hasOnCellDblClick.val |= gui4jCell.mOnCellDblClick != null;
            }
        });

        if (hasOnCellSelect.val)
        {
            Gui4jMouseListenerMatrix.CellListener cellListener = new Gui4jMouseListenerMatrix.CellListener() {
                public void handle(int[] rows, int[] cols, Gui4jCallBase gui4jController,
                        Gui4jThreadManager gui4jThreadManager)
                {
                    if (rows.length == 1 && cols.length == 1)
                    {
                        tableModel.handleOnCellSelect(rows[0], cols[0], gui4jController, gui4jThreadManager);
                    }
                }
            };
            Gui4jMouseListenerMatrix selectionListener = new Gui4jMouseListenerMatrix(gui4jComponentInstance, 1,
                    cellListener);
            table.getSelectionModel().addListSelectionListener(selectionListener);
            table.getColumnModel().getSelectionModel().addListSelectionListener(selectionListener);
            gui4jComponentInstance.setStorage(SELECTION_LISTENER_ID, selectionListener);
        }

        if (hasOnCellClick.val)
        {
            Gui4jMouseListenerMatrix.CellListener cellListener = new Gui4jMouseListenerMatrix.CellListener() {
                public void handle(int[] rows, int[] cols, Gui4jCallBase gui4jController,
                        Gui4jThreadManager gui4jThreadManager)
                {
                    if (rows.length == 1 && cols.length == 1)
                    {
                        tableModel.handleOnCellClick(rows[0], cols[0], gui4jController, gui4jThreadManager);
                    }
                }
            };
            Gui4jMouseListenerMatrix mouseListener = new Gui4jMouseListenerMatrix(gui4jComponentInstance, 1,
                    cellListener);
            table.addMouseListener(mouseListener);
        }

        if (hasOnCellDblClick.val)
        {
            Gui4jMouseListenerMatrix.CellListener cellListener = new Gui4jMouseListenerMatrix.CellListener() {
                public void handle(int[] rows, int[] cols, Gui4jCallBase gui4jController,
                        Gui4jThreadManager gui4jThreadManager)
                {
                    if (rows.length == 1 && cols.length == 1)
                    {
                        tableModel.handleOnCellDblClick(rows[0], cols[0], gui4jController, gui4jThreadManager);
                    }
                }
            };
            Gui4jMouseListenerMatrix mouseListener = new Gui4jMouseListenerMatrix(gui4jComponentInstance, 2,
                    cellListener);
            table.addMouseListener(mouseListener);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.gui4j.core.Gui4jAbstractComponent#createComponentInstance(org.gui4j.core.Gui4jSwingContainer,
     *      org.gui4j.Gui4jCallBase, org.gui4j.core.Gui4jQualifiedComponent)
     */
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
        if (!mUseColumnHeaders)
        {
            table.setTableHeader(null);
        }
        table.getSelectionModel().setSelectionMode(mRowSelectionMode);
        table.getColumnModel().getSelectionModel().setSelectionMode(mColSelectionMode);
        table.setRowSelectionAllowed(mRowSelectionAllowed);

        Gui4jComponentInstance gui4jComponentInstance = new Gui4jComponentInstance(gui4jSwingContainer, table,
                gui4jComponentInPath);
        tm.setGui4jComponentInstance(gui4jComponentInstance);

        return gui4jComponentInstance;
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

    protected Object getPopupContext(Gui4jComponentInstance gui4jComponentInstance, MouseEvent mouseEvent)
    {
        JTable table = (JTable) gui4jComponentInstance.getComponent();
        Gui4jTableModel model = (Gui4jTableModel) table.getModel();
        int row = table.getSelectedRow();
        int col = table.getSelectedColumn();
        mLogger.debug("Computing popup context for row " + row + ", col " + col);
        Gui4jCell cell = model.getGui4jCell(row, col);
        if (cell != null && cell.mPopupContext != null)
        {
            Map paramMap = cell.getParamMap(gui4jComponentInstance.getGui4jCallBase(), row, col, true, model);
            return cell.mPopupContext.getValue(gui4jComponentInstance.getGui4jCallBase(), paramMap, null);
        }
        return null;
    }

    /**
     * @see org.gui4j.core.Gui4jComponent#dispose(Gui4jComponentInstance)
     */
    public void dispose(Gui4jComponentInstance gui4jComponentInstance)
    {
        super.dispose(gui4jComponentInstance);
        Gui4jJTable jTable = (Gui4jJTable) gui4jComponentInstance.getSwingComponent();
        Gui4jTableModel model = (Gui4jTableModel) jTable.getModel();
        model.dispose();
    }

    // *************************************************************************************
    // ROW
    // *************************************************************************************
    public final class Gui4jRow implements Serializable
    {
        private final Gui4jCall mRowName;
        private final Gui4jTextAttribute mGui4jTextAttribute;
        protected final Class mClassType;

        public Gui4jRow(Class classType, Gui4jCall rowName, Gui4jTextAttribute gui4jTextAttribute)
        {
            mRowName = rowName;
            mGui4jTextAttribute = gui4jTextAttribute;
            mClassType = classType;
        }

        public Gui4jRow copy(Class newClassType)
        {
            return new Gui4jRow(newClassType, mRowName, mGui4jTextAttribute);
        }

        public Gui4jTextAttribute getGui4jTextAttribute()
        {
            return mGui4jTextAttribute;
        }

        protected String getName(Gui4jCallBase gui4jController, int row, Gui4jTableModel model)
        {
            if (mRowName != null)
            {
                Map paramMap = new HashMap();
                paramMap.put(PARAM_ROW, new Integer(row));
                paramMap.put(PARAM_ROWVALUE, model.mRows.get(row));
                return (String) mRowName.getValue(gui4jController, paramMap, null);
            }
            else
            {
                return null;
            }
        }
    }

    // *************************************************************************************
    // COL
    // *************************************************************************************

    public final class Gui4jCol implements Serializable
    {
        protected final Class mClassType;
        private final Gui4jCall mColName;
        private final Gui4jCall mCharacters;
        private final Gui4jCall mMaxCharacters;
        private final Gui4jTextAttribute mGui4jTextAttribute;
        private double mWeight = 1.0;

        public Gui4jCol(Class classType, Gui4jCall colName, Gui4jTextAttribute gui4jTextAttribute,
                Gui4jCall characters, Gui4jCall maxCharacters)
        {
            mColName = colName;
            mGui4jTextAttribute = gui4jTextAttribute;
            mCharacters = characters;
            mMaxCharacters = maxCharacters;
            mClassType = classType;
        }

        public Gui4jCol copy(Class newClassType)
        {
            Gui4jCol c = new Gui4jCol(newClassType, mColName, mGui4jTextAttribute, mCharacters, mMaxCharacters);
            c.setWeight(mWeight);
            return c;
        }

        public void setWeight(double weight)
        {
            mWeight = weight;
        }

        public double getWeight()
        {
            return mWeight;
        }

        public Gui4jTextAttribute getGui4jTextAttribute()
        {
            return mGui4jTextAttribute;
        }

        public String getName(Gui4jCallBase gui4jController, int col, Gui4jTableModel model)
        {
            if (mColName != null)
            {
                Map paramMap = new HashMap();
                paramMap.put(PARAM_COL, new Integer(col));
                paramMap.put(PARAM_COLVALUE, model.mColumns.get(col));
                return (String) mColName.getValue(gui4jController, paramMap, null);
            }
            else
            {
                return null;
            }
        }

        public boolean charactersDefined()
        {
            return mCharacters != null;
        }

        public boolean maxCharactersDefined()
        {
            return mMaxCharacters != null;
        }

        public int getCharacters(Gui4jCallBase gui4jController)
        {
            assert mCharacters != null;
            Integer i = (Integer) mCharacters.getValueNoParams(gui4jController, null);
            return i.intValue();
        }

        public int getMaxCharacters(Gui4jCallBase gui4jController)
        {
            assert mMaxCharacters != null;
            Integer i = (Integer) mMaxCharacters.getValueNoParams(gui4jController, null);
            return i.intValue();
        }
    }

    // *************************************************************************************
    // CELL
    // *************************************************************************************
    public final class Gui4jCell implements Serializable
    {
        private final Gui4jRow mGui4jRow;
        private final Gui4jCol mGui4jCol;
        private final Gui4jTextAttribute mGui4jTextAttribute;

        protected Gui4jCall mValue;
        private final Gui4jCall mSetValue;
        private final Gui4jCall mEnabled;
        private final Gui4jCall mList;
        private final Gui4jCall mListItem;
        private final Gui4jCall mListNullItem;
        private final Gui4jCall mListEditable;
        private final Gui4jCall mStringConvert;
        protected final Gui4jCall mOnCellSelect;
        protected final Gui4jCall mOnCellClick;
        protected final Gui4jCall mOnCellDblClick;
        protected final Gui4jCall mTooltip;
        protected final Gui4jCall mNotifyTempValue;
        protected final Gui4jCall mPopupContext;
        
        /*
         * mMaximumRowCount is the maximum number of items a combobox associated with this cell 
         * can display without a scrollbar. It's default value in Swing is 8.
         */
        protected int mMaximumRowCount = 8;
        
        // attribute used to define whether a combobox expands automatically on cell selection or not. (L.B.)
        protected boolean mAutoExtend;

        public Gui4jCell(Class rowType, Class columnType, Gui4jTextAttribute gui4jTextAttribute, Gui4jCall value,
                Gui4jCall setValue, Gui4jCall enabled, Gui4jCall list, Gui4jCall listItem, Gui4jCall listNullItem,
                Gui4jCall listEditable, Gui4jCall stringConvert, Gui4jCall onCellSelect, Gui4jCall onCellClick,
                Gui4jCall onCellDblClick, Gui4jCall tooltip, Gui4jCall notifyTempValue, Gui4jCall popupContext)
        {
            assert rowType != null;
            assert columnType != null;
            {
                Gui4jRow row = (Gui4jRow) mRowManager.get(rowType);
                /*
                 * if (row != null) { mLogger.debug("RowType =
                 * "+rowType.getName()+" gui4jRow="+row.mClassType.getName()); }
                 */
                if (row == null)
                {
                    mLogger.info(getId() + ": row for type " + rowType.getName() + " not defined");
                    mGui4jRow = new Gui4jRow(rowType, null, null);
                    mRowManager.add(rowType, mGui4jRow);
                }
                else
                {
                    //
                    if (row.mClassType != rowType)
                    {
                        mLogger.debug("Creating new Gui4jRow instance for type " + rowType.getName() + " as copy from "
                                + row.mClassType.getName());
                        row = row.copy(rowType);
                        mRowManager.add(rowType, row);
                    }
                    // */
                    mGui4jRow = row;
                }
            }
            {
                Gui4jCol col = (Gui4jCol) mColManager.get(columnType);
                if (col == null)
                {
                    mLogger.info(getId() + ": column for type " + columnType.getName() + " not defined");
                    mGui4jCol = new Gui4jCol(columnType, null, null, null, null);
                    mColManager.add(columnType, mGui4jCol);
                }
                else
                {
                    //
                    if (col.mClassType != columnType)
                    {
                        mLogger.debug("Creating new Gui4jCol instance for type " + columnType.getName()
                                + " as copy from " + col.mClassType.getName());
                        col = col.copy(columnType);
                        mColManager.add(columnType, col);
                    }
                    // */
                    mGui4jCol = col;
                }
            }
            mGui4jTextAttribute = gui4jTextAttribute;
            mValue = value;
            mSetValue = setValue;
            mEnabled = enabled;
            mList = list;
            mListItem = listItem;
            mListNullItem = listNullItem;
            mListEditable = listEditable;
            mStringConvert = stringConvert;
            mOnCellSelect = onCellSelect;
            mOnCellClick = onCellClick;
            mOnCellDblClick = onCellDblClick;
            mTooltip = tooltip;
            mNotifyTempValue = notifyTempValue;
            mPopupContext = popupContext;
        }
        
        public void setMaximumRowCount(int maximumRowCount) {
            mMaximumRowCount = maximumRowCount;
        }
        
        public void setAutoExtend(boolean autoExtend) {
            mAutoExtend = autoExtend;
        }

        public String getTooltip(Gui4jCallBase gui4jController, int row, int col, Gui4jTableModel model)
        {
            if (mTooltip != null)
            {
                Map m = getParamMap(gui4jController, row, col, true, model);
                String value = (String) mTooltip.getValue(gui4jController, m, null);
                return value;
            }
            else
            {
                return null;
            }
        }

        public Object getValue(Gui4jCallBase gui4jController, int row, int col, Gui4jTableModel model)
        {
            if (mValue != null)
            {
                Map m = getParamMap(gui4jController, row, col, true, model);
                Object value = mValue.getValue(gui4jController, m, null);
                // mLogger.debug(getId()+": getValue("+row+", "+col+") =
                // "+value);
                return value;
            }
            else
            {
                return null;
            }
        }

        public void invalidateCall()
        {
            mValue = null;
        }

        public void setValue(Gui4jCallBase gui4jController, int row, int col, Object value, Gui4jTableModel model)
        {
            if (mSetValue != null)
            {
                Map paramMap = getParamMap(gui4jController, row, col, false, model);

                if (value instanceof ComboBoxNullItem)
                {
                    value = null;
                }

                paramMap.put(PARAM_VALUE, value);

                Gui4jGetValue[] work;
                if (mOnSetValue != null)
                {
                    work = new Gui4jGetValue[] { mSetValue, new Gui4jRefreshTable(model, row, col),
                            model.getAutomaticRefresh(), mOnSetValue };
                }
                else
                {
                    work = new Gui4jGetValue[] { mSetValue, new Gui4jRefreshTable(model, row, col),
                            model.getAutomaticRefresh() };
                }

                mGui4jThreadManager.performWork(gui4jController, work, paramMap, false);
            }
        }

        public void handleOnCellSelect(int row, int col, Gui4jCallBase gui4jController,
                Gui4jThreadManager gui4jThreadManager, Gui4jTableModel model)
        {
            if (mOnCellSelect != null)
            {
                gui4jThreadManager.performWork(gui4jController, mOnCellSelect, getParamMap(gui4jController, row, col,
                        false, model));
            }
        }
        
        public void handleOnCellClick(int row, int col, Gui4jCallBase gui4jController,
                Gui4jThreadManager gui4jThreadManager, Gui4jTableModel model)
        {
            if (mOnCellClick != null)
            {
                gui4jThreadManager.performWork(gui4jController, mOnCellClick, getParamMap(gui4jController, row, col,
                        false, model));
            }
        }

        public void handleOnCellDblClick(int row, int col, Gui4jCallBase gui4jController,
                Gui4jThreadManager gui4jThreadManager, Gui4jTableModel model)
        {
            if (mOnCellDblClick != null)
            {
                gui4jThreadManager.performWork(gui4jController, mOnCellDblClick, getParamMap(gui4jController, row, col,
                        false, model));
            }
        }

        public boolean isEnabled(Gui4jCallBase gui4jController, int row, int col, Gui4jTableModel model)
        {
            if (mEnabled != null)
            {
                Map m = getParamMap(gui4jController, row, col, true, model);

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

        public boolean isEditable(Gui4jCallBase gui4jController, int row, int col, Gui4jTableModel model)
        {
            return mSetValue != null && isEnabled(gui4jController, row, col, model);
        }

        public void prepareEditor(Gui4jCallBase gui4jController, TableCellEditor editor, int row, int column,
                Gui4jTableModel model)
        {
            if (mList != null)
            {
                ComboBoxCellEdit edit = (ComboBoxCellEdit) editor;
                Map m = getParamMap(gui4jController, row, column, true, model);
                edit.setParamMap(m);
                Collection content = (Collection) m.get(PARAM_LIST);
                String nullItemText = null;
                if (mListNullItem != null)
                {
                    nullItemText = (String) mListNullItem.getValueNoParams(gui4jController, "(undefined)");
                    if (nullItemText == null || nullItemText.length() == 0)
                    {
                        nullItemText = " ";
                        // combobox can't deal correctly with null elements and
                        // empty strings
                    }
                }
                edit.setContent(content, nullItemText);
                
                Object selectedItem = m.get(PARAM_LIST_ITEM);
                edit.setSelectedItem(selectedItem);
            }
            else if (editor instanceof Gui4jCellEditor)
            {
                Gui4jCellEditor edit = (Gui4jCellEditor) editor;
                if (mNotifyTempValue != null)
                {
                    Map m = getParamMap(gui4jController, row, column, false, model);
                    edit.setNotificationCallback(gui4jController, mNotifyTempValue, m, PARAM_VALUE);
                }
                else
                {
                    edit.setNotificationCallback(null, null, null, null);
                }
            }
        }

        protected final Map getParamMap(Gui4jCallBase gui4jController, int row, int col, boolean includeListItem,
                Gui4jTableModel model)
        {
            Map m = new HashMap();
            m.put(PARAM_COL, new Integer(col));
            m.put(PARAM_ROW, new Integer(row));
            m.put(PARAM_COLVALUE, model.mColumns.get(col));
            m.put(PARAM_ROWVALUE, model.mRows.get(row));

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

        public TableCellEditor getCellEditor(Gui4jCallBase gui4jController, Font font, TableCellEditor editor, int row,
                int column, Gui4jTableModel model)
        {     
            if (mList != null)
            {
                if (mGui4jTextAttribute != null && mGui4jTextAttribute.getFont() != null)
                {
                    font = (Font) mGui4jTextAttribute.getFont().getValueNoParams(gui4jController, font);
                }
                ComboBoxCellEdit edit = ComboBoxCellEdit.getInstance(gui4jController, mValue, mStringConvert,
                        PARAM_LIST_ITEM, font, mMaximumRowCount);
                
                edit.setMaximumRowCount(mMaximumRowCount);
                
                edit.setParamMap(getParamMap(gui4jController, row, column, true, model));
                if (mListEditable != null)
                {
                    Boolean editable = (Boolean) mListEditable.getValueNoParams(gui4jController, null);
                    edit.setEditable(Boolean.TRUE.equals(editable));
                }
                
                // set autoexpand to true or false according to cell attribute
                Component c = edit.getComponent();
                if(c instanceof ComboBoxHorizontalScroll) {
                    ComboBoxHorizontalScroll cbhs = (ComboBoxHorizontalScroll) c;
                    cbhs.setAutoPopup(mAutoExtend);
                }
                
                return edit;
            }
            else
            {
                return editor;
            }
        }

        public Gui4jCol getGui4jCol()
        {
            return mGui4jCol;
        }

        public Gui4jRow getGui4jRow()
        {
            return mGui4jRow;
        }

        public Gui4jTextAttribute getTextAttribute()
        {
            return mGui4jTextAttribute;
        }

        public TableCellRenderer getCellRenderer(Gui4jComponentInstance gui4jComponentInstance, Font font,
                TableCellRenderer renderer)
        {
            renderer = (TableCellRenderer) gui4jComponentInstance.getStorage(this);
            if (renderer == null)
            {
                Gui4jJTable table = (Gui4jJTable) gui4jComponentInstance.getSwingComponent();
                renderer = new CellRenderer(gui4jComponentInstance, table, this);
                gui4jComponentInstance.setStorage(this, renderer);
            }
            return renderer;
        }

    }

    // *************************************************************************************
    // CellRenderer
    // *************************************************************************************
    public interface Enabled
    {
        boolean isEnabled(int row, int column);
    }

    public final class CellRenderer extends DefaultTableCellRenderer
    {
        private final Color mDefaultForeground;
        private final Color mDefaultBackground;
        private final Font mDefaultFont;
        private final int mAlignment;

        private final Color mBackground;
        private final Gui4jCall mBackgroundCall;

        private final Color mForeground;
        private final Gui4jCall mForegroundCall;
        private final Color mEvenBackground;
        private final Font mFont;
        private final TableCellRenderer booleanCellRenderer;

        private final Gui4jCell mGui4jCell;
        private final Gui4jCallBase mGui4jController;
        private final Gui4jTableModel mTableModel;

        public CellRenderer(Gui4jComponentInstance gui4jComponentInstance, Gui4jJTable table, Gui4jCell gui4jCell)
        {
            mGui4jCell = gui4jCell;
            mGui4jController = gui4jComponentInstance.getGui4jCallBase();
            mTableModel = (Gui4jTableModel) table.getModel();

            mDefaultBackground = getBackground();
            mDefaultForeground = getForeground();
            mDefaultFont = getFont();

            booleanCellRenderer = new BooleanTableCellRenderer(noFocusBorder);

            // use local variables to simplify calls
            Gui4jTextAttribute cellAttribute = mGui4jCell.getTextAttribute();
            Gui4jTextAttribute colAttribute = mGui4jCell.getGui4jCol().getGui4jTextAttribute();
            Gui4jTextAttribute rowAttribute = mGui4jCell.getGui4jRow().getGui4jTextAttribute();

            {
                // background may be evaluated dynamically
                Color color = null;
                mBackgroundCall = Gui4jTextAttribute.getCall(cellAttribute, colAttribute, rowAttribute,
                        new Gui4jTextAttribute.Gui4jCallProvider() {
                            public Gui4jCall retrieveGui4jCall(Gui4jTextAttribute textAttribute)
                            {
                                return textAttribute.getBackground();
                            }
                        });
                if (mBackgroundCall != null && mBackgroundCall.getUsedParams().isEmpty())
                {
                    color = Gui4jTextAttribute.getColor(mGui4jController, mBackgroundCall);
                }
                mBackground = color;
            }
            {
                // foreground may be evaluated dynamically
                Color color = null;
                mForegroundCall = Gui4jTextAttribute.getCall(cellAttribute, colAttribute, rowAttribute,
                        new Gui4jTextAttribute.Gui4jCallProvider() {
                            public Gui4jCall retrieveGui4jCall(Gui4jTextAttribute textAttribute)
                            {
                                return textAttribute.getForeground();
                            }
                        });
                if (mForegroundCall != null && mForegroundCall.getUsedParams().isEmpty())
                {
                    color = Gui4jTextAttribute.getColor(mGui4jController, mForegroundCall);
                }
                mForeground = color;
            }

            {
                // even background, font and alignment are evaluated statically
                mEvenBackground = Gui4jTextAttribute.getEvenBackground(mGui4jController, cellAttribute, colAttribute,
                        rowAttribute);
                mFont = Gui4jTextAttribute.getFont(mGui4jController, cellAttribute, colAttribute, rowAttribute);
                mAlignment = Gui4jTextAttribute.getAlignment(cellAttribute, colAttribute, rowAttribute);
            }
        }

        /**
         * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(JTable,
         *      Object, boolean, boolean, int, int)
         */
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column)
        {
            setForeground(mDefaultForeground);
            setBackground(mDefaultBackground);
            setFont(mDefaultFont);
            setEnabled(mGui4jCell.isEnabled(mGui4jController, row, column, mTableModel));

            // get the renderer component for this cell
            JComponent c;
            if (value != null && value.getClass() == Boolean.class)
            {
                // special checkbox renderer for booleans
                BooleanTableCellRenderer renderer = (BooleanTableCellRenderer) booleanCellRenderer
                        .getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                // renderer.setUnselectedBackground(background);
                c = renderer;
            }
            else
            {
                c = (JComponent) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }

            if (!isSelected)
            {
                if (mForeground != null)
                {
                    c.setForeground(mForeground);
                    
                    // TODO: As swing does not render disabled components foreground according to their foreground color,
                    // a workaround as the following has to be implemented here. The below code has not been tested and is
                    // not claiming to work correctly. It rather wants to show a possible way to solve this problem. (L.B.)
                    /*
                    if(!c.isEnabled()){
                        BasicLabelUI newUI = new BasicLabelUI () {
                            protected void paintDisabledText(JLabel l, Graphics g, String s, int textX, int textY) {
                                g.setColor(mForeground);
                                super.paintDisabledText(l, g, s, textX, textY);
                                
                                // reset old color!
                            }
                        };
                        super.setUI(newUI);
                        super.updateUI();
                    }
                    */
                }
                else if (mForegroundCall != null)
                {
                    Map paramMap = mGui4jCell.getParamMap(mGui4jController, row, column, false, mTableModel);
                    paramMap.put(PARAM_VALUE, value);
                    Color color = (Color) mForegroundCall.getValue(mGui4jController, paramMap, null);
                    c.setForeground(color);
                }
                if (!mTableModel.ignoreBackground())
                {
                    if (row % 2 == 0 && mEvenBackground != null)
                    {
                        c.setBackground(mEvenBackground);
                    }
                    else
                    {
                        if (mBackground != null)
                        {
                            c.setBackground(mBackground);
                        }
                        else if (mBackgroundCall != null)
                        {
                            Map paramMap = mGui4jCell.getParamMap(mGui4jController, row, column, false, mTableModel);
                            paramMap.put(PARAM_VALUE, value);
                            c.setBackground((Color) mBackgroundCall.getValue(mGui4jController, paramMap, null));
                        }
                    }

                }
            }
            if (mFont != null)
            {
                c.setFont(mFont);
            }
            // MA 26.04.06: tooltip for all renderers
            String tooltip = mGui4jCell.getTooltip(mGui4jController, row, column, mTableModel);
            if ("".equals(tooltip))
            {
                tooltip = null;
            }
            c.setToolTipText(tooltip);

            if (c instanceof JLabel)
            {
                JLabel l = (JLabel) c;
                l.setHorizontalAlignment(mAlignment);
                
                if (value != null && value instanceof Icon)
                {
                    l.setText("");
                    l.setIcon((Icon) value);
                    l.setHorizontalAlignment(SwingConstants.CENTER);
                }
                else
                {
                    l.setIcon(null);
                }
            }
            return c;
        }

    }

    // *************************************************************************************
    // TABLE MODEL
    // *************************************************************************************

    public final class Gui4jTableModel extends RowHeaderAbstractTableModel implements Gui4jTableListener, Enabled,
            Gui4jEventListener
    {
        protected List mColumns = new ArrayList();
        protected List mRows = new ArrayList();
        private boolean mUseCache;
        private Object[][] mCache;

        private Gui4jSwingContainer mGui4jSwingContainer;
        private Gui4jCallBase mGui4jController;
        private Gui4jComponentInstance mGui4jComponentInstance;
        private Gui4jJTable mTable;

        private Gui4jRow[] mGui4jRows;
        private Gui4jCol[] mGui4jCols;
        private Gui4jCell[][] mGui4jCells;

        private boolean arraysAreValid = false;
        private boolean initialized = false;
        private int mPageNo = -1; // für Drucken
        private boolean ignoreBackground = false; // für Drucken ohne

        // Hintergrund (=true)

        protected Gui4jTableModel(Gui4jSwingContainer gui4jSwingContainer, Gui4jCallBase gui4jCallBase)
        {
            mGui4jSwingContainer = gui4jSwingContainer;
            mGui4jController = gui4jCallBase;
            mUseCache = mUseCacheDflt;
            clearCache();
        }

        public Gui4jGetValue getAutomaticRefresh()
        {
            if (mAutomaticRefresh && mTable != null)
            {
                return mTable.getAutomaticRefreshAction();
            }
            else
            {
                return null;
            }
        }

        public void setIgnoreBackground(boolean flag)
        {
            if (flag != ignoreBackground)
            {
                ignoreBackground = flag;
                if (mTable != null)
                {
                    mTable.repaint();
                    if (mTable instanceof RowHeaderTable)
                    {
                        RowHeaderTable rowHeaderTable = (RowHeaderTable) mTable;
                        rowHeaderTable.repaint();
                    }
                }
            }

        }

        public boolean ignoreBackground()
        {
            return ignoreBackground;
        }

        public void setInitialized()
        {
            initialized = true;
        }

        public void setRows(Collection rows)
        {
            if (rows != null)
            {
                mRows = new ArrayList(rows);
            }
            else
            {
                mRows = new ArrayList();
            }
            clearCache();
            refreshRows();
        }

        public void setContent(Collection rows, Collection colummns)
        {
            setColumns(colummns);
            setRows(rows);
        }

        public void setColumns(Collection columns)
        {
            mColumns = new ArrayList(columns);
            clearCache();
            refreshColumns(this);
        }

        public void clearCache()
        {
            if (mUseCache)
            {
                mCache = new Object[mRows.size()][mColumns.size()];
            }
            else
            {
                mCache = null;
            }
        }

        /**
         * @see org.gui4j.core.swing.RowHeaderTableModel#getRowName(int)
         */
        public String getRowName(int row)
        {
            Gui4jRow gui4jRow = getGui4jRow(row);
            return gui4jRow == null ? "" : gui4jRow.getName(mGui4jController, row, this);
        }

        /**
         * @see javax.swing.table.TableModel#getColumnName(int)
         */
        public String getColumnName(int col)
        {
            Gui4jCol gui4jCol = getGui4jCol(col);
            if (gui4jCol != null)
            {
                String name = gui4jCol.getName(mGui4jController, col, this);
                if (name == null || name.equals(""))
                {
                    return name;
                }
                if (name.startsWith("\n"))
                {
                    return " " + name;
                }
                return name;
            }
            else
            {
                return " ";
            }
        }

        /**
         * @see org.gui4j.core.swing.RowHeaderAbstractTableModel#setRowName(int,
         *      String)
         */
        public void setRowName(int rowNumber, String newName)
        {
            // nothing todo
        }

        /**
         * @see org.gui4j.core.swing.RowHeaderTableModel#setRowName(int, Object)
         */
        public void setRowName(int rowNumber, Object newName)
        {
            // nothing todo
        }

        /**
         * @see javax.swing.table.TableModel#getColumnCount()
         */
        public int getColumnCount()
        {
            return mColumns.size();
        }

        /**
         * @see javax.swing.table.TableModel#getRowCount()
         */
        public int getRowCount()
        {
            return mRows.size();
        }

        private Gui4jCell findByInheritance(Gui4jRow row, Gui4jCol col)
        {
            final EncapsulateGui4jCell container = new EncapsulateGui4jCell();
            container.mGui4jCell = null;
            final Class rowClass = row.mClassType;
            final Class colClass = col.mClassType;
            /*
             * mLogger.debug( "Searching for rowClass = " + rowClass.getName() + ",
             * colClass = " + colClass.getName());
             */
            mCellManager.traverse(new SparseMatrix.Traverser() {
                public void work(Object pRow, Object pCol, Object value)
                {
                    Class currentRowClass = ((Gui4jRow) pRow).mClassType;
                    Class currentColClass = ((Gui4jCol) pCol).mClassType;
                    Gui4jCell currentCell = (Gui4jCell) value;
                    if (currentRowClass.isAssignableFrom(rowClass) && currentColClass.isAssignableFrom(colClass))
                    {
                        if (container.mGui4jCell != null)
                        {
                            if (container.mGui4jCell.getGui4jRow().mClassType.isAssignableFrom(currentRowClass)
                                    && container.mGui4jCell.getGui4jCol().mClassType.isAssignableFrom(currentColClass))
                            {
                                container.mGui4jCell = currentCell;
                                /*
                                 * mLogger.debug( "Using rowClass = " +
                                 * currentRowClass.getName() + ", colClass = " +
                                 * currentColClass.getName());
                                 */
                            }
                        }
                        else
                        {
                            container.mGui4jCell = currentCell;
                            /*
                             * mLogger.debug( "Using rowClass = " +
                             * currentRowClass.getName() + ", colClass = " +
                             * currentColClass.getName());
                             */
                        }
                    }
                }
            });
            return container.mGui4jCell;
        }

        private void recompute()
        {
            if (!arraysAreValid && mRows != null && mColumns != null)
            {
                int rows = mRows.size();
                int cols = mColumns.size();
                int i = 0;
                mGui4jRows = new Gui4jRow[rows];
                for (Iterator it = mRows.iterator(); it.hasNext(); i++)
                {
                    Object object = it.next();
                    assert object != null;
                    Gui4jRow gui4jRow = (Gui4jRow) mRowManager.get(object.getClass());
                    if (gui4jRow == null)
                    {
                        gui4jRow = new Gui4jRow(object.getClass(), null, null);
                        mRowManager.add(object.getClass(), gui4jRow);
                    }
                    mGui4jRows[i] = gui4jRow;
                }

                i = 0;
                mGui4jCols = new Gui4jCol[cols];
                for (Iterator it = mColumns.iterator(); it.hasNext(); i++)
                {
                    Object object = it.next();
                    assert object != null;
                    Gui4jCol gui4jCol = (Gui4jCol) mColManager.get(object.getClass());
                    if (gui4jCol == null)
                    {
                        gui4jCol = new Gui4jCol(object.getClass(), null, null, null, null);
                        mColManager.add(object.getClass(), gui4jCol);
                    }
                    if (i < mGui4jCols.length) {
                        mGui4jCols[i] = gui4jCol;
                    }
                }
                mGui4jCells = new Gui4jCell[rows][cols];
                for (int r = 0; r < rows; r++)
                {
                    for (int c = 0; c < cols; c++)
                    {
                        Gui4jRow gui4jRow = mGui4jRows[r];
                        Gui4jCol gui4jCol = mGui4jCols[c];
                        if (gui4jRow != null && gui4jCol != null)
                        {
                            Gui4jCell gui4jCell = (Gui4jCell) mCellManager.get(gui4jRow, gui4jCol);
                            if (gui4jCell == null)
                            {
                                gui4jCell = findByInheritance(gui4jRow, gui4jCol);
                            }
                            else
                            {
                            }
                            if (gui4jCell == null)
                            {
                                mLogger.debug("Creating 'on the fly' instance of Gui4jCell for ("
                                        + mRows.get(r).getClass().getName() + " gui4jRow="
                                        + gui4jRow.mClassType.getName() + ", " + mColumns.get(c).getClass().getName()
                                        + " gui4jCol=" + gui4jCol.mClassType.getName() + ")");
                                // mRows.get(r).getClass(),
                                // mColumns.get(c).getClass(),
                                gui4jCell = new Gui4jCell(gui4jRow.mClassType, gui4jCol.mClassType, null, null, null,
                                        null, null, null, null, null, null, null, null, null, null, null, null);
                                mCellManager.set(gui4jRow, gui4jCol, gui4jCell);
                            }
                            if (mGui4jCells != null)
                            {
                                mGui4jCells[r][c] = gui4jCell;
                            }
                        }
                    }
                }
                arraysAreValid = true;
            }
        }

        private Gui4jRow getGui4jRow(int row)
        {
            recompute();
            assert row >= 0 && row < mRows.size();
            return mGui4jRows == null ? null : mGui4jRows[row];
        }

        private Gui4jCol getGui4jCol(int col)
        {
            recompute();
            assert col >= 0 && col < mColumns.size();
            return mGui4jCols[col];
        }

        private Gui4jCell getGui4jCell(int row, int col)
        {
            recompute();
            if (row >= 0 && row < mRows.size() && col >= 0 && col < mColumns.size())
            {
                return mGui4jCells[row][col];
            }
            else
            {
                mLogger.error("Invalid row or column index: row=" + row + ", col=" + col + ", rows=" + mRows.size()
                        + ", cols=" + mColumns.size());
                return null;
            }
        }

        public boolean isUsingCache()
        {
            return mUseCache;
        }

        public void setUseCache(boolean flag, int pageNo)
        {
            mUseCache = flag;
            if (!mUseCache || pageNo != mPageNo)
            {
                mPageNo = pageNo;
                clearCache();
            }
        }

        /**
         * @see javax.swing.table.TableModel#getValueAt(int, int)
         */
        public Object getValueAt(int row, int col)
        {
            if (mUseCache && mCache != null && row < mCache.length && col < mCache[row].length)
            {
                Object object = mCache[row][col];
                if (object != null)
                {
                    return object;
                }
            }
            Gui4jCell gui4jCell = getGui4jCell(row, col);
            try
            {
                Object object = gui4jCell == null ? null : gui4jCell.getValue(mGui4jController, row, col, this);
                if (mUseCache && mCache != null && row < mCache.length && col < mCache[row].length)
                {
                    mCache[row][col] = object;
                }
                return object;
            }
            catch (Throwable t)
            {
                getGui4j().handleException(mGui4jController, t, null);
                gui4jCell.invalidateCall();
                return null;
            }
        }

        /**
         * @see javax.swing.table.TableModel#setValueAt(Object, int, int)
         */
        public void setValueAt(Object value, int row, int col)
        {
            Gui4jCell gui4jCell = getGui4jCell(row, col);
            if (gui4jCell != null)
            {
                gui4jCell.setValue(mGui4jController, row, col, value, this);
            }
        }

        public int getRowIndex(Object object)
        {
            return mRows.indexOf(object);
        }

        public int getColumnIndex(Object object)
        {
            return mColumns.indexOf(object);
        }

        /**
         * @see javax.swing.table.TableModel#isCellEditable(int, int)
         */
        public boolean isCellEditable(int row, int col)
        {
            if (handleReadOnly() && mGui4jSwingContainer.inReadOnlyMode())
            {
                return false;
            }
            Gui4jCell cell = getGui4jCell(row, col);
            if (cell != null)
            {
                return cell.isEditable(mGui4jController, row, col, this);
            }
            else
            {
                return true;
            }
        }

        /**
         * @see org.gui4j.core.swing.Gui4jTableListener#getCellEditor(TableCellEditor,
         *      int, int)
         */
        public TableCellEditor getCellEditor(TableCellEditor editor, int row, int column)
        {
            Gui4jCell cell = getGui4jCell(row, column);
            return cell == null ? editor : cell.getCellEditor(mGui4jController, mGui4jComponentInstance
                    .getSwingComponent().getFont(), editor, row, column, this);
        }

        /**
         * @see org.gui4j.core.swing.Gui4jTableListener#getCellRenderer(TableCellRenderer,
         *      int, int)
         */
        public TableCellRenderer getCellRenderer(TableCellRenderer renderer, int row, int column)
        {
            Gui4jCell cell = getGui4jCell(row, column);
            return cell == null ? renderer : cell.getCellRenderer(mGui4jComponentInstance, mGui4jComponentInstance
                    .getSwingComponent().getFont(), renderer);
        }

        /**
         * @see org.gui4j.core.swing.Gui4jTableListener#prepareEditor(TableCellEditor,
         *      int, int)
         */
        public void prepareEditor(TableCellEditor editor, int row, int column)
        {
            Gui4jCell cell = getGui4jCell(row, column);
            if (cell != null)
            {
                cell.prepareEditor(mGui4jController, editor, row, column, this);
            }
        }

        public void handleOnCellSelect(int row, int col, Gui4jCallBase gui4jController,
                Gui4jThreadManager gui4jThreadManager)
        {
            Gui4jCell cell = getGui4jCell(row, col);
            if (cell != null)
            {
                cell.handleOnCellSelect(row, col, gui4jController, gui4jThreadManager, this);
            }
        }

        public void handleOnCellClick(int row, int col, Gui4jCallBase gui4jController,
                Gui4jThreadManager gui4jThreadManager)
        {
            Gui4jCell cell = getGui4jCell(row, col);
            if (cell != null)
            {
                cell.handleOnCellClick(row, col, gui4jController, gui4jThreadManager, this);
            }
        }

        public void handleOnCellDblClick(int row, int col, Gui4jCallBase gui4jController,
                Gui4jThreadManager gui4jThreadManager)
        {
            Gui4jCell cell = getGui4jCell(row, col);
            if (cell != null)
            {
                cell.handleOnCellDblClick(row, col, gui4jController, gui4jThreadManager, this);
            }
        }

        public void setGui4jComponentInstance(Gui4jComponentInstance gui4jComponentInstance)
        {
            mGui4jComponentInstance = gui4jComponentInstance;
            mTable = (Gui4jJTable) mGui4jComponentInstance.getSwingComponent();
        }

        protected void refreshColumns(Gui4jTableModel model)
        {
            if (mTable != null)
            {
                // Tabelle noch nicht zerstoert
                if (!initialized)
                {
                    return;
                }
                arraysAreValid = false;
                // mLogger.debug("Refreshing columns of table " + getId() + "
                // count = " + mColumns.size());
                // mTable.setModel(this);
                mTable.createDefaultColumnsFromModel();
                // fireTableStructureChanged();
                Font font = mTable.getFont();
                JTableHeader tableHeader = mTable.getTableHeader();

                double sumWeight = 0.0;
                int sumWidth = 0;

                if (tableHeader != null)
                {

                    double headerHeight = tableHeader.getPreferredSize().getHeight();
                    int columnCount = mTable.getColumnCount();
                    if (mColumns.size() < columnCount) {
                        columnCount = mColumns.size();
                    }
                    for (int c = 0; c < columnCount; c++)
                    {
                        Gui4jCol gui4jCol = getGui4jCol(c);
                        if (c >= mTable.getColumnCount()) {
                            continue;
                        }
                        TableColumn tableColumn = mTable.getColumnModel().getColumn(c);
                        sumWidth += tableColumn.getPreferredWidth();
                        sumWeight += gui4jCol.getWeight();
                        String name = gui4jCol.getName(mGui4jController, c, this);
                        TableCellRenderer renderer = tableHeader.getDefaultRenderer();
                        if (renderer != null)
                        {
                            if (name != null && name.startsWith("\n"))
                            {
                                name = "XXX" + name;
                            }
                            JLabel label = new JLabel(name);
                            label.setFont(font);
                            label.setUI(MultiLineLabelUI.getInstance());
                            double height = label.getPreferredSize().getHeight() + 4;
                            if (height > headerHeight)
                            {
                                headerHeight = height;
                            }
                        }
                    }
                    {
                        Dimension d = new Dimension(tableHeader.getPreferredSize());
                        d.height = (int) headerHeight;

                        // workaround for bug in swing
                        // resizing does not work if we have horizontal
                        // scrollbars
                        if (mResizeMode != JTable.AUTO_RESIZE_OFF)
                        {
                            tableHeader.setPreferredSize(d);
                        }
                    }
                }
                {
                    for (int c = 0; c < mColumns.size(); c++)
                    {
                        Gui4jCol gui4jCol = getGui4jCol(c);
                        if (c >= mTable.getColumnCount()) {
                            continue;
                        }
                        TableColumn tableColumn = mTable.getColumnModel().getColumn(c);
                        double weight = gui4jCol.getWeight();
                        if (gui4jCol.maxCharactersDefined())
                        {
                            int width = mTable.getFontMetrics(font).stringWidth(
                                    StringUtil.copy('M', gui4jCol.getMaxCharacters(mGui4jController)));
                            tableColumn.setMaxWidth(width);
                            tableColumn.setPreferredWidth(width);
                        }
                        else if (gui4jCol.charactersDefined())
                        {
                            int width = mTable.getFontMetrics(font).stringWidth(
                                    StringUtil.copy('M', gui4jCol.getCharacters(mGui4jController)));
                            tableColumn.setPreferredWidth(width);
                        }
                        else
                        {
                            tableColumn.setPreferredWidth((int) (sumWidth * weight / sumWeight));
                        }
                    }
                }
            }
        }

        public void dispose()
        {
            mTable = null;
            mGui4jRows = null;
            mGui4jCols = null;
            mGui4jCells = null;
            mGui4jController = null;
            mGui4jSwingContainer = null;
            mColumns.clear();
            mRows.clear();
            mGui4jComponentInstance = null;
            mTable = null;
        }

        protected void refreshRows()
        {
            arraysAreValid = false;
            // mLogger.debug("Refreshing rows of table " + getId() + " count = "
            // + mRows.size());
            Font font = mTable.getFont();
            if (mUseRowHeaders)
            {
                JLabel label = new JLabel();
                label.setFont(font);
                int preferredWidth = 10;
                for (int i = 0; i < mRows.size(); i++)
                {
                    label.setText(getRowName(i));
                    int width = label.getPreferredSize().width + 8;
                    if (width > preferredWidth)
                    {
                        preferredWidth = width;
                    }
                }
                RowHeaderTable ctable = (RowHeaderTable) mTable;
                if (ctable != null)
                {
                    ctable.setRowHeaderWidth(preferredWidth);
                    ctable.setRowHeaderFont(font);
                    ctable.setRowHeaderHeight(mTable.getRowHeight());
                    ctable.refreshRowHeaders();
                }
            }

            int selectedCol = mTable.getSelectedColumn();
            int selectedRow = mTable.getSelectedRow();
            clearRenderes();
            fireTableDataChanged();
            if (selectedCol != -1 && selectedRow != -1
                    && selectedRow < getRowCount()
                    && selectedCol < getColumnCount())
            {
                mTable.setRowSelectionInterval(selectedRow, selectedRow);
                mTable.setColumnSelectionInterval(selectedCol, selectedCol);
            }
        }

        /**
         * @see org.gui4j.component.Gui4jMatrix.Enabled#isEnabled(int, int)
         */
        public boolean isEnabled(int row, int column)
        {
            Gui4jCell gui4jCell = getGui4jCell(row, column);
            if (gui4jCell != null)
            {
                return gui4jCell.isEnabled(mGui4jController, row, column, this);
            }
            else
            {
                return true;
            }
        }

        /**
         * @see org.gui4j.event.Gui4jEventListener#eventOccured()
         */
        public void eventOccured()
        {
            Runnable run = new Runnable() {
                public void run()
                {
                    clearRenderes();
                    clearCache();
                    fireTableRowsUpdated(0, getRowCount() - 1);
                }
            };
            Gui4jThreadManager.executeInSwingThreadAndWait(run);
        }
        
        public void clearRenderes() {
            mCellManager.traverse(new SparseMatrix.Traverser() {
                public void work(Object pRow, Object pCol, Object value)
                {
                    Gui4jCell currentCell = (Gui4jCell) value;
                    mGui4jComponentInstance.setStorage(currentCell, null);
                }
            });
        }

    }

    private static class Flag
    {
        boolean val;
    }

    private static class EncapsulateGui4jCell
    {
        protected Gui4jCell mGui4jCell;
    }
    
    /**
     * This method checks whether the selected cell contains a list and so it's editor is a combobox/popup.
     * If so, the combobox (i.e. ComboBoxHorizontalScroll) requests focus and is expanded in it's
     * processFocusEvent()-method if it is an autoexpanding combobox. (L.B.)
     */
    private void checkAndExpandComboBox(Gui4jTableModel model) {
        final JTable table = (JTable) model.mGui4jComponentInstance.getSwingComponent();
        if(table.hasFocus() && !table.isEditing()) {
            final int row = table.getSelectedRow();
            final int col = table.getSelectedColumn();
            final Gui4jCell cell = model.getGui4jCell(row, col);
            if(null != cell) {
                if(cell.mList != null && cell.mAutoExtend) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            if(table.editCellAt(row, col)) {
                                table.getEditorComponent().requestFocus();
                                /*
                                if(table.getEditorComponent() instanceof ComboBoxHorizontalScroll) {
                                    ComboBoxHorizontalScroll editor = (ComboBoxHorizontalScroll) table.getEditorComponent();
                                    editor.showPopup();
                                }
                                */
                            }
                        }
                    });
                }
            }
        }
    }
    
}