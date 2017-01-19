package org.gui4j.component.factory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.LElement;

import org.gui4j.component.Gui4jComboBox;
import org.gui4j.component.Gui4jJComponent;
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


public final class Gui4jComboBoxFactory extends Gui4jJComponentFactory
{
    private static final String NAME = "comboBox";
    private static final String ARRAYCONTENT = "arrayContent";
    private static final String CONTENT = "content";
    private static final String CONTENTTYPE = "contentType";
    private static final String SELECTEDITEM = "selectedItem";
    private static final String EDITABLE = "editable";
    private static final String ROWVALUE = "value";
    private static final String ONSELECT = "onSelect";
    private static final String MANUALACTIONONLY = "manualActionOnly";
    private static final String STRINGCONVERT = "stringConvert";
    private static final String NULLITEM = "nullItem";
    
    /**
     * maximumRowCount is the maximum number of items the combo box can display without a scrollbar.
     * it's default value in swing is 8.
     */
    private static final String MAXIMUMROWCOUNT = "maximumRowCount";
    private static final int MAXIMUMROWCOUNTDEFAULT = 8;
    

    /* (non-Javadoc)
     * @see org.gui4j.component.factory.Gui4jJComponentFactory#defineGui4jJComponentBy(org.gui4j.core.Gui4jComponentContainer, java.lang.String, org.dom4j.LElement)
     */
    protected Gui4jJComponent defineGui4jJComponentBy(Gui4jComponentContainer gui4jComponentContainer, String id, LElement e)
    {
        String contentTypeAlias = e.attributeValue(CONTENTTYPE);
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
        Gui4jComboBox gui4jComboBox = new Gui4jComboBox(gui4jComponentContainer, id);
        {
            Map m = new Gui4jMap1(Const.PARAM_ITEM, contentType);
            Gui4jCall rowValue = getGui4jAccessInstance(Object.class, m, gui4jComboBox, e, ROWVALUE);
            gui4jComboBox.setRowValue(rowValue);
        }
        Gui4jCall stringConvert = getGui4jAccessInstance(contentType, String.class, gui4jComboBox, e, STRINGCONVERT);
        gui4jComboBox.setStringConvert(stringConvert);

        gui4jComboBox.setNullItem(getGui4jAccessInstance(String.class, gui4jComboBox, e, NULLITEM));
        gui4jComboBox.setManualActionOnly(gui4jComponentContainer.getBooleanAttrValue(e, MANUALACTIONONLY, false));
        
        gui4jComboBox.definePropertySetter(
            ARRAYCONTENT,
            getGui4jAccessInstance(Object[].class, gui4jComboBox, e, ARRAYCONTENT));
        gui4jComboBox.definePropertySetter(CONTENT, getGui4jAccessInstance(Collection.class, gui4jComboBox, e, CONTENT));

        Gui4jCall selectedItem = getGui4jAccessInstance(contentType, gui4jComboBox, e, SELECTEDITEM);
        gui4jComboBox.setSelectedItem(selectedItem);
        gui4jComboBox.definePropertySetter(SELECTEDITEM, selectedItem);
        {
            Gui4jCall editable = getGui4jAccessInstance(Boolean.TYPE, gui4jComboBox, e, EDITABLE);
            gui4jComboBox.definePropertySetter(EDITABLE, editable);
            if (editable != null && stringConvert == null && !String.class.isAssignableFrom(contentType))
            {
                Object[] args = {
                };
                String configurationName = gui4jComponentContainer.getConfigurationName();
                throw new Gui4jUncheckedException.ResourceError(
                    configurationName,
                    Gui4jComponentContainerManager.getLineNumber(e.attribute(EDITABLE)),
                    RESOURCE_ERROR_attribute_editable_defined,
                    args);
            }
        }

        {
            Map paramMap = new HashMap();
            paramMap.put(Const.PARAM_ITEM, contentType);
            paramMap.put(Const.PARAM_INDEX, Integer.TYPE);
            gui4jComboBox.setOnSelect(getGui4jAccessInstance(null, paramMap, gui4jComboBox, e, ONSELECT));
        }
        
        int maximumRowCount = getIntValue(gui4jComponentContainer, e, MAXIMUMROWCOUNT, MAXIMUMROWCOUNTDEFAULT); 
        gui4jComboBox.setMaximumRowCount(maximumRowCount);
        
        return gui4jComboBox;
    }

    public void addToplevelAttributes(List attrList, Filter filter)
    {
        super.addToplevelAttributes(attrList, filter);
        if (filter == null || filter.takeIt(Gui4jComboBoxFactory.class))
        {
            ArrayList paramItem = new ArrayList();
            paramItem.add(new Param(Const.PARAM_ITEM, CONTENTTYPE));
            
            ArrayList dfltParam = new ArrayList();
            dfltParam.add(new Param("", String.class));
            
            ArrayList paramIndex = new ArrayList();
            paramIndex.add(new Param(Const.PARAM_ITEM, CONTENTTYPE));
            paramIndex.add(new Param(Const.PARAM_INDEX, Integer.TYPE));
            
            attrList.add(new Attribute(ARRAYCONTENT, new AttributeTypeMethodCall(Object[].class), IMPLIED, false));
            attrList.add(new Attribute(CONTENT, new AttributeTypeMethodCall(Collection.class, true), IMPLIED, false));
            attrList.add(new Attribute(SELECTEDITEM, new AttributeTypeMethodCall(Object.class), IMPLIED, false));
            attrList.add(new Attribute(EDITABLE, new AttributeTypeMethodCall(Boolean.TYPE), IMPLIED, false));
            attrList.add(new Attribute(CONTENTTYPE, new AttributeTypeAlias(), IMPLIED, false));
            attrList.add(new Attribute(ROWVALUE, new AttributeTypeMethodCall(Object.class, paramItem), IMPLIED, false));
            attrList.add(new Attribute(ONSELECT, new AttributeTypeMethodCall(null, paramIndex), IMPLIED, false));
            attrList.add(
                new Attribute(STRINGCONVERT, new AttributeTypeMethodCall(Object.class, dfltParam), IMPLIED, false));
            attrList.add(new Attribute(NULLITEM, new AttributeTypeMethodCall(String.class), IMPLIED, false));
            attrList.add(new Attribute(MANUALACTIONONLY, AttributeTypeEnumeration.getBooleanInstance(false), IMPLIED, false));
            attrList.add(new Attribute(MAXIMUMROWCOUNT, new AttributeTypeInteger(), IMPLIED, false));
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
