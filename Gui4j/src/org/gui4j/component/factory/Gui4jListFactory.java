package org.gui4j.component.factory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ListSelectionModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.LElement;
import org.gui4j.component.Gui4jJComponent;
import org.gui4j.component.Gui4jList;
import org.gui4j.constants.Const;
import org.gui4j.core.Gui4jCall;
import org.gui4j.core.Gui4jComponentContainer;
import org.gui4j.core.Gui4jComponentContainerManager;
import org.gui4j.core.Gui4jMap1;
import org.gui4j.core.definition.Attribute;
import org.gui4j.core.definition.AttributeTypeAlias;
import org.gui4j.core.definition.AttributeTypeEnumeration;
import org.gui4j.core.definition.AttributeTypeInteger;
import org.gui4j.core.definition.AttributeTypeMethodCall;
import org.gui4j.core.definition.Param;
import org.gui4j.exception.Gui4jUncheckedException;
import org.gui4j.util.Filter;


public final class Gui4jListFactory extends Gui4jJComponentFactory
{
    private static final String NAME = "list";
    private static final String ARRAYCONTENT = "arrayContent";
    private static final String CONTENT = "content";
    private static final String CONTENTTYPE = "contentType";
    private static final String SELECTIONMODE = "selectionMode";
    private static final String SELECTEDINDICES = "selectedIndices";
    private static final String SELECTEDITEMS = "selectedItems";
    private static final String SELECTEDITEM = "selectedItem";    
    private static final String ROWVALUE = "value";
    private static final String ONSELECT = "onSelect";
    private static final String ONDOUBLECLICK = "onDoubleClick";
    private static final String VISIBLEROWCOUNT = "visibleRowCount";

    private static final Log mLogger = LogFactory.getLog(Gui4jListFactory.class);

    public static final Map mSelectionMode;
    static {
        mSelectionMode = new HashMap();
        mSelectionMode.put("multiple", new Integer(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION));
        mSelectionMode.put("single_interval", new Integer(ListSelectionModel.SINGLE_INTERVAL_SELECTION));
        mSelectionMode.put("single", new Integer(ListSelectionModel.SINGLE_SELECTION));
    }

    public static String listSelectionModeType()
    {
        return "(multiple|single_interval|single)";
    }

    public static Collection listSelectionModeTypeParams()
    {
        List l = new ArrayList();
        l.add(new Param("multiple"));
        l.add(new Param("singleInterval"));
        l.add(new Param("single"));
        return l;
    }

    public static int getSelectionMode(String selectionMode)
    {
        Integer i = (Integer) mSelectionMode.get(selectionMode);
        if (i != null)
        {
            return i.intValue();
        }
        else
        {
            mLogger.warn("Unknown list-selection-mode " + selectionMode);
            return ListSelectionModel.SINGLE_SELECTION;
        }
    }

    /* (non-Javadoc)
     * @see org.gui4j.component.factory.Gui4jJComponentFactory#defineGui4jJComponentBy(org.gui4j.core.Gui4jComponentContainer, java.lang.String, org.dom4j.LElement)
     */
    protected Gui4jJComponent defineGui4jJComponentBy(
        Gui4jComponentContainer gui4jComponentContainer,
        String id,
        LElement e)
    {
        String contentTypeAlias = gui4jComponentContainer.getAttrValue(e, CONTENTTYPE);
        Class contentType = gui4jComponentContainer.getClassForAliasName(contentTypeAlias);
        if (contentTypeAlias != null && contentType == null)
        {
            Object[] args = { contentTypeAlias, contentTypeAlias };
            throw new Gui4jUncheckedException.ResourceError(
                gui4jComponentContainer.getConfigurationName(),
                Gui4jComponentContainerManager.getLineNumber(e.attribute(CONTENTTYPE)),
                RESOURCE_ERROR_alias_not_defined_in_path,
                args);

        }
        if (contentType == null)
        {
            contentType = Object.class;
        }
        int visibleRowCount = getIntValue(gui4jComponentContainer, e, VISIBLEROWCOUNT, 0);
        Gui4jList gui4jList = new Gui4jList(gui4jComponentContainer, id, visibleRowCount);
        {
            Map m = new Gui4jMap1(Const.PARAM_ITEM, contentType);
            Gui4jCall rowValue = getGui4jAccessInstance(Object.class, m, gui4jList, e, ROWVALUE);
            gui4jList.setRowValue(rowValue);
        }
        gui4jList.definePropertySetter(
            ARRAYCONTENT,
            getGui4jAccessInstance(Object[].class, gui4jList, e, ARRAYCONTENT));
        gui4jList.definePropertySetter(CONTENT, getGui4jAccessInstance(Collection.class, gui4jList, e, CONTENT));
        gui4jList.definePropertySetter(
            SELECTEDINDICES,
            getGui4jAccessInstance(int[].class, gui4jList, e, SELECTEDINDICES));

        Gui4jCall selectedItemsCall = getGui4jAccessInstance(Collection.class, gui4jList, e, SELECTEDITEMS);
        gui4jList.setSelectedItemsCall(selectedItemsCall);
        gui4jList.definePropertySetter(SELECTEDITEMS, selectedItemsCall);

        Gui4jCall selectedItemCall = getGui4jAccessInstance(Object.class, gui4jList, e, SELECTEDITEM);
        gui4jList.setSelectedItemCall(selectedItemCall);
        gui4jList.definePropertySetter(SELECTEDITEM, selectedItemCall);

        {
            String listSelectionMode = gui4jComponentContainer.getAttrValue(e, SELECTIONMODE);
            if (listSelectionMode != null)
            {
                gui4jList.setSelectionMode(getSelectionMode(listSelectionMode));
            }
        }
        
        {
            Map paramMap = new HashMap();
            paramMap.put(Const.PARAM_ITEM, contentType);
            paramMap.put(Const.PARAM_INDEX, Integer.TYPE);
            paramMap.put(Const.PARAM_INDICES, int[].class);
            paramMap.put(Const.PARAM_ITEMS, Object[].class);
            gui4jList.setOnSelect(getGui4jAccessInstance(null, paramMap, gui4jList, e, ONSELECT));
            gui4jList.setOnDoubleClick(getGui4jAccessInstance(null, paramMap, gui4jList, e, ONDOUBLECLICK));
        }
        return gui4jList;
    }

    public void addToplevelAttributes(List attrList, Filter filter)
    {
        super.addToplevelAttributes(attrList, filter);
        if (filter == null || filter.takeIt(Gui4jListFactory.class))
        {
            attrList.add(
                new Attribute(ARRAYCONTENT, new AttributeTypeMethodCall(Object[].class), IMPLIED, false));
            attrList.add(
                new Attribute(CONTENT, new AttributeTypeMethodCall(Collection.class), IMPLIED, false));
            attrList.add(
                new Attribute(
                    SELECTIONMODE,
                    new AttributeTypeEnumeration(listSelectionModeTypeParams()),
                    IMPLIED,
                    false));
            attrList.add(
                new Attribute(SELECTEDINDICES, new AttributeTypeMethodCall(int[].class), IMPLIED, false));
            attrList.add(
                new Attribute(SELECTEDITEMS, new AttributeTypeMethodCall(Collection.class), IMPLIED, false));
            attrList.add(
                new Attribute(SELECTEDITEM, new AttributeTypeMethodCall(Object.class), IMPLIED, false));                
            attrList.add(new Attribute(CONTENTTYPE, new AttributeTypeAlias(), IMPLIED, false));
            {
                List params = new ArrayList();
                params.add(new Param(Const.PARAM_ITEM));
                attrList.add(
                    new Attribute(
                        ROWVALUE,
                        new AttributeTypeMethodCall(Object.class, params),
                        IMPLIED,
                        false));
            }
            {
                List params = new ArrayList();
                params.add(new Param(Const.PARAM_ITEM));
                params.add(new Param(Const.PARAM_INDEX));
                params.add(new Param(Const.PARAM_INDICES));
                params.add(new Param(Const.PARAM_ITEMS));
                attrList.add(
                    new Attribute(ONSELECT, new AttributeTypeMethodCall(null, params), IMPLIED, false));
                attrList.add(new Attribute(ONDOUBLECLICK, new AttributeTypeMethodCall(null, params), IMPLIED, false));    
            }
            attrList.add(new Attribute(VISIBLEROWCOUNT, new AttributeTypeInteger(), IMPLIED, false));
        }
    }

    /**
     * @see org.gui4j.core.Gui4jComponentFactory#getName()
     */
    public String getName()
    {
        return NAME;
    }

}
