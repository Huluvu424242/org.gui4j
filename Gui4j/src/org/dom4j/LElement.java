package org.dom4j;

import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.tree.DefaultElement;

public class LElement extends DefaultElement
{
    private int lineNumber;

    public LElement(String name) {
        super(name);
    }

    public LElement(QName qname) {
        super(qname);
    }

    public String toString() {
        return super.toString() + " lineNumber: " + lineNumber;
    }

    public Object clone() {
        LElement answer = (LElement) super.clone();

        if (answer != this) {
            answer.lineNumber = lineNumber;
        }

        return answer;
    }

    // Implementation methods
    // -------------------------------------------------------------------------

    protected Element createElement(String name) {
        LElement answer = (LElement)getDocumentFactory().createElement(name);
        answer.setLineNumber(getLineNumber());

        return answer;
    }

    protected Element createElement(QName qName) {
        LElement answer = (LElement)getDocumentFactory().createElement(qName);
        answer.setLineNumber(getLineNumber());

        return answer;
    }

    /**
     * @return Returns the lineNumber.
     */
    public int getLineNumber()
    {
        return lineNumber;
    }
    /**
     * @param lineNumber The lineNumber to set.
     */
    public void setLineNumber(int lineNumber)
    {
        this.lineNumber = lineNumber;
    }
}
