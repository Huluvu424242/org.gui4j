package org.gui4j.doc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;
import org.gui4j.Gui4jFactory;
import org.gui4j.component.factory.Gui4jJComponentFactory;
import org.gui4j.core.Gui4jComponentFactory;
import org.gui4j.core.Gui4jComponentManager;
import org.gui4j.core.Gui4jInternal;
import org.gui4j.exception.Gui4jException;
import org.gui4j.util.Filter;

import com.lowagie.text.Anchor;
import com.lowagie.text.Cell;
import com.lowagie.text.Chunk;
import com.lowagie.text.DocumentException;
import com.lowagie.text.ElementTags;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Header;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Table;
import com.lowagie.text.markup.MarkupTags;

public class DocumentationManager
{
    public static final Log log = LogFactory.getLog(DocumentationManager.class);

    public static final String HTML = ".html";
    public static final String PDF = ".pdf";
    public static final String INDEX_PDF = "index" + PDF;
    public static final String GUI4J_DOC = "GUI4J-Documentation" + PDF;

    public static final String CLASS_STYLE_TITLE = "title";
    public static final String CLASS_STYLE_PLAINTEXT = "plaintext";
    public static final String CLASS_STYLE_STRUCTURE = "structure";

    private final boolean generatePropertiesFiles;

    private final Gui4jComponentManager gui4jComponentManager;
    private final Map gui4jComponentMap; // elementName:String -> Gui4jComponentFactory
    private final File outputDirectory;
    private final Set allElements;
    private Map propertiesWriteOutMap; // className:String -> Set(propertySkeleton:String)
    private Properties installFiles; // Contains the filenames to install to the destination directory.

    private int errorCount = 0;

    public DocumentationManager(String outputDirectory, boolean generatePropertiesFiles)
    {
        initLogging();
        log.info("*******************");

        this.outputDirectory = new File(outputDirectory);
        this.allElements = new HashSet();
        this.generatePropertiesFiles = generatePropertiesFiles;
        this.installFiles = new Properties();

        URL configPropertiesURL = getClass().getResource("gui4jComponents.properties");
        copyURLToFile(configPropertiesURL, new File(outputDirectory, "gui4jComponents.properties"));
        Gui4jInternal gui4j = (Gui4jInternal)Gui4jFactory.createGui4j(true, false, -1, configPropertiesURL);
        this.gui4jComponentManager = gui4j.getGui4jComponentManager();
        gui4jComponentMap = gui4jComponentManager.getGui4jComponentMap();
        log.info("Generating docs for " + gui4jComponentMap.size() + " GUI4J components.");
        log.info("*******************");

        if (generatePropertiesFiles)
        {
            propertiesWriteOutMap = new HashMap();
        }
    }
    
    private void copyURLToFile(URL url, File f)
    {
        try
        {
            InputStream in = url.openStream();
            OutputStream out = new FileOutputStream(f);
            byte[] buffer = new byte[4094];
            int count;
            while ((count=in.read(buffer))>0)
            {
                out.write(buffer,0,count);
            }
            out.close();
            in.close();
            
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
        if (args.length != 2 && args.length != 1)
        {
            log.info("Usage: java org.gui4j.DocumentationManager <outputDirectory> [<generatePropertiesFiles:true|false>]");
            log.info("Note: the second parameter is optional (default: false).");
            return;
        }
        String outputDir = args[0];
        boolean lGeneratePropertiesFiles = args.length == 2 && args[1].equals("true");
        DocumentationManager dm = new DocumentationManager(outputDir, lGeneratePropertiesFiles);
        dm.writeDocumentation();
        dm.installFiles();
        dm.createDTD();
        String doneMessage = "Done.";
        if (dm.errorCount() > 0)
        {
            doneMessage += " - " + dm.errorCount() + " ERRORS occurred !";
            System.err.println(doneMessage); // to make it stick out in console output
        }
        log.info(doneMessage);
    }

    public void createDTD()
    {
        log.info("Creating DTD.");
        File outputFile = new File(outputDirectory, "view.dtd");
        try
        {
            gui4jComponentManager.writeDTD(outputFile);
        }
        catch (Gui4jException e)
        {
            log.error("Error while creating DTD.", e);
            errorCount++;
        }
    }

    public int errorCount()
    {
        return errorCount;
    }

    public void addInstallFile(String fileName)
    {
        installFiles.setProperty(fileName, "");
    }

    private void installFiles()
    {
        InputStream in = DocumentationManager.class.getResourceAsStream("files/files.properties");
        try
        {
            installFiles.load(in);
            in.close();
        }
        catch (IOException e)
        {
            log.error(e, e);
        }

        for (Iterator it = installFiles.keySet().iterator(); it.hasNext();)
        {
            String fileName = (String) it.next();
            installFile(outputDirectory, fileName);
        }
    }

    public Map getPropertiesWriteOutMap()
    {
        return propertiesWriteOutMap;
    }

    public Set getPropertiesWriteOutSet(String className)
    {
        if (propertiesWriteOutMap.containsKey(className))
        {
            return (Set) propertiesWriteOutMap.get(className);
        }
        else
        {
            Set s = new HashSet();
            propertiesWriteOutMap.put(className, s);
            return s;
        }
    }

    private void initLogging()
    {
        InputStream is = getClass().getResourceAsStream("log4j.properties");
        Properties props = new Properties();
        try
        {
            props.load(is);
        }
        catch (IOException ioe)
        {
            /* nocheck */System.out.println("Could not initialize Logging with property file! Fallback to default settings!");
        }
        if (props.size() != 0)
        {
            PropertyConfigurator.configure(props);
        }
    }

    //    // Maybe we will later come back to PDF documentation...
    //    public void mergePDFs(String dir)
    //    {
    //        File directory = new File(dir);
    //        File[] pdfFiles = directory.listFiles(new FilenameFilter()
    //        {
    //            public boolean accept(File dir, String name)
    //            {
    //                if (name.endsWith(PDF) && name.indexOf(XUI_DOC)<0)
    //                {
    //                    return true;
    //                }
    //                return false;
    //            }
    //        });
    //        String[] pdfFileNames = new String[pdfFiles.length + 1];
    //        for (int i = 0; i < pdfFiles.length; i++)
    //        {
    //            pdfFileNames[i] = pdfFiles[i].getAbsolutePath();
    //        }
    //
    //        Arrays.sort(pdfFileNames, getPDFBookOrderComparator());
    //
    //        pdfFileNames[pdfFileNames.length-1] = pdfFiles[0].getParent() + File.separator + XUI_DOC;
    //
    //        concat_pdf.main(pdfFileNames);
    //        log.info("Built PDF documentation file.");
    //    }

    public static Comparator getPDFBookOrderComparator()
    {
        return new Comparator()
        {
            public int compare(Object o1, Object o2)
            {
                if (o1 == null || o2 == null) return 0;
                if (o1.toString().endsWith(INDEX_PDF)) return -1;
                if (o2.toString().endsWith(INDEX_PDF)) return 1;
                return 0;
            }
        };
    }

    public void installFile(File dir, String file)
    {
        InputStream is = getClass().getResourceAsStream("files/"+file);
        assert is != null : file;
        try
        {
            FileOutputStream fos = new FileOutputStream(new File(dir, file));
            while (is.available() > 0)
            {
                fos.write(is.read());
            }
        }
        catch (Exception e)
        {
            log.error("Could not install file: " + file, e);
            errorCount++;
        }
        log.debug("Installed File: '" + file + "'");
    }

    private void checkDirectory()
    {
        if (!outputDirectory.exists())
        {
            outputDirectory.mkdir();
        }
        if (!outputDirectory.isDirectory())
        {
            log.error("Sorry, cannot build documentation, because '"
                    + outputDirectory
                    + "' is a file! Aborting!");
            System.exit(-1);
        }
    }

    public void writeDocumentation()
    {
        checkDirectory();

        OutputManager omHTML = OutputManager.createHTMLWriter(outputDirectory, "index_toplevel");
        writeDocumentation(omHTML.getDocument());
        omHTML.finish();

        OutputManager omHTMLAll = OutputManager.createHTMLWriter(outputDirectory, "index_all");
        writeDocumentationForAll(omHTMLAll.getDocument());
        omHTMLAll.finish();

        OutputManager omHTMLComponent = OutputManager.createHTMLWriter(outputDirectory, "Component");
        writeDocumentationForCommonAttributes(omHTMLComponent.getDocument());
        omHTMLComponent.finish();

        if (generatePropertiesFiles)
        {
            writeRestOfPropertiesFiles();
        }
    }

    /**
     * All Property-skeleton-files of top level components have been written directly. The files for inherited
     * classes are still missing. These are stored in the propertiesWriteOutMap. This method writes the
     * corresponding property files.
     */
    private void writeRestOfPropertiesFiles()
    {
        for (Iterator it = getPropertiesWriteOutMap().entrySet().iterator(); it.hasNext();)
        {
            Map.Entry entry = (Map.Entry) it.next();
            Set s = (Set) entry.getValue();
            List l = new ArrayList(s);
            Collections.sort(l);
            String fileName = outputDirectory + File.separator + ((String) entry.getKey()) + ".properties";
            try
            {
                File file = new File(fileName);
                file.createNewFile();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));

                for (Iterator it1 = l.iterator(); it1.hasNext();)
                {
                    String propertySkeleton = (String) it1.next();
                    bw.write(propertySkeleton);
                }
                bw.flush();
                bw.close();
            }
            catch (Exception e)
            {
                log.error("Could not write property-skeletons to file '" + fileName + "'!");
            }
        }
    }

    private void writeHeader(com.lowagie.text.Document doc, String componentsTitle)
    {
        DateFormat date = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
        String dateS = date.format(new Date(System.currentTimeMillis()));

        doc.addTitle("gui4j - Documentation (" + dateS + ")");

        try
        {
            doc.add(new Header(MarkupTags.STYLESHEET, "style.css"));
        }
        catch (DocumentException de)
        {
            de.printStackTrace();
        }
        doc.open();

        // Paragraph title = new Paragraph("gui4j");
        // title.setMarkupAttribute(MarkupTags.CLASS, CLASS_STYLE_TITLE);
        // Paragraph subTitle = new Paragraph(dateS + "\n\n");
        // subTitle.setMarkupAttribute(MarkupTags.CLASS, CLASS_STYLE_PLAINTEXT);
        Paragraph components = new Paragraph(componentsTitle + "\n");
        components.setMarkupAttribute(MarkupTags.CLASS, CLASS_STYLE_STRUCTURE);
        try
        {
            // doc.add(title);
            // doc.add(subTitle);
            doc.add(components);

        }
        catch (DocumentException de)
        {
            de.printStackTrace();
        }
    }

    public void writeDocumentation(com.lowagie.text.Document doc)
    {
        writeHeader(doc, "Components");

        List list = new ArrayList(gui4jComponentMap.entrySet());
        Collections.sort(list, getEntrySetKeyComparator());

        for (Iterator it = list.iterator(); it.hasNext();)
        {
            Map.Entry entry = (Map.Entry) it.next();
            Gui4jComponentFactory gui4jComponent = (Gui4jComponentFactory) entry.getValue();

            String elemName = gui4jComponent.getName();
            addElementEntry(elemName);

            Properties pp = new Properties();
            pp.setProperty("target", "BODY");
            pp.setProperty(ElementTags.FONT, FontFactory.HELVETICA);

            Anchor anchor = new Anchor(pp);
            anchor.add(new Chunk(elemName));
            anchor.setReference(elemName + HTML);
            anchor.setName(elemName);

            Paragraph p = new Paragraph("");
            p.setMarkupAttribute(MarkupTags.CLASS, CLASS_STYLE_STRUCTURE);
            p.setIndentationLeft(1.0f);
            p.add(anchor);

            try
            {
                doc.add(p);
            }
            catch (DocumentException de)
            {
                de.printStackTrace();
            }

            //Since we alredy have the right component factory, why should we not generate its documentation:
            ComponentDocumentationWriter docWriter = new ComponentDocumentationWriter(
                outputDirectory,
                this,
                generatePropertiesFiles);
            try
            {
                docWriter.writeDocumentation(gui4jComponent);
            }
            catch (Exception e)
            {
                log.error("Could not write documentation for '"
                        + gui4jComponent.getName()
                        + "'. Index of Docs may be corrupt!", e);
                e.printStackTrace();
                errorCount++;
            }
        }

        doc.close();
        log.debug("Generated index file 'index_toplevel.html'.");
    }

    public void writeDocumentationForAll(com.lowagie.text.Document doc)
    {
        writeHeader(doc, "All\nComponents");

        List list = new ArrayList(allElements);
        Collections.sort(list);

        for (Iterator it = list.iterator(); it.hasNext();)
        {
            String element = (String) it.next();

            Properties pp = new Properties();
            pp.setProperty("target", "BODY");
            pp.setProperty(ElementTags.FONT, FontFactory.HELVETICA);
            
            Anchor anchor = new Anchor(pp);
            anchor.add(new Chunk(element));
            anchor.setReference(element + HTML);
            anchor.setName(element);

            Paragraph p = new Paragraph("");
            p.setMarkupAttribute(MarkupTags.CLASS, CLASS_STYLE_STRUCTURE);
            p.setIndentationLeft(1.0f);
            p.add(anchor);

            try
            {
                doc.add(p);
            }
            catch (DocumentException de)
            {
                de.printStackTrace();
            }

        }

        doc.close();
        log.debug("Generated index file 'index_all.html'.");
    }

    public void writeDocumentationForCommonAttributes(com.lowagie.text.Document doc)
    {
        ComponentDocumentationWriter componentDocWriter = new ComponentDocumentationWriter(
            outputDirectory,
            this,
            false);
        componentDocWriter.addProperties(Gui4jJComponentFactory.class);
        componentDocWriter.addProperties(Gui4jComponentFactory.class);

        try
        {
            componentDocWriter.writeHeader(doc, "Common toplevel attributes");
        }
        catch (DocumentException de)
        {
            log.error("Could not write required attributes for common toplevel attributes!", de);
        }

        // Just have to select one factory that extends Gui4jComponentFactory and Gui4jJComponentFactory:
        String elementName = "table";

        Gui4jComponentFactory factory = (Gui4jComponentFactory) gui4jComponentMap.get(elementName);

        List list = new ArrayList();
        factory.addToplevelAttributes(list, new Filter()
        {
            public boolean takeIt(Object object)
            {
                Class c = (Class) object;
                return c.equals(Gui4jJComponentFactory.class) || c.equals(Gui4jComponentFactory.class);
            }
        });
        {
            Table tableReq = componentDocWriter.createTable(2);
            Cell cell1Req = new Cell(ComponentDocumentationWriter.REQUIRED_ATTRIBUTES);
            cell1Req.setMarkupAttribute(MarkupTags.CLASS, CLASS_STYLE_STRUCTURE);
            cell1Req.setColspan(2);
            tableReq.addCell(cell1Req);

            List lReq = componentDocWriter.filterAttributes(list, true);
            Collections.sort(lReq, ComponentDocumentationWriter.getAttributesComparator());
            try
            {
                componentDocWriter.writeAttributeDocumentation(tableReq, lReq, elementName, factory);
                doc.add(tableReq);
            }
            catch (DocumentException de)
            {
                log.error("Could not write required attributes for common toplevel attributes!");
            }
        }

        {
            Table tableImpl = componentDocWriter.createTable(2);
            Cell cell1Impl = new Cell(ComponentDocumentationWriter.IMPLIED_ATTRIBUTES);
            cell1Impl.setMarkupAttribute(MarkupTags.CLASS, CLASS_STYLE_STRUCTURE);
            cell1Impl.setColspan(2);
            tableImpl.addCell(cell1Impl);

            List lImpl = componentDocWriter.filterAttributes(list, false);
            Collections.sort(lImpl, ComponentDocumentationWriter.getAttributesComparator());
            try
            {
                componentDocWriter.writeAttributeDocumentation(tableImpl, lImpl, elementName, factory);
                doc.add(tableImpl);
            }
            catch (DocumentException de)
            {
                log.error("Could not write required attribures for common toplevel attributes!");
            }
        }

        doc.close();
        log.debug("Generated file 'Component.html' for common toplevel attributes.");
    }

    public void addElementEntry(String elementName)
    {
        allElements.add(elementName);
    }

    public static Comparator getEntrySetKeyComparator()
    {
        return new Comparator()
        {
            public int compare(Object o1, Object o2)
            {
                if (o1 instanceof Map.Entry && o2 instanceof Map.Entry)
                {
                    Map.Entry m1 = (Map.Entry) o1;
                    Map.Entry m2 = (Map.Entry) o2;
                    return ((String) m1.getKey()).compareTo(m2.getKey());
                }
                return 0;
            }
        };
    }

    Set getTopLevelElements() {
        return gui4jComponentMap.keySet();
    }

}