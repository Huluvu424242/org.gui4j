package org.dom4j.io;

import org.dom4j.DocumentFactory;
import org.dom4j.ElementHandler;
import org.dom4j.LElement;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class LNSAXContentHandler extends SAXContentHandler
{
    private Locator locator;

    public LNSAXContentHandler()
    {
        super();
    }

    /**
     * @param documentFactory
     */
    public LNSAXContentHandler(DocumentFactory documentFactory)
    {
        super(documentFactory);
    }

    /**
     * @param documentFactory
     * @param elementHandler
     */
    public LNSAXContentHandler(DocumentFactory documentFactory, ElementHandler elementHandler)
    {
        super(documentFactory, elementHandler);
    }

    
    
    /* (non-Javadoc)
     * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(String namespaceURI, String localName, String qualifiedName, Attributes attributes)
            throws SAXException
    {
        int lineNumber = locator.getLineNumber();
        super.startElement(namespaceURI, localName, qualifiedName, attributes);
        LElement element = (LElement)getElementStack().getCurrent();
        element.setLineNumber(lineNumber);
    }
    
    
    /* (non-Javadoc)
     * @see org.xml.sax.ContentHandler#setDocumentLocator(org.xml.sax.Locator)
     */
    public void setDocumentLocator(Locator documentLocator)
    {
        super.setDocumentLocator(documentLocator);
        this.locator = documentLocator;
    }
}
