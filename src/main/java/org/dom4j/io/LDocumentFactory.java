package org.dom4j.io;

import org.dom4j.Attribute;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.LElement;
import org.dom4j.QName;
import org.dom4j.util.UserDataAttribute;

public class LDocumentFactory extends DocumentFactory
{
    /** The Singleton instance */
    protected static transient LDocumentFactory singleton
            = new LDocumentFactory();

    /**
     * <p>
     * Access to the singleton instance of this factory.
     * </p>
     * 
     * @return the default singleon instance
     */
    public static DocumentFactory getInstance() {
        return singleton;
    }

    // DocumentFactory methods
    // -------------------------------------------------------------------------
    public Element createElement(QName qname) {
        return new LElement(qname);
    }

    public Attribute createAttribute(Element owner, QName qname, String value) {
        return new UserDataAttribute(qname, value);
    }
}
