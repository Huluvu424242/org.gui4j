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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.LElement;
import org.gui4j.component.Gui4jCellTable;
import org.gui4j.component.Gui4jJComponent;
import org.gui4j.core.Gui4jCall;
import org.gui4j.core.Gui4jComponentContainer;
import org.gui4j.core.Gui4jComponentContainerManager;
import org.gui4j.core.Gui4jMap1;
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

public class Gui4jCellTableFactory extends Gui4jJComponentFactory 
{
    private static final Log mLogger = LogFactory.getLog(Gui4jCellTableFactory.class);

    private static final String NAME = "cellTable";
    private static final String ELEMENT_CELL = "cell";
    private static final String ELEMENT_COLUMN = "column";
    private static final String ELEMENT_ROW = "row";
    private static final String COLVALUE = "colValue";
    private static final String ROWVALUE = "rowValue";
    private static final String ROWNAME = "name";
    private static final String ATTR_NAME = "name";
    private static final String COLS = "cols";
    private static final String ROWS = "rows";
    private static final String VISIBLEROWS = "visibleRows";
    private static final String COL = "col";
    private static final String ROW = "row";
    private static final String VALUE = "value";
    private static final String SETVALUE = "setValue";
    private static final String REFRESH = "refresh";
    private static final String AUTOMATIC_REFRESH = "automaticRefresh";
    private static final String ONSETVALUE = "onSetValue";
    private static final String WEIGHT = "weight";
    private static final String REORDERINGALLOWED = "reorderingAllowed";
    private static final String ROWSELECTIONALLOWED = "rowSelectionAllowed";
    private static final String ROWSELECTIONMODE = "rowSelectionMode";
    private static final String COLSELECTIONMODE = "colSelectionMode";
    private static final String ROWHEADERS = "rowHeaders";
    private static final String COLUMNHEADERS = "columnHeaders";
    private static final String INDENTATION = "indentation";
    private static final String LIST = "list";
    private static final String LISTTYPE = "listType";
    private static final String LISTITEM = "listItem";
    private static final String LISTNULLITEM = "listNullItem";
    private static final String LISTEDITABLE = "listEditable";
    private static final String STRINGCONVERT = "stringConvert";
    private static final String BACKGROUND_HEADER = "headerBackground";
    private static final String FOREGROUND = Gui4jTextAttribute.FOREGROUND;
    private static final String BACKGROUND = Gui4jTextAttribute.BACKGROUND;
    private static final String FONT = Gui4jTextAttribute.FONT;
    private static final String ALIGNMENT = Gui4jTextAttribute.ALIGNMENT;

    /* (non-Javadoc)
     * @see org.gui4j.component.factory.Gui4jJComponentFactory#defineGui4jJComponentBy(org.gui4j.core.Gui4jComponentContainer, java.lang.String, org.dom4j.LElement)
     */
    protected Gui4jJComponent defineGui4jJComponentBy(
        Gui4jComponentContainer gui4jComponentContainer,
        String id,
        LElement e)
    {
        int rows = getIntValue(gui4jComponentContainer, e, ROWS);
        int visibleRows = getIntValue(gui4jComponentContainer, e, VISIBLEROWS, -1);
        int cols = getIntValue(gui4jComponentContainer, e, COLS);
        boolean useRowHeaders = gui4jComponentContainer.getBooleanAttrValue(e, ROWHEADERS, false);
        boolean useColumnHeaders = gui4jComponentContainer.getBooleanAttrValue(e, COLUMNHEADERS, true);
        boolean automaticRefresh = gui4jComponentContainer.getBooleanAttrValue(e, AUTOMATIC_REFRESH, true);
        Gui4jCellTable gui4jCellTable = new Gui4jCellTable(
            gui4jComponentContainer,
            id,
            rows,
            cols,
            visibleRows,
            useRowHeaders,
            useColumnHeaders,
            automaticRefresh);
        gui4jCellTable.setHeaderBackground(getGui4jAccessInstance(Color.class, gui4jCellTable, e, BACKGROUND_HEADER));
        {
            String reorderingAllowed = gui4jComponentContainer.getAttrValue(e, REORDERINGALLOWED);
            if (reorderingAllowed != null)
            {
                gui4jCellTable.setReorderingAllowed(reorderingAllowed.equalsIgnoreCase("true"));
            }
        }
        {
            String rowSelectionAllowed = gui4jComponentContainer.getAttrValue(e, ROWSELECTIONALLOWED);
            if (rowSelectionAllowed != null)
            {
                gui4jCellTable.setRowSelectionAllowed(rowSelectionAllowed.equalsIgnoreCase("true"));
            }
        }
        {
            Map m = null;
            Gui4jCall refresh = getGui4jAccessInstance(null, m, gui4jCellTable, e, REFRESH);
            if (refresh != null)
            {
                Gui4jCall[] dependantProperties = refresh.getDependantProperties();
                if (dependantProperties == null || dependantProperties.length == 0)
                {
                    mLogger.warn("Set of dependant events is empty");
                }
                gui4jCellTable.setRefresh(dependantProperties);
            }
        }
        {
            Map m = null;
            gui4jCellTable.setOnSetValue(getGui4jAccessInstance(null, m, gui4jCellTable, e, ONSETVALUE));
        }
        {
            String listSelectionMode = gui4jComponentContainer.getAttrValue(e, ROWSELECTIONMODE);
            if (listSelectionMode != null)
            {
                gui4jCellTable.setRowSelectionMode(Gui4jListFactory.getSelectionMode(listSelectionMode));
            }
        }
        {
            String listSelectionMode = gui4jComponentContainer.getAttrValue(e, COLSELECTIONMODE);
            if (listSelectionMode != null)
            {
                gui4jCellTable.setColSelectionMode(Gui4jListFactory.getSelectionMode(listSelectionMode));
            }
        }
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
                    int col = getIntValue(gui4jComponentContainer, child, COL);

                    if (col < 0 || col >= cols)
                    {
                        Object[] args = { new Integer(col), new Integer(cols) };
                        throw new Gui4jUncheckedException.ResourceError(
                            gui4jComponentContainer.getConfigurationName(),
                            Gui4jComponentContainerManager.getLineNumber(child),
                            RESOURCE_ERROR_invalid_column,
                            args);
                    }

                    Gui4jCall enabled;
                    Gui4jCall colValue = null;
                    Gui4jCall columnName = null;
                    {
                        Map m = null;
                        colValue = getGui4jAccessInstance(Object.class, m, gui4jCellTable, child, COLVALUE);
                        enabled = getGui4jAccessInstance(Boolean.TYPE, m, gui4jCellTable, child, ENABLED);
                    }
                    {
                        Map m = null;
                        if (colValue != null)
                        {
                            m = new Gui4jMap1(Gui4jCellTable.PARAM_COLVALUE, colValue.getResultClass());
                        }
                        columnName = getGui4jAccessInstance(Object.class, m, gui4jCellTable, child, ATTR_NAME);
                    }

                    Gui4jTextAttribute textAttribute = Gui4jTextAttribute.getInstance(
                        this,
                        gui4jCellTable,
                        child);

                    // column is automatically added by constructor
                    Gui4jCellTable.Gui4jColumnTable gui4jColumnTable = gui4jCellTable.new Gui4jColumnTable(
                        col,
                        columnName,
                        colValue,
                        textAttribute,
                        enabled);

                    gui4jColumnTable.setWeight(getDoubleValue(gui4jComponentContainer, child, WEIGHT, 1.0));

                }
                if (elemName.equals(ELEMENT_ROW))
                {
                    int row = getIntValue(gui4jComponentContainer, child, ROW);

                    if (row < 0 || row >= rows)
                    {
                        Object[] args = { new Integer(row), new Integer(rows) };
                        throw new Gui4jUncheckedException.ResourceError(
                            gui4jComponentContainer.getConfigurationName(),
                            Gui4jComponentContainerManager.getLineNumber(child),
                            RESOURCE_ERROR_invalid_row,
                            args);
                    }

                    int indentation = getIntValue(gui4jComponentContainer, child, INDENTATION, 0);
                    Map m = null;
                    Gui4jCall rowValue = getGui4jAccessInstance(Object.class, m, gui4jCellTable, child, ROWVALUE);
                    Gui4jCall rowName;
                    {
                        Map paramMap = new HashMap();
                        paramMap.put(Gui4jCellTable.PARAM_ROW, Integer.TYPE);
                        if (rowValue != null)
                        {
                            paramMap.put(Gui4jCellTable.PARAM_ROWVALUE, rowValue.getResultClass());
                        }
                        rowName = getGui4jAccessInstance(String.class, paramMap, gui4jCellTable, child, ROWNAME);
                    }
                    Gui4jTextAttribute textAttribute = Gui4jTextAttribute.getInstance(
                        this,
                        gui4jCellTable,
                        child);

                    gui4jCellTable.setRow(row, rowValue, rowName, textAttribute, indentation);
                }
                if (elemName.equals(ELEMENT_CELL))
                {
                    Gui4jCall list = null;
                    Gui4jCall listItem = null;
                    Gui4jCall listNullItem = null;
                    Gui4jCall listEditable = null;
                    Gui4jCall stringConvert = null;

                    int col = getIntValue(gui4jComponentContainer, child, COL);
                    int row = getIntValue(gui4jComponentContainer, child, ROW);

                    if (col < 0 || col >= cols)
                    {
                        Object[] args = { new Integer(col), new Integer(cols) };
                        throw new Gui4jUncheckedException.ResourceError(
                            gui4jComponentContainer.getConfigurationName(),
                            Gui4jComponentContainerManager.getLineNumber(child),
                            RESOURCE_ERROR_invalid_column,
                            args);
                    }

                    if (row < 0 || row >= rows)
                    {
                        Object[] args = { new Integer(row), new Integer(rows) };
                        throw new Gui4jUncheckedException.ResourceError(
                            gui4jComponentContainer.getConfigurationName(),
                            Gui4jComponentContainerManager.getLineNumber(child),
                            RESOURCE_ERROR_invalid_row,
                            args);
                    }

                    Map m = new HashMap();
                    m.put(Gui4jCellTable.PARAM_COL, Integer.TYPE);
                    m.put(Gui4jCellTable.PARAM_ROW, Integer.TYPE);
                    Class colValueType = gui4jCellTable.getColumnValueType(col);
                    Class rowValueType = gui4jCellTable.getRowValueType(row);
                    if (colValueType != null)
                    {
                        m.put(Gui4jCellTable.PARAM_COLVALUE, colValueType);
                    }
                    if (rowValueType != null)
                    {
                        m.put(Gui4jCellTable.PARAM_ROWVALUE, rowValueType);
                    }

                    String listTypeAliasName = gui4jComponentContainer.getAttrValue(child, LISTTYPE);
                    Class listTypeClazz = gui4jComponentContainer.getClassForAliasName(listTypeAliasName);
                    if (listTypeClazz != null)
                    {
                        list = getGui4jAccessInstance(Collection.class, m, gui4jCellTable, child, LIST);

                        if (list != null)
                        {
                            m.put(Gui4jCellTable.PARAM_LIST, list.getResultClass());
                            listItem = getGui4jAccessInstance(listTypeClazz, m, gui4jCellTable, child, LISTITEM);
                            if (listItem == null)
                            {
                                Object[] args = { child.getName(), LISTITEM };
                                throw new Gui4jUncheckedException.ResourceError(
                                    gui4jComponentContainer.getConfigurationName(),
                                    Gui4jComponentContainerManager.getLineNumber(child.attribute(LISTTYPE)),
                                    RESOURCE_ERROR_attribute_not_defined,
                                    args);
                            }
                            listNullItem = getGui4jAccessInstance(
                                String.class,
                                gui4jCellTable,
                                child,
                                LISTNULLITEM);
                            Map nullMap = null;
                            listEditable = getGui4jAccessInstance(
                                Boolean.TYPE,
                                nullMap,
                                gui4jCellTable,
                                child,
                                LISTEDITABLE);
                            stringConvert = getGui4jAccessInstance(
                                listTypeClazz,
                                String.class,
                                gui4jCellTable,
                                child,
                                STRINGCONVERT);
                            if (listEditable != null
                                    && stringConvert == null
                                    && !String.class.isAssignableFrom(listTypeClazz))
                            {
                                Object[] args = {};
                                throw new Gui4jUncheckedException.ResourceError(
                                    gui4jComponentContainer.getConfigurationName(),
                                    Gui4jComponentContainerManager.getLineNumber(child.attribute(LISTEDITABLE)),
                                    RESOURCE_ERROR_attribute_listEditable_defined,
                                    args);
                            }
                        }
                        else
                        {
                            Object[] args = { child.getName(), LIST };
                            throw new Gui4jUncheckedException.ResourceError(
                                gui4jComponentContainer.getConfigurationName(),
                                Gui4jComponentContainerManager.getLineNumber(child.attribute(LISTTYPE)),
                                RESOURCE_ERROR_attribute_not_defined,
                                args);
                        }
                    }

                    Gui4jCall value = null;
                    Gui4jCall setValue = null;
                    Gui4jCall enabled = null;

                    if (listTypeClazz != null && list != null && listItem != null)
                    {
                        m.put(Gui4jCellTable.PARAM_LIST, list.getResultClass());
                        m.put(Gui4jCellTable.PARAM_LIST_ITEM, listItem.getResultClass());
                    }

                    // display value
                    value = getGui4jAccessInstance(Object.class, m, gui4jCellTable, child, VALUE);
                    enabled = getGui4jAccessInstance(Boolean.TYPE, m, gui4jCellTable, child, ENABLED);

                    // edit value
                    if (value != null)
                    {
                        if (listTypeClazz != null && list != null && listItem != null)
                        {
                            m.put(Gui4jCellTable.PARAM_VALUE, listTypeClazz);
                        }
                        else
                        {
                            m.put(Gui4jCellTable.PARAM_VALUE, value.getResultClass());
                        }
                    }
                    else
                    {
                        m.put(Gui4jCellTable.PARAM_VALUE, Object.class);
                    }
                    m.remove(Gui4jCellTable.PARAM_LIST_ITEM);
                    setValue = getGui4jAccessInstance(null, m, gui4jCellTable, child, SETVALUE);
                    Gui4jTextAttribute textAttribute = Gui4jTextAttribute.getInstance(
                        this,
                        gui4jCellTable,
                        child);

                    if (gui4jCellTable.isDefined(row, col))
                    {
                        Object[] args = { new Integer(row), new Integer(col) };
                        throw new Gui4jUncheckedException.ResourceError(
                            gui4jComponentContainer.getConfigurationName(),
                            Gui4jComponentContainerManager.getLineNumber(child),
                            RESOURCE_ERROR_element_at_row_col_already_defined,
                            args);

                    }
                    // automatically inserted by the constructor of Gui4jCell in gui4jCellTable
                    /* Gui4jCellTable.Gui4jCell cell = */
                    gui4jCellTable.new Gui4jCell(
                        value,
                        setValue,
                        enabled,
                        list,
                        listItem,
                        listNullItem,
                        listEditable,
                        stringConvert,
                        row,
                        col,
                        textAttribute);

                }
            }
            catch (Throwable t)
            {
                errorList.add(t);
            }
        }
        checkErrorList(errorList);
        return gui4jCellTable;
    }

    public void addToplevelAttributes(List attrList, Filter filter)
    {
        super.addToplevelAttributes(attrList, filter);
        if (filter == null || filter.takeIt(Gui4jCellTableFactory.class))
        {
            attrList.add(new Attribute(COLS, new AttributeTypeInteger(), REQUIRED, false));
            attrList.add(new Attribute(ROWS, new AttributeTypeInteger(), REQUIRED, false));
            attrList.add(new Attribute(VISIBLEROWS, new AttributeTypeInteger(), IMPLIED, false));
            attrList.add(new Attribute(
                REFRESH,
                new AttributeTypeMethodCall(null, EVENT_AWARE),
                IMPLIED,
                false));
            attrList.add(new Attribute(ONSETVALUE, new AttributeTypeMethodCall(null), IMPLIED, false));
            attrList.add(new Attribute(
                REORDERINGALLOWED,
                AttributeTypeEnumeration.getBooleanInstance(true),
                IMPLIED,
                false));
            attrList.add(new Attribute(ROWSELECTIONMODE, new AttributeTypeEnumeration(
                Gui4jListFactory.listSelectionModeTypeParams(),
                "single"), IMPLIED, false));
            attrList.add(new Attribute(COLSELECTIONMODE, new AttributeTypeEnumeration(
                Gui4jListFactory.listSelectionModeTypeParams(),
                "single"), IMPLIED, false));
            attrList.add(new Attribute(
                ROWSELECTIONALLOWED,
                AttributeTypeEnumeration.getBooleanInstance(true),
                IMPLIED,
                false));
            attrList.add(new Attribute(
                ROWHEADERS,
                AttributeTypeEnumeration.getBooleanInstance(false),
                IMPLIED,
                false));
            attrList.add(new Attribute(
                COLUMNHEADERS,
                AttributeTypeEnumeration.getBooleanInstance(true),
                IMPLIED,
                false));
            attrList.add(new Attribute(
                AUTOMATIC_REFRESH,
                AttributeTypeEnumeration.getBooleanInstance(false),
                IMPLIED,
                false));
            attrList.add(new Attribute(
                BACKGROUND_HEADER,
                new AttributeTypeMethodCall(Color.class),
                IMPLIED,
                false));
        }
    }

    public void addInnerAttributes(String elementName, List list)
    {
        Set s = new HashSet();
        s.add(new Param("right"));
        s.add(new Param("center"));
        s.add(new Param("left"));
        s.add(new Param("leading"));
        s.add(new Param("trailing"));

        if (ELEMENT_CELL.equals(elementName))
        {
            List paramsValue = new ArrayList();
            List paramsCell = new ArrayList();
            List paramsList = new ArrayList();
            List paramsSet = new ArrayList();
            paramsCell.add(new Param(Gui4jCellTable.PARAM_COL));
            paramsCell.add(new Param(Gui4jCellTable.PARAM_ROW));
            paramsCell.add(new Param(Gui4jCellTable.PARAM_COLVALUE));
            paramsCell.add(new Param(Gui4jCellTable.PARAM_ROWVALUE));
            paramsList.addAll(paramsCell);
            paramsSet.addAll(paramsCell);
            paramsSet.add(new Param(Gui4jCellTable.PARAM_VALUE));
            paramsList.add(new Param(Gui4jCellTable.PARAM_LIST));
            paramsValue.addAll(paramsList);
            paramsValue.add(new Param(Gui4jCellTable.PARAM_LIST_ITEM));
            Attribute[] attrs = {
                new Attribute(COL, new AttributeTypeInteger(), REQUIRED, false),
                new Attribute(ROW, new AttributeTypeInteger(), REQUIRED, false),
                new Attribute(VALUE, new AttributeTypeMethodCall(Object.class, paramsValue), REQUIRED, false),
                new Attribute(ENABLED, new AttributeTypeMethodCall(Boolean.TYPE, paramsValue), IMPLIED, false),
                new Attribute(SETVALUE, new AttributeTypeMethodCall(null, paramsSet), IMPLIED, false),
                new Attribute(LIST, new AttributeTypeMethodCall(Collection.class, paramsCell), IMPLIED, false),
                new Attribute(LISTTYPE, new AttributeTypeAlias(), IMPLIED, false),
                new Attribute(LISTITEM, new AttributeTypeMethodCall(Object.class, paramsList), IMPLIED, false),
                new Attribute(LISTNULLITEM, new AttributeTypeMethodCall(String.class), IMPLIED, false),
                new Attribute(LISTEDITABLE, new AttributeTypeMethodCall(Boolean.TYPE), IMPLIED, false),
                new Attribute(STRINGCONVERT, new AttributeTypeMethodCall(Object.class), IMPLIED, false),
                new Attribute(FOREGROUND, new AttributeTypeMethodCall(Color.class), IMPLIED, false),
                new Attribute(BACKGROUND, new AttributeTypeMethodCall(Color.class), IMPLIED, false),
                new Attribute(FONT, new AttributeTypeMethodCall(Font.class), IMPLIED, false),
                new Attribute(ALIGNMENT, new AttributeTypeEnumeration(s, "leading"), IMPLIED, true) };
            list.addAll(Arrays.asList(attrs));
            return;
        }
        if (ELEMENT_COLUMN.equals(elementName))
        {
            Attribute[] attrs = { new Attribute(COL, new AttributeTypeInteger(), REQUIRED, false),
                new Attribute(ATTR_NAME, new AttributeTypeMethodCall(Object.class), IMPLIED, false),
                new Attribute(COLVALUE, new AttributeTypeMethodCall(Object.class), IMPLIED, false),
                new Attribute(WEIGHT, new AttributeTypeFloatingPoint(), IMPLIED, false),
                new Attribute(ENABLED, new AttributeTypeMethodCall(Boolean.TYPE), IMPLIED, false),
                new Attribute(FOREGROUND, new AttributeTypeMethodCall(Color.class), IMPLIED, false),
                new Attribute(BACKGROUND, new AttributeTypeMethodCall(Color.class), IMPLIED, false),
                new Attribute(FONT, new AttributeTypeMethodCall(Font.class), IMPLIED, false),
                new Attribute(ALIGNMENT, new AttributeTypeEnumeration(s), IMPLIED, true) };
            list.addAll(Arrays.asList(attrs));
            return;
        }
        if (ELEMENT_ROW.equals(elementName))
        {
            Attribute[] attrs = { new Attribute(ROW, new AttributeTypeInteger(), REQUIRED, false),
                new Attribute(ROWVALUE, new AttributeTypeMethodCall(Object.class), IMPLIED, false),
                new Attribute(ROWNAME, new AttributeTypeMethodCall(String.class), IMPLIED, false),
                new Attribute(INDENTATION, new AttributeTypeInteger(), IMPLIED, false),
                new Attribute(FOREGROUND, new AttributeTypeMethodCall(Color.class), IMPLIED, false),
                new Attribute(BACKGROUND, new AttributeTypeMethodCall(Color.class), IMPLIED, false),
                new Attribute(FONT, new AttributeTypeMethodCall(Font.class), IMPLIED, false),
                new Attribute(ALIGNMENT, new AttributeTypeEnumeration(s), IMPLIED, true) };
            list.addAll(Arrays.asList(attrs));
            return;
        }
    }

    public SubElement getSubElement(String elementName)
    {
        if (ELEMENT_ROW.equals(elementName)
                || ELEMENT_COLUMN.equals(elementName)
                || ELEMENT_CELL.equals(elementName))
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