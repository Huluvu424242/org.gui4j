package org.gui4j.component.factory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Icon;

import org.dom4j.LElement;

import org.gui4j.component.Gui4jJComponent;
import org.gui4j.component.Gui4jTree;
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

public final class Gui4jTreeFactory extends Gui4jJComponentFactory
{

    private static final String NAME = "tree";
    private static final String ROOT = "root";
    private static final String NODE = "node";
    private static final String CONTENTTYPE = "contentType";
    private static final String VALUE = "value";
    private static final String CHILDREN = "children";
    private static final String ONSELECT = "onSelect";
    private static final String ONDBLCLICK = "onDblClick";
    private static final String SELECTEDNODE = "selectedNode";
    private static final String SELECTEDPATH = "selectedPath";
    private static final String REFRESH = "refresh";
    private static final String RELOAD = "reload";
    private static final String LAZY = "lazy";
    private static final String LAZYMESSAGE = "lazyMessage";
    private static final String ISLEAF = "isLeaf";
    private static final String HIDE_ROOT_NODE = "hideRootNode";
    private static final String ICON = "icon";
    private static final String ICONPOSITION = "iconPosition";
    private static final String USEORIGINALCOLLECTION = "useOriginalCollection";
    private static final String INITIALLYEXPAND = "initiallyExpand";
    private static final String ADDITIONAL_ICON_WIDTH = "nodeIconWidth";

    protected Gui4jJComponent defineGui4jJComponentBy(Gui4jComponentContainer gui4jComponentContainer, String id,
            LElement e)
    {
        String configurationName = gui4jComponentContainer.getConfigurationName();

        boolean lazy = gui4jComponentContainer.getBooleanAttrValue(e, LAZY, false);

        boolean hideRootNode = gui4jComponentContainer.getBooleanAttrValue(e, HIDE_ROOT_NODE, false);
        boolean useOriginalCollection = gui4jComponentContainer.getBooleanAttrValue(e, USEORIGINALCOLLECTION, false);
        boolean initiallyExpand = gui4jComponentContainer.getBooleanAttrValue(e, INITIALLYEXPAND, false);
        int iconNodeWidth = getIntValue(gui4jComponentContainer, e, ADDITIONAL_ICON_WIDTH, 0);
        Gui4jTree gui4jTree = new Gui4jTree(gui4jComponentContainer, id, lazy, hideRootNode, useOriginalCollection,
                initiallyExpand, iconNodeWidth);
        gui4jTree.definePropertySetter(ROOT, getGui4jAccessInstance(Object.class, gui4jTree, e, ROOT));
        gui4jTree.definePropertySetter(RELOAD, getGui4jAccessInstance(Object[].class, gui4jTree, e, RELOAD));

        Gui4jCall lazyMessage = getGui4jAccessInstance(String.class, gui4jTree, e, LAZYMESSAGE);
        gui4jTree.setLazyMessageCall(lazyMessage);

        Gui4jCall selectedNode = getGui4jAccessInstance(Object.class, gui4jTree, e, SELECTEDNODE);
        gui4jTree.definePropertySetter(SELECTEDNODE, selectedNode);
        gui4jTree.setSelectedNodeCall(selectedNode);

        Gui4jCall selectedPath = getGui4jAccessInstance(Object.class, gui4jTree, e, SELECTEDPATH);
        gui4jTree.definePropertySetter(SELECTEDPATH, selectedPath);
        gui4jTree.setSelectedPathCall(selectedPath);

        {
            Map params = new Gui4jMap1(Gui4jTree.PARAM_PATH, Object[].class);
            Gui4jCall onSelectCall = getGui4jAccessInstance(null, params, gui4jTree, e, ONSELECT);
            if (onSelectCall != null)
            {
                gui4jTree.setOnSelectCallTree(onSelectCall);
            }
        }

        {
            Map m = null;
            Gui4jCall refresh = getGui4jAccessInstance(null, m, gui4jTree, e, REFRESH);
            if (refresh != null)
            {
                Gui4jCall[] dependantProperties = refresh.getDependantProperties();
                if (dependantProperties == null || dependantProperties.length == 0)
                {
                    // mLogger.warn("Set of dependant events is empty");
                }
                gui4jTree.setRefreshEvents(dependantProperties);
            }
        }

        List errorList = new ArrayList();
        List children = e.elements();
        for (Iterator iter = children.iterator(); iter.hasNext();)
        {
            try
            {
                LElement child = (LElement) iter.next();

                assert child.getName().equals(NODE);

                String nodeContentAliasName = gui4jComponentContainer.getAttrValue(child, CONTENTTYPE);
                Class nodeContentClazz = gui4jComponentContainer.getClassForAliasName(nodeContentAliasName);
                if (nodeContentClazz == null)
                {
                    Object[] args = { nodeContentAliasName, nodeContentAliasName };
                    throw new Gui4jUncheckedException.ResourceError(configurationName, Gui4jComponentContainerManager
                            .getLineNumber(child.attribute(CONTENTTYPE)), RESOURCE_ERROR_alias_not_defined_in_path,
                            args);
                }

                Map paramTypes = new Gui4jMap1(Gui4jTree.PARAM_ITEM, nodeContentClazz);
                Gui4jCall valueCall = getGui4jAccessInstance(String.class, paramTypes, gui4jTree, child, VALUE);
                Gui4jCall childrenCall = getGui4jAccessInstance(List.class, paramTypes, gui4jTree, child, CHILDREN);
                Gui4jCall isLeafCall = getGui4jAccessInstance(Boolean.TYPE, paramTypes, gui4jTree, child, ISLEAF);
                Gui4jCall onSelectCall = getGui4jAccessInstance(null, paramTypes, gui4jTree, child, ONSELECT);
                Gui4jCall onDblClickCall = getGui4jAccessInstance(null, paramTypes, gui4jTree, child, ONDBLCLICK);
                Gui4jCall iconCall = getGui4jAccessInstance(Icon.class, paramTypes, gui4jTree, child, ICON);
                String iconPosition = child.attributeValue(ICONPOSITION, "leading");
                boolean lazyNode = gui4jComponentContainer.getBooleanAttrValue(child, LAZY, false);
                gui4jTree.addNode(nodeContentClazz, valueCall, childrenCall, onSelectCall, onDblClickCall, iconCall,
                        isLeafCall, iconPosition, lazyNode);
            }
            catch (Throwable t)
            {
                errorList.add(t);
            }
        }
        checkErrorList(errorList);

        return gui4jTree;
    }

    public String getName()
    {
        return NAME;
    }

    public SubElement getSubElement(String elementName)
    {
        if (NAME.equals(elementName))
        {
            return SubElement.star(SubElement.getInstance(NODE));
        }
        if (NODE.equals(elementName))
        {
            return SubElement.empty();
        }
        return null;
    }

    public void addInnerAttributes(String elementName, List list)
    {
        if (NODE.equals(elementName))
        {
            Set setIconPosition = new HashSet();
            setIconPosition.add(new Param("leading"));
            setIconPosition.add(new Param("trailing"));

            List itemParam = new ArrayList();
            itemParam.add(new Param(Gui4jTree.PARAM_ITEM));

            Attribute[] attrs = { new Attribute(CONTENTTYPE, new AttributeTypeAlias(), REQUIRED, false),
                    new Attribute(VALUE, new AttributeTypeMethodCall(String.class, itemParam), REQUIRED, false),
                    new Attribute(CHILDREN, new AttributeTypeMethodCall(List.class, itemParam), IMPLIED, false),
                    new Attribute(ISLEAF, new AttributeTypeMethodCall(Boolean.TYPE, itemParam), IMPLIED, false),
                    new Attribute(ICON, new AttributeTypeMethodCall(Icon.class, itemParam), IMPLIED, false),
                    new Attribute(ICONPOSITION, new AttributeTypeEnumeration(setIconPosition), IMPLIED, false),
                    new Attribute(LAZY, AttributeTypeEnumeration.getBooleanInstance(false), IMPLIED, false),
                    new Attribute(ONSELECT, new AttributeTypeMethodCall(null, itemParam), IMPLIED, false),
                    new Attribute(ONDBLCLICK, new AttributeTypeMethodCall(null, itemParam), IMPLIED, false) };
            list.addAll(Arrays.asList(attrs));
        }
    }

    public void addToplevelAttributes(List attrList, Filter filter)
    {
        super.addToplevelAttributes(attrList, filter);
        if (filter == null || filter.takeIt(Gui4jTreeFactory.class))
        {
            attrList.add(new Attribute(ROOT, new AttributeTypeMethodCall(Object.class), REQUIRED, false));
            attrList.add(new Attribute(SELECTEDNODE, new AttributeTypeMethodCall(Object.class), IMPLIED, false));
            attrList.add(new Attribute(SELECTEDPATH, new AttributeTypeMethodCall(Object[].class), IMPLIED, false));
            attrList.add(new Attribute(REFRESH, new AttributeTypeMethodCall(null), IMPLIED, false));
            attrList.add(new Attribute(RELOAD, new AttributeTypeMethodCall(Object[].class), IMPLIED, false));
            attrList.add(new Attribute(LAZY, AttributeTypeEnumeration.getBooleanInstance(false), IMPLIED, false));
            attrList.add(new Attribute(LAZYMESSAGE, new AttributeTypeMethodCall(String.class), IMPLIED, false));
            attrList.add(new Attribute(HIDE_ROOT_NODE, AttributeTypeEnumeration.getBooleanInstance(false), IMPLIED,
                    false));
            attrList.add(new Attribute(USEORIGINALCOLLECTION, AttributeTypeEnumeration.getBooleanInstance(false),
                    IMPLIED, false));
            attrList.add(new Attribute(INITIALLYEXPAND, AttributeTypeEnumeration.getBooleanInstance(false), IMPLIED,
                    false));
            attrList.add(new Attribute(ADDITIONAL_ICON_WIDTH, new AttributeTypeInteger(), IMPLIED, false));

            {
                List params = new ArrayList();
                params.add(new Param(Gui4jTree.PARAM_PATH));
                attrList.add(new Attribute(ONSELECT, new AttributeTypeMethodCall(null, params), IMPLIED, false));
            }
        }
    }

    public String[] getInnerElements()
    {
        String[] elems = { NODE };
        return elems;
    }

}
