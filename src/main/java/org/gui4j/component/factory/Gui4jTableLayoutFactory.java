package org.gui4j.component.factory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.dom4j.LElement;

import org.gui4j.component.Gui4jJComponent;
import org.gui4j.component.Gui4jTableLayout;
import org.gui4j.core.Gui4jComponentContainer;
import org.gui4j.core.Gui4jComponentContainerManager;
import org.gui4j.core.Gui4jQualifiedComponent;
import org.gui4j.core.definition.Attribute;
import org.gui4j.core.definition.AttributeTypeEnumeration;
import org.gui4j.core.definition.AttributeTypeID;
import org.gui4j.core.definition.AttributeTypeInteger;
import org.gui4j.core.definition.AttributeTypeString;
import org.gui4j.core.swing.TableLayoutConstants;
import org.gui4j.core.swing.TableLayoutConstraints;
import org.gui4j.exception.Gui4jUncheckedException;
import org.gui4j.util.Filter;


/**
 * Syntax:<br>
 * &lt;tableLayout rows=$rowStr col=$colStr&gt;<br>
 *   &lt;placeTl row="0" col="0" hAlign="left" vAlign="top" id="xxx"/&gt;<br>
 * &lt;/tableLayout&gt;<br>
 * rowStr="p,f,m,100.5"<br>
 * colStr="preferred,fill,minimum,100.5"<br>
 * 
 */
public final class Gui4jTableLayoutFactory extends Gui4jJComponentFactory 
{
    private static final String NAME = "tableLayout";
    private static final String PLACEMENT = "placeTl";
    private static final String ROWS = "rows";
    private static final String COLS = "cols";
    private static final String HSPACING = "hSpacing";
    private static final String VSPACING = "vSpacing";
    private static final String ROW = "row";
    private static final String COL = "col";
    private static final String WIDTH = "width";
    private static final String HEIGHT = "height";
    private static final String HALIGN = "hAlign";
    private static final String VALIGN = "vAlign";
    private static final String ID = "id";

    private static final Map mHAlign;
    private static final Map mVAlign;

    static {
        mHAlign = new HashMap();
        mVAlign = new HashMap();
        mHAlign.put("left", new Integer(TableLayoutConstants.LEFT));
        mHAlign.put("center", new Integer(TableLayoutConstants.CENTER));
        mHAlign.put("full", new Integer(TableLayoutConstants.FULL));
        mHAlign.put("right", new Integer(TableLayoutConstants.RIGHT));
        mVAlign.put("top", new Integer(TableLayoutConstants.TOP));
        mVAlign.put("center", new Integer(TableLayoutConstants.CENTER));
        mVAlign.put("full", new Integer(TableLayoutConstants.FULL));
        mVAlign.put("bottom", new Integer(TableLayoutConstants.BOTTOM));
    }

    /* (non-Javadoc)
     * @see org.gui4j.component.factory.Gui4jJComponentFactory#defineGui4jJComponentBy(org.gui4j.core.Gui4jComponentContainer, java.lang.String, org.dom4j.LElement)
     */
    protected Gui4jJComponent defineGui4jJComponentBy(
        Gui4jComponentContainer gui4jComponentContainer,
        String id,
        LElement e)
    {
        String rowStr = gui4jComponentContainer.getAttrValueReplaceAll(e, ROWS);
        String colStr = gui4jComponentContainer.getAttrValueReplaceAll(e, COLS);
        double[][] size = new double[2][];
        size[0] =
            parseRowColStr(
                colStr,
                gui4jComponentContainer.getConfigurationName(),
                Gui4jComponentContainerManager.getLineNumber(e.attribute(COLS)));
        size[1] =
            parseRowColStr(
                rowStr,
                gui4jComponentContainer.getConfigurationName(),
                Gui4jComponentContainerManager.getLineNumber(e.attribute(ROWS)));
        Gui4jTableLayout gui4jTableLayout = new Gui4jTableLayout(gui4jComponentContainer, size, id);
        gui4jTableLayout.definePropertySetter(
            HSPACING,
            getGui4jAccessInstance(Integer.TYPE, gui4jTableLayout, e, HSPACING));
        gui4jTableLayout.definePropertySetter(
            VSPACING,
            getGui4jAccessInstance(Integer.TYPE, gui4jTableLayout, e, VSPACING));

        List children = e.elements();
        List errorList = new ArrayList();
        for (Iterator it = children.iterator(); it.hasNext();)
        {
            try
            {
                LElement child = (LElement) it.next();
                gui4jComponentContainer.autoExtend(child);
                int row = getIntValue(gui4jComponentContainer, child, ROW);
                int col = getIntValue(gui4jComponentContainer, child, COL);
                int width = getIntValue(gui4jComponentContainer, child, WIDTH, 1);
                int height = getIntValue(gui4jComponentContainer, child, HEIGHT, 1);

                int hAlign =
                    getMapValue(gui4jComponentContainer, child, HALIGN, mHAlign, TableLayoutConstants.FULL);
                int vAlign =
                    getMapValue(gui4jComponentContainer, child, VALIGN, mVAlign, TableLayoutConstants.FULL);

                String gui4jId = child.attributeValue(ID);
                Gui4jQualifiedComponent gui4jQualifiedComponent;
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
                    gui4jQualifiedComponent = gui4jComponentContainer.extractGui4jComponent(gui4jElement);
                }
                else
                {
                    gui4jQualifiedComponent = gui4jComponentContainer.getGui4jQualifiedComponent(gui4jId);
                }

                gui4jTableLayout.addPlacement(
                    new TableLayoutConstraints(col, row, col + width - 1, row + height - 1, hAlign, vAlign),
                    gui4jQualifiedComponent);
            }
            catch (Throwable t)
            {
                errorList.add(t);
            }
        }
        checkErrorList(errorList);
        return gui4jTableLayout;
    }

    private double[] parseRowColStr(String str, String configurationName, int line)
    {
        StringTokenizer tokenizer = new StringTokenizer(str, ",");
        List values = new ArrayList();
        boolean ok = true;
        while (tokenizer.hasMoreTokens())
        {
            String token = tokenizer.nextToken().trim();
            if ("preferred".equalsIgnoreCase(token) || "p".equalsIgnoreCase(token))
            {
                values.add(new Double(TableLayoutConstants.PREFERRED));
            }
            else if ("minimum".equalsIgnoreCase(token) || "m".equalsIgnoreCase(token))
            {
                values.add(new Double(TableLayoutConstants.MINIMUM));
            }
            else if ("fill".equalsIgnoreCase(token) || "f".equalsIgnoreCase(token))
            {
                values.add(new Double(TableLayoutConstants.FILL));
            }
            else if (token.length() > 0)
            {
                try
                {
                    values.add(Double.valueOf(token));
                }
                catch (Throwable t)
                {
                    ok = false;
                }
            }
        }
        double[] val = new double[values.size()];
        for (int i = 0; i < values.size(); i++)
        {
            val[i] = ((Double) values.get(i)).doubleValue();
        }
        if (!ok)
        {
            Object[] args = { str };
            throw new Gui4jUncheckedException.ResourceError(
                configurationName,
                line,
                RESOURCE_ERROR_tableLayout_invalid_col_row_str,
                args);
        }
        return val;
    }

    public SubElement getSubElement(String elementName)
    {
        if (PLACEMENT.equals(elementName))
        {
            return SubElement.optional(SubElement.gui4jComponent());
        }
        if (NAME.equals(elementName))
        {
            SubElement place = SubElement.getInstance(PLACEMENT);
            return SubElement.star(place);
        }
        return null;
    }
    
    public void addInnerAttributes(String elementName, List list)
    {
        if (PLACEMENT.equals(elementName))
        {
            Attribute[] attributesSubElement =
                {
                    new Attribute(ROW, new AttributeTypeInteger(), REQUIRED, false),
                    new Attribute(COL, new AttributeTypeInteger(), REQUIRED, false),
                    new Attribute(WIDTH, new AttributeTypeInteger(), IMPLIED, false),
                    new Attribute(HEIGHT, new AttributeTypeInteger(), IMPLIED, false),
                    new Attribute(HALIGN, new AttributeTypeEnumeration(mHAlign), IMPLIED, false),
                    new Attribute(VALIGN, new AttributeTypeEnumeration(mVAlign), IMPLIED, false),
                    new Attribute(ID, new AttributeTypeID(), IMPLIED, false),
                    };
            list.addAll(Arrays.asList(attributesSubElement));
        }
    }

    public void addToplevelAttributes(List attrList, Filter filter)
    {
        super.addToplevelAttributes(attrList, filter);
        if (filter == null || filter.takeIt(Gui4jTableLayoutFactory.class))
        {
            attrList.add(new Attribute(ROWS, new AttributeTypeString(), REQUIRED, false));
            attrList.add(new Attribute(COLS, new AttributeTypeString(), REQUIRED, false));
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
