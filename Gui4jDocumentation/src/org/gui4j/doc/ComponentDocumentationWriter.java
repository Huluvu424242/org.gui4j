package org.gui4j.doc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gui4j.component.factory.Gui4jJComponentFactory;
import org.gui4j.core.Gui4jComponentFactory;
import org.gui4j.core.definition.Attribute;
import org.gui4j.core.definition.AttributeTypeAlias;
import org.gui4j.core.definition.AttributeTypeEnumeration;
import org.gui4j.core.definition.AttributeTypeFloatingPoint;
import org.gui4j.core.definition.AttributeTypeID;
import org.gui4j.core.definition.AttributeTypeInteger;
import org.gui4j.core.definition.AttributeTypeMethodCall;
import org.gui4j.core.definition.AttributeTypeString;
import org.gui4j.core.definition.AttributeTypeVisitor;
import org.gui4j.core.definition.DefaultValueAttributeType;
import org.gui4j.core.definition.Param;
import org.gui4j.core.definition.ParameterAttributeType;
import org.gui4j.core.util.Extract;
import org.gui4j.util.Filter;

import com.lowagie.text.Anchor;
import com.lowagie.text.BadElementException;
import com.lowagie.text.Cell;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ElementTags;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Header;
import com.lowagie.text.Image;
import com.lowagie.text.ListItem;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Table;
import com.lowagie.text.markup.MarkupTags;

/**
 * This class is responsible for the documentation of a factory class and its superclasses.
 */
public class ComponentDocumentationWriter
{
    public static final Log log = LogFactory.getLog(ComponentDocumentationWriter.class);

    public static final String MISSING = "[documentation missing]";
    public static final String PDF = ".pdf";
    public static final String HTML = ".html";
    public static final String STYLESHEET = "style_form.css";
    public static final String DESCRIPTION = "Description";
    public static final String NONE = "NONE";
    public static final String EMPTY = "EMPTY";
    public static final String SUBELEMENTS = "Sub-Elements";
    public static final String REQUIRED_ATTRIBUTES = "Required Attributes";
    public static final String IMPLIED_ATTRIBUTES = "Optional Attributes";
    public static final String CLASS_STYLE_STRUCTURE = "structure";
    public static final String CLASS_STYLE_TITLE = "title";
    public static final String CLASS_STYLE_PLAINTEXT = "plaintext";
    public static final String CLASS_STYLE_SUBTITLEPLAIN = "subtitleplain";
    public static final String CLASS_STYLE_PLAINTEXTBOLD = "plaintextbold";
    public static final String CLASS_STYLE_PLAINTEXTTT = "plaintexttt";
    public static final String CLASS_SUFFIX_RIGHTMARGIN = "_rightmargin";
    public static final String PROPERTIES = ".properties";

    private static final String TODO = "[TODO]";
    private static final String LINK = "link";
    private static final String TT = "tt";
    private static final String B = "b";
    private static final String BR = "br";
    private static final String IMG = "img";
    private static final String TAG_BEGIN = "{@";
    private static final String TAG_END = "}";

    private static final String[] NON_GENERATES = { Gui4jComponentFactory.ELEMENT_COMPONENT,
        Gui4jComponentFactory.ELEMENT_GUI4JREF };

    private final File outputDirectory;
    protected final Properties props;
    private final DocumentationManager documentationManager;
    private final boolean generatePropertiesFile;

    private final String[] topLevelElements;
    private final Set writtenElements = new HashSet();
    private PrintWriter propertiesFileWriter;

    /**
     * @param outputDirectory the directory to store all generated files.
     * @param dm the DocumentationManager. Required to get access to the <code>propertiesWriteOutMap</code>
     * @param generatePropertiesFile signals, if also a property-skeleton file shall be created. If true,
     *            superclasses' attributes and their params are written to the
     *            <code>propertiesWriteOutMap</code> of the DocumentationManger
     */
    ComponentDocumentationWriter(File outputDirectory, DocumentationManager dm, boolean generatePropertiesFile)
    {
        this.outputDirectory = outputDirectory;
        this.documentationManager = dm;
        this.props = new Properties();
        this.generatePropertiesFile = generatePropertiesFile;

        // create array of top level elements
        Set elements = dm.getTopLevelElements();
        this.topLevelElements = new String[elements.size()];
        int index = 0;
        for (Iterator iter = elements.iterator(); iter.hasNext();)
        {
            String element = (String) iter.next();
            this.topLevelElements[index++] = element;
        }
    }

    void addProperties(Class clazz)
    {
        Properties properties = loadProperties(clazz);
        props.putAll(properties);
    }

    private Properties loadProperties(Class clazz)
    {
        Properties properties = new Properties();
        String propsName = Extract.getClassname(clazz) + PROPERTIES;
        try
        {
            log.debug("Loading documentation file: '" + propsName + "'.");
            properties.load(clazz.getResourceAsStream(propsName));
        }
        catch (Exception e)
        {
            log.error("Could not find properties-file '" + propsName + "'!");
        }
        return properties;
    }

    private void write2Props(String line)
    {
        if (generatePropertiesFile)
        {
            try
            {
                propertiesFileWriter.println(line);
            }
            catch (Exception e)
            {
                log.error("Could not write '" + line + "' to properties file!");
            }
        }
    }

    /**
     * Writes the documentation to the specified directory
     * @param factory The one, its doc is tobe written
     */
    public void writeDocumentation(Gui4jComponentFactory factory)
    {
        if (generatePropertiesFile)
        {
            generatePropertySkeleton(factory);
        }

        log.debug("*************************************************");        
        log.info("Generating doc for '" + factory.getName() + "'.");

        // Load translation property-files of the handled class and its superclasses:
        Class clazz = factory.getClass();
        String className = Extract.getClassname(clazz);
        do
        {
            Properties properties = loadProperties(clazz);
            for (Iterator it = props.keySet().iterator(); it.hasNext();)
            {
                String key = (String) it.next();
                properties.remove(key);
            }
            props.putAll(properties);
            clazz = clazz.getSuperclass();
            className = Extract.getClassname(clazz);
        }
        while (!"Gui4jComponentFactory".equals(className));

        OutputManager omHTML = OutputManager.createHTMLWriter(outputDirectory, factory.getName());
        writeDocumentation(factory, omHTML.getDocument());
        omHTML.finish();

        if (generatePropertiesFile)
        {
            try
            {
                propertiesFileWriter.close();
            }
            catch (Exception e)
            {
                log.error("Could not close properties file at the end!");
            }
        }
    }

    public void writeDocumentation(Gui4jComponentFactory factory, Document doc)
    {
        // write document for top level element of factory
        writeDocumentation(factory.getName(), factory, doc);

        // write documents for inner elements
        String[] innerElements = factory.getInnerElements();
        if (innerElements != null)
        {
            for (int index = 0; index < innerElements.length; index++)
            {
                String element = innerElements[index];
                documentationManager.addElementEntry(element); // collect for "all elements" overview
                log.debug("Generating doc für inner element: " + element);
                OutputManager omHTML = OutputManager.createHTMLWriter(outputDirectory, element);
                writeDocumentation(element, factory, omHTML.getDocument());
                omHTML.finish();
            }
        }
    }

    private void generatePropertySkeleton(final Gui4jComponentFactory factory)
    {
        String propFileStr = outputDirectory
                + File.separator
                + Extract.getClassname(factory.getClass())
                + PROPERTIES;
        try
        {
            File propFile = new File(propFileStr);
            propFile.createNewFile();
            this.propertiesFileWriter = new PrintWriter(new FileOutputStream(propFile));
        }
        catch (Exception e)
        {
            log.error("Could not create Properties file '" + propFileStr + "'");
        }
        write2Props("description = " + TODO);

        // Build skeleton for class:
        log.debug("Build skeleton for class");
        List instanceAttributes = new ArrayList();
        factory.addToplevelAttributes(instanceAttributes, new Filter()
        {
            public boolean takeIt(Object object)
            {
                return ((Class) object).equals(factory.getClass());
            }
        });
        Collections.sort(instanceAttributes, getAttributesComparator());
        generatePropertySkeletonForElement(factory, factory.getName(), instanceAttributes);

        // Build skeletons for superclasses:
        log.debug("Build skeletons for superclasses");
        Class clazz = factory.getClass().getSuperclass();
        while (!clazz.equals(Object.class))
        {
            List superAttributes = new ArrayList();
            final Class clazzI = clazz;
            factory.addToplevelAttributes(superAttributes, new Filter()
            {
                public boolean takeIt(Object object)
                {
                    return ((Class) object).equals(clazzI);
                }
            });
            Collections.sort(superAttributes, getAttributesComparator());
            generatePropertySkeletonForElement(clazz, superAttributes);
            clazz = clazz.getSuperclass();
        }

        // Build skeletons for inner elements of class:
        String[] innerElems = factory.getInnerElements();        
        if (innerElems != null)
        {
            log.debug("Build skeletons for inner elements of class");
            for (int i = 0; i < innerElems.length; i++)
            {
                String innerElem = innerElems[i];
                List attributes = new ArrayList();
                factory.addInnerAttributes(innerElem, attributes);
                Collections.sort(attributes, getAttributesComparator());
                generatePropertySkeletonForElement(factory, innerElem, attributes);
            }
        }
    }

    /**
     * Generates the property skeletons for a specified superclass. These are only toplevel!!
     * @param clazz the class of the inheritance tree, to whose properties file the skeletons shall be written
     * @param attributeList the list holding the attributes
     */
    private void generatePropertySkeletonForElement(Class clazz, List attributeList)
    {
        for (Iterator it = attributeList.iterator(); it.hasNext();)
        {
            Attribute attribute = (Attribute) it.next();
            Set propertiesWriteOutSet = documentationManager.getPropertiesWriteOutSet(Extract.getClassname(clazz));
            propertiesWriteOutSet.add(getDocumentationKey(attribute) + " = " + TODO);
            generatePropertySkeletonForAttributeParams(attribute, propertiesWriteOutSet);
        }
    }

    /**
     * Generates the property skeletons for a specified element. The element can be a inner one as well as a
     * top level one.
     * @param factory The factory that provides the elements
     * @param elementName the name of the element to handle
     * @param attributeList the list of attributes to handle
     */
    private void generatePropertySkeletonForElement(
        Gui4jComponentFactory factory,
        String elementName,
        List attributeList)
    {
        if (!elementName.equals(factory.getName()))
        {
            write2Props(elementName + "_description = " + TODO);
        }
        for (Iterator it = attributeList.iterator(); it.hasNext();)
        {
            Attribute attribute = (Attribute) it.next();

            String key = getDocumentationKey(elementName, factory, attribute);
            write2Props(key + " = " + TODO);
            generatePropertySkeletonForAttributeParams(attribute, factory, elementName);
        }

    }

    /**
     * Writes te attributes of a superclass to the corresponding <code>propertiesWriteOutSet</code>.
     * @param attribute The attribute, whose params skeletons shall be generated
     * @param propertiesWriteOutSet the set to deposit the generated attributes
     */
    private void generatePropertySkeletonForAttributeParams(Attribute attribute, Set propertiesWriteOutSet)
    {
        if (attribute.getAttributeType() instanceof ParameterAttributeType)
        {
            Collection params = ((ParameterAttributeType) attribute.getAttributeType()).getParams();
            List paramList = new ArrayList(params);
            Collections.sort(paramList, getParamComparator());
            for (Iterator it = paramList.iterator(); it.hasNext();)
            {
                Param param = (Param) it.next();
                propertiesWriteOutSet.add(getDocumentationKey(attribute, param) + " = " + TODO);
            }
        }
    }

    /**
     * Writes all param property skeletons for a specified attribute of a direct associated class (no
     * superclass):
     * 
     * @param attribute attribute The attribute, whose params skeletons shall be generated
     * @param factory factory The factory that provided these attributes
     * @param elementName elementName The name of teh element that holds the given attribute
     */
    private void generatePropertySkeletonForAttributeParams(
        Attribute attribute,
        Gui4jComponentFactory factory,
        String elementName)
    {
        if (!(attribute.getAttributeType() instanceof ParameterAttributeType))
        {
            return;
        }

        List paramList = new ArrayList(((ParameterAttributeType) attribute.getAttributeType()).getParams());
        Collections.sort(paramList, getParamComparator());
        for (Iterator it = paramList.iterator(); it.hasNext();)
        {
            Param param = (Param) it.next();
            String key = getDocumentationKey(elementName, factory, attribute, param);
            write2Props(key + " = " + TODO);
        }
    }

    public void writeHeader(Document doc, String elementName) throws DocumentException
    {
        DateFormat date = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");

        doc.addTitle("GUI4J - "
                + elementName
                + " ("
                + date.format(new Date(System.currentTimeMillis()))
                + ")");
        doc.addKeywords(elementName);

        doc.add(new Header(MarkupTags.STYLESHEET, STYLESHEET));
        doc.open();

        // Title:
        Paragraph titleP = new Paragraph(elementName);
        titleP.setMarkupAttribute(MarkupTags.CLASS, CLASS_STYLE_TITLE);
        // Add anchor for links:
        Anchor dest = new Anchor("");
        dest.setName(elementName);
        titleP.add(dest);

        try
        {
            doc.add(titleP);
        }
        catch (DocumentException de)
        {
            log.error("Could not write header for '" + elementName + "'");
        }
    }

    /**
     * Writes the docs for a specified element. The element must be defined by the supplied factory.
     * @param elementName element the documentation will be written for
     * @param factory the factory that provides the attributes
     * @param doc the document to write the documentation into
     */
    public void writeDocumentation(String elementName, Gui4jComponentFactory factory, Document doc)
    {
        writtenElements.add(elementName);

        try
        {
            writeHeader(doc, elementName);
            writeDescriptionParagraph(elementName, factory, doc);
            writeSubElementsParagraph(elementName, factory, doc);
            writeAttributeParagraphs(elementName, factory, doc);
            doc.close();
        }
        catch (DocumentException de)
        {
            log.error("Error creating documentation for: " + elementName, de);
            de.printStackTrace();
        }

    }

    private void writeAttributeParagraphs(String elementName, Gui4jComponentFactory factory, Document doc)
            throws DocumentException
    {
        // Attributes:
        Table tableReq = createTable(2);
        Cell cell1Req = new Cell(REQUIRED_ATTRIBUTES);
        cell1Req.setMarkupAttribute(MarkupTags.CLASS, CLASS_STYLE_STRUCTURE);
        cell1Req.setColspan(2);
        tableReq.addCell(cell1Req);

        List list = new ArrayList();
        if (!elementName.equals(factory.getName()))
        {
            factory.addInnerAttributes(elementName, list);
        }
        else
        {
            factory.addToplevelAttributes(list, new Filter()
            {
                public boolean takeIt(Object object)
                {
                    Class c = (Class) object;
                    return !c.equals(Gui4jJComponentFactory.class) && !c.equals(Gui4jComponentFactory.class);
                }
            });
        }
        List lReq = filterAttributes(list, true);
        Collections.sort(lReq, getAttributesComparator());
        writeAttributeDocumentation(tableReq, lReq, elementName, factory);
        doc.add(tableReq);

        Table tableImp = createTable(2);
        Cell cell1Imp = new Cell(IMPLIED_ATTRIBUTES);
        cell1Imp.setMarkupAttribute(MarkupTags.CLASS, CLASS_STYLE_STRUCTURE);
        cell1Imp.setColspan(2);
        tableImp.addCell(cell1Imp);

        List lImpl = filterAttributes(list, false);
        Collections.sort(lImpl, getAttributesComparator());
        writeAttributeDocumentation(tableImp, lImpl, elementName, factory);
        doc.add(tableImp);
    }

    private void writeSubElementsParagraph(String elementName, Gui4jComponentFactory factory, Document doc)
            throws DocumentException
    {
        // create 'Sub-Elements' paragraph
        Table tableSub = createTable(1);
        Cell cell1Sub = new Cell(SUBELEMENTS);
        cell1Sub.setMarkupAttribute(MarkupTags.CLASS, CLASS_STYLE_STRUCTURE);
        tableSub.addCell(cell1Sub);

        Paragraph paragraph = buildSubElements(elementName, factory);

        paragraph.setMarkupAttribute(MarkupTags.CLASS, CLASS_STYLE_PLAINTEXT);
        Cell cell2Sub = new Cell(paragraph);
        tableSub.addCell(cell2Sub);
        doc.add(tableSub);
    }

    private void writeDescriptionParagraph(String elementName, Gui4jComponentFactory factory, Document doc)
            throws DocumentException
    {
        // --- create 'description' paragraph
        Table table = createTable(1);
        Cell cell1 = new Cell(DESCRIPTION);
        cell1.setMarkupAttribute(MarkupTags.CLASS, CLASS_STYLE_STRUCTURE);
        table.addCell(cell1);

        String descKey = getDocumentationKey(elementName, factory, "description");
        String descS = props.getProperty(descKey);
        if (descS == null)
        {
            descS = MISSING;
            log.warn("Property '" + descKey + "' not documented!");
        }
        Cell cell2 = new Cell(buildParagraph(descS));
        cell2.setMarkupAttribute(MarkupTags.CLASS, CLASS_STYLE_PLAINTEXT);
        table.addCell(cell2);
        doc.add(table);
    }

    private String getDocumentationKey(String elementName, Gui4jComponentFactory factory, String key)
    {
        if (elementName.equals(factory.getName()))
        {
            return key;
        }
        else
        {
            return elementName + "_" + key;
        }
    }

    protected String getDocumentationKey(
        String elementName,
        Gui4jComponentFactory factory,
        Attribute attribute,
        Param param)
    {
        return getDocumentationKey(elementName, factory, getDocumentationKey(attribute, param));
    }

    protected String getDocumentationKey(String elementName, Gui4jComponentFactory factory, Attribute attribute)
    {
        return getDocumentationKey(elementName, factory, attribute, null);
    }

    private String getDocumentationKey(Attribute attribute, Param param)
    {
        String key = "attribute_" + attribute.getName();
        if (param != null)
        {
            key += "_param_" + param.getName();
        }
        return key;
    }

    private String getDocumentationKey(Attribute attribute)
    {
        return getDocumentationKey(attribute, null);
    }

    /**
     * A simple factory method for tables.
     * 
     * @param cols the amount of columns. If cols <1 is specified, cols is set to 1!
     * @return a table with at least one column.
     */
    Table createTable(int cols)
    {
        if (cols < 1)
        {
            cols = 1;
        }
        Table table = null;
        try
        {
            table = new Table(cols);
            // ---------------------------------------
            float[] widths = new float[cols];
            widths[0] = 3.0f;
            for (int i = 1; i < widths.length; i++)
            {
                widths[i] = 100.0f;
            }
            table.setWidths(widths);
            // ---------------------------------------
        }
        catch (BadElementException bee)
        {
            // Can never happen, because if-condition asserts that cols > 0
            // and columns are handled equally.
            log.error("Exception in creation of a table!");
        }
        table.setWidth(100.0f);
        return table;
    }

    /**
     * Fills an iText Paragraph with a Regular Expression of SubElements. Sub elements are linked to their
     * documentation page.
     * 
     * @param elementName name of the element to create sub-element paragraph for
     * @param factory The factory the element belongs to (either top-level or inner element)
     * @return The iText Paragraph containing the linked RegExp
     */
    private Paragraph buildSubElements(String elementName, Gui4jComponentFactory factory)
    {

        Paragraph paragraph = new Paragraph();
        String subElementRegexp = factory.getSubElement(elementName).toString();
        String[] possibleSubElements = buildPossibleSubElements(factory.getInnerElements());

        int begin = 0;
        int end = 0;
        String linkedElement = findNextSubelement(subElementRegexp, possibleSubElements, end);
        while (linkedElement != null)
        {
            end = subElementRegexp.indexOf(linkedElement, end);

            Chunk c = new Chunk(subElementRegexp.substring(begin, end));
            paragraph.add(c);
            end += linkedElement.length();
            begin = end;

            paragraph.add(createSubelementAnchor(linkedElement));
            log.debug("  Added link for subElement '" + linkedElement + "'.");

            linkedElement = findNextSubelement(subElementRegexp, possibleSubElements, end);
        }
        // finally add rest of regexp
        Chunk c = new Chunk(subElementRegexp.substring(end));
        paragraph.add(c);

        return paragraph;

    }

    private Anchor createSubelementAnchor(String linkedElement)
    {
        String reference = linkedElement + HTML;

        // ugly workaround
        if (linkedElement.equals(Gui4jComponentFactory.ELEMENT_COMPONENT)
                || linkedElement.equals(Gui4jComponentFactory.ELEMENT_GUI4JREF))
        {
            reference = "Gui4j" + reference;
        }

        Properties p = new Properties();
        p.setProperty("target", "BODY");
        p.setProperty(ElementTags.FONT, FontFactory.HELVETICA);
        Anchor anchor = new Anchor(p);
        anchor.add(new Chunk(linkedElement));
        anchor.setReference(reference);
        return anchor;
    }

    private String[] buildPossibleSubElements(String[] innerElements)
    {
        // build one array from all top level elements, given inner elements and
        // references to non-generated sub elements.

        int size = topLevelElements.length + NON_GENERATES.length;
        if (innerElements != null)
        {
            size += innerElements.length;
        }
        String[] elements = new String[size];
        System.arraycopy(topLevelElements, 0, elements, 0, topLevelElements.length);
        System.arraycopy(NON_GENERATES, 0, elements, topLevelElements.length, NON_GENERATES.length);
        if (innerElements != null)
        {
            System.arraycopy(
                innerElements,
                0,
                elements,
                topLevelElements.length + NON_GENERATES.length,
                innerElements.length);
        }

        return elements;
    }

    /**
     * Returns the first element of <code>subElems</code> that appears in <code>subElemRegExp</code>;
     * @param subElemRegExp a regular expression
     * @param subElems an array of Strings that are contained in <code>subElemRegExp</code>
     * @param fromIndex
     * @return The first occuring subElem of <code>subElems</code> in <code>subElemRegExp</code> OR NULL
     *         if nothing found!
     */
    private String findNextSubelement(String subElemRegExp, String[] subElems, int fromIndex)
    {
        int pos = Integer.MAX_VALUE;
        String elem = null;
        for (int i = 0; i < subElems.length; i++)
        {
            int newPos = subElemRegExp.indexOf(subElems[i], fromIndex);
            // Now check, if next char is a letter. If so, the found one is only a substring and not suitable!
            int nextChar = newPos + subElems[i].length();
            if (subElemRegExp.length() > nextChar
                    && Character.isLetterOrDigit(subElemRegExp.charAt(nextChar)))
            {
                continue;
            }
            if (newPos < pos & newPos >= 0)
            {
                pos = newPos;
                elem = subElems[i];
            }
        }
        return elem;
    }

    /**
     * Given a list of <code>Gui4jComponentFactory.Attribute</code>, all those are filtered out that are
     * REQUIRED or not.
     * 
     * @param list a list of <code>Gui4jComponentFactory.Attribute</code>.
     * @param filterForRequired if this is <code>true</code>, only those are returned that are tagged as
     *            REQUIRED in the DTD. In case of <code>false</code>, those that are IMPLIED are returned.
     * @return List of attributes, either only the required ones or the implied ones.
     */
    List filterAttributes(List list, boolean filterForRequired)
    {
        List l = new ArrayList();
        for (Iterator it = list.iterator(); it.hasNext();)
        {
            Attribute attribute = (Attribute) it.next();
            if ((attribute.isRequired() && filterForRequired)
                    || (!attribute.isRequired() && !filterForRequired))
            {
                l.add(attribute);
            }
        }
        return l;
    }

    /**
     * Loops over all given attributes and builds the documentation for them.
     * 
     * @param table the table to add the attributes. Should have two column, because two are added per row.
     * @param list the list of <code>Gui4jComponentFactory.Attribute</code>s.
     * @param elementName the name of the XML-Element, these attributes belong to. Is only used in debug
     *            messages.
     * @param factory the object that provides to attributes. Required to find out to which class (superclass)
     *            an attibute belongs. Necessary for building property skeletons.
     */
    void writeAttributeDocumentation(Table table, List list, String elementName, Gui4jComponentFactory factory)
    {

        AttributeDocumentationWriter attributeWriter = new AttributeDocumentationWriter(
            table,
            elementName,
            factory);

        for (Iterator it = list.iterator(); it.hasNext();)
        {
            Attribute attribute = (Attribute) it.next();
            //log.debug(" Building doc for attribute '" + attribute.getName() + "'.");
            attributeWriter.write(attribute);
        }
    }

    protected Paragraph buildParagraph(String text)
    {
        Paragraph paragraph = new Paragraph();

        while (text.indexOf(TAG_BEGIN) != -1)
        {
            // extract tag name and value
            int tagBeginPos = text.indexOf(TAG_BEGIN);
            int tagEndPos = text.indexOf(TAG_END);
            String tag = text.substring(tagBeginPos + TAG_BEGIN.length(), tagEndPos);

            // add to paragraph the text chunk up to the tag
            paragraph.add(new Chunk(text.substring(0, tagBeginPos)));

            // get remaining text after tag
            text = text.substring(tagEndPos + TAG_END.length());

            // Workaround for automatic trim()s inside the com.lowagie classes:
            // If the text following after the tag starts with a space (which will
            // get trimmed) wi simulate it by specifying a right-margin for the
            // tag's span element.
            boolean simulateBlank = text.startsWith(" ");

            // extract tag name and value
            String tagName;
            String tagValue;
            int spacePos = tag.indexOf(" ");
            if (spacePos == -1)
            {
                tagName = tag;
                tagValue = "";
            }
            else
            {
                tagName = tag.substring(0, spacePos);
                tagValue = tag.substring(spacePos + 1);
            }

            // add tag to paragraph
            if (LINK.equals(tagName))
            {
                Anchor anchor = new Anchor(tagValue);
                anchor.setReference(tagValue + HTML);
                paragraph.add(anchor);
            }
            else if (TT.equals(tagName))
            {
                paragraph.add(getTTChunk(tagValue, simulateBlank));
            }
            else if (B.equals(tagName))
            {
                paragraph.add(getBChunk(tagValue, simulateBlank));
            }
            else if (BR.equals(tagName))
            {
                Chunk c = new Chunk(" \n ");
                paragraph.add(c);
            }
            else if (IMG.equals(tagName))
            {
                try
                {
                    Image i = Image.getInstance(getClass().getResource(tagValue));
                    this.documentationManager.addInstallFile(tagValue);
                    paragraph.add(i);
                }
                catch (Exception e)
                {
                    log.error("Could not add '" + tagValue + "' as an image!", e);
                }
            }

        }

        paragraph.add(new Chunk(text));
        return paragraph;
    }

    protected Paragraph buildParagraph(String text, String classAttribute)
    {
        Paragraph p = buildParagraph(text);
        p.setMarkupAttribute(MarkupTags.CLASS, classAttribute);
        return p;
    }

    protected static Chunk getTTChunk(String text, boolean simulateBlank)
    {
        return getChunk(text, simulateBlank, FontFactory.COURIER, CLASS_STYLE_PLAINTEXTTT);
    }

    private static Chunk getBChunk(String text, boolean simulateBlank)
    {
        return getChunk(text, simulateBlank, FontFactory.HELVETICA, CLASS_STYLE_PLAINTEXTBOLD);
    }

    private static Chunk getChunk(String text, boolean simulateBlank, String fontName, String baseStyle)
    {
        Chunk c = new Chunk(text);
        c.setFont(FontFactory.getFont(fontName, 10, Font.NORMAL));
        String style = baseStyle;
        if (simulateBlank)
        {
            style += CLASS_SUFFIX_RIGHTMARGIN;
        }
        c.setMarkupAttribute(MarkupTags.CLASS, style);
        return c;

    }

    /**
     * Provides a <code>Comparator</code> to sort <code>Attibutes</code> in alphabetical order of their
     * names.
     * @return Comparator to sort <code>Attibutes</code> in alphabetical order of their names
     */
    public static Comparator getAttributesComparator()
    {
        return new Comparator()
        {
            public int compare(Object o1, Object o2)
            {
                if (o1 instanceof Attribute && o2 instanceof Attribute)
                {
                    Attribute a1 = (Attribute) o1;
                    Attribute a2 = (Attribute) o2;
                    return a1.getName().compareTo(a2.getName());
                }
                return 0;
            }
        };
    }

    /**
     * Prodides a <code>Comparator</code> to sort <code>Params</code> in alphabetical order of their
     * names.
     * @return Comparator to sort <code>Params</code> in alphabetical order of their names
     */
    public static Comparator getParamComparator()
    {
        return new Comparator()
        {
            public int compare(Object o1, Object o2)
            {
                if (o1 instanceof Param && o2 instanceof Param)
                {
                    Param p1 = (Param) o1;
                    Param p2 = (Param) o2;
                    return p1.getName().compareTo(p2.getName());
                }
                return 0;
            }
        };
    }

    /**
     * Class responsible for creating the documentation entry for one attribute.
     */
    private class AttributeDocumentationWriter implements AttributeTypeVisitor
    {

        private final Table table;
        private final String elementName;
        private final Gui4jComponentFactory factory;

        private Attribute attribute;

        // attributes to be filled in the visit() methods
        private String typeDescription;
        private Paragraph[] infoParagraphs;
        private String paramHeader;
        private com.lowagie.text.List paramList;

        public AttributeDocumentationWriter(Table table, String elementName, Gui4jComponentFactory factory)
        {
            this.table = table;
            this.elementName = elementName;
            this.factory = factory;
        }

        /**
         * This method creates two table cells. One table cell on the left with the name of the attribute and
         * some info about its type and default values. And one cell to the right with the description of the
         * attribute and its parameters.
         * @param pAttribute
         */
        public void write(Attribute pAttribute)
        {
            this.attribute = pAttribute;

            typeDescription = "";
            infoParagraphs = null;
            paramHeader = "";
            paramList = null;

            // Visitor pattern -> use double-dispatch to prepare for the correct attribute type
            pAttribute.getAttributeType().accept(this);

            // --- Build left side of table entry: attribute name, type, default value
            Cell cellLeft = new Cell();
            Paragraph pName = new Paragraph(pAttribute.getName());
            pName.setMarkupAttribute(MarkupTags.CLASS, CLASS_STYLE_SUBTITLEPLAIN);
            cellLeft.add(pName);
            Paragraph pTypeDesc = new Paragraph(typeDescription);
            pTypeDesc.setMarkupAttribute(MarkupTags.CLASS, CLASS_STYLE_PLAINTEXT);
            cellLeft.add(pTypeDesc);

            if (infoParagraphs != null)
            {
                for (int i = 0; i < infoParagraphs.length; i++)
                {
                    cellLeft.add(infoParagraphs[i]);
                }
            }

            cellLeft.setVerticalAlignment(Element.ALIGN_TOP);
            cellLeft.setNoWrap(true);
            table.addCell(cellLeft);

            // -- Build right side: description, parameters
            Cell cellRight = new Cell();

            String attrKey = getDocumentationKey(elementName, factory, pAttribute);
            String attrDesc = props.getProperty(attrKey);

            // warn if description is missing
            if (attrDesc == null)
            {
                attrDesc = MISSING;
                log.warn("Property '" + attrKey + "' not documented!");
            }

            // description
            cellRight.add(buildParagraph(attrDesc, CLASS_STYLE_PLAINTEXT));

            // parameters
            if (paramList != null)
            {
                cellRight.add(buildParagraph("\n" + paramHeader + ":\n", CLASS_STYLE_PLAINTEXT));
                cellRight.add(paramList);
            }

            cellRight.setVerticalAlignment(Element.ALIGN_TOP);
            table.addCell(cellRight);

        }

        public void visit(AttributeTypeAlias type)
        {
            typeDescription = "Type alias";
        }

        public void visit(AttributeTypeEnumeration type)
        {
            typeDescription = "Enumeration";

            // create info paragraph
            infoParagraphs = createInfoParagraphs(type);

            // -- create parameter list
            Collection params = type.getParams();
            if (params.size() > 0)
            {
                paramHeader = "Allowed values";

                log.debug("  Generating "
                        + params.size()
                        + " enum values(s) for attribute '"
                        + attribute.getName()
                        + "'.");
                List sortedParams = new ArrayList(params);
                Collections.sort(sortedParams, getParamComparator());
                paramList = new com.lowagie.text.List(false, 10);
                for (Iterator it1 = sortedParams.iterator(); it1.hasNext();)
                {
                    Param param = (Param) it1.next();
                    Paragraph pParam = new Paragraph();
                    pParam.add(getTTChunk(param.getName(), true));
                    pParam.add(new Chunk("\n"));
                    log.debug("    Added: param '" + param.getName() + "'.");

                    String propName = getDocumentationKey(elementName, factory, attribute, param);
                    String paramDesc = props.getProperty(propName);
                    // warn if param description is missing
                    if (paramDesc == null)
                    {
                        paramDesc = MISSING;
                        log.warn("Property '" + propName + "' not documented!");
                    }
                    pParam.add(buildParagraph(paramDesc));

                    paramList.add(new ListItem(pParam));
                }
            }

        }

        public void visit(AttributeTypeFloatingPoint type)
        {
            typeDescription = "floating point constant";

            // create info paragraph
            infoParagraphs = createInfoParagraphs(type);

        }

        public void visit(AttributeTypeID type)
        {
            typeDescription = "Gui4j-ID";
        }

        public void visit(AttributeTypeInteger type)
        {
            typeDescription = "integer constant";

            // create info paragraph
            infoParagraphs = createInfoParagraphs(type);

        }

        public void visit(AttributeTypeMethodCall type)
        {
            typeDescription = "Method call";

            // create Info paragraphs
            Paragraph pInfo1 = new Paragraph("return type: ");
            pInfo1.setMarkupAttribute(MarkupTags.CLASS, CLASS_STYLE_PLAINTEXT);
            pInfo1.add(getTTChunk(type.getReturnType(), true));
            if (type.isEventAware())
            {
                Paragraph pInfo2 = new Paragraph("event aware");
                pInfo2.setMarkupAttribute(MarkupTags.CLASS, CLASS_STYLE_PLAINTEXT);
                infoParagraphs = new Paragraph[] { pInfo1, pInfo2 };
            }
            else
            {
                infoParagraphs = new Paragraph[] { pInfo1 };
            }

            // -- create parameter list
            Collection params = type.getParams();
            if (params.size() > 0)
            {
                paramHeader = "Allowed parameters in method call";

                log.debug("  Generating "
                        + params.size()
                        + " parameter(s) for attribute '"
                        + attribute.getName()
                        + "'.");
                List sortedParams = new ArrayList(params);
                Collections.sort(sortedParams, getParamComparator());
                paramList = new com.lowagie.text.List(false, 10);
                for (Iterator it1 = sortedParams.iterator(); it1.hasNext();)
                {
                    Param param = (Param) it1.next();
                    Paragraph pParam = new Paragraph();
                    pParam.add(getTTChunk("?" + param.getName(), true));
                    if (param.getType() != null)
                    {
                        pParam.add(new Chunk("(type: "));
                        pParam.add(getTTChunk(param.getType(), false));
                        pParam.add(new Chunk(")"));
                    }
                    else if (param.getTypeOrigin() != null)
                    {
                        pParam.add(new Chunk("(type as defined by: "));
                        pParam.add(getTTChunk(param.getTypeOrigin(), false));
                        pParam.add(new Chunk(")"));
                    }
                    else
                    {
                        pParam.add(new Chunk("([type description missing])"));
                    }
                    pParam.add(new Chunk("\n"));
                    log.debug("    Added: param '" + param.getName() + "'.");

                    String propName = getDocumentationKey(elementName, factory, attribute, param);
                    String paramDesc = props.getProperty(propName);

                    // warn if param description is missing
                    if (paramDesc == null)
                    {
                        paramDesc = MISSING;
                        log.warn("Property '" + propName + "' not documented!");
                    }

                    pParam.add(buildParagraph(paramDesc));
                    paramList.add(new ListItem(pParam));
                }
            }

        }

        public void visit(AttributeTypeString type)
        {
            typeDescription = "string constant";
        }

        private Paragraph[] createInfoParagraphs(DefaultValueAttributeType type)
        {
            if (type.getDefaultValue() == null)
            {
                return null;
            }

            Paragraph p = new Paragraph("default: ");
            p.setMarkupAttribute(MarkupTags.CLASS, CLASS_STYLE_PLAINTEXT);
            p.add(getTTChunk(type.getDefaultValue(), true));
            return new Paragraph[] { p };
        }
    }

}