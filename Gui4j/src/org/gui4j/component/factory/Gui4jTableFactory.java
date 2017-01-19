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

import javax.swing.Icon;
import javax.swing.JTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.LElement;

import org.gui4j.component.Gui4jJComponent;
import org.gui4j.component.Gui4jTable;
import org.gui4j.constants.Const;
import org.gui4j.core.Gui4jCall;
import org.gui4j.core.Gui4jComponentContainer;
import org.gui4j.core.Gui4jComponentContainerManager;
import org.gui4j.core.Gui4jComponentFactory;
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
import org.gui4j.util.Pair;

public final class Gui4jTableFactory extends Gui4jJComponentFactory
{
    private static final Log mLogger = LogFactory.getLog(Gui4jTableFactory.class);

    private static final String NAME = "table";
    private static final String CONTENT = "content";
    private static final String CONTENTTYPE = "contentType";
    private static final String TABLECOLUMN = "tableColumn";
    private static final String TABLECOLUMNTYPE = "tableColumnType";
    private static final String TABLETYPE = "tableType";
    private static final String TABLEHEADER = "tableHeader";
    private static final String COLUMNNAME = "name";
    private static final String COLUMNVALUE = "value";
    private static final String COLUMNSETVALUE = "setValue";
    private static final String EDITCELLSELECTION = "editCellSelection";
    private static final String CELLSELECTIONPAIR = "cellSelectionPair";
    private static final String ROWSELECTIONINDEX = "rowSelectionIndex";
    private static final String ROWSELECTIONITEM = "rowSelectionItem";
    private static final String ONCELLSELECT = "onCellSelect";
    private static final String ONROWSELECT = "onRowSelect";
    private static final String ONCOLSELECT = "onColSelect";
    private static final String ONDBLCLICK = "onDoubleClick";
    private static final String REFRESH = "refresh";
    private static final String AUTOMATIC_REFRESH = "automaticRefresh";
    private static final String USE_ORIGINAL_LIST = "useOriginalList";
    private static final String ONSETVALUE = "onSetValue";
    private static final String WEIGHT = "weight";
    private static final String AUTOMATIC_VISIBLEROWS = "automaticVisibleRows";
    private static final String VISIBLEROWS = "visibleRows";
    private static final String HEADERLINES = "headerLines";
    private static final String REORDERINGALLOWED = "reorderingAllowed";
    private static final String ROWSELECTIONALLOWED = "rowSelectionAllowed";
    private static final String ROWSELECTIONMODE = "rowSelectionMode";
    private static final String COLSELECTIONMODE = "colSelectionMode";
    private static final String LIST = "list";
    private static final String LISTTYPE = "listType";
    private static final String LISTITEM = "listItem";
    private static final String LISTNULLITEM = "listNullItem";
    private static final String LISTEDITABLE = "listEditable";
    private static final String LISTINDICATOR = "listIndicator";
    private static final String STRINGCONVERT = "stringConvert";
    private static final String CHARACTERS = "characters";
    private static final String MAXCHARACTERS = "maxCharacters";
    private static final String POPUP_CONTEXT = "popupContext";
    private static final String BACKGROUND_HEADER = "headerBackground";
    private static final String ACTIONCOMMAND = "actionCommand";
    private static final String HIDEFOCUS = "hideFocus";
    private static final String RESIZEMODE = "resizeMode";
    private static final String ROWHEADERS = "rowHeaders";
    private static final String ROWHEADERCHARACTERS = "rowHeaderCharacters";
    private static final String ROWNAME = "rowName";
    private static final String TOOLTIP = "tooltip";
    private static final String FOREGROUND = Gui4jTextAttribute.FOREGROUND;
    private static final String BACKGROUND = Gui4jTextAttribute.BACKGROUND;
    private static final String EVEN_BACKGROUND = Gui4jTextAttribute.EVEN_BACKGROUND;
    private static final String FONT = Gui4jTextAttribute.FONT;
    private static final String ALIGNMENT = Gui4jTextAttribute.ALIGNMENT;

    private static final int HEADERLINES_DEFAULT = 1;

    int nofColumns = 0;

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

    /* (non-Javadoc)
     * @see org.gui4j.component.factory.Gui4jJComponentFactory#defineGui4jJComponentBy(org.gui4j.core.Gui4jComponentContainer, java.lang.String, org.dom4j.LElement)
     */
    protected Gui4jJComponent defineGui4jJComponentBy(
        Gui4jComponentContainer gui4jComponentContainer,
        String id,
        LElement e)
    {
        String configurationName = gui4jComponentContainer.getConfigurationName();

        int visibleRows = getIntValue(gui4jComponentContainer, e, VISIBLEROWS, -1);
        int headerLines = getIntValue(gui4jComponentContainer, e, HEADERLINES, HEADERLINES_DEFAULT);
        boolean automaticVisibleRows = gui4jComponentContainer.getBooleanAttrValue(
            e,
            AUTOMATIC_VISIBLEROWS,
            false);
        boolean automaticRefresh = gui4jComponentContainer.getBooleanAttrValue(e, AUTOMATIC_REFRESH, false);
        boolean useOriginalList = gui4jComponentContainer.getBooleanAttrValue(e, USE_ORIGINAL_LIST, false);
        boolean useRowHeaders = gui4jComponentContainer.getBooleanAttrValue(e, ROWHEADERS, false);
        String contentAliasName = gui4jComponentContainer.getAttrValue(e, CONTENTTYPE);
        Class contentClazz = gui4jComponentContainer.getClassForAliasName(contentAliasName);

        if (contentClazz == null)
        {
            Object[] args = { contentAliasName, contentAliasName };
            throw new Gui4jUncheckedException.ResourceError(
                configurationName,
                Gui4jComponentContainerManager.getLineNumber(e.attribute(CONTENTTYPE)),
                RESOURCE_ERROR_alias_not_defined_in_path,
                args);

        }

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

        Gui4jTable gui4jTable = new Gui4jTable(
            gui4jComponentContainer,
            id,
            visibleRows,
            headerLines,
            contentClazz,
            automaticVisibleRows,
            automaticRefresh,
            useOriginalList,
            useRowHeaders,
            resizeMode);

        gui4jTable.setHeaderBackground(getGui4jAccessInstance(Color.class, gui4jTable, e, BACKGROUND_HEADER));
        gui4jTable.definePropertySetter(CONTENT, getGui4jAccessInstance(Collection.class, gui4jTable, e, CONTENT));
        gui4jTable.definePropertySetter(EDITCELLSELECTION, getGui4jAccessInstance(
            Pair.class,
            gui4jTable,
            e,
            EDITCELLSELECTION));
        {
            Gui4jCall cellSelection = getGui4jAccessInstance(Pair.class, gui4jTable, e, CELLSELECTIONPAIR);
            gui4jTable.setCellSelectionCall(cellSelection);
            gui4jTable.definePropertySetter(CELLSELECTIONPAIR, cellSelection);
        }
        {
            Gui4jCall rowSelection = getGui4jAccessInstance(Integer.TYPE, gui4jTable, e, ROWSELECTIONINDEX);
            gui4jTable.setRowSelectionIndexCall(rowSelection);
            gui4jTable.definePropertySetter(ROWSELECTIONINDEX, rowSelection);
        }
        {
            Gui4jCall rowSelectionItem = getGui4jAccessInstance(Object.class, gui4jTable, e, ROWSELECTIONITEM);
            gui4jTable.setRowSelectionItemCall(rowSelectionItem);
            gui4jTable.definePropertySetter(ROWSELECTIONITEM, rowSelectionItem);
        }
        {
            Map m = new HashMap();
            m.put(Const.PARAM_ITEM, contentClazz);
            gui4jTable.addRowHeaderName(contentClazz, getGui4jAccessInstance(null, m, gui4jTable, e, ROWNAME));

            Map nullMap = null;
            Gui4jCall rowHeaderCharacters = getGui4jAccessInstance(
                Integer.TYPE,
                nullMap,
                gui4jTable,
                e,
                ROWHEADERCHARACTERS);
            gui4jTable.setRowHeaderCharacters(rowHeaderCharacters);
        }
        {
            Map m = null;
            Gui4jCall refresh = getGui4jAccessInstance(null, m, gui4jTable, e, REFRESH);
            if (refresh != null)
            {
                Gui4jCall[] dependantProperties = refresh.getDependantProperties();
                if (dependantProperties == null || dependantProperties.length == 0)
                {
                    mLogger.warn("Set of dependant events is empty");
                }
                gui4jTable.setRefresh(dependantProperties);
            }
        }
        {
            Map m = null;
            gui4jTable.setOnSetValue(getGui4jAccessInstance(null, m, gui4jTable, e, ONSETVALUE));
        }

        gui4jTable.setActionCommand(getGui4jAccessInstance(null, gui4jTable, e, ACTIONCOMMAND));

        {
            String hideFocus = gui4jComponentContainer.getAttrValue(e, HIDEFOCUS);
            if (hideFocus != null)
            {
                gui4jTable.setHideFocus(hideFocus.equalsIgnoreCase("true"));
            }
        }
        {
            String reorderingAllowed = gui4jComponentContainer.getAttrValue(e, REORDERINGALLOWED);
            if (reorderingAllowed != null)
            {
                gui4jTable.setReorderingAllowed(reorderingAllowed.equalsIgnoreCase("true"));
            }
        }
        {
            String rowSelectionAllowed = gui4jComponentContainer.getAttrValue(e, ROWSELECTIONALLOWED);
            if (rowSelectionAllowed != null)
            {
                gui4jTable.setRowSelectionAllowed(rowSelectionAllowed.equalsIgnoreCase("true"));
            }
        }
        {
            String listSelectionMode = gui4jComponentContainer.getAttrValue(e, ROWSELECTIONMODE);
            if (listSelectionMode != null)
            {
                gui4jTable.setRowSelectionMode(Gui4jListFactory.getSelectionMode(listSelectionMode));
            }
        }
        {
            String listSelectionMode = gui4jComponentContainer.getAttrValue(e, COLSELECTIONMODE);
            if (listSelectionMode != null)
            {
                gui4jTable.setColSelectionMode(Gui4jListFactory.getSelectionMode(listSelectionMode));
            }
        }

        List children = e.elements();
        List errorList = new ArrayList();

        for (Iterator it = children.iterator(); it.hasNext();)
        {
            try
            {
                LElement child = (LElement) it.next();
                if (child.getName().equals(TABLECOLUMN))
                {
                    nofColumns++;
                    Gui4jTable.Gui4jColumnTable column = extractColumn(
                        gui4jComponentContainer,
                        gui4jTable,
                        contentClazz,
                        child);
                    gui4jTable.addColumn(contentClazz, column);
                }
                if (child.getName().equals(TABLEHEADER))
                {

                    Gui4jCall name = getGui4jAccessInstance(String.class, gui4jTable, child, COLUMNNAME);

                    Gui4jTable.Gui4jColumnHeaderTable columnGroup = gui4jTable.new Gui4jColumnHeaderTable(name);
                    gui4jTable.addColumnGroup(columnGroup);
                    createColumnGroups(child, columnGroup, gui4jComponentContainer, gui4jTable, contentClazz);
                }
                if (child.getName().equals(TABLETYPE))
                {
                    String subContentAliasName = gui4jComponentContainer.getAttrValue(child, CONTENTTYPE);
                    Class subContentClazz = gui4jComponentContainer.getClassForAliasName(subContentAliasName);
                    if (subContentClazz == null)
                    {
                        Object[] args = { subContentAliasName, subContentAliasName };
                        throw new Gui4jUncheckedException.ResourceError(
                            configurationName,
                            Gui4jComponentContainerManager.getLineNumber(child.attribute(CONTENTTYPE)),
                            RESOURCE_ERROR_alias_not_defined_in_path,
                            args);

                    }
                    Iterator iter = child.elements().iterator();

                    Gui4jTable.Gui4jColumnTable column = null;
                    for (int i = 0; i < nofColumns; i++)
                    {
                        if (iter.hasNext())
                        {
                            LElement subChild = (LElement) iter.next();
                            column = extractColumn(gui4jComponentContainer, gui4jTable, subContentClazz, subChild);
                        }
                        gui4jTable.addColumn(subContentClazz, column);
                    }

                    {
                        Map paramMap = new HashMap();
                        paramMap.put(Const.PARAM_ROW_INDEX, Integer.TYPE);
                        paramMap.put(Const.PARAM_ROW_INDICES, int[].class);
                        paramMap.put(Const.PARAM_COL_INDEX, Integer.TYPE);
                        paramMap.put(Const.PARAM_COL_INDICES, int[].class);
                        extractListener(gui4jTable, paramMap, subContentClazz, child);
                    }
                    {
                        Map m = new HashMap();
                        m.put(Const.PARAM_ITEM, subContentClazz);
                        gui4jTable.addRowHeaderName(subContentClazz, getGui4jAccessInstance(
                            null,
                            m,
                            gui4jTable,
                            child,
                            ROWNAME));

                    }
                }
            }
            catch (Throwable t)
            {
                errorList.add(t);
            }
        }
        checkErrorList(errorList);
        {
            Map paramMap = new HashMap();
            paramMap.put(Const.PARAM_ROW_INDEX, Integer.TYPE);
            paramMap.put(Const.PARAM_ROW_INDICES, int[].class);
            paramMap.put(Const.PARAM_COL_INDEX, Integer.TYPE);
            paramMap.put(Const.PARAM_COL_INDICES, int[].class);
            extractListener(gui4jTable, paramMap, contentClazz, e);
        }
        return gui4jTable;
    }

    private void createColumnGroups(
        LElement e,
        Gui4jTable.Gui4jColumnHeaderTable columnGroup,
        Gui4jComponentContainer gui4jComponentContainer,
        Gui4jTable gui4jTable,
        Class contentClazz)
    {
        for (Iterator iter = e.elements().iterator(); iter.hasNext();)
        {
            LElement child = (LElement) iter.next();
            // inneres Element ist <tableHeader>
            if (child.getName().equals(TABLEHEADER))
            {
                //String columnGroupName = child.getAttributeValue(COLUMNNAME);
                Gui4jCall columnGroupName = getGui4jAccessInstance(String.class, gui4jTable, child, COLUMNNAME);
                Gui4jTable.Gui4jColumnHeaderTable childColumnGroup = gui4jTable.new Gui4jColumnHeaderTable(
                    columnGroupName);
                columnGroup.addColumn(childColumnGroup);
                createColumnGroups(child, childColumnGroup, gui4jComponentContainer, gui4jTable, contentClazz);
            }
            // inneres Element ist <tableColumn>
            else
            {
                nofColumns++;
                Gui4jTable.Gui4jColumnTable column = extractColumn(
                    gui4jComponentContainer,
                    gui4jTable,
                    contentClazz,
                    child);
                // Column in den ColumnManager im Gui4jTable schreiben
                gui4jTable.addColumn(contentClazz, column);
                // Column in den ColumnManager im Gui4jTable schreiben
                columnGroup.addColumn(column);
            }
        }
    }

    private void extractListener(Gui4jTable gui4jTable, Map paramMap, Class typeClass, LElement e)
    {
        paramMap.put(Const.PARAM_ITEM, typeClass);
        gui4jTable.setOnCellSelect(typeClass, getGui4jAccessInstance(null, paramMap, gui4jTable, e, ONCELLSELECT));
        gui4jTable.setOnRowSelect(typeClass, getGui4jAccessInstance(null, paramMap, gui4jTable, e, ONROWSELECT));
        gui4jTable.setOnColSelect(typeClass, getGui4jAccessInstance(null, paramMap, gui4jTable, e, ONCOLSELECT));
        gui4jTable.setOnDoubleClick(typeClass, getGui4jAccessInstance(null, paramMap, gui4jTable, e, ONDBLCLICK));
        gui4jTable.setPopupContext(typeClass, getGui4jAccessInstance(null, paramMap, gui4jTable, e, POPUP_CONTEXT));
    }

    private Gui4jTable.Gui4jColumnTable extractColumn(
        Gui4jComponentContainer gui4jComponentContainer,
        Gui4jTable gui4jTable,
        Class contentClazz,
        LElement child)
    {
        String configurationName = gui4jComponentContainer.getConfigurationName();
        gui4jComponentContainer.autoExtend(child);
        Gui4jCall name = getGui4jAccessInstance(String.class, gui4jTable, child, COLUMNNAME);
        Gui4jCall enabled;
        {
            Map paramMap = new Gui4jMap1(Const.PARAM_ITEM, contentClazz);
            enabled = getGui4jAccessInstance(Boolean.TYPE, paramMap, gui4jTable, child, ENABLED);
        }

        Gui4jCall list = null;
        Gui4jCall listItem = null;
        Gui4jCall listNullItem = null;
        Gui4jCall listEditable = null;
        Gui4jCall listIndicator = null;
        Gui4jCall stringConvert = null;
        Gui4jCall value;
        Gui4jCall tooltip;
        Gui4jCall setValue;

        Map nullMap = null;
        Gui4jCall characters = getGui4jAccessInstance(Integer.TYPE, nullMap, gui4jTable, child, CHARACTERS);
        Gui4jCall maxCharacters = getGui4jAccessInstance(Integer.TYPE, nullMap, gui4jTable, child, MAXCHARACTERS);

        String listTypeAliasName = gui4jComponentContainer.getAttrValue(child, LISTTYPE);
        Class listTypeClazz = gui4jComponentContainer.getClassForAliasName(listTypeAliasName);
        if (listTypeClazz != null)
        {
            Map m = new HashMap();
            m.put(Const.PARAM_ITEM, contentClazz);
            m.put(Const.PARAM_ROW_INDEX, Integer.TYPE);
            list = getGui4jAccessInstance(Collection.class, m, gui4jTable, child, LIST);

            if (list != null)
            {
                m.put(Const.PARAM_LIST, list.getResultClass());
                listItem = getGui4jAccessInstance(listTypeClazz, m, gui4jTable, child, LISTITEM);
                if (listItem == null)
                {
                    Object[] args = { child.getName(), LISTITEM };
                    throw new Gui4jUncheckedException.ResourceError(
                        configurationName,
                        Gui4jComponentContainerManager.getLineNumber(child.attribute(LISTTYPE)),
                        RESOURCE_ERROR_attribute_not_defined,
                        args);
                }
                listNullItem = getGui4jAccessInstance(String.class, gui4jTable, child, LISTNULLITEM);
                listEditable = getGui4jAccessInstance(Boolean.TYPE, nullMap, gui4jTable, child, LISTEDITABLE);
                listIndicator = getGui4jAccessInstance(Icon.class, nullMap, gui4jTable, child, LISTINDICATOR);
                stringConvert = getGui4jAccessInstance(
                    listTypeClazz,
                    String.class,
                    gui4jTable,
                    child,
                    STRINGCONVERT);
                if (listEditable != null
                        && stringConvert == null
                        && !String.class.isAssignableFrom(listTypeClazz))
                {
                    Object[] args = {};
                    throw new Gui4jUncheckedException.ResourceError(
                        configurationName,
                        Gui4jComponentContainerManager.getLineNumber(child.attribute(LISTEDITABLE)),
                        RESOURCE_ERROR_attribute_listEditable_defined,
                        args);
                }
            }
            else
            {
                Object[] args = { child.getName(), LIST };
                throw new Gui4jUncheckedException.ResourceError(
                    configurationName,
                    Gui4jComponentContainerManager.getLineNumber(child.attribute(LISTTYPE)),
                    RESOURCE_ERROR_attribute_not_defined,
                    args);
            }
        }

        {
            Map m = new HashMap();
            m.put(Const.PARAM_ITEM, contentClazz);
            m.put(Const.PARAM_ROW_INDEX, Integer.TYPE);
            if (listTypeClazz != null && list != null && listItem != null)
            {
                m.put(Const.PARAM_LIST, list.getResultClass());
                m.put(Const.PARAM_LIST_ITEM, listItem.getResultClass());
            }
            value = getGui4jAccessInstance(Object.class, m, gui4jTable, child, COLUMNVALUE);
            tooltip = getGui4jAccessInstance(String.class, m, gui4jTable, child, TOOLTIP);
        }
        {
            Class valueType = Object.class;
            if (value != null)
            {
                valueType = value.getResultClass();
            }
            Map m = new HashMap();
            m.put(Const.PARAM_ROW_INDEX, Integer.TYPE);
            m.put(Const.PARAM_ITEM, contentClazz);
            if (listTypeClazz != null && list != null && listItem != null)
            {
                m.put(Const.PARAM_LIST, list.getResultClass());
                m.put(Const.PARAM_VALUE, listTypeClazz);
            }
            else
            {
                m.put(Const.PARAM_VALUE, valueType);
            }
            setValue = getGui4jAccessInstance(null, m, gui4jTable, child, COLUMNSETVALUE);
        }

        Gui4jTextAttribute textAttribute = Gui4jTextAttribute.getInstance(this, gui4jTable, child);

        Gui4jTable.Gui4jColumnTable column = gui4jTable.new Gui4jColumnTable(
            name,
            value,
            setValue,
            enabled,
            characters,
            maxCharacters,
            list,
            listItem,
            listNullItem,
            listEditable,
            listIndicator,
            stringConvert,
            tooltip,
            textAttribute);

        column.setWeight(getDoubleValue(gui4jComponentContainer, child, WEIGHT, 1.0));

        return column;

    }

    public void addToplevelAttributes(List attrList, Filter filter)
    {
        super.addToplevelAttributes(attrList, filter);

        Collection paramsOnSelect = new ArrayList();
        paramsOnSelect.add(new Param(Const.PARAM_ITEM, CONTENTTYPE));
        paramsOnSelect.add(new Param(Const.PARAM_COL_INDEX, Integer.TYPE));
        paramsOnSelect.add(new Param(Const.PARAM_COL_INDICES, int[].class));
        paramsOnSelect.add(new Param(Const.PARAM_ROW_INDEX, Integer.TYPE));
        paramsOnSelect.add(new Param(Const.PARAM_ROW_INDICES, int[].class));

        if (filter == null || filter.takeIt(Gui4jTableFactory.class))
        {
            attrList.add(new Attribute(
                CONTENT,
                new AttributeTypeMethodCall(Collection.class),
                REQUIRED,
                false));
            attrList.add(new Attribute(CONTENTTYPE, new AttributeTypeAlias(), REQUIRED, false));
            attrList.add(new Attribute(ROWSELECTIONMODE, new AttributeTypeEnumeration(
                Gui4jListFactory.listSelectionModeTypeParams()), IMPLIED, false));
            attrList.add(new Attribute(COLSELECTIONMODE, new AttributeTypeEnumeration(
                Gui4jListFactory.listSelectionModeTypeParams()), IMPLIED, false));
            attrList.add(new Attribute(
                BACKGROUND_HEADER,
                new AttributeTypeMethodCall(Color.class),
                IMPLIED,
                false));
            attrList.add(new Attribute(
                ONCELLSELECT,
                new AttributeTypeMethodCall(void.class, paramsOnSelect),
                IMPLIED,
                false));
            attrList.add(new Attribute(
                ONROWSELECT,
                new AttributeTypeMethodCall(void.class, paramsOnSelect),
                IMPLIED,
                false));
            attrList.add(new Attribute(
                ONCOLSELECT,
                new AttributeTypeMethodCall(void.class, paramsOnSelect),
                IMPLIED,
                false));
            attrList.add(new Attribute(
                ONDBLCLICK,
                new AttributeTypeMethodCall(void.class, paramsOnSelect),
                IMPLIED,
                false));
            attrList.add(new Attribute(
                REFRESH,
                new AttributeTypeMethodCall(null, EVENT_AWARE),
                IMPLIED,
                false));
            attrList.add(new Attribute(
                AUTOMATIC_REFRESH,
                AttributeTypeEnumeration.getBooleanInstance(false),
                IMPLIED,
                false));
            attrList.add(new Attribute(ONSETVALUE, new AttributeTypeMethodCall(null), IMPLIED, false));
            attrList.add(new Attribute(VISIBLEROWS, new AttributeTypeInteger(), IMPLIED, false));
            attrList.add(new Attribute(
                AUTOMATIC_VISIBLEROWS,
                AttributeTypeEnumeration.getBooleanInstance(),
                IMPLIED,
                false));
            attrList.add(new Attribute(
                HEADERLINES,
                new AttributeTypeInteger(HEADERLINES_DEFAULT),
                IMPLIED,
                false));
            attrList.add(new Attribute(
                REORDERINGALLOWED,
                AttributeTypeEnumeration.getBooleanInstance(),
                IMPLIED,
                false));
            attrList.add(new Attribute(
                ROWSELECTIONALLOWED,
                AttributeTypeEnumeration.getBooleanInstance(),
                IMPLIED,
                false));
            attrList.add(new Attribute(
                EDITCELLSELECTION,
                new AttributeTypeMethodCall(Pair.class),
                IMPLIED,
                false));
            attrList.add(new Attribute(
                CELLSELECTIONPAIR,
                new AttributeTypeMethodCall(Pair.class, true),
                IMPLIED,
                false));
            attrList.add(new Attribute(
                ROWSELECTIONINDEX,
                new AttributeTypeMethodCall(Integer.TYPE),
                IMPLIED,
                false));
            attrList.add(new Attribute(
                ROWSELECTIONITEM,
                new AttributeTypeMethodCall(Object.class),
                IMPLIED,
                false));
            attrList.add(new Attribute(
                POPUP_CONTEXT,
                new AttributeTypeMethodCall(Object.class),
                IMPLIED,
                false));
            attrList.add(new Attribute(ACTIONCOMMAND, new AttributeTypeMethodCall(null), IMPLIED, false));
            attrList.add(new Attribute(
                HIDEFOCUS,
                AttributeTypeEnumeration.getBooleanInstance(false),
                IMPLIED,
                false));
            attrList.add(new Attribute(
                USE_ORIGINAL_LIST,
                AttributeTypeEnumeration.getBooleanInstance(false),
                IMPLIED,
                false));
            attrList.add(new Attribute(RESIZEMODE, new AttributeTypeEnumeration(
                mResizeMode,
                "subsequentColumns"), IMPLIED, false));
            attrList.add(new Attribute(
                ROWHEADERS,
                AttributeTypeEnumeration.getBooleanInstance(false),
                IMPLIED,
                false));
            attrList.add(new Attribute(ROWNAME, new AttributeTypeMethodCall(String.class), IMPLIED, false));
            attrList.add(new Attribute(ROWHEADERCHARACTERS, new AttributeTypeInteger(), IMPLIED, false));
        }
    }

    public void addInnerAttributes(String elementName, List list)
    {
        Collection paramItem = new ArrayList();
        paramItem.add(new Param(Const.PARAM_ITEM, CONTENTTYPE));
        
        Collection paramsForSetValue = new ArrayList();
        paramsForSetValue.add(new Param(Const.PARAM_ROW_INDEX, Integer.TYPE));
        paramsForSetValue.add(new Param(Const.PARAM_ITEM, CONTENTTYPE));
        paramsForSetValue.add(new Param(Const.PARAM_VALUE, COLUMNVALUE + " or " + LISTTYPE));
        
        Collection paramsForValue = new ArrayList();
        paramsForValue.add(new Param(Const.PARAM_ROW_INDEX, Integer.TYPE));
        paramsForValue.add(new Param(Const.PARAM_ITEM, CONTENTTYPE));
        paramsForValue.add(new Param(Const.PARAM_LIST, Collection.class));
        paramsForValue.add(new Param(Const.PARAM_LIST_ITEM, LISTTYPE));
        
        Set s = new HashSet();
        s.add(new Param("right"));
        s.add(new Param("left"));
        s.add(new Param("center"));

        if (TABLECOLUMN.equals(elementName))
        {
            Attribute[] attrs = {
                new Attribute(COLUMNNAME, new AttributeTypeMethodCall(String.class), REQUIRED, false),
                new Attribute(TOOLTIP, new AttributeTypeMethodCall(String.class), IMPLIED, false),
                new Attribute(COLUMNVALUE, new AttributeTypeMethodCall(Object.class, paramsForValue), REQUIRED, false),
                new Attribute(COLUMNSETVALUE, new AttributeTypeMethodCall(null, paramsForSetValue), IMPLIED, false),
                new Attribute(WEIGHT, new AttributeTypeFloatingPoint(), IMPLIED, false),
                new Attribute(LIST, new AttributeTypeMethodCall(Collection.class), IMPLIED, false),
                new Attribute(LISTTYPE, new AttributeTypeAlias(), IMPLIED, false),
                new Attribute(LISTITEM, new AttributeTypeMethodCall(Object.class), IMPLIED, false),
                new Attribute(LISTNULLITEM, new AttributeTypeMethodCall(String.class), IMPLIED, false),
                new Attribute(LISTEDITABLE, new AttributeTypeMethodCall(Boolean.TYPE), IMPLIED, false),
                new Attribute(LISTINDICATOR, new AttributeTypeMethodCall(Icon.class), IMPLIED, false),
                new Attribute(STRINGCONVERT, new AttributeTypeMethodCall(Object.class), IMPLIED, false),
                new Attribute(ENABLED, new AttributeTypeMethodCall(Boolean.TYPE, paramItem), IMPLIED, false),
                new Attribute(CHARACTERS, new AttributeTypeInteger(), IMPLIED, false),
                new Attribute(MAXCHARACTERS, new AttributeTypeInteger(), IMPLIED, false),
                new Attribute(FOREGROUND, new AttributeTypeMethodCall(Color.class), IMPLIED, false),
                new Attribute(BACKGROUND, new AttributeTypeMethodCall(Color.class), IMPLIED, false),
                new Attribute(EVEN_BACKGROUND, new AttributeTypeMethodCall(Color.class), IMPLIED, false),
                new Attribute(ALIGNMENT, new AttributeTypeEnumeration(s), IMPLIED, false),
                new Attribute(FONT, new AttributeTypeMethodCall(Font.class), IMPLIED, false) };
            Attribute[] aa = attrs;
            list.addAll(Arrays.asList(aa));
            return;
        }
        if (TABLETYPE.equals(elementName))
        {
            Attribute[] attrs = { new Attribute(CONTENTTYPE, new AttributeTypeAlias(), REQUIRED, false),
                new Attribute(ONCELLSELECT, new AttributeTypeMethodCall(null), IMPLIED, false),
                new Attribute(ONROWSELECT, new AttributeTypeMethodCall(null), IMPLIED, false),
                new Attribute(ONCOLSELECT, new AttributeTypeMethodCall(null), IMPLIED, false),
                new Attribute(ONDBLCLICK, new AttributeTypeMethodCall(null), IMPLIED, false),
                new Attribute(POPUP_CONTEXT, new AttributeTypeMethodCall(Object.class), IMPLIED, false),
                new Attribute(ROWNAME, new AttributeTypeMethodCall(String.class), IMPLIED, false) };

            list.addAll(Arrays.asList(attrs));
            return;
        }
        if (TABLECOLUMNTYPE.equals(elementName))
        {
            Attribute[] attrs = {
                new Attribute(COLUMNVALUE, new AttributeTypeMethodCall(String.class, paramsForValue), IMPLIED, false),
                new Attribute(TOOLTIP, new AttributeTypeMethodCall(String.class), IMPLIED, false),
                new Attribute(COLUMNSETVALUE, new AttributeTypeMethodCall(null, paramsForSetValue), IMPLIED, false),
                new Attribute(LIST, new AttributeTypeMethodCall(Collection.class), IMPLIED, false),
                new Attribute(LISTTYPE, new AttributeTypeAlias(), IMPLIED, false),
                new Attribute(LISTITEM, new AttributeTypeMethodCall(Object.class), IMPLIED, false),
                new Attribute(LISTNULLITEM, new AttributeTypeMethodCall(String.class), IMPLIED, false),
                new Attribute(LISTEDITABLE, new AttributeTypeMethodCall(Boolean.TYPE), IMPLIED, false),
                new Attribute(STRINGCONVERT, new AttributeTypeMethodCall(Object.class), IMPLIED, false),
                new Attribute(ENABLED, new AttributeTypeMethodCall(Boolean.TYPE, paramItem), IMPLIED, false),
                new Attribute(FOREGROUND, new AttributeTypeMethodCall(Color.class), IMPLIED, false),
                new Attribute(BACKGROUND, new AttributeTypeMethodCall(Color.class), IMPLIED, false),
                new Attribute(EVEN_BACKGROUND, new AttributeTypeMethodCall(Color.class), IMPLIED, false),
                new Attribute(ALIGNMENT, new AttributeTypeEnumeration(s), IMPLIED, false),
                new Attribute(FONT, new AttributeTypeMethodCall(Font.class), IMPLIED, false) };
            Attribute[] aa = attrs;
            list.addAll(Arrays.asList(aa));
            return;
        }
        if (TABLEHEADER.equals(elementName))
        {
            Attribute[] attrs = {
                new Attribute(COLUMNNAME, new AttributeTypeMethodCall(String.class), REQUIRED, false),
                new Attribute(FOREGROUND, new AttributeTypeMethodCall(Color.class), IMPLIED, false),
                new Attribute(BACKGROUND, new AttributeTypeMethodCall(Color.class), IMPLIED, false),
                new Attribute(ALIGNMENT, new AttributeTypeEnumeration(s), IMPLIED, false),
                new Attribute(FONT, new AttributeTypeMethodCall(Font.class), IMPLIED, false) };
            Attribute[] aa = attrs;
            list.addAll(Arrays.asList(aa));
            return;
        }
    }

    public Gui4jComponentFactory.SubElement getSubElement(String elementName)
    {
        if (TABLECOLUMN.equals(elementName) || TABLECOLUMNTYPE.equals(elementName))
        {
            return SubElement.empty();
        }
        if (TABLETYPE.equals(elementName))
        {
            return SubElement.star(SubElement.getInstance(TABLECOLUMNTYPE));
        }
        if (TABLEHEADER.equals(elementName))
        {
            SubElement[] columnOrGroup = { SubElement.getInstance(TABLECOLUMN),
                SubElement.getInstance(TABLEHEADER) };
            return SubElement.plus(SubElement.or(columnOrGroup));
        }
        if (NAME.equals(elementName))
        {
            SubElement[] columnOrGroup = { SubElement.getInstance(TABLECOLUMN),
                SubElement.getInstance(TABLEHEADER) };
            SubElement[] subElements = { SubElement.plus(SubElement.or(columnOrGroup)),
                SubElement.star(SubElement.getInstance(TABLETYPE)), };

            return SubElement.seq(subElements);
        }
        return null;
    }

    public String[] getInnerElements()
    {
        String[] elems = { TABLECOLUMN, TABLECOLUMNTYPE, TABLETYPE, TABLEHEADER };
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