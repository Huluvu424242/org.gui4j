package org.dom4j.io;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class LNSAXReader extends SAXReader
{

    public LNSAXReader()
    {
        super();
    }

    /**
     * @param validating
     */
    public LNSAXReader(boolean validating)
    {
        super(validating);
    }

    /**
     * @param xmlReader
     */
    public LNSAXReader(XMLReader xmlReader)
    {
        super(xmlReader);
    }

    /**
     * @param xmlReader
     * @param validating
     */
    public LNSAXReader(XMLReader xmlReader, boolean validating)
    {
        super(xmlReader, validating);
    }

    /**
     * @param xmlReaderClassName
     * @throws org.xml.sax.SAXException
     */
    public LNSAXReader(String xmlReaderClassName) throws SAXException
    {
        super(xmlReaderClassName);
    }

    /**
     * @param xmlReaderClassName
     * @param validating
     * @throws org.xml.sax.SAXException
     */
    public LNSAXReader(String xmlReaderClassName, boolean validating) throws SAXException
    {
        super(xmlReaderClassName, validating);
    }
    
    

    /* (non-Javadoc)
     * @see org.dom4j.io.SAXReader#createContentHandler(org.xml.sax.XMLReader)
     */
    protected SAXContentHandler createContentHandler(XMLReader reader)
    {
        return new LNSAXContentHandler(new LDocumentFactory(), getDispatchHandler());
    }
}
