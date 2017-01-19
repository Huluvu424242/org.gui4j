package org.gui4j.component.factory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.dom4j.LElement;
import org.gui4j.component.Gui4jGridLayout;
import org.gui4j.component.Gui4jJComponent;
import org.gui4j.core.Gui4jComponentContainer;
import org.gui4j.core.Gui4jComponentContainerManager;
import org.gui4j.core.Gui4jComponentFactory;
import org.gui4j.core.Gui4jQualifiedComponent;
import org.gui4j.core.definition.Attribute;
import org.gui4j.core.definition.AttributeTypeID;
import org.gui4j.core.definition.AttributeTypeInteger;
import org.gui4j.exception.Gui4jUncheckedException;
import org.gui4j.util.Filter;


public final class Gui4jGridLayoutFactory extends Gui4jJComponentFactory 
{
    private static final String NAME = "gridLayout";
    private static final String PLACEMENT = "placeGl";
    private static final String ROWS = "rows";
    private static final String COLS = "cols";
    private static final String HSPACING = "hSpacing";
    private static final String VSPACING = "vSpacing";
    private static final String ROW = "row";
    private static final String COL = "col";
    private static final String ID = "id";

    /* (non-Javadoc)
     * @see org.gui4j.component.factory.Gui4jJComponentFactory#defineGui4jJComponentBy(org.gui4j.core.Gui4jComponentContainer, java.lang.String, org.dom4j.LElement)
     */
    protected Gui4jJComponent defineGui4jJComponentBy(
        Gui4jComponentContainer gui4jComponentContainer,
        String id,
        LElement e)
    {
        int rows = getIntValue(gui4jComponentContainer, e, ROWS);
        int cols = getIntValue(gui4jComponentContainer, e, COLS);
        Gui4jGridLayout gui4jGridLayout = new Gui4jGridLayout(gui4jComponentContainer, id, rows, cols);
        gui4jGridLayout.definePropertySetter(
            HSPACING,
            getGui4jAccessInstance(Integer.TYPE, gui4jGridLayout, e, HSPACING));
        gui4jGridLayout.definePropertySetter(
            VSPACING,
            getGui4jAccessInstance(Integer.TYPE, gui4jGridLayout, e, VSPACING));

        List children = e.elements();
        List errorList = new ArrayList();
        for (Iterator it = children.iterator(); it.hasNext();)
        {
            try
            {
                LElement child = (LElement) it.next();
                gui4jComponentContainer.autoExtend(child);
                int row = getIntValue(gui4jComponentContainer, child, ROW);

                if (row < 0 || row >= rows)
                {
                    Object[] args = { new Integer(row), new Integer(rows)};
                    throw new Gui4jUncheckedException.ResourceError(
                        gui4jComponentContainer.getConfigurationName(),
                        Gui4jComponentContainerManager.getLineNumber(child),
                        RESOURCE_ERROR_invalid_row,
                        args);
                }

                int col = getIntValue(gui4jComponentContainer, child, COL);

                if (col < 0 || col >= cols)
                {
                    Object[] args = { new Integer(col), new Integer(cols)};
                    throw new Gui4jUncheckedException.ResourceError(
                        gui4jComponentContainer.getConfigurationName(),
                        Gui4jComponentContainerManager.getLineNumber(child),
                        RESOURCE_ERROR_invalid_column,
                        args);
                }

                String gui4jId = child.attributeValue(ID);
                Gui4jQualifiedComponent gui4jComponentInPath;
                if (gui4jId == null)
                {
                    if (child.elements().isEmpty())
                    {
                        Object[] args = { PLACEMENT };
                        throw new Gui4jUncheckedException.ResourceError(
                            gui4jComponentContainer.getConfigurationName(),
                            Gui4jComponentContainerManager.getLineNumber(child),
                            RESOURCE_ERROR_element_must_contain_gui4jComponent,
                            args);
                    }
                    LElement gui4jElement = (LElement) child.elements().iterator().next();
                    gui4jComponentInPath = gui4jComponentContainer.extractGui4jComponent(gui4jElement);
                }
                else
                {
                    gui4jComponentInPath = gui4jComponentContainer.getGui4jQualifiedComponent(gui4jId);
                }

                if (gui4jGridLayout.isDefined(row, col))
                {
                    Object[] args = { new Integer(row), new Integer(col)};
                    new Gui4jUncheckedException.ResourceError(
                        gui4jComponentContainer.getConfigurationName(),
                        Gui4jComponentContainerManager.getLineNumber(child),
                        RESOURCE_ERROR_element_at_row_col_already_defined,
                        args);
                }
                else
                {
                    gui4jGridLayout.addPlacement(row, col, gui4jComponentInPath);
                }
            }
            catch (Throwable t)
            {
                errorList.add(t);
            }
        }
        checkErrorList(errorList);
        return gui4jGridLayout;
    }

    public void addInnerAttributes(String elementName, List list)
    {
        if (PLACEMENT.equals(elementName))
        {
            Attribute[] attributesSubElement = {
                new Attribute(ROW, new AttributeTypeInteger(), REQUIRED, false),
                    new Attribute(COL, new AttributeTypeInteger(), REQUIRED, false),
                    new Attribute(ID, new AttributeTypeID(), IMPLIED, false)};
            list.addAll(Arrays.asList(attributesSubElement));
        }
    }

    public Gui4jComponentFactory.SubElement getSubElement(String elementName)
    {
        if (NAME.equals(elementName))
        {
            SubElement place = SubElement.getInstance(PLACEMENT);
            return SubElement.star(place);
        }
        if (PLACEMENT.equals(elementName))
        {
            return SubElement.optional(SubElement.gui4jComponent());
        }
        return null;
    }

    public void addToplevelAttributes(List attrList, Filter filter)
    {
        super.addToplevelAttributes(attrList, filter);
        if (filter == null || filter.takeIt(Gui4jGridLayoutFactory.class))
        {
            attrList.add(new Attribute(ROWS, new AttributeTypeInteger(), REQUIRED, false));
            attrList.add(new Attribute(COLS, new AttributeTypeInteger(), REQUIRED, false));
            attrList.add(new Attribute(HSPACING, new AttributeTypeInteger(), IMPLIED, false));
            attrList.add(new Attribute(VSPACING, new AttributeTypeInteger(), IMPLIED, false));
        }
    }

    public String[] getInnerElements()
    {
        String[] elems = { PLACEMENT };
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
