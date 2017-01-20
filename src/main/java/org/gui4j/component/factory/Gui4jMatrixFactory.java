package org.gui4j.component.factory;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.LElement;
import org.gui4j.component.Gui4jJComponent;
import org.gui4j.component.Gui4jMatrix;
import org.gui4j.core.Gui4jCall;
import org.gui4j.core.Gui4jComponentContainer;
import org.gui4j.core.Gui4jComponentContainerManager;
import org.gui4j.core.Gui4jTextAttribute;
import org.gui4j.core.definition.Attribute;
import org.gui4j.core.definition.AttributeTypeAlias;
import org.gui4j.core.definition.AttributeTypeEnumeration;
import org.gui4j.core.definition.AttributeTypeFloatingPoint;
import org.gui4j.core.definition.AttributeTypeInteger;
import org.gui4j.core.definition.AttributeTypeMethodCall;
import org.gui4j.core.definition.Param;
import org.gui4j.exception.Gui4jUncheckedException;
import org.gui4j.util.Filter;
import org.gui4j.util.Pair;

public final class Gui4jMatrixFactory extends Gui4jJComponentFactory
{
    private static final Log mLogger = LogFactory.getLog(Gui4jMatrixFactory.class);
    private static final String NAME = "matrix";
    private static final String ELEMENT_CELL = "matrixCell";
    private static final String ELEMENT_COLUMN = "matrixCol";
    private static final String ELEMENT_ROW = "matrixRow";
    private static final String VISIBLEROWS = "visibleRows";
    private static final String HEADERLINES = "headerLines";
    private static final String COLS = "columns";
    private static final String ROWS = "rows";
    private static final String CONTENT = "content";
    private static final String VALUE = "value";
    private static final String SETVALUE = "setValue";
    private static final String NOTIFY_TEMP_VALUE = "notifyTempValue";
    private static final String REFRESH = "refresh";
    private static final String AUTOMATIC_REFRESH = "automaticRefresh";
    private static final String ONSETVALUE = "onSetValue";
    private static final String WEIGHT = "weight";
    private static final String CHARACTERS = "characters";
    private static final String MAXCHARACTERS = "maxCharacters";
    private static final String USECACHE = "useCache";
    private static final String REORDERINGALLOWED = "reorderingAllowed";
    private static final String ROWSELECTIONALLOWED = "rowSelectionAllowed";
    private static final String ROWSELECTIONMODE = "rowSelectionMode";
    private static final String COLSELECTIONMODE = "colSelectionMode";
    private static final String CELLSELECTIONPAIR = "cellSelectionPair";
    private static final String ROWHEADERS = "rowHeaders";
    private static final String COLUMNHEADERS = "columnHeaders";
    private static final String LIST = "list";
    private static final String LISTTYPE = "listType";
    private static final String LISTITEM = "listItem";
    private static final String LISTNULLITEM = "listNullItem";
    private static final String LISTEDITABLE = "listEditable";
    private static final String STRINGCONVERT = "stringConvert";
    private static final String COLUMN_TYPE = "columnType";
    private static final String ROW_TYPE = "rowType";
    private static final String COLUMN_NAME = "columnName";
    private static final String ROW_NAME = "rowName";
    private static final String RESIZEMODE = "resizeMode";
    private static final String ONCELLSELECT = "onCellSelect";
    private static final String ONCELLCLICK = "onCellClick";
    private static final String ONCELLDBLCLICK = "onCellDblClick";
    private static final String BACKGROUND_HEADER = "headerBackground";
    private static final String TOOLTIP = "tooltip";
    private static final String ALIGNMENT = Gui4jTextAttribute.ALIGNMENT;
    private static final String FOREGROUND = Gui4jTextAttribute.FOREGROUND;
    private static final String BACKGROUND = Gui4jTextAttribute.BACKGROUND;
    private static final String EVEN_BACKGROUND = Gui4jTextAttribute.EVEN_BACKGROUND;
    private static final String FONT = Gui4jTextAttribute.FONT;
    private static final String POPUP_CONTEXT = "popupContext";
    private static final String AUTO_EXTEND_COMBOBOX = "autoExtend";
    /*
     * L.B.:
     * maximumRowCount is the maximum number of items the combo box can display without a scrollbar.
     * it's default value in swing is 8.
     */
    private static final String MAXIMUMROWCOUNT = "maximumRowCount";
    private static final int MAXIMUMROWCOUNTDEFAULT = 8;

    private static final Map mResizeMode;

    static
    {
        mResizeMode = new HashMap();
        mResizeMode.put("off", new Integer(JTable.AUTO_RESIZE_OFF));
        mResizeMode.put("nextColumn", new Integer(JTable.AUTO_RESIZE_NEXT_COLUMN));
        mResizeMode.put("subsequentColumns", new Integer(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS));
        mResizeMode.put("lastColumn", new Integer(JTable.AUTO_RESIZE_LAST_COLUMN));
        mResizeMode.put("allColumns", new Integer(JTable.AUTO_RESIZE_ALL_COLUMNS));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.gui4j.component.factory.Gui4jJComponentFactory#defineGui4jJComponentBy(org.gui4j.core.Gui4jComponentContainer,
     *      java.lang.String, org.dom4j.LElement)
     */
    protected Gui4jJComponent defineGui4jJComponentBy(Gui4jComponentContainer gui4jComponentContainer, String id,
            LElement e)
    {
        int visibleRows = getIntValue(gui4jComponentContainer, e, VISIBLEROWS, -1);
        int headerLines = getIntValue(gui4jComponentContainer, e, HEADERLINES, 1);

        boolean useRowHeaders = gui4jComponentContainer.getBooleanAttrValue(e, ROWHEADERS, false);
        boolean useCache = gui4jComponentContainer.getBooleanAttrValue(e, USECACHE, false);
        boolean useColumnHeaders = gui4jComponentContainer.getBooleanAttrValue(e, COLUMNHEADERS, true);
        boolean automaticRefresh = gui4jComponentContainer.getBooleanAttrValue(e, AUTOMATIC_REFRESH, true);

        String resizeModeStr = gui4jComponentContainer.getAttrValue(e, RESIZEMODE);
        int resizeMode = JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS;
        if (resizeModeStr != null)
        {
            Integer val = (Integer) mResizeMode.get(resizeModeStr);
            if (val != null)
            {
                resizeMode = val.intValue();
            }
            else
            {
                mLogger.warn("ResizeMode " + resizeModeStr + " is not defined");
            }
        }

        Gui4jMatrix gui4jMatrix = new Gui4jMatrix(gui4jComponentContainer, id, visibleRows, headerLines, useCache,
                useRowHeaders, useColumnHeaders, automaticRefresh, resizeMode);

        gui4jMatrix.setHeaderBackground(getGui4jAccessInstance(Color.class, gui4jMatrix, e, BACKGROUND_HEADER));

        {
            String reorderingAllowed = gui4jComponentContainer.getAttrValue(e, REORDERINGALLOWED);
            if (reorderingAllowed != null)
            {
                gui4jMatrix.setReorderingAllowed(reorderingAllowed.equalsIgnoreCase("true"));
            }
        }
        {
            String rowSelectionAllowed = gui4jComponentContainer.getAttrValue(e, ROWSELECTIONALLOWED);
            if (rowSelectionAllowed != null)
            {
                gui4jMatrix.setRowSelectionAllowed(rowSelectionAllowed.equalsIgnoreCase("true"));
            }
        }
        {
            Map m = null;
            Gui4jCall refresh = getGui4jAccessInstance(null, m, gui4jMatrix, e, REFRESH);
            if (refresh != null)
            {
                Gui4jCall[] dependantProperties = refresh.getDependantProperties();
                if (dependantProperties == null || dependantProperties.length == 0)
                {
                    mLogger.warn("Set of dependant events is empty");
                }
                gui4jMatrix.setRefresh(dependantProperties);
            }
        }
        {
            Map m = null;
            gui4jMatrix.setOnSetValue(getGui4jAccessInstance(null, m, gui4jMatrix, e, ONSETVALUE));
        }
        {
            String listSelectionMode = gui4jComponentContainer.getAttrValue(e, ROWSELECTIONMODE);
            if (listSelectionMode != null)
            {
                gui4jMatrix.setRowSelectionMode(Gui4jListFactory.getSelectionMode(listSelectionMode));
            }
        }
        {
            String listSelectionMode = gui4jComponentContainer.getAttrValue(e, COLSELECTIONMODE);
            if (listSelectionMode != null)
            {
                gui4jMatrix.setColSelectionMode(Gui4jListFactory.getSelectionMode(listSelectionMode));
            }
        }
        gui4jMatrix.definePropertySetter(CELLSELECTIONPAIR, getGui4jAccessInstance(Pair.class, gui4jMatrix, e,
                CELLSELECTIONPAIR));
        gui4jMatrix.definePropertySetter(ROWS, getGui4jAccessInstance(Collection.class, gui4jMatrix, e, ROWS));
        gui4jMatrix.definePropertySetter(COLS, getGui4jAccessInstance(Collection.class, gui4jMatrix, e, COLS));
        gui4jMatrix.definePropertySetter(CONTENT, getGui4jAccessInstance(Pair.class, gui4jMatrix, e, CONTENT));
        List errorList = new ArrayList();
        for (Iterator it = e.elements().iterator(); it.hasNext();)
        {
            try
            {
                LElement child = (LElement) it.next();
                gui4jComponentContainer.autoExtend(child);
                String elemName = child.getName();
                if (elemName.equals(ELEMENT_COLUMN))
                {
                    String columnTypeAliasName = gui4jComponentContainer.getAttrValue(child, COLUMN_TYPE);
                    Class columnTypeClazz = gui4jComponentContainer.getClassForAliasName(columnTypeAliasName);

                    if (columnTypeAliasName != null && columnTypeClazz == null)
                    {
                        Object[] args = { columnTypeAliasName };
                        throw new Gui4jUncheckedException.ResourceError(gui4jComponentContainer
                                .getConfigurationName(), Gui4jComponentContainerManager.getLineNumber(child
                                .attribute(COLUMN_TYPE)), RESOURCE_ERROR_alias_undefined, args);

                    }

                    Map nullMap = null;
                    Gui4jCall characters = getGui4jAccessInstance(Integer.TYPE, nullMap, gui4jMatrix, child, CHARACTERS);
                    Gui4jCall maxCharacters = getGui4jAccessInstance(Integer.TYPE, nullMap, gui4jMatrix, child, MAXCHARACTERS);
                    Gui4jCall columnName;
                    Gui4jTextAttribute textAttribute;
                    {
                        Map m = new HashMap();
                        m.put(Gui4jMatrix.PARAM_COL, Integer.TYPE);
                        m.put(Gui4jMatrix.PARAM_COLVALUE, columnTypeClazz);
                        columnName = getGui4jAccessInstance(String.class, m, gui4jMatrix, child, COLUMN_NAME);
                        textAttribute = Gui4jTextAttribute.getInstance(this, gui4jMatrix, child, m);
                    }

                    Gui4jMatrix.Gui4jCol gui4jCol = gui4jMatrix.new Gui4jCol(columnTypeClazz, columnName,
                            textAttribute, characters, maxCharacters);
                    gui4jCol.setWeight(getDoubleValue(gui4jComponentContainer, child, WEIGHT, 1.0));
                    gui4jMatrix.addGui4jCol(columnTypeClazz, gui4jCol);

                }
                if (elemName.equals(ELEMENT_ROW))
                {
                    String rowTypeAliasName = gui4jComponentContainer.getAttrValue(child, ROW_TYPE);
                    Class rowTypeClazz = gui4jComponentContainer.getClassForAliasName(rowTypeAliasName);

                    if (rowTypeAliasName != null && rowTypeClazz == null)
                    {
                        Object[] args = { rowTypeAliasName };
                        throw new Gui4jUncheckedException.ResourceError(gui4jComponentContainer
                                .getConfigurationName(), Gui4jComponentContainerManager.getLineNumber(child
                                .attribute(ROW_TYPE)), RESOURCE_ERROR_alias_undefined, args);

                    }

                    Gui4jCall rowName;
                    Gui4jTextAttribute textAttribute;
                    {
                        Map m = new HashMap();
                        m.put(Gui4jMatrix.PARAM_ROW, Integer.TYPE);
                        m.put(Gui4jMatrix.PARAM_ROWVALUE, rowTypeClazz);
                        rowName = getGui4jAccessInstance(String.class, m, gui4jMatrix, child, ROW_NAME);
                        textAttribute = Gui4jTextAttribute.getInstance(this, gui4jMatrix, child, m);
                    }

                    Gui4jMatrix.Gui4jRow gui4jRow = gui4jMatrix.new Gui4jRow(rowTypeClazz, rowName, textAttribute);
                    gui4jMatrix.addGui4jRow(rowTypeClazz, gui4jRow);
                }
                if (elemName.equals(ELEMENT_CELL))
                {
                    Gui4jCall list = null;
                    Gui4jCall listItem = null;
                    Gui4jCall listNullItem = null;
                    Gui4jCall listEditable = null;
                    Gui4jCall stringConvert = null;

                    String rowTypeAliasName = gui4jComponentContainer.getAttrValue(child, ROW_TYPE);
                    Class rowTypeClazz = gui4jComponentContainer.getClassForAliasName(rowTypeAliasName);

                    if (rowTypeAliasName != null && rowTypeClazz == null)
                    {
                        Object[] args = { rowTypeAliasName };
                        throw new Gui4jUncheckedException.ResourceError(gui4jComponentContainer
                                .getConfigurationName(), Gui4jComponentContainerManager.getLineNumber(child
                                .attribute(ROW_TYPE)), RESOURCE_ERROR_alias_undefined, args);

                    }

                    String columnTypeAliasName = gui4jComponentContainer.getAttrValue(child, COLUMN_TYPE);
                    Class columnTypeClazz = gui4jComponentContainer.getClassForAliasName(columnTypeAliasName);
                    if (columnTypeAliasName != null && columnTypeClazz == null)
                    {
                        Object[] args = { columnTypeAliasName };
                        throw new Gui4jUncheckedException.ResourceError(gui4jComponentContainer
                                .getConfigurationName(), Gui4jComponentContainerManager.getLineNumber(child
                                .attribute(COLUMN_TYPE)), RESOURCE_ERROR_alias_undefined, args);

                    }

                    Map m = new HashMap();
                    m.put(Gui4jMatrix.PARAM_COL, Integer.TYPE);
                    m.put(Gui4jMatrix.PARAM_ROW, Integer.TYPE);
                    m.put(Gui4jMatrix.PARAM_COLVALUE, columnTypeClazz);
                    m.put(Gui4jMatrix.PARAM_ROWVALUE, rowTypeClazz);

                    Gui4jCall onCellSelect = getGui4jAccessInstance(null, m, gui4jMatrix, child, ONCELLSELECT);
                    Gui4jCall onCellClick = getGui4jAccessInstance(null, m, gui4jMatrix, child, ONCELLCLICK);
                    Gui4jCall onCellDblClick = getGui4jAccessInstance(null, m, gui4jMatrix, child, ONCELLDBLCLICK);

                    String listTypeAliasName = gui4jComponentContainer.getAttrValue(child, LISTTYPE);
                    Class listTypeClazz = gui4jComponentContainer.getClassForAliasName(listTypeAliasName);
                    if (listTypeClazz != null)
                    {
                        list = getGui4jAccessInstance(Collection.class, m, gui4jMatrix, child, LIST);

                        if (list != null)
                        {
                            m.put(Gui4jMatrix.PARAM_LIST, list.getResultClass());
                            listItem = getGui4jAccessInstance(listTypeClazz, m, gui4jMatrix, child, LISTITEM);
                            if (listItem == null)
                            {
                                Object[] args = { child.getName(), LISTITEM };
                                throw new Gui4jUncheckedException.ResourceError(gui4jComponentContainer
                                        .getConfigurationName(), Gui4jComponentContainerManager.getLineNumber(child
                                        .attribute(LISTTYPE)), RESOURCE_ERROR_attribute_not_defined, args);
                            }
                            listNullItem = getGui4jAccessInstance(String.class, gui4jMatrix, child, LISTNULLITEM);
                            Map nullMap = null;
                            listEditable = getGui4jAccessInstance(Boolean.TYPE, nullMap, gui4jMatrix, child,
                                    LISTEDITABLE);
                            stringConvert = getGui4jAccessInstance(listTypeClazz, String.class, gui4jMatrix, child,
                                    STRINGCONVERT);
                            if (listEditable != null && stringConvert == null
                                    && !String.class.isAssignableFrom(listTypeClazz))
                            {
                                Object[] args = {};
                                throw new Gui4jUncheckedException.ResourceError(gui4jComponentContainer
                                        .getConfigurationName(), Gui4jComponentContainerManager.getLineNumber(child
                                        .attribute(LISTEDITABLE)), RESOURCE_ERROR_attribute_listEditable_defined, args);
                            }
                        }
                        else
                        {
                            Object[] args = { child.getName(), LIST };
                            throw new Gui4jUncheckedException.ResourceError(gui4jComponentContainer
                                    .getConfigurationName(), Gui4jComponentContainerManager.getLineNumber(child
                                    .attribute(LISTTYPE)), RESOURCE_ERROR_attribute_not_defined, args);
                        }
                    }

                    Gui4jCall value = null;
                    Gui4jCall tooltip = null;
                    Gui4jCall popupContext = null;
                    Gui4jCall setValue = null;
                    Gui4jCall enabled = null;

                    if (listTypeClazz != null && list != null && listItem != null)
                    {
                        m.put(Gui4jMatrix.PARAM_LIST, list.getResultClass());
                        m.put(Gui4jMatrix.PARAM_LIST_ITEM, listItem.getResultClass());
                    }

                    // display value
                    value = getGui4jAccessInstance(Object.class, m, gui4jMatrix, child, VALUE);
                    enabled = getGui4jAccessInstance(Boolean.TYPE, m, gui4jMatrix, child, ENABLED);
                    tooltip = getGui4jAccessInstance(String.class, m, gui4jMatrix, child, TOOLTIP);
                    popupContext = getGui4jAccessInstance(null, m, gui4jMatrix, child, POPUP_CONTEXT);

                    // edit value
                    if (value != null)
                    {
                        if (listTypeClazz != null && list != null && listItem != null)
                        {
                            m.put(Gui4jMatrix.PARAM_VALUE, listTypeClazz);
                        }
                        else
                        {
                            m.put(Gui4jMatrix.PARAM_VALUE, value.getResultClass());
                        }
                    }
                    else
                    {
                        m.put(Gui4jMatrix.PARAM_VALUE, Object.class);
                    }
                    m.remove(Gui4jMatrix.PARAM_LIST_ITEM);
                    Gui4jTextAttribute textAttribute = Gui4jTextAttribute.getInstance(this, gui4jMatrix, child, m);
                    setValue = getGui4jAccessInstance(null, m, gui4jMatrix, child, SETVALUE);
                    m.put(Gui4jMatrix.PARAM_VALUE, String.class);
                    Gui4jCall notifyTempValue = getGui4jAccessInstance(null, m, gui4jMatrix, child, NOTIFY_TEMP_VALUE);

                    Gui4jMatrix.Gui4jCell gui4jCell = gui4jMatrix.new Gui4jCell(rowTypeClazz, columnTypeClazz,
                            textAttribute, value, setValue, enabled, list, listItem, listNullItem, listEditable,
                            stringConvert, onCellSelect, onCellClick, onCellDblClick, tooltip, notifyTempValue, popupContext);
                    
                    {
                        String autoExtend = gui4jComponentContainer.getAttrValue(child, AUTO_EXTEND_COMBOBOX);
                        if (autoExtend != null)
                        {
                            gui4jCell.setAutoExtend(autoExtend.equalsIgnoreCase("true"));
                        }
                    }
                    
                    int maximumRowCount = getIntValue(gui4jComponentContainer, child, MAXIMUMROWCOUNT, MAXIMUMROWCOUNTDEFAULT);
                    gui4jCell.setMaximumRowCount(maximumRowCount);
                    
                    gui4jMatrix.addGui4jCell(gui4jCell);
                }
            }
            catch (Throwable t)
            {
                errorList.add(t);
            }
        }
        checkErrorList(errorList);
        return gui4jMatrix;
    }

    public void addToplevelAttributes(List attrList, Filter filter)
    {
        super.addToplevelAttributes(attrList, filter);
        if (filter == null || filter.takeIt(Gui4jMatrixFactory.class))
        {
            attrList.add(new Attribute(COLS, new AttributeTypeMethodCall(Collection.class), IMPLIED, false));
            attrList.add(new Attribute(ROWS, new AttributeTypeMethodCall(Collection.class), IMPLIED, false));
            attrList.add(new Attribute(CONTENT, new AttributeTypeMethodCall(Pair.class), IMPLIED, false));
            attrList
                    .add(new Attribute(CELLSELECTIONPAIR, new AttributeTypeMethodCall(Pair.class, true), IMPLIED, false));
            attrList.add(new Attribute(BACKGROUND_HEADER, new AttributeTypeMethodCall(Color.class), IMPLIED, false));
            attrList.add(new Attribute(VISIBLEROWS, new AttributeTypeInteger(), IMPLIED, false));
            attrList.add(new Attribute(HEADERLINES, new AttributeTypeInteger(), IMPLIED, false));
            attrList.add(new Attribute(REFRESH, new AttributeTypeMethodCall(null), IMPLIED, false));
            attrList.add(new Attribute(ONSETVALUE, new AttributeTypeMethodCall(null), IMPLIED, false));
            attrList.add(new Attribute(REORDERINGALLOWED, AttributeTypeEnumeration.getBooleanInstance(), false, false));
            attrList.add(new Attribute(ROWSELECTIONMODE, new AttributeTypeEnumeration(Gui4jListFactory
                    .listSelectionModeTypeParams()), IMPLIED, false));
            attrList.add(new Attribute(COLSELECTIONMODE, new AttributeTypeEnumeration(Gui4jListFactory
                    .listSelectionModeTypeParams()), IMPLIED, false));
            attrList.add(new Attribute(ROWSELECTIONALLOWED, AttributeTypeEnumeration.getBooleanInstance(), IMPLIED,
                    false));
            attrList.add(new Attribute(USECACHE, AttributeTypeEnumeration.getBooleanInstance(false), IMPLIED, false));
            attrList.add(new Attribute(ROWHEADERS, AttributeTypeEnumeration.getBooleanInstance(), IMPLIED, false));
            attrList.add(new Attribute(COLUMNHEADERS, AttributeTypeEnumeration.getBooleanInstance(), IMPLIED, false));
            attrList.add(new Attribute(RESIZEMODE, new AttributeTypeEnumeration(mResizeMode, "subsequentColumns"),
                    IMPLIED, false));
            attrList.add(new Attribute(AUTOMATIC_REFRESH, AttributeTypeEnumeration.getBooleanInstance(false), IMPLIED,
                    false));
        }
    }

    public void addInnerAttributes(String elementName, List list)
    {
        Set s = new HashSet();
        s.add(new Param("right"));
        s.add(new Param("left"));
        s.add(new Param("center"));
        if (elementName.equals(ELEMENT_CELL))
        {
            List paramListBasic = new ArrayList();
            paramListBasic.add(new Param(Gui4jMatrix.PARAM_COL, int.class));
            paramListBasic.add(new Param(Gui4jMatrix.PARAM_ROW, int.class));
            paramListBasic.add(new Param(Gui4jMatrix.PARAM_COLVALUE, COLUMN_TYPE));
            paramListBasic.add(new Param(Gui4jMatrix.PARAM_ROWVALUE, ROW_TYPE));
            List paramListWithValue = new ArrayList(paramListBasic);
            paramListWithValue.add(new Param(Gui4jMatrix.PARAM_VALUE, Object.class));

            Attribute[] attrs = { new Attribute(ROW_TYPE, new AttributeTypeAlias(), REQUIRED, false),
                    new Attribute(COLUMN_TYPE, new AttributeTypeAlias(), REQUIRED, false),
                    new Attribute(VALUE, new AttributeTypeMethodCall(Object.class, paramListBasic), REQUIRED, false),
                    new Attribute(TOOLTIP, new AttributeTypeMethodCall(String.class, paramListBasic), IMPLIED, false),
                    new Attribute(ENABLED, new AttributeTypeMethodCall(Boolean.TYPE, paramListBasic), IMPLIED, false),
                    new Attribute(SETVALUE, new AttributeTypeMethodCall(null, paramListWithValue), IMPLIED, false),
                    new Attribute(NOTIFY_TEMP_VALUE, new AttributeTypeMethodCall(null, paramListBasic), IMPLIED, false),
                    new Attribute(LIST, new AttributeTypeMethodCall(Collection.class), IMPLIED, false),
                    new Attribute(LISTTYPE, new AttributeTypeAlias(), IMPLIED, false),
                    new Attribute(LISTITEM, new AttributeTypeMethodCall(Object.class), IMPLIED, false),
                    new Attribute(LISTNULLITEM, new AttributeTypeMethodCall(String.class), IMPLIED, false),
                    new Attribute(LISTEDITABLE, new AttributeTypeMethodCall(Boolean.TYPE), IMPLIED, false),
                    new Attribute(STRINGCONVERT, new AttributeTypeMethodCall(Object.class), IMPLIED, false),
                    new Attribute(ONCELLCLICK, new AttributeTypeMethodCall(null, paramListBasic), IMPLIED, false),
                    new Attribute(ONCELLDBLCLICK, new AttributeTypeMethodCall(null, paramListBasic), IMPLIED, false),
                    new Attribute(ONCELLSELECT, new AttributeTypeMethodCall(null, paramListBasic), IMPLIED, false),
                    new Attribute(FOREGROUND, new AttributeTypeMethodCall(Color.class, paramListBasic), IMPLIED, false),
                    new Attribute(BACKGROUND, new AttributeTypeMethodCall(Color.class, paramListBasic), IMPLIED, false),
                    new Attribute(EVEN_BACKGROUND, new AttributeTypeMethodCall(Color.class), IMPLIED, false),
                    new Attribute(POPUP_CONTEXT, new AttributeTypeMethodCall(Object.class, paramListBasic), IMPLIED, false),
                    new Attribute(FONT, new AttributeTypeMethodCall(Font.class), IMPLIED, false),
                    new Attribute(ALIGNMENT, new AttributeTypeEnumeration(s), IMPLIED, true),
                    new Attribute(AUTO_EXTEND_COMBOBOX, AttributeTypeEnumeration.getBooleanInstance(), IMPLIED, false),
                    new Attribute(MAXIMUMROWCOUNT, new AttributeTypeInteger(), IMPLIED, false)};
                    
            Attribute[] aa = attrs;
            list.addAll(Arrays.asList(aa));
            return;
        }
        if (elementName.equals(ELEMENT_COLUMN))
        {
            List paramList = new ArrayList();
            paramList.add(new Param(Gui4jMatrix.PARAM_COL, int.class));
            paramList.add(new Param(Gui4jMatrix.PARAM_COLVALUE, COLUMN_TYPE));

            Attribute[] attrs = { new Attribute(COLUMN_TYPE, new AttributeTypeAlias(), REQUIRED, false),
                    new Attribute(COLUMN_NAME, new AttributeTypeMethodCall(String.class), REQUIRED, false),
                    new Attribute(WEIGHT, new AttributeTypeFloatingPoint(), IMPLIED, false),
                    new Attribute(CHARACTERS, new AttributeTypeInteger(), IMPLIED, false),
                    new Attribute(MAXCHARACTERS, new AttributeTypeInteger(), IMPLIED, false),
                    new Attribute(FOREGROUND, new AttributeTypeMethodCall(Color.class, paramList), IMPLIED, false),
                    new Attribute(BACKGROUND, new AttributeTypeMethodCall(Color.class, paramList), IMPLIED, false),
                    new Attribute(EVEN_BACKGROUND, new AttributeTypeMethodCall(Color.class), IMPLIED, false),
                    new Attribute(FONT, new AttributeTypeMethodCall(Font.class), IMPLIED, false),
                    new Attribute(ALIGNMENT, new AttributeTypeEnumeration(s), IMPLIED, true) };
            Attribute[] aa = attrs;
            list.addAll(Arrays.asList(aa));
            return;
        }
        if (elementName.equals(ELEMENT_ROW))
        {
            List paramList = new ArrayList();
            paramList.add(new Param(Gui4jMatrix.PARAM_ROW, int.class));
            paramList.add(new Param(Gui4jMatrix.PARAM_ROWVALUE, ROW_TYPE));

            Attribute[] attrs = { new Attribute(ROW_TYPE, new AttributeTypeAlias(), REQUIRED, false),
                    new Attribute(ROW_NAME, new AttributeTypeMethodCall(String.class), IMPLIED, false),
                    new Attribute(FOREGROUND, new AttributeTypeMethodCall(Color.class, paramList), IMPLIED, false),
                    new Attribute(BACKGROUND, new AttributeTypeMethodCall(Color.class, paramList), IMPLIED, false),
                    new Attribute(EVEN_BACKGROUND, new AttributeTypeMethodCall(Color.class), IMPLIED, false),
                    new Attribute(FONT, new AttributeTypeMethodCall(Font.class), IMPLIED, false),
                    new Attribute(ALIGNMENT, new AttributeTypeEnumeration(s), IMPLIED, true) };
            Attribute[] aa = attrs;
            list.addAll(Arrays.asList(aa));
            return;
        }
    }

    public SubElement getSubElement(String elementName)
    {
        if (ELEMENT_CELL.equals(elementName) || ELEMENT_COLUMN.equals(elementName) || ELEMENT_ROW.equals(elementName))
        {
            return SubElement.empty();
        }
        if (NAME.equals(elementName))
        {
            SubElement cell = SubElement.getInstance(ELEMENT_CELL);
            SubElement column = SubElement.getInstance(ELEMENT_COLUMN);
            SubElement row = SubElement.getInstance(ELEMENT_ROW);
            SubElement[] columnOrRow = { column, row };
            SubElement[] elems = { SubElement.star(SubElement.or(columnOrRow)), SubElement.star(cell) };
            return SubElement.seq(elems);
        }
        return null;
    }

    public String[] getInnerElements()
    {
        String[] elems = { ELEMENT_CELL, ELEMENT_COLUMN, ELEMENT_ROW };
        return elems;
    }

    /**
     * @see org.gui4j.core.Gui4jComponentFactory#getName()
     */
    public String getName()
    {
        return NAME;
    }

}