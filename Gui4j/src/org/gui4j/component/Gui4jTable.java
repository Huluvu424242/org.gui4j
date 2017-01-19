package org.gui4j.component;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.gui4j.Gui4jCallBase;
import org.gui4j.Gui4jGetValue;
import org.gui4j.component.util.ExcelAdapter;
import org.gui4j.component.util.StringUtil;
import org.gui4j.constants.Const;
import org.gui4j.core.Gui4jCall;
import org.gui4j.core.Gui4jComponentContainer;
import org.gui4j.core.Gui4jComponentInstance;
import org.gui4j.core.Gui4jMap1;
import org.gui4j.core.Gui4jMouseListener;
import org.gui4j.core.Gui4jQualifiedComponent;
import org.gui4j.core.Gui4jSwingContainer;
import org.gui4j.core.Gui4jTextAttribute;
import org.gui4j.core.Gui4jThreadManager;
import org.gui4j.core.Gui4jTypeManager;
import org.gui4j.core.listener.Gui4jMouseListenerTable;
import org.gui4j.core.listener.Gui4jMouseListenerTablePopup;
import org.gui4j.core.swing.BooleanTableCellRenderer;
import org.gui4j.core.swing.ColumnGroup;
import org.gui4j.core.swing.ComboBoxCellEdit;
import org.gui4j.core.swing.GroupableTableHeader;
import org.gui4j.core.swing.Gui4jCellEditor;
import org.gui4j.core.swing.Gui4jJTable;
import org.gui4j.core.swing.Gui4jJTableHeader;
import org.gui4j.core.swing.Gui4jRefreshTable;
import org.gui4j.core.swing.Gui4jTableListener;
import org.gui4j.core.swing.MultiLineLabelUI;
import org.gui4j.core.swing.RowHeaderAbstractTableModel;
import org.gui4j.core.swing.RowHeaderTable;
import org.gui4j.core.swing.RowRetriever;
import org.gui4j.core.util.ComboBoxNullItem;
import org.gui4j.event.Gui4jEventListener;
import org.gui4j.exception.Gui4jUncheckedException;
import org.gui4j.util.Pair;

public final class Gui4jTable extends Gui4jJComponent
{

    protected static final Log mLogger = LogFactory.getLog(Gui4jTable.class);

    protected final ColumnManager mColumnManager;
    protected final Class mDefaultType;
    private final Gui4jTypeManager mOnCellSelect; // Class -> Gui4jCall
    private final Gui4jTypeManager mOnRowSelect; // Class -> Gui4jCall
    private final Gui4jTypeManager mOnColSelect; // Class -> Gui4jCall
    private final Gui4jTypeManager mOnDoubleClick; // Class -> Gui4jCall
    private final Gui4jTypeManager mPopupContext; // Class -> Gui4jCall
    protected final Gui4jTypeManager mRowManager; // Class -> Gui4jCall
    private Gui4jCall[] mRefresh;
    protected Gui4jCall mOnSetValue;
    protected Gui4jCall mCellSelectionPair;
    protected Gui4jCall mRowSelectionIndex;
    protected Gui4jCall mRowSelectionItem;
    protected Gui4jCall mHeaderBackground;
    protected Gui4jCall mActionCommand;
    protected Gui4jCall mRowHeaderCharacters;
    protected boolean mHideFocus;
    private final int mVisibleRows;
    protected final boolean mAutomaticVisibleRows;
    protected final boolean mAutomaticRefresh;
    protected final boolean mUseRowHeaders;
    private final int mHeaderLines;
    private boolean mReorderingAllowed = true;
    protected boolean mRowSelectionAllowed = true;
    private int mRowSelectionMode = ListSelectionModel.SINGLE_SELECTION;
    private int mColSelectionMode = ListSelectionModel.SINGLE_SELECTION;
    private List mColumnGroups; // List(Gui4jColumnHeaderTable)

    private int mResizeMode;

    /**
     * @param gui4jComponentContainer
     * @param id
     * @param visibleRows
     * @param headerLines
     * @param defaultType
     * @param automaticVisibleRows
     * @param automaticRefresh
     * @param useOriginalCollection
     * @param useRowHeaders
     * @param resizeMode
     */
    public Gui4jTable(Gui4jComponentContainer gui4jComponentContainer, String id, int visibleRows, int headerLines,
            Class defaultType, boolean automaticVisibleRows, boolean automaticRefresh, boolean useOriginalCollection,
            boolean useRowHeaders, int resizeMode)
    {
        super(gui4jComponentContainer, Gui4jJTable.class, id);
        mColumnManager = new ColumnManager(defaultType);
        mVisibleRows = visibleRows;
        mHeaderLines = headerLines;
        mDefaultType = defaultType;
        mAutomaticVisibleRows = automaticVisibleRows;
        mAutomaticRefresh = automaticRefresh;
        mUseRowHeaders = useRowHeaders;
        mResizeMode = resizeMode;

        mOnCellSelect = new Gui4jTypeManager();
        mOnRowSelect = new Gui4jTypeManager();
        mOnColSelect = new Gui4jTypeManager();
        mOnDoubleClick = new Gui4jTypeManager();
        mPopupContext = new Gui4jTypeManager();
        mRowManager = new Gui4jTypeManager();
        mColumnGroups = new ArrayList();
    }
    
    protected void setProperties(Gui4jComponentInstance gui4jComponentInstance)
    {
        super.setProperties(gui4jComponentInstance);
        final Gui4jCallBase gui4jCallBase = gui4jComponentInstance.getGui4jCallBase();
        Gui4jJTable table = (Gui4jJTable) gui4jComponentInstance.getComponent();
        Gui4jTableModel model = (Gui4jTableModel) table.getModel();
        
        table.setAutoResizeMode(mResizeMode);

        Font font = table.getFont();

        table.setDefaultEditor(String.class, Gui4jCellEditor.createTextEditor(font, true));

        {
            JLabel l = new JLabel("X");
            l.setFont(font);
            table.setRowHeight(l.getPreferredSize().height + 4);
        }
        setHeader(gui4jComponentInstance.getGui4jCallBase(), table);
        JTableHeader tableHeader = table.getTableHeader();

        Color headerBackground = null;
        if (mHeaderBackground != null)
        {
            headerBackground = (Color) mHeaderBackground.getValueNoParams(gui4jComponentInstance.getGui4jCallBase(),
                    null);
            if (headerBackground != null)
            {
                tableHeader.setBackground(headerBackground);
            }
        }

        table.setTableHeader(tableHeader);

        // JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setReorderingAllowed(mReorderingAllowed);
        {
            TableCellRenderer renderer = tableHeader.getDefaultRenderer();
            tableHeader.setFont(font);
            Component c = renderer.getTableCellRendererComponent(table, "", false, false, 0, 0);
            if (mHeaderLines == 1 && c instanceof JLabel)
            {
                ((JLabel) c).setUI(MultiLineLabelUI.getInstance());
            }
        }
        
        RowHeaderTable ctable = null;
        if (mUseRowHeaders)
        {
            JLabel label = new JLabel();
            label.setFont(font);
            /*RowHeaderTable*/ ctable = (RowHeaderTable) table;
            ctable.setRowHeaderFont(font);
            ctable.setRowHeaderHeight(table.getRowHeight());

            model.refreshRows(false);
            if (headerBackground != null)
            {
                ((RowHeaderTable.RowHeaderRenderer) ctable.getRowHeader().getCellRenderer())
                        .setBackground(headerBackground);
            }
        }
        
        // init excel export adapter
        if(ctable != null)
            new ExcelAdapter(table, ctable.getRowHeader(), getGui4j().createExcelCopyHandler(getId()));
        else
            new ExcelAdapter(table, null, getGui4j().createExcelCopyHandler(getId()));
        // end init
        
        if (mOnRowSelect.size() > 0)
        {
            Gui4jMouseListenerTable mouseListener = new Gui4jMouseListenerTable(gui4jComponentInstance, 1,
                    mDefaultType, mOnRowSelect);
            model.addListener(mouseListener);
            table.getSelectionModel().addListSelectionListener(mouseListener);
        }
        if (mOnCellSelect.size() > 0)
        {
            Gui4jMouseListenerTable mouseListener = new Gui4jMouseListenerTable(gui4jComponentInstance, 1,
                    mDefaultType, mOnCellSelect);
            model.addListener(mouseListener);
            table.getSelectionModel().addListSelectionListener(mouseListener);
            table.getColumnModel().getSelectionModel().addListSelectionListener(mouseListener);
        }
        if (mOnColSelect.size() > 0)
        {
            Gui4jMouseListenerTable mouseListener = new Gui4jMouseListenerTable(gui4jComponentInstance, 1,
                    mDefaultType, mOnColSelect);
            model.addListener(mouseListener);
            table.getColumnModel().getSelectionModel().addListSelectionListener(mouseListener);
        }
        if (mOnDoubleClick.size() > 0)
        {
            Gui4jMouseListenerTable mouseListener = new Gui4jMouseListenerTable(gui4jComponentInstance, 2,
                    mDefaultType, mOnDoubleClick);
            model.addListener(mouseListener);
            table.addMouseListener(mouseListener);
        }
        if (mActionCommand != null)
        {
            table.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "actionCommand");
            table.getActionMap().put("actionCommand", new AbstractAction() {
                public void actionPerformed(ActionEvent e)
                {
                    getGui4j().getGui4jThreadManager().performWork(gui4jCallBase, mActionCommand, null);
                }
            });
        }
        if (mRowManager.size() > 0)
        {

        }

        int sumWidth = 0;
        double sumWeight = 0.0;

        {
            int columnIndex = 0;
            double headerHeight = tableHeader.getPreferredSize().getHeight();
            for (Iterator it = mColumnManager.getMainColumns().iterator(); it.hasNext(); columnIndex++)
            {
                Gui4jColumnTable gui4jColumn = (Gui4jColumnTable) it.next();
                TableColumn tableColumn = table.getColumnModel().getColumn(columnIndex);
                sumWidth += tableColumn.getPreferredWidth();
                sumWeight += gui4jColumn.getWeight();
                String name = gui4jColumn.getName(gui4jCallBase);
                TableCellRenderer renderer = tableHeader.getDefaultRenderer();
                if (renderer != null)
                {
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
                Dimension d = tableHeader.getPreferredSize();
                d.height = (int) headerHeight;

                // workaround for bug in swing
                // resizing does not work if we have horizontal scrollbars
                if (mResizeMode != JTable.AUTO_RESIZE_OFF)
                {
                    tableHeader.setPreferredSize(d);
                }
            }
        }

        {
            int columnIndex = 0;
            for (Iterator it = mColumnManager.getMainColumns().iterator(); it.hasNext(); columnIndex++)
            {
                Gui4jColumnTable gui4jColumn = (Gui4jColumnTable) it.next();
                TableColumn tableColumn = table.getColumnModel().getColumn(columnIndex);
                TableCellEditor editor = gui4jColumn.createEditor(gui4jCallBase, font);
                if (gui4jColumn.isMainColumn())
                {
                    if (editor != null)
                    {
                        tableColumn.setCellEditor(editor);
                    }
                    double weight = gui4jColumn.getWeight();
                    if (gui4jColumn.isMaxCharactersAttributeDefined())
                    {
                        int width = table.getFontMetrics(font).stringWidth(
                                StringUtil.copy('M', gui4jColumn.getMaxCharacters(gui4jCallBase)));
                        tableColumn.setMaxWidth(width);
                        tableColumn.setPreferredWidth(width);
                    }
                    else if (gui4jColumn.isCharactersAttributeDefined())
                    {
                        int width = table.getFontMetrics(font).stringWidth(
                                StringUtil.copy('M', gui4jColumn.getCharacters(gui4jCallBase)));
                        tableColumn.setPreferredWidth(width);
                    }
                    else
                    {
                        tableColumn.setPreferredWidth((int) (sumWidth * weight / sumWeight));
                    }
                }
            }
        }
        if (mAutomaticVisibleRows)
        {
            Dimension d = new Dimension(table.getPreferredSize());
            int rows = table.getRowCount();
            if (mVisibleRows != -1 && rows > mVisibleRows)
            {
                rows = mVisibleRows;
            }
            d.setSize(d.getWidth(), table.getRowHeight() * rows);
            table.setPreferredScrollableViewportSize(d);
        }
        else if (mVisibleRows != -1)
        {
            Dimension d = new Dimension(table.getPreferredSize());
            d.setSize(d.getWidth(), table.getRowHeight() * mVisibleRows);
            table.setPreferredScrollableViewportSize(d);
        }

        if (getGui4j().traceMode())
        {
            mLogger.debug("Preferred size of table with id " + getId() + " is " + table.getPreferredSize());
            mLogger.debug("Preferred scrollable viewport size of table with id " + getId() + " is "
                    + table.getPreferredScrollableViewportSize());
        }

        if (!model.setSelection())
        {
            if (mRowSelectionAllowed && table.getRowCount() > 0)
            {
                table.setRowSelectionInterval(0, 0);
            }
        }

        if (mPopupContext.size() > 0)
        {
            model.mPopupHandler = new Gui4jMouseListenerTable(gui4jComponentInstance, 0, mDefaultType, mPopupContext);
        }
    }

    protected Gui4jComponentInstance createComponentInstance(Gui4jSwingContainer gui4jSwingContainer,
            Gui4jCallBase gui4jCallBase, Gui4jQualifiedComponent gui4jComponentInPath)
    {
        Gui4jTableModel tm = new Gui4jTableModel(gui4jSwingContainer, gui4jCallBase);
        registerEvents(gui4jSwingContainer, gui4jCallBase, mRefresh, tm);

        // Tabelle erzeugen
        Gui4jJTable table;
        if (!mUseRowHeaders)
        {
            table = new Gui4jJTable(tm, tm);
        }
        else
        {
            table = new RowHeaderTable(tm, tm);
        }

        table.getSelectionModel().setSelectionMode(mRowSelectionMode);
        TableColumnModel tcm = table.getColumnModel();
        tcm.getSelectionModel().setSelectionMode(mColSelectionMode);
        table.setRowSelectionAllowed(mRowSelectionAllowed);
        Gui4jComponentInstance gui4jComponentInstance = new Gui4jComponentInstance(gui4jSwingContainer, table,
                gui4jComponentInPath);
        tm.setGui4jComponentInstance(gui4jComponentInstance);

        TableCellRenderer renderer = new CellRenderer(gui4jComponentInstance, mColumnManager.getMainColumns(), tm);
        table.setDefaultRenderer(String.class, renderer);
        table.setDefaultRenderer(Boolean.class, renderer);
        table.setDefaultRenderer(Icon.class, renderer);

        return gui4jComponentInstance;
    }

    private void setHeader(Gui4jCallBase gui4jCallBase, JTable table)
    {
        TableColumnModel tcm = table.getColumnModel();
        if (mColumnGroups.isEmpty())
        {
            table.setTableHeader(new Gui4jJTableHeader(table.getColumnModel(), table.getFont(), mHeaderLines));
        }
        else
        {
            // Grouped-Headers erzeugen
            GroupableTableHeader groupableTableHeader = new GroupableTableHeader(table.getColumnModel());
            table.setTableHeader(groupableTableHeader);
            for (Iterator iter = mColumnGroups.iterator(); iter.hasNext();)
            {
                Gui4jColumnHeaderTable columnGroup = (Gui4jColumnHeaderTable) iter.next();
                ColumnGroup topColumnGroup = createColumnGroupInstances(tcm, columnGroup, gui4jCallBase);
                groupableTableHeader.addColumnGroup(topColumnGroup);
            }
        }
    }

    private ColumnGroup createColumnGroupInstances(TableColumnModel tcm, Gui4jColumnHeaderTable columnGroup,
            Gui4jCallBase gui4jCall)
    {
        ColumnGroup cg = new ColumnGroup(columnGroup.getName(gui4jCall));
        for (Iterator iter = columnGroup.getColumns().iterator(); iter.hasNext();)
        {
            Object o = iter.next();
            if (o instanceof Gui4jColumnHeaderTable)
            {
                Gui4jColumnHeaderTable newColumnGroup = (Gui4jColumnHeaderTable) o;
                ColumnGroup subCg = createColumnGroupInstances(tcm, newColumnGroup, gui4jCall);
                cg.add(subCg);

            }
            else if (o instanceof Gui4jColumnTable)
            {
                int index = mColumnManager.getMainColumns().indexOf(o);
                cg.add(tcm.getColumn(index));
            }

        }
        return cg;
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

    /*
     * (non-Javadoc)
     * 
     * @see org.gui4j.core.Gui4jAbstractComponent#createMouseListener(org.gui4j.core.Gui4jComponentInstance)
     */
    protected Gui4jMouseListener createMouseListener(Gui4jComponentInstance gui4jComponentInstance)
    {
        return new Gui4jMouseListenerTablePopup(gui4jComponentInstance);
    }

    public void setRowSelectionIndexCall(Gui4jCall rowSelection)
    {
        mRowSelectionIndex = rowSelection;
    }

    public void setRowSelectionItemCall(Gui4jCall rowSelectionItem)
    {
        mRowSelectionItem = rowSelectionItem;
    }

    public void setCellSelectionCall(Gui4jCall cellSelection)
    {
        mCellSelectionPair = cellSelection;
    }

    public void setRowHeaderCharacters(Gui4jCall rowHeaderCharacters)
    {
        mRowHeaderCharacters = rowHeaderCharacters;
    }

    public void setOnRowSelect(Class classType, Gui4jCall onRowSelect)
    {
        if (onRowSelect != null)
        {
            mOnRowSelect.add(classType, onRowSelect);
        }
    }

    public void setActionCommand(Gui4jCall actionCommand)
    {
        mActionCommand = actionCommand;
    }

    public void setHideFocus(boolean hideFocus)
    {
        mHideFocus = hideFocus;
    }

    public void setPopupContext(Class classType, Gui4jCall onRowSelect)
    {
        if (onRowSelect != null)
        {
            mPopupContext.add(classType, onRowSelect);
        }
    }

    public void setContent(Gui4jComponentInstance gui4jComponentInstance, Collection content)
    {
        if (content == null)
        {
            throw new Gui4jUncheckedException.ProgrammingError(PROGRAMMING_ERROR_parameter_null);
        }
        Gui4jJTable table = (Gui4jJTable) gui4jComponentInstance.getSwingComponent();
        if (table != null)
        {
            // Tabelle noch nicht zerstoert
            Gui4jTableModel tm = (Gui4jTableModel) table.getModel();
            ArrayList newContent = new ArrayList(content);
            if (newContent.size() == tm.mContent.size() && newContent.equals(tm.mContent))
            {
                tm.refreshRows(true);
            }
            else
            {
                if (newContent.size() == tm.mContent.size())
                {
                    mLogger.debug("Setting new content for table with id " + getId() + " oldSize=" + tm.mContent.size()
                            + " newSize=" + newContent.size());
                    /*
                     * int n = newContent.size(); for (int i=0;i <n;i++) {
                     * Object o1 = newContent.get(i); Object o2 =
                     * tm.mContent.get(i); if (!o1.equals(o2)) {
                     * mLogger.debug("Entry: "+i+", old="+o2+", new="+o1); } }
                     */
                }
                else
                {
                    mLogger.debug("Setting new content for table with id " + getId());
                }
                table.endCellEditing();

                tm.setRows(newContent, false);
                tm.setContent(newContent);
            }
        }
    }

    public void setEditCellSelection(Gui4jJTable table, Pair pair)
    {
        if (pair != null)
        {
            int row = ((Integer) pair.getFirst()).intValue();
            int col = ((Integer) pair.getSecond()).intValue();
            table.editCellAt(row, col);
            table.editCellAt(row, col);
            Rectangle rect = table.getCellRect(row, col, true);
            table.scrollRectToVisible(rect);
        }
    }

    public boolean setCellSelectionPair(Gui4jJTable table, Pair pair)
    {
        if (pair != null)
        {
            int row = ((Integer) pair.getFirst()).intValue();
            int col = ((Integer) pair.getSecond()).intValue();
            if (row >= 0 && row < table.getRowCount() && col >= 0 && col < table.getColumnCount())
            {
                table.setRowSelectionInterval(row, row);
                table.setColumnSelectionInterval(col, col);
                Rectangle rect = table.getCellRect(row, col, true);
                table.scrollRectToVisible(rect);
                return true;
            }
        }
        return false;
    }

    public boolean setRowSelectionIndex(Gui4jJTable table, int row)
    {
        if (row >= 0 && row < table.getRowCount())
        {
            table.setRowSelectionInterval(row, row);
            Rectangle rect = table.getCellRect(row, 0, true);
            table.scrollRectToVisible(rect);
            return true;
        }
        return false;
    }

    public boolean setRowSelectionItem(Gui4jJTable table, Object item)
    {
        Gui4jTableModel model = (Gui4jTableModel) table.getModel();
        int row = model.mContent.indexOf(item);
        if (row >= 0 && row < table.getRowCount())
        {
            table.setRowSelectionInterval(row, row);
            Rectangle rect = table.getCellRect(row, 0, true);
            table.scrollRectToVisible(rect);
            return true;
        }
        return false;
    }

    public void addColumn(Class contentClass, Gui4jColumnTable column)
    {
        mColumnManager.addColumn(contentClass, column);
    }

    public void addRowHeaderName(Class type, Gui4jCall gui4jCall)
    {
        mRowManager.add(type, gui4jCall);
    }

    public boolean hasRowHeaderName(Class type)
    {
        return mRowManager.get(type) != null ? true : false;
    }

    public void addColumnGroup(Gui4jColumnHeaderTable columnGroup)
    {
        mColumnGroups.add(columnGroup);
    }

    public void setSelectionMode(Gui4jJTable table, int selectionMode)
    {
        table.setSelectionMode(selectionMode);
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

    /**
     * Sets the onDoubleClick.
     * 
     * @param typeClass
     * @param onDoubleClick
     *            The onDoubleClick to set
     */
    public void setOnDoubleClick(Class typeClass, Gui4jCall onDoubleClick)
    {
        if (onDoubleClick != null)
        {
            mOnDoubleClick.add(typeClass, onDoubleClick);
        }
    }

    /**
     * Sets the onCellSelect.
     * 
     * @param typeClass
     * @param onCellSelect
     *            The onCellSelect to set
     */
    public void setOnCellSelect(Class typeClass, Gui4jCall onCellSelect)
    {
        if (onCellSelect != null)
        {
            mOnCellSelect.add(typeClass, onCellSelect);
        }
    }

    /**
     * Sets the onColSelect.
     * 
     * @param typeClass
     * @param onColSelect
     *            The onColSelect to set
     */
    public void setOnColSelect(Class typeClass, Gui4jCall onColSelect)
    {
        if (onColSelect != null)
        {
            mOnColSelect.add(typeClass, onColSelect);
        }
    }

    public void setRownames(Gui4jComponentInstance gui4jComponentInstance, Collection rows)
    {
        Gui4jJTable jTable = (Gui4jJTable) gui4jComponentInstance.getSwingComponent();
        jTable.endCellEditing();
        Gui4jTableModel model = (Gui4jTableModel) jTable.getModel();
        model.setRows(new ArrayList(rows), true);
    }

    public void setNames(Gui4jComponentInstance gui4jComponentInstance, Collection rows)
    {
        // nothing todo
    }

    /**
     * @see org.gui4j.core.Gui4jComponent#dispose(Gui4jComponentInstance)
     */
    public void dispose(Gui4jComponentInstance gui4jComponentInstance)
    {
        super.dispose(gui4jComponentInstance);
        Gui4jJTable jTable = (Gui4jJTable) gui4jComponentInstance.getSwingComponent();
        Gui4jTableModel tableModel = (Gui4jTableModel) jTable.getModel();
        tableModel.dispose();
    }

    // **************************************************************************

    public class Gui4jColumnTable implements Serializable
    {
        private final Gui4jCall mColumnName;
        private Gui4jCall mColumnValue;
        private final Gui4jCall mColumnSetValue;
        private final Gui4jCall mEnabled;
        private final Gui4jCall mCharacters;
        private final Gui4jCall mMaxCharacters;
        private final Gui4jCall mList;
        private final Gui4jCall mListItem;
        private final Gui4jCall mListNullItem;
        private final Gui4jCall mListEditable;
        private final Gui4jCall mListIndicator;
        private final Gui4jCall mStringConvert;
        private Gui4jCall mTooltip;
        private final Gui4jThreadManager mGui4jThreadManager;
        private final Gui4jTextAttribute mGui4jTextAttribute;

        private double mWeight;

        public Gui4jColumnTable(Gui4jCall columnName, Gui4jCall getValue, Gui4jCall setValue, Gui4jCall enabled,
                Gui4jCall characters, Gui4jCall maxCharacters, Gui4jCall list, Gui4jCall listItem,
                Gui4jCall listNullItem, Gui4jCall listEditable, Gui4jCall listIndicator, Gui4jCall stringConvert,
                Gui4jCall tooltip, Gui4jTextAttribute gui4jTextAttribute)
        {
            mColumnName = columnName;
            mColumnValue = getValue;
            mColumnSetValue = setValue;
            mEnabled = enabled;
            mCharacters = characters;
            mMaxCharacters = maxCharacters;
            mList = list;
            mListItem = listItem;
            mListNullItem = listNullItem;
            mListEditable = listEditable;
            mListIndicator = listIndicator;
            mStringConvert = stringConvert;
            mTooltip = tooltip;
            mGui4jThreadManager = getGui4j().getGui4jThreadManager();
            mGui4jTextAttribute = gui4jTextAttribute;
        }

        public boolean isMainColumn()
        {
            return true;
        }

        public String getName(Gui4jCallBase gui4jController)
        {
            return (String) mColumnName.getValueNoParams(gui4jController, "");
        }

        public boolean isCharactersAttributeDefined()
        {
            return mCharacters != null;
        }

        public boolean isMaxCharactersAttributeDefined()
        {
            return mMaxCharacters != null;
        }

        public int getCharacters(Gui4jCallBase gui4jCallBase)
        {
            assert mCharacters != null;
            Integer i = (Integer) mCharacters.getValueNoParams(gui4jCallBase, new Integer(5));
            assert i != null;
            return i.intValue();
        }

        public int getMaxCharacters(Gui4jCallBase gui4jCallBase)
        {
            assert mMaxCharacters != null;
            Integer i = (Integer) mMaxCharacters.getValueNoParams(gui4jCallBase, new Integer(5));
            return i.intValue();
        }

        public String getTooltip(Gui4jCallBase gui4jCallBase, Object rowInstance, int row)
        {
            if (mTooltip != null)
            {
                Map paramMap = new HashMap();
                assert rowInstance != null;
                paramMap.put(Const.PARAM_ITEM, rowInstance);
                paramMap.put(Const.PARAM_ROW_INDEX, new Integer(row));
                if (mList != null)
                {
                    Collection collection = (Collection) mList.getValue(gui4jCallBase, paramMap, null);
                    assert collection != null;
                    paramMap.put(Const.PARAM_LIST, collection);
                }
                if (mListItem != null)
                {
                    Object item = mListItem.getValue(gui4jCallBase, paramMap, null);
                    paramMap.put(Const.PARAM_LIST_ITEM, item);
                }
                try
                {
                    return (String) mTooltip.getValue(gui4jCallBase, paramMap, null);
                }
                catch (Throwable t)
                {
                    getGui4j().handleException(gui4jCallBase, t, null);
                    mTooltip = null;
                    return null;
                }
            }
            else
            {
                return null;
            }
        }

        public Object getValue(Gui4jCallBase gui4jCallBase, Object rowInstance, int row)
        {
            if (mColumnValue != null)
            {
                Map paramMap = new HashMap();
                assert rowInstance != null;
                paramMap.put(Const.PARAM_ITEM, rowInstance);
                paramMap.put(Const.PARAM_ROW_INDEX, new Integer(row));
                if (mList != null)
                {
                    Collection collection = (Collection) mList.getValue(gui4jCallBase, paramMap, null);
                    assert collection != null;
                    paramMap.put(Const.PARAM_LIST, collection);
                }
                if (mListItem != null)
                {
                    Object item = mListItem.getValue(gui4jCallBase, paramMap, null);
                    paramMap.put(Const.PARAM_LIST_ITEM, item);
                }
                try
                {
                    return mColumnValue.getValue(gui4jCallBase, paramMap, null);
                }
                catch (Throwable t)
                {
                    getGui4j().handleException(gui4jCallBase, t, null);
                    mColumnValue = null;
                    return null;
                }
            }
            else
            {
                return null;
            }
        }

        public void setValue(Gui4jCallBase gui4jController, Object rowInstance, Object value, int row, int col,
                Gui4jTableModel model)
        {
            if (mColumnSetValue != null)
            {
                if (value instanceof ComboBoxNullItem)
                {
                    value = null;
                }

                Map paramMap = new HashMap();
                paramMap.put(Const.PARAM_ROW_INDEX, new Integer(row));
                paramMap.put(Const.PARAM_ITEM, rowInstance);
                paramMap.put(Const.PARAM_VALUE, value);

                if (mList != null)
                {
                    Collection collection = (Collection) mList.getValue(gui4jController, paramMap, null);
                    assert collection != null;
                    paramMap.put(Const.PARAM_LIST, collection);
                }

                Gui4jGetValue[] work;
                if (mOnSetValue != null)
                {
                    work = new Gui4jGetValue[] { mColumnSetValue, new Gui4jRefreshTable(model, row, col),
                            model.getAutomaticRefresh(), mOnSetValue };
                }
                else
                {
                    work = new Gui4jGetValue[] { mColumnSetValue, new Gui4jRefreshTable(model, row, col),
                            model.getAutomaticRefresh() };
                }
                mGui4jThreadManager.performWork(gui4jController, work, paramMap);
            }
        }

        public boolean isEnabled(Gui4jCallBase gui4jController, Object rowContent)
        {
            if (mEnabled == null)
            {
                return true;
            }
            else
            {
                Map paramMap = new Gui4jMap1(Const.PARAM_ITEM, rowContent);
                return Boolean.TRUE.equals(mEnabled.getValue(gui4jController, paramMap, Boolean.TRUE));
            }
        }

        public boolean hasSetter(Gui4jCallBase gui4jController, Object rowContent)
        {
            return (mColumnSetValue != null || mList != null) && isEnabled(gui4jController, rowContent);
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

        public ComboBoxCellEdit createEditor(Gui4jCallBase gui4jController, Font font)
        {
            if (mList != null)
            {
                if (mGui4jTextAttribute != null && mGui4jTextAttribute.getFont() != null)
                {
                    font = (Font) mGui4jTextAttribute.getFont().getValueNoParams(gui4jController, font);
                }
                ComboBoxCellEdit cellEdit = ComboBoxCellEdit.getInstance(gui4jController, mColumnValue, mStringConvert,
                        Const.PARAM_LIST_ITEM, font);
                if (mListEditable != null)
                {
                    Boolean editable = (Boolean) mListEditable.getValueNoParams(gui4jController, null);
                    cellEdit.setEditable(Boolean.TRUE.equals(editable));
                }
                return cellEdit;
            }
            return null;
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

        public Icon getListIndicator(Gui4jCallBase callBase)
        {
            if (mListIndicator == null)
            {
                return null;
            }
            return (Icon) mListIndicator.getValueNoParams(callBase, null);
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

        public void prepareEditor(Gui4jCallBase gui4jController, TableCellEditor editor, int row, Object rowElement)
        {
            if (mList != null)
            {
                ComboBoxCellEdit edit = (ComboBoxCellEdit) editor;
                Map m = new HashMap();
                m.put(Const.PARAM_ITEM, rowElement);
                m.put(Const.PARAM_ROW_INDEX, new Integer(row));
                Collection list = (Collection) mList.getValue(gui4jController, m, null);
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
                edit.setContent(list, nullItemText);
                m.put(Const.PARAM_LIST, list);
                Object listItem = mListItem.getValue(gui4jController, m, null);
                edit.setParamMap(m);
                edit.setSelectedItem(listItem);
            }
        }
    }

    // *******************************************************************************

    private final class ColumnManager implements Serializable
    {
        private final HashMap mTypeMap; // Class -> List(Gui4jColumnTable)
        private final List mMainColumns;
        // private final Class mDefaultType;

        private boolean mContainsSeveralTypes = false;

        public ColumnManager(Class defaultType)
        {
            mMainColumns = new ArrayList();
            mTypeMap = new HashMap();
            mTypeMap.put(defaultType, mMainColumns);
            // mDefaultType = defaultType;
        }

        private List getColumnsOfType(Class contentClass)
        {
            List l = (List) mTypeMap.get(contentClass);
            if (l == null)
            {
                mContainsSeveralTypes = true;
                l = new ArrayList();
                mTypeMap.put(contentClass, l);
            }
            return l;
        }

        public boolean containsSeveralTypes()
        {
            return mContainsSeveralTypes;
        }

        public void addColumn(Class contentClass, Gui4jColumnTable gui4jColumnTable)
        {
            getColumnsOfType(contentClass).add(gui4jColumnTable);
        }

        public List getMainColumns()
        {
            return mMainColumns;
        }

        public List getColumns(Class rowClass)
        {
            Class lastClass = null;
            List lastList = null;
            for (Iterator it = mTypeMap.entrySet().iterator(); it.hasNext();)
            {
                Map.Entry entry = (Map.Entry) it.next();
                Class c = (Class) entry.getKey();
                if (c.isAssignableFrom(rowClass))
                {
                    if (lastClass == null || lastClass.isAssignableFrom(c))
                    {
                        lastClass = c;
                        lastList = (List) entry.getValue();
                    }
                }
            }
            return lastList;
        }

        public int columnCount()
        {
            return mMainColumns.size();
        }

        public Gui4jColumnTable getGui4jColumnTable(Object row, int col)
        {
            if (row == null)
            {
                mLogger.warn("Table " + getId() + " has null instance in rows");
                return null;
            }
            List l = getColumns(row.getClass());
            return l != null && col < l.size() ? (Gui4jColumnTable) l.get(col) : null;
        }

        public Gui4jColumnTable getMainGui4jColumnTable(int col)
        {
            return (Gui4jColumnTable) mMainColumns.get(col);
        }
    }

    // *******************************************************************************

    public final class CellRenderer extends DefaultTableCellRenderer
    {
        private final int mCols;
        private final Color[] mForeground;
        private final Color[] mBackground;
        private final Color[] mEvenBackground;
        private final Font[] mFont;
        private final int[] mAlignment;
        private final Icon[] mListIndicators;
        private final Gui4jColumnTable[] mColumns;
        private final Enabled mEnabledInstance;
        private final Gui4jTableModel mTableModel;
        private final TableCellRenderer booleanCellRenderer;
        private final Gui4jCallBase mGui4jCallBase;

        public CellRenderer(Gui4jComponentInstance gui4jComponentInstance, List columns, Enabled enabledInstance)
        {
            mGui4jCallBase = gui4jComponentInstance.getGui4jCallBase();
            mTableModel = (Gui4jTableModel) ((Gui4jJTable) gui4jComponentInstance.getSwingComponent()).getModel();
            mCols = columns == null ? 0 : columns.size();
            mForeground = new Color[mCols];
            mBackground = new Color[mCols];
            mEvenBackground = new Color[mCols];
            mColumns = new Gui4jColumnTable[mCols];
            mFont = new Font[mCols];
            mAlignment = new int[mCols];
            mListIndicators = new Icon[mCols];
            mEnabledInstance = enabledInstance;

            booleanCellRenderer = new BooleanTableCellRenderer(noFocusBorder);

            for (int col = 0; col < mCols; col++)
            {
                Gui4jColumnTable tableColumn = (Gui4jColumnTable) columns.get(col);
                mColumns[col] = tableColumn;
                mAlignment[col] = -1;
                if (tableColumn != null && tableColumn.getGui4jTextAttribute() != null)
                {
                    Gui4jTextAttribute textAttribute = tableColumn.getGui4jTextAttribute();
                    Map nullMap = null;
                    if (textAttribute.getForeground() != null)
                    {
                        mForeground[col] = (Color) textAttribute.getForeground()
                                .getValue(mGui4jCallBase, nullMap, null);
                    }
                    if (textAttribute.getBackground() != null)
                    {
                        mBackground[col] = (Color) textAttribute.getBackground()
                                .getValue(mGui4jCallBase, nullMap, null);
                    }
                    if (textAttribute.getEvenBackground() != null)
                    {
                        mEvenBackground[col] = (Color) textAttribute.getEvenBackground().getValue(mGui4jCallBase,
                                nullMap, null);
                    }
                    if (textAttribute.getFont() != null)
                    {
                        mFont[col] = (Font) textAttribute.getFont().getValue(mGui4jCallBase, nullMap, null);
                    }
                    mAlignment[col] = textAttribute.getAlignment();
                }
                if (tableColumn != null)
                {
                    mListIndicators[col] = tableColumn.getListIndicator(mGui4jCallBase);
                }
            }
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column)
        {
            if (column >= mCols)
            {
                return this;
            }

            // determine colors to use
            Color foreground = mForeground[column];
            if (foreground == null)
            {
                foreground = table.getForeground();
            }
            Color background = mBackground[column];
            Color evenBackground = mEvenBackground[column];
            if (evenBackground != null && row % 2 == 0)
            {
                background = evenBackground;
            }
            if (background == null)
            {
                background = table.getBackground();
            }

            // get the renderer component for this cell
            JComponent c;
            if (value != null && value.getClass() == Boolean.class)
            {
                // special checkbox renderer for booleans
                BooleanTableCellRenderer renderer = (BooleanTableCellRenderer) booleanCellRenderer
                        .getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                renderer.setUnselectedBackground(background);
                c = renderer;
            }
            else
            {
                c = (JComponent) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }

            // set colors (this intentionally overrides strategies implemented
            // in the getTableCellRendererComponent() calls above, to not use
            // a special color for editable cells having the focus).
            if (isSelected)
            {
                c.setForeground(table.getSelectionForeground());
                c.setBackground(table.getSelectionBackground());
            }
            else
            {
                c.setForeground(foreground);
                if (!mTableModel.ignoreBackground)
                {
                    c.setBackground(background);
                }
            }

            // set tooltip
            Gui4jColumnTable columnTable = mColumns[column];
            String tooltip = columnTable.getTooltip(mGui4jCallBase, mTableModel.getRowInstance(row), row);
            if ("".equals(tooltip))
            {
                tooltip = null;
            }
            c.setToolTipText(tooltip);

            // handle text and icons
            if (c instanceof JLabel)
            {
                JLabel l = (JLabel) c;
                int hAlign = mAlignment[column];
                if (hAlign != -1)
                {
                    l.setHorizontalAlignment(hAlign);
                }
                else
                {
                    l.setHorizontalAlignment(SwingConstants.LEFT);
                }
                if (value != null && value instanceof Icon)
                {
                    l.setText("");
                    l.setIcon((Icon) value);
                    l.setHorizontalAlignment(SwingConstants.CENTER);
                }
                else if (mListIndicators[column] != null)
                {
                    l.setIcon(mListIndicators[column]);
                    l.setHorizontalTextPosition(JLabel.RIGHT);
                }
                else
                {
                    l.setIcon(null);
                }
            }

            // System.out.println(getId()
            // + ": Rendering ("
            // + row
            // + ", "
            // + column
            // + "): "
            // + value
            // + " -"
            // + (value == null ? "null" : value.getClass().toString())
            // + " isopaque = "
            // + c.isOpaque()
            // + " has icon: "
            // + ((c instanceof JLabel) && (((JLabel) c).getIcon() != null)));

            Font font = mFont[column];
            if (font != null)
            {
                c.setFont(font);
            }

            c.setEnabled(mEnabledInstance.isEnabled(row, column));

            if (mHideFocus)
            {
                c.setBorder(noFocusBorder);
            }

            return c;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.gui4j.core.Gui4jAbstractComponent#getPopupContext(org.gui4j.core.Gui4jComponentInstance,
     *      java.awt.event.MouseEvent)
     */
    protected Object getPopupContext(Gui4jComponentInstance gui4jComponentInstance, MouseEvent mouseEvent)
    {
        Gui4jCall call = null;
        Map paramMap = null;
        JTable table = (JTable) gui4jComponentInstance.getComponent();
        Gui4jTableModel model = (Gui4jTableModel) table.getModel();
        if (model.mPopupHandler != null)
        {
            paramMap = new HashMap();
            call = model.mPopupHandler.getGui4jCall(paramMap);
        }
        if (call != null)
        {
            return call.getValue(gui4jComponentInstance.getGui4jCallBase(), paramMap, null);
        }
        else
        {
            return null;
        }
    }

    // *************************************************************************************

    public interface Enabled
    {
        boolean isEnabled(int row, int column);
    }

    public class Gui4jTableModel extends RowHeaderAbstractTableModel implements Gui4jEventListener, Gui4jTableListener,
            Enabled, RowRetriever
    {

        private Gui4jSwingContainer mGui4jSwingContainer;
        private Gui4jCallBase mGui4jController;
        private Gui4jComponentInstance mGui4jComponentInstance;
        private Gui4jJTable mTable;

        private List mRows = new ArrayList();
        protected List mContent;
        private final Map mCellRenderers; // Class -> TableCellRenderer
        private final Map mCellEditors; // Class -> TableCellEditor
        private final List mListeners; // List(Gui4jMouseListenerTable)
        protected Gui4jMouseListenerTable mPopupHandler;

        protected boolean ignoreBackground = false; // Used by CellRenderer

        public Gui4jTableModel(Gui4jSwingContainer gui4jSwingContainer, Gui4jCallBase gui4jCallBase)
        {
            mGui4jSwingContainer = gui4jSwingContainer;
            mGui4jController = gui4jCallBase;
            mContent = new ArrayList();
            mCellRenderers = new HashMap();
            mCellEditors = new HashMap();
            mListeners = new ArrayList();
        }

        public Object getRowInstance(int row)
        {
            if (row >= mContent.size())
            {
                mLogger
                        .warn("Index of requested table row out of bounds (table content was presumably changed in the meantime): "
                                + row);
                return null;
            }
            else
            {
                return mContent.get(row);
            }
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

        public void setIgnoreBackground(boolean flag)
        {
            if (ignoreBackground != flag)
            {
                ignoreBackground = flag;
                if (mGui4jComponentInstance != null)
                {
                    mGui4jComponentInstance.getSwingComponent().repaint();
                }
            }
        }

        public boolean ignoreBackground()
        {
            return ignoreBackground;
        }

        public boolean setSelection()
        {
            Gui4jJTable table = (Gui4jJTable) mGui4jComponentInstance.getSwingComponent();
            if (mCellSelectionPair != null)
            {
                Pair pair = (Pair) mCellSelectionPair.getValueNoParams(mGui4jController, null);
                if (!setCellSelectionPair(table, pair))
                {
                    table.clearSelection();
                }
            }
            else if (mRowSelectionItem != null)
            {
                Object item = mRowSelectionItem.getValueNoParams(mGui4jController, null);
                if (!setRowSelectionItem(table, item))
                {
                    table.clearSelection();
                }
            }
            else if (mRowSelectionIndex != null)
            {
                Integer i = (Integer) mRowSelectionIndex.getValueNoParams(mGui4jController, null);
                int idx = i == null ? -1 : i.intValue();
                if (!setRowSelectionIndex(table, idx))
                {
                    table.clearSelection();
                }
            }
            else
            {
                return false;
            }
            return true;
        }

        public void addListener(Gui4jMouseListenerTable mouseListenerTable)
        {
            mGui4jComponentInstance.getGui4jSwingContainer().addDispose(mouseListenerTable);
            mListeners.add(mouseListenerTable);
        }

        public int getColumnCount()
        {
            return mColumnManager.columnCount();
        }

        public int getRowCount()
        {
            return mContent == null ? 0 : mContent.size();
        }

        public boolean isCellEditable(int row, int col)
        {
            if (handleReadOnly() && mGui4jSwingContainer.inReadOnlyMode())
            {
                return false;
            }
            else
            {
                Object rowContent = getRowInstance(row);
                Gui4jColumnTable column = mColumnManager.getGui4jColumnTable(rowContent, col);
                return column != null && column.hasSetter(mGui4jController, rowContent);
            }
        }

        public boolean isEnabled(int row, int col)
        {
            Object rowContent = getRowInstance(row);
            Gui4jColumnTable column = mColumnManager.getGui4jColumnTable(rowContent, col);
            return column == null || column.isEnabled(mGui4jController, rowContent);
        }

        public Object getValueAt(int row, int col)
        {
            Object rowElement = getRowInstance(row);
            Gui4jColumnTable column = mColumnManager.getGui4jColumnTable(rowElement, col);
            Object value = column == null ? null : column.getValue(mGui4jController, rowElement, row);
            return value;
        }

        public void setValueAt(Object value, int row, int col)
        {
            Object rowElement = getRowInstance(row);
            Gui4jColumnTable column = mColumnManager.getGui4jColumnTable(rowElement, col);
            column.setValue(mGui4jController, rowElement, value, row, col, this);
        }

        public Class getColumnClass(int columnIndex)
        {
            Gui4jColumnTable column = mColumnManager.getMainGui4jColumnTable(columnIndex);
            return column.getColumnClass();
        }

        public String getColumnName(int columnIndex)
        {
            Gui4jColumnTable column = mColumnManager.getMainGui4jColumnTable(columnIndex);
            String name = column.getName(mGui4jController);
            if (name == null || name.equals(""))
            {
                return " ";
            }
            return name;
        }

        public void setGui4jComponentInstance(Gui4jComponentInstance gui4jComponentInstance)
        {
            mGui4jComponentInstance = gui4jComponentInstance;
            mTable = (Gui4jJTable) mGui4jComponentInstance.getSwingComponent();
        }

        protected synchronized void setContent(ArrayList content)
        {
            if (mGui4jComponentInstance != null)
            {
                // Gui4jComponentInstance noch nicht zerstoert.
                Gui4jJTable table = (Gui4jJTable) mGui4jComponentInstance.getSwingComponent();
                // Deaktiviere listeners
                for (Iterator it = mListeners.iterator(); it.hasNext();)
                {
                    Gui4jMouseListenerTable mouseListenerTable = (Gui4jMouseListenerTable) it.next();
                    mouseListenerTable.setActive(false);
                }
                int selectedRow = table.getSelectedRow();
                Object selectedRowItem = null;
                if (selectedRow >= 0 && selectedRow < mContent.size())
                {
                    selectedRowItem = mContent.get(selectedRow);
                }
                int selectedCol = table.getSelectedColumn();
                mContent = content;
                /*
                 * mContent.clear(); if (!mUseOriginalCollection) {
                 * mContent.addAll(content); } else { mContent = (List) content; }
                 */
                fireTableDataChanged();
                // Aktiviere Listeners
                for (Iterator it = mListeners.iterator(); it.hasNext();)
                {
                    Gui4jMouseListenerTable mouseListenerTable = (Gui4jMouseListenerTable) it.next();
                    mouseListenerTable.setActive(true);
                }
                // Setze Selektion
                if (!setSelection())
                {
                    if (selectedRowItem != null)
                    {
                        int oldSelectedRow = selectedRow;
                        selectedRow = mContent.indexOf(selectedRowItem);
                        if (selectedRow != -1 && mRowSelectionAllowed)
                        {
                            setRowSelectionItem(table, selectedRowItem);
                        }
                        if (selectedRow == -1 && oldSelectedRow != -1) {
                            selectedRow = oldSelectedRow;
                        }
                        if (selectedRow != -1 && selectedCol != -1 && !mRowSelectionAllowed)
                        {
                            setCellSelectionPair(table, new Pair(new Integer(selectedRow), new Integer(selectedCol)));
                        }
                    }
                }
            }
        }

        protected void setRows(ArrayList rows, boolean refresh)
        {
            if (rows != null)
            {
                mRows = rows;
            }
            else
            {
                mRows = new ArrayList();
            }
            refreshRows(refresh);
        }

        public Object getRow(int row)
        {
            return getRowInstance(row);
        }

        public void eventOccured()
        {
            Runnable run = new Runnable() {
                public void run()
                {
                    if (mContent != null)
                    {
                        mCellRenderers.clear();
                        fireTableRowsUpdated(0, mContent.size() - 1);
                    }
                }
            };
            Gui4jThreadManager.executeInSwingThreadAndWait(run);
        }

        public void dispose()
        {
            mCellRenderers.clear();
            mCellEditors.clear();
            if (mContent != null)
            {
                mContent.clear();
                mContent = null;
            }
            mGui4jSwingContainer = null;
            mGui4jController = null;
            mGui4jComponentInstance = null;
            mListeners.clear();
        }

        /**
         * @see org.gui4j.core.swing.Gui4jTableListener#prepareEditor(TableCellEditor,
         *      int, int)
         */
        public void prepareEditor(TableCellEditor editor, int row, int column)
        {
            Object rowElement = getRowInstance(row);
            Gui4jColumnTable columnTable = mColumnManager.getGui4jColumnTable(rowElement, column);
            columnTable.prepareEditor(mGui4jController, editor, row, rowElement);
        }

        /**
         * @see org.gui4j.core.swing.Gui4jTableListener#getCellEditor(TableCellEditor,
         *      int, int)
         */
        public TableCellEditor getCellEditor(TableCellEditor editor, int row, int column)
        {
            Object rowElement = getRowInstance(row);
            Class rowClass = rowElement.getClass();
            if (!mColumnManager.containsSeveralTypes() || mDefaultType.isAssignableFrom(rowClass))
            {
                return editor;
            }
            else
            {
                String key = rowClass + "#" + column + "#";
                TableCellEditor editorSpecial = (TableCellEditor) mCellEditors.get(key);
                if (editorSpecial == null)
                {
                    Gui4jColumnTable columnTable = mColumnManager.getGui4jColumnTable(rowElement, column);
                    if (columnTable != null)
                    {
                        editorSpecial = columnTable.createEditor(mGui4jController, mGui4jComponentInstance
                                .getSwingComponent().getFont());
                    }
                    if (editorSpecial == null)
                    {
                        editorSpecial = editor;
                    }
                }
                mCellEditors.put(key, editorSpecial);
                return editorSpecial;
            }
        }

        /**
         * @see org.gui4j.core.swing.Gui4jTableListener#getCellRenderer(TableCellRenderer,
         *      int, int)
         */
        public TableCellRenderer getCellRenderer(TableCellRenderer renderer, int row, int column)
        {
            if (!mColumnManager.containsSeveralTypes() /*
                                                         * ||
                                                         * mDefaultType.isAssignableFrom(rowClass)
                                                         */
            )
            {
                return renderer;
            }
            else
            {
                Object rowElement = getRowInstance(row);
                if (rowElement == null)
                {
                    mLogger.warn("Table: " + getId() + " has null instance in rows at index " + row);
                    return renderer;
                }
                Class rowClass = rowElement.getClass();
                TableCellRenderer rendererSpecial = (TableCellRenderer) mCellRenderers.get(rowClass);
                if (rendererSpecial == null)
                {
                    rendererSpecial = new CellRenderer(mGui4jComponentInstance, mColumnManager.getColumns(rowClass),
                            this);
                    mCellRenderers.put(rowClass, rendererSpecial);
                }
                return rendererSpecial;
            }
        }

        /*
         * @see de.bea.gui4j.swingcomponents.RowHeaderTableModel#getRowName(int)
         */
        public String getRowName(int row)
        {
            Object o = mRows.get(row);
            Gui4jCall rowHeaderName = (Gui4jCall) mRowManager.get(o.getClass());
            if (rowHeaderName == null)
            {
                return "null";
            }
            Map m = new HashMap();
            m.put(Const.PARAM_ITEM, o);
            return (String) rowHeaderName.getValue(mGui4jController, m, "");
        }

        /*
         * @see de.bea.gui4j.swingcomponents.RowHeaderAbstractTableModel#setRowName(int,
         *      java.lang.String)
         */
        public void setRowName(int rowNumber, String newName)
        {
            // nothing todo

        }

        /*
         * @see de.bea.gui4j.swingcomponents.RowHeaderTableModel#setRowName(int,
         *      java.lang.Object)
         */
        public void setRowName(int rowNumber, Object newName)
        {
            // nothing todo

        }

        protected void refreshRows(boolean refresh)
        {
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

                    if (mRowHeaderCharacters != null)
                    {
                        int rowHeaderWidth = ((Integer) mRowHeaderCharacters.getValueNoParams(mGui4jController, null))
                                .intValue();
                        preferredWidth = ctable.getFontMetrics(font).stringWidth(StringUtil.copy('M', rowHeaderWidth));
                    }

                    ctable.setRowHeaderWidth(preferredWidth);
                    ctable.setRowHeaderFont(font);
                    ctable.setRowHeaderHeight(mTable.getRowHeight());
                    ctable.refreshRowHeaders();
                }
            }

            if (refresh)
            {
                int selectedCol = mTable.getSelectedColumn();
                int selectedRow = mTable.getSelectedRow();
                mCellRenderers.clear();
                fireTableDataChanged();
                if (selectedCol != -1 && selectedRow != -1
                        && selectedRow < getRowCount()
                        && selectedCol < getColumnCount())
                {
                    mTable.setRowSelectionInterval(selectedRow, selectedRow);
                    mTable.setColumnSelectionInterval(selectedCol, selectedCol);
                }
            }
        }

    }

    // *************************************************************************************

    public class Gui4jColumnHeaderTable implements Serializable
    {
        private Gui4jCall mName;
        private List mColumns;

        public Gui4jColumnHeaderTable(Gui4jCall name)
        {
            mName = name;
            mColumns = new ArrayList();
        }

        public void addColumn(Object o)
        {
            assert o instanceof Gui4jColumnHeaderTable || o instanceof Gui4jColumnTable;
            mColumns.add(o);
        }

        public List getColumns()
        {
            return mColumns;
        }

        public String getName(Gui4jCallBase gui4jController)
        {
            return (String) mName.getValueNoParams(gui4jController, "");
        }
    }

    // *************************************************************************************
    // ROW
    // *************************************************************************************
    public final class Gui4jRow implements Serializable
    {
        private final Gui4jCall mRowName;
        private final Gui4jTextAttribute mGui4jTextAttribute;

        // private final Class mClassType;

        public Gui4jRow(Class classType, Gui4jCall rowName, Gui4jTextAttribute gui4jTextAttribute)
        {
            mRowName = rowName;
            mGui4jTextAttribute = gui4jTextAttribute;
            // mClassType = classType;
        }

        public Gui4jRow copy(Class newClassType)
        {
            return new Gui4jRow(newClassType, mRowName, mGui4jTextAttribute);
        }

        public Gui4jTextAttribute getGui4jTextAttribute()
        {
            return mGui4jTextAttribute;
        }
    }

}