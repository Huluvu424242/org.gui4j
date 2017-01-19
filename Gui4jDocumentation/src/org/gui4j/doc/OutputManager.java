package org.gui4j.doc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lowagie.text.Document;
import com.lowagie.text.html.HtmlWriter;

public class OutputManager
{
    public static final Log log = LogFactory.getLog(OutputManager.class);
    public static final String HTML = ".html";

    private final Document doc;
    private final OutputStream outStream;
    private final String outLocation;

    private OutputManager(Document doc, OutputStream outStream, String outLocation)
    {
        this.doc = doc;
        this.outStream = outStream;
        this.outLocation = outLocation;
    }

    public static OutputManager createHTMLWriter(File outputDirectory, String fileNameWithoutSuffix)
    {
        com.lowagie.text.Document lDoc = new com.lowagie.text.Document();
        OutputStream outHTML = null;
        File file = new File(outputDirectory, fileNameWithoutSuffix + HTML);
        try
        {
            outHTML = new FileOutputStream(file);
            HtmlWriter htmlWriter = HtmlWriter.getInstance(lDoc, outHTML);
            htmlWriter.setImagepath("");
            htmlWriter.toString(); // This is just to supress the eclipse warning.
        }
        catch (Exception e)
        {
            log.error("Could not create output machinery for '" + file.getPath() + "'!");
        }
        return new OutputManager(lDoc, outHTML, file.getPath());
    }
    
    public Document getDocument()
    {
        return doc;
    }
    
    /**
     * Cleans up all outputstreams.
     */
    public void finish()
    {
        try
        {
            outStream.flush();
            outStream.close();
        }
        catch(Exception e)
        {
            log.error("Could not flush and close output machinery for '" + outLocation + "'!");
        }
    }
}
