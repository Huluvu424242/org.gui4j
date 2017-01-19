package org.gui4j.component.factory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.LElement;
import org.gui4j.component.Gui4jJComponent;
import org.gui4j.component.Gui4jLabelForm;
import org.gui4j.core.Gui4jComponentContainer;
import org.gui4j.core.Gui4jComponentContainerManager;
import org.gui4j.core.Gui4jQualifiedComponent;
import org.gui4j.core.definition.Attribute;
import org.gui4j.core.definition.AttributeTypeInteger;
import org.gui4j.core.definition.AttributeTypeString;
import org.gui4j.exception.Gui4jUncheckedException;
import org.gui4j.util.Filter;


public class Gui4jLabelFormFactory extends Gui4jJComponentFactory 
{

    private static final String NAME = "labelForm";
    private static final String HSPACING = "hSpacing";
    private static final String VSPACING = "vSpacing";
    private static final String COLUMNS = "columns";
    private static final String COMPONENTFORMAT = "componentFormat";
    private static final String COMPONENTWIDTHS = "componentWidths";
    private static final String COMPONENTWIDTH = "componentWidth";    
    private static final String COLSPACING = "colSpacing";
    private static final String SUFFIXSPACING = "suffixSpacing";
    private static final String COLSPANS = "colSpans";
    private static final String COLUMN = "labelColumn";
    private static final String LABELS = "labels";
    private static final String COMPONENTS = "components";
    private static final String MARKERS = "markers";
    private static final String SUFFIXES = "suffixes";

    protected Gui4jJComponent defineGui4jJComponentBy(
        Gui4jComponentContainer gui4jComponentContainer,
        String id,
        LElement e)
    {
        Gui4jLabelForm gui4jLabelForm = new Gui4jLabelForm(gui4jComponentContainer, id);

        gui4jLabelForm.setHspCall(getGui4jAccessInstance(Integer.TYPE, gui4jLabelForm, e, HSPACING));
        gui4jLabelForm.setVspCall(getGui4jAccessInstance(Integer.TYPE, gui4jLabelForm, e, VSPACING));
        gui4jLabelForm.setColSpacing(getIntValue(gui4jComponentContainer, e, COLSPACING, 10));
        gui4jLabelForm.setSuffixSpacing(getIntValue(gui4jComponentContainer, e, SUFFIXSPACING, 0));
        gui4jLabelForm.setComponentWidths(e.attributeValue(COMPONENTWIDTHS));
        gui4jLabelForm.setComponentFormats(e.attributeValue(COMPONENTFORMAT));

        int numColumns = getIntValue(gui4jComponentContainer, e, COLUMNS, 1);
        gui4jLabelForm.setNumColumns(numColumns);
        
        {
            List children = e.elements();
            if (children.isEmpty())
            {
                return gui4jLabelForm;
            }

            LElement firstChild = (LElement) children.get(0);
            if (firstChild.getName().equals(COLUMN))
            {
                // loop over all column tags and extract label/component tags for each one
                for (Iterator iter = children.iterator(); iter.hasNext();)
                {
                    LElement columnTag = (LElement) iter.next();
                    Gui4jLabelForm.Column column = new Gui4jLabelForm.Column();                                    
                    extractColumnContent(columnTag.elements(), column, gui4jComponentContainer);
                    column.setComponentWidth(columnTag.attributeValue(COMPONENTWIDTH));
                    column.setColSpans(columnTag.attributeValue(COLSPANS));
                    gui4jLabelForm.addColumn(column);                    
                }

            }
            else
            {
                // extract label/component tags for the default column
                Gui4jLabelForm.Column column = new Gui4jLabelForm.Column();                
                extractColumnContent(children, column, gui4jComponentContainer);
                gui4jLabelForm.addColumn(column);
            }

        }

        return gui4jLabelForm;
    }

    private void extractColumnContent(        
        List elements,
        Gui4jLabelForm.Column column,
        Gui4jComponentContainer gui4jComponentContainer)
    {
        List errorList = new ArrayList();
        for (Iterator iter = elements.iterator(); iter.hasNext();)
        {
            try
            {
                LElement element = (LElement) iter.next();

                if (element.getName().equals(COLUMN))
                {
                    throw new Gui4jUncheckedException.ResourceError(
                        gui4jComponentContainer.getConfigurationName(),
                        Gui4jComponentContainerManager.getLineNumber(element),
                        RESOURCE_ERROR_labelform_column_conflict);
                }

                for (Iterator it = element.elements().iterator(); it.hasNext();)
                {
                    LElement child = (LElement) it.next();
                    gui4jComponentContainer.autoExtend(child);
                    Gui4jQualifiedComponent childComponent = gui4jComponentContainer.extractGui4jComponent(child);
                    if (element.getName().equals(LABELS))
                    {
                        column.addLabel(childComponent);
                    }
                    else if (element.getName().equals(COMPONENTS))
                    {
                        column.addComponent(childComponent);
                    }
                    else if (element.getName().equals(MARKERS))
                    {
                        column.addMarker(childComponent);
                    }
                    else if (element.getName().equals(SUFFIXES))
                    {
                        column.addSuffix(childComponent);
                    }
                    else
                    {
                        throw new RuntimeException("Unexpected subelement.");
                    }
                }
            }
            catch (Throwable t)
            {
                errorList.add(t);
            }
        }
        checkErrorList(errorList);

    }

    public void addInnerAttributes(String elementName, List list)
    {
        if (COLUMN.equals(elementName)) {            
            list.add(new Attribute(COMPONENTWIDTH, new AttributeTypeString(), IMPLIED, false));
            list.add(new Attribute(COLSPANS, new AttributeTypeString(), IMPLIED, false));            
        }
    }

    public SubElement getSubElement(String elementName)
    {
        if (SUFFIXES.equals(elementName) || MARKERS.equals(elementName) || LABELS.equals(elementName) || COMPONENTS.equals(elementName))
        {
            return SubElement.star(SubElement.gui4jComponent());
        }
        if (NAME.equals(elementName)) {
            SubElement columns = SubElement.plus(SubElement.getInstance(COLUMN));
            SubElement content = getSubElementColumnContent();
            return SubElement.or(new SubElement[] {columns, content});
        }        
        if (COLUMN.equals(elementName)) {
            return getSubElementColumnContent();
        }
        return null;
    }
    
    private SubElement getSubElementColumnContent() {
        SubElement[] mandatorySubElements = new SubElement[] { SubElement.getInstance(LABELS),
            SubElement.getInstance(COMPONENTS) };
        SubElement[] allSubElements = new SubElement[] {
            SubElement.optional(SubElement.getInstance(MARKERS)), SubElement.seq(mandatorySubElements),
            SubElement.optional(SubElement.getInstance(SUFFIXES))};
        return SubElement.seq(allSubElements);        
    }
    
    public String getName()
    {
        return NAME;
    }

    public void addToplevelAttributes(List attrList, Filter filter)
    {
        super.addToplevelAttributes(attrList, filter);
        if (filter == null || filter.takeIt(Gui4jLabelFormFactory.class))
        {
            attrList.add(new Attribute(HSPACING, new AttributeTypeInteger(), IMPLIED, false));
            attrList.add(new Attribute(VSPACING, new AttributeTypeInteger(), IMPLIED, false));
            attrList.add(new Attribute(COLSPACING, new AttributeTypeInteger(), IMPLIED, false));
            attrList.add(new Attribute(SUFFIXSPACING, new AttributeTypeInteger(), IMPLIED, false));
            attrList.add(new Attribute(COLUMNS, new AttributeTypeInteger(), IMPLIED, false));
            attrList.add(new Attribute(COMPONENTWIDTHS, new AttributeTypeString(), IMPLIED, false));
            attrList.add(new Attribute(COMPONENTFORMAT, new AttributeTypeString(), IMPLIED, false));
        }
    }

    public String[] getInnerElements()
    {
        String[] elems = { MARKERS, LABELS, COMPONENTS, COLUMN, SUFFIXES };
        return elems;
    }

}